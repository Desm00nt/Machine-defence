package com.desm00nt.machinedefence;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
        // Gameplay systems are added incrementally from this entry point.
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

            buildStarterArena(target);
            player.changeDimension(target);
            player.teleportTo(target, 0.5D, 65.0D, 0.5D, player.getYRot(), player.getXRot());
            source.sendSuccess(() -> Component.literal("Entered Machine Defence."), true);
            return 1;
        }

        private static void buildStarterArena(ServerLevel level) {
            BlockPos marker = new BlockPos(0, 64, 0);
            if (level.getBlockState(marker).is(Blocks.GOLD_BLOCK)) {
                return;
            }

            BlockState floor = Blocks.STONE_BRICKS.defaultBlockState();
            BlockState fog = Blocks.GRAY_STAINED_GLASS.defaultBlockState();
            BlockState trim = Blocks.DEEPSLATE_BRICKS.defaultBlockState();

            // Start platform: a compact 25x25 construction area.
            fill(level, -12, 64, -12, 12, 64, 12, floor);
            fill(level, -12, 65, -12, 12, 76, -12, fog);
            fill(level, -12, 65, -12, -12, 76, 12, fog);
            fill(level, 12, 65, -12, 12, 76, 12, fog);

            // Trim the platform and open the fourth side into the corridor.
            fill(level, -12, 64, -12, 12, 64, -12, trim);
            fill(level, -12, 64, -12, -12, 64, 12, trim);
            fill(level, 12, 64, -12, 12, 64, 12, trim);

            // First corridor segment. Its length is intentionally finite in this
            // bootstrap; later waves will extend and stream the route dynamically.
            fill(level, -8, 64, 13, 8, 64, 160, floor);
            fill(level, -10, 65, 13, -10, 76, 160, fog);
            fill(level, 10, 65, 13, 10, 76, 160, fog);

            // Small entrance markers make the intended travel direction obvious.
            fill(level, -8, 64, 13, 8, 64, 14, trim);
            level.setBlock(marker, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
        }

        private static void fill(ServerLevel level, int minX, int minY, int minZ,
                                 int maxX, int maxY, int maxZ, BlockState state) {
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        level.setBlock(new BlockPos(x, y, z), state, 3);
                    }
                }
            }
        }
    }
}
