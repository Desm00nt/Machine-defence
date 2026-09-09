package com.desm00nt.machinedefence;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod(MachineDefence.MOD_ID)
public final class MachineDefence {
    public static final String MOD_ID = "machinedefence";
    public static final ResourceKey<Level> MACHINE_DEFENCE_LEVEL = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(MOD_ID, "machine_defence"));
    private static final Set<UUID> DRIVERS = new HashSet<>();
    private static final Map<UUID, Integer> DISTANCE = new HashMap<>();
    private static final Map<UUID, Integer> WAVE = new HashMap<>();

    public MachineDefence() {
        MachineBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        MachineBlocks.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeEvents {
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
            d.register(Commands.literal("machinedefence")
                    .then(Commands.literal("enter").executes(c -> enter(c.getSource())))
                    .then(Commands.literal("start").executes(c -> start(c.getSource())))
                    .then(Commands.literal("stop").executes(c -> stop(c.getSource())))
                    .then(Commands.literal("status").executes(c -> status(c.getSource()))));
        }

        private static ServerPlayer player(CommandSourceStack source) {
            try { return source.getPlayerOrException(); }
            catch (CommandSyntaxException ignored) { return null; }
        }

        private static int enter(CommandSourceStack source) {
            ServerPlayer player = player(source);
            if (player == null) return 0;
            ServerLevel target = player.getServer().getLevel(MACHINE_DEFENCE_LEVEL);
            if (target == null) { source.sendFailure(Component.literal("Machine Defence dimension is unavailable.")); return 0; }
            buildArena(target);
            DRIVERS.remove(player.getUUID());
            player.changeDimension(target);
            player.teleportTo(target, 0.5D, 65.0D, 0.5D, 180.0F, 45.0F);
            source.sendSuccess(Component.literal("Entered Machine Defence. Use /machinedefence start."), true);
            return 1;
        }

        private static int start(CommandSourceStack source) {
            ServerPlayer player = player(source);
            if (player == null) return 0;
            if (player.level.dimension() != MACHINE_DEFENCE_LEVEL) { source.sendFailure(Component.literal("Enter Machine Defence first.")); return 0; }
            buildArena((ServerLevel) player.level);
            DRIVERS.add(player.getUUID());
            DISTANCE.put(player.getUUID(), 0);
            WAVE.put(player.getUUID(), 0);
            player.teleportTo((ServerLevel) player.level, 0.5D, 65.0D, 16.0D, 180.0F, 90.0F);
            player.sendSystemMessage(Component.literal("Run started. Use /machinedefence stop to return."));
            return 1;
        }

        private static int stop(CommandSourceStack source) {
            ServerPlayer player = player(source);
            if (player == null) return 0;
            DRIVERS.remove(player.getUUID());
            DISTANCE.remove(player.getUUID());
            WAVE.remove(player.getUUID());
            if (player.level.dimension() == MACHINE_DEFENCE_LEVEL) player.teleportTo((ServerLevel) player.level, 0.5D, 65.0D, 0.5D, 180.0F, 45.0F);
            player.sendSystemMessage(Component.literal("Run stopped. Return to the build platform."));
            return 1;
        }

        private static int status(CommandSourceStack source) {
            ServerPlayer player = player(source);
            if (player == null) return 0;
            source.sendSuccess(Component.literal("Distance: " + DISTANCE.getOrDefault(player.getUUID(), 0) + " | Wave: " + WAVE.getOrDefault(player.getUUID(), 0) + " | DNA: " + player.getPersistentData().getInt("MachineDefenceDNA")), false);
            return 1;
        }

        @SubscribeEvent
        public static void serverTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            MinecraftServer server = event.getServer();
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (!DRIVERS.contains(player.getUUID()) || player.level.dimension() != MACHINE_DEFENCE_LEVEL) continue;
                ServerLevel level = (ServerLevel) player.level;
                int distance = DISTANCE.merge(player.getUUID(), 1, Integer::sum);
                int wave = Math.max(1, distance / 40 + 1);
                WAVE.put(player.getUUID(), wave);
                player.teleportTo(level, player.getX(), 65.0D, player.getZ() + 0.18D, 180.0F, 90.0F);
                if (distance % 40 == 1) { spawnWave(level, player, wave); player.sendSystemMessage(Component.literal("Wave " + wave + " incoming! Distance " + distance)); }
            }
        }

        private static void spawnWave(ServerLevel level, ServerPlayer player, int wave) {
            for (int i = 0; i < Math.min(12, 2 + wave); i++) {
                Monster mob = (Monster) EntityType.ZOMBIE.spawn(level, null, null, new BlockPos(-6 + level.random.nextInt(13), 65, (int) player.getZ() + 12 + level.random.nextInt(8)), MobSpawnType.EVENT, true, false);
                if (mob != null) { mob.setCustomName(Component.literal("Machine Defence Raider")); mob.getPersistentData().putBoolean("MachineDefenceEnemy", true); }
            }
        }

        @SubscribeEvent
        public static void onMobKilled(LivingDeathEvent event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player) || !DRIVERS.contains(player.getUUID()) || !event.getEntity().getPersistentData().getBoolean("MachineDefenceEnemy")) return;
            player.addItem(new ItemStack(Items.GOLD_NUGGET, 1 + player.level.random.nextInt(3)));
            if (player.level.random.nextFloat() < 0.25F) { int dna = player.getPersistentData().getInt("MachineDefenceDNA") + 1; player.getPersistentData().putInt("MachineDefenceDNA", dna); }
        }

        @SubscribeEvent
        public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || !DRIVERS.remove(player.getUUID())) return;
            DISTANCE.remove(player.getUUID()); WAVE.remove(player.getUUID());
            ServerLevel target = player.getServer().getLevel(MACHINE_DEFENCE_LEVEL);
            if (target != null) { buildArena(target); player.changeDimension(target); player.teleportTo(target, 0.5D, 65.0D, 0.5D, 180.0F, 45.0F); }
        }

        private static void buildArena(ServerLevel level) {
            BlockPos marker = new BlockPos(0, 64, 0);
            if (level.getBlockState(marker).is(Blocks.GOLD_BLOCK)) return;
            BlockState floor = Blocks.STONE_BRICKS.defaultBlockState(), fog = Blocks.GRAY_STAINED_GLASS.defaultBlockState(), trim = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
            fill(level, -12, 64, -12, 12, 64, 12, floor); fill(level, -12, 65, -12, 12, 76, -12, fog); fill(level, -12, 65, -12, -12, 76, 12, fog); fill(level, 12, 65, -12, 12, 76, 12, fog);
            fill(level, -12, 64, -12, 12, 64, -12, trim); fill(level, -12, 64, -12, -12, 64, 12, trim); fill(level, 12, 64, -12, 12, 64, 12, trim);
            fill(level, -8, 64, 13, 8, 64, 160, floor); fill(level, -10, 65, 13, -10, 76, 160, fog); fill(level, 10, 65, 13, 10, 76, 160, fog); fill(level, -8, 64, 13, 8, 64, 14, trim);
            level.setBlock(marker, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
        }

        private static void fill(ServerLevel level, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState state) {
            for (int x = minX; x <= maxX; x++) for (int y = minY; y <= maxY; y++) for (int z = minZ; z <= maxZ; z++) level.setBlock(new BlockPos(x, y, z), state, 3);
        }
    }
}
