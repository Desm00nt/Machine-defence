package com.desm00nt.machinedefence;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MachineDefence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MachineDecorations {
    private MachineDecorations() {
    }

    @SubscribeEvent
    public static void onEnter(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!event.getTo().equals(MachineDefence.MACHINE_DEFENCE_LEVEL)) return;
        decorate((ServerLevel) player.level);
    }

    private static void decorate(ServerLevel level) {
        for (int z = 24; z <= 152; z += 16) {
            tree(level, -7, z);
            tree(level, 7, z + 8);
        }
        shop(level);
    }

    private static void shop(ServerLevel level) {
        for (int x = -10; x <= -6; x++) {
            for (int z = -10; z <= -7; z++) {
                level.setBlock(new BlockPos(x, 65, z), Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
        }
        for (int x = -10; x <= -6; x++) {
            level.setBlock(new BlockPos(x, 66, -10), Blocks.OAK_LOG.defaultBlockState(), 3);
            level.setBlock(new BlockPos(x, 66, -7), Blocks.OAK_LOG.defaultBlockState(), 3);
        }
        level.setBlock(new BlockPos(-8, 65, -8), Blocks.CHEST.defaultBlockState(), 3);
        level.setBlock(new BlockPos(-8, 66, -10), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
    }

    private static void tree(ServerLevel level, int x, int z) {
        for (int y = 65; y <= 68; y++) {
            level.setBlock(new BlockPos(x, y, z), Blocks.OAK_LOG.defaultBlockState(), 3);
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 2; dy++) {
                    if (Math.abs(dx) + Math.abs(dz) <= 3 || dy == 2) {
                        level.setBlock(new BlockPos(x + dx, 69 + dy, z + dz), Blocks.OAK_LEAVES.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}
