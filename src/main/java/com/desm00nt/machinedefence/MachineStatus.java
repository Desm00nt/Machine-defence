package com.desm00nt.machinedefence;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MachineDefence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MachineStatus {
    private MachineStatus() {
    }

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("machinedefence")
                .then(Commands.literal("dashboard").executes(context -> dashboard(context.getSource()))));
    }

    private static int dashboard(CommandSourceStack source) throws Exception {
        ServerPlayer player = source.getPlayerOrException();
        int armor = player.getPersistentData().getInt("MachineDefenceArmor");
        if (armor <= 0) armor = 100;
        final int displayedArmor = armor;
        int dna = player.getPersistentData().getInt("MachineDefenceDNA");
        int turrets = player.getPersistentData().getInt("MachineDefenceTurrets");
        int gold = player.getInventory().countItem(Items.GOLD_NUGGET);
        int armorLevel = player.getPersistentData().getInt("MachineDefenceArmorLevel");
        int turretLevel = player.getPersistentData().getInt("MachineDefenceTurretLevel");
        source.sendSuccess(Component.literal(
                "Machine | armor " + displayedArmor + " | turrets " + turrets + " | gold " + gold
                        + " | DNA " + dna + " | upgrades A" + armorLevel + "/T" + turretLevel), false);
        return 1;
    }
}
