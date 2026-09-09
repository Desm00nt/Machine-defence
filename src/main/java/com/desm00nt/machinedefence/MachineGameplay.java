package com.desm00nt.machinedefence;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MachineDefence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MachineGameplay {
    private static final String BUILT = "MachineDefenceBuilt";
    private static final String TURRETS = "MachineDefenceTurrets";

    private MachineGameplay() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("machinedefence")
                .then(Commands.literal("build").executes(context -> build(context.getSource())))
                .then(Commands.literal("shop").executes(context -> shop(context.getSource())))
                .then(Commands.literal("install-turret").executes(context -> installTurret(context.getSource()))));
    }

    private static int build(CommandSourceStack source) throws Exception {
        ServerPlayer player = source.getPlayerOrException();
        if (player.level().dimension() != MachineDefence.MACHINE_DEFENCE_LEVEL) {
            source.sendFailure(Component.literal("Enter Machine Defence first."));
            return 0;
        }
        if (player.getPersistentData().getBoolean(BUILT)) {
            source.sendFailure(Component.literal("Your starter machine is already built."));
            return 0;
        }

        give(player, new ItemStack(MachineBlocks.MACHINE_FRAME.get(), 12));
        give(player, new ItemStack(MachineBlocks.MACHINE_WHEEL.get(), 4));
        give(player, new ItemStack(MachineBlocks.MACHINE_ENGINE.get(), 1));
        give(player, new ItemStack(MachineBlocks.MACHINE_TURRET.get(), 1));
        placeStarterMachine(player);
        player.getPersistentData().putBoolean(BUILT, true);
        player.getPersistentData().putInt(TURRETS, 1);
        player.sendSystemMessage(Component.literal("Starter machine built: frame, wheels, engine, and one turret."));
        return 1;
    }

    private static void placeStarterMachine(ServerPlayer player) {
        int y = 65;
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) player.level();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                level.setBlock(new BlockPos(x, y, z), MachineBlocks.MACHINE_FRAME.get().defaultBlockState(), 3);
            }
        }
        level.setBlock(new BlockPos(-2, y + 1, -2), MachineBlocks.MACHINE_WHEEL.get().defaultBlockState(), 3);
        level.setBlock(new BlockPos(2, y + 1, -2), MachineBlocks.MACHINE_WHEEL.get().defaultBlockState(), 3);
        level.setBlock(new BlockPos(-2, y + 1, 2), MachineBlocks.MACHINE_WHEEL.get().defaultBlockState(), 3);
        level.setBlock(new BlockPos(2, y + 1, 2), MachineBlocks.MACHINE_WHEEL.get().defaultBlockState(), 3);
        level.setBlock(new BlockPos(0, y + 1, 0), MachineBlocks.MACHINE_ENGINE.get().defaultBlockState(), 3);
        level.setBlock(new BlockPos(0, y + 2, 0), MachineBlocks.MACHINE_TURRET.get().defaultBlockState(), 3);
    }

    private static int shop(CommandSourceStack source) throws Exception {
        ServerPlayer player = source.getPlayerOrException();
        int gold = player.getInventory().countItem(Items.GOLD_NUGGET);
        if (gold < 10) {
            source.sendFailure(Component.literal("Shop: a turret costs 10 gold nuggets. You have " + gold + "."));
            return 0;
        }
        consume(player, Items.GOLD_NUGGET, 10);
        give(player, new ItemStack(MachineBlocks.MACHINE_TURRET.get(), 1));
        player.sendSystemMessage(Component.literal("Purchased one turret. Install it with /machinedefence install-turret."));
        return 1;
    }

    private static int installTurret(CommandSourceStack source) throws Exception {
        ServerPlayer player = source.getPlayerOrException();
        int held = countItem(player, MachineBlocks.MACHINE_TURRET.get().asItem());
        if (held < 1) {
            source.sendFailure(Component.literal("You need a Machine Turret from /machinedefence build or the shop."));
            return 0;
        }
        consume(player, MachineBlocks.MACHINE_TURRET.get().asItem(), 1);
        int turrets = player.getPersistentData().getInt(TURRETS) + 1;
        player.getPersistentData().putInt(TURRETS, turrets);
        player.sendSystemMessage(Component.literal("Turret installed. Active turrets: " + turrets));
        return 1;
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.level().dimension() != MachineDefence.MACHINE_DEFENCE_LEVEL) return;
        if (player.getXRot() < 85.0F) return;

        int turrets = player.getPersistentData().getInt(TURRETS);
        if (turrets <= 0 || player.tickCount % 10 != 0) return;
        List<Monster> enemies = player.level().getEntitiesOfClass(Monster.class,
                player.getBoundingBox().inflate(18.0D),
                mob -> mob.getPersistentData().getBoolean("MachineDefenceEnemy"));
        int shots = Math.min(turrets, enemies.size());
        for (int i = 0; i < shots; i++) {
            enemies.get(i).hurt(player.level().damageSources().playerAttack(player), 6.0F);
        }
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }

    private static int countItem(ServerPlayer player, net.minecraft.world.item.Item item) {
        return player.getInventory().countItem(item);
    }

    private static void consume(ServerPlayer player, net.minecraft.world.item.Item item, int amount) {
        for (int slot = 0; slot < player.getInventory().getContainerSize() && amount > 0; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.is(item)) continue;
            int removed = Math.min(amount, stack.getCount());
            stack.shrink(removed);
            amount -= removed;
        }
    }
}
