package com.desm00nt.machinedefence;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MachineDefence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MachineUpgrades {
    private static final String ARMOR_LEVEL = "MachineDefenceArmorLevel";
    private static final String TURRET_LEVEL = "MachineDefenceTurretLevel";

    private MachineUpgrades() {
    }

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("machinedefence")
                .then(Commands.literal("upgrade-armor").executes(context -> upgradeArmor(context.getSource())))
                .then(Commands.literal("upgrade-turret").executes(context -> upgradeTurret(context.getSource()))));
    }

    private static int upgradeArmor(CommandSourceStack source) throws Exception {
        ServerPlayer player = source.getPlayerOrException();
        int dna = player.getPersistentData().getInt("MachineDefenceDNA");
        int level = player.getPersistentData().getInt(ARMOR_LEVEL);
        int cost = 3 + level * 2;
        if (dna < cost) {
            source.sendFailure(Component.literal("Armor upgrade costs " + cost + " DNA. You have " + dna + "."));
            return 0;
        }
        player.getPersistentData().putInt("MachineDefenceDNA", dna - cost);
        player.getPersistentData().putInt(ARMOR_LEVEL, level + 1);
        player.getPersistentData().putInt("MachineDefenceArmor", 100 + (level + 1) * 25);
        player.sendSystemMessage(Component.literal("Armor upgraded to level " + (level + 1) + "."));
        return 1;
    }

    private static int upgradeTurret(CommandSourceStack source) throws Exception {
        ServerPlayer player = source.getPlayerOrException();
        int dna = player.getPersistentData().getInt("MachineDefenceDNA");
        int level = player.getPersistentData().getInt(TURRET_LEVEL);
        int cost = 4 + level * 3;
        if (dna < cost) {
            source.sendFailure(Component.literal("Turret upgrade costs " + cost + " DNA. You have " + dna + "."));
            return 0;
        }
        player.getPersistentData().putInt("MachineDefenceDNA", dna - cost);
        player.getPersistentData().putInt(TURRET_LEVEL, level + 1);
        player.getPersistentData().putInt("MachineDefenceTurretDamage", 6 + (level + 1) * 2);
        player.sendSystemMessage(Component.literal("Turret damage upgraded to level " + (level + 1) + "."));
        return 1;
    }
}
