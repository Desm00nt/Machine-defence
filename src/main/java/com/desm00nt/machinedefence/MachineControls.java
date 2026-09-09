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
public final class MachineControls {
    private MachineControls() {
    }

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("machinedefence")
                .then(Commands.literal("left").executes(context -> steer(context.getSource(), -2.0D)))
                .then(Commands.literal("right").executes(context -> steer(context.getSource(), 2.0D)))
                .then(Commands.literal("center").executes(context -> steer(context.getSource(), -999.0D))));
    }

    private static int steer(CommandSourceStack source, double amount) throws Exception {
        ServerPlayer player = source.getPlayerOrException();
        if (player.level.dimension() != MachineDefence.MACHINE_DEFENCE_LEVEL || player.getXRot() < 85.0F) {
            source.sendFailure(Component.literal("Start a Machine Defence run first."));
            return 0;
        }
        double x = amount == -999.0D ? 0.5D : Math.max(-7.0D, Math.min(7.0D, player.getX() + amount));
        player.teleportTo((net.minecraft.server.level.ServerLevel) player.level, x, 65.0D, player.getZ(), 180.0F, 90.0F);
        source.sendSuccess(Component.literal("Machine steering: x=" + String.format("%.1f", x)), false);
        return 1;
    }
}
