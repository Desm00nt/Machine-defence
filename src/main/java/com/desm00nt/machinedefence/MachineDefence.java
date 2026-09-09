package com.desm00nt.machinedefence;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(MachineDefence.MOD_ID)
public final class MachineDefence {
    public static final String MOD_ID = "machinedefence";
    public static final ResourceKey<Level> MACHINE_DEFENCE_LEVEL = ResourceKey.create(
            Registry.DIMENSION_REGISTRY,
            new ResourceLocation(MOD_ID, "machine_defence")
    );

    public MachineDefence() {
        // The first milestone is intentionally small: a loadable custom dimension
        // and an in-game command to enter it while the world-preset UI is built.
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeEvents {
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            dispatcher.register(Commands.literal("machinedefence")
                    .then(Commands.literal("enter")
                            .executes(context -> enterDimension(context.getSource()))));
        }

        private static int enterDimension(CommandSourceStack source) throws Exception {
            ServerPlayer player = source.getPlayerOrException();
            ServerLevel target = player.getServer().getLevel(MACHINE_DEFENCE_LEVEL);
            if (target == null) {
                source.sendFailure(Component.literal("Machine Defence dimension is unavailable."));
                return 0;
            }

            player.changeDimension(target);
            player.teleportTo(target, 0.5D, 65.0D, 0.5D, player.getYRot(), player.getXRot());
            source.sendSuccess(() -> Component.literal("Entered Machine Defence."), true);
            return 1;
        }
    }
}
