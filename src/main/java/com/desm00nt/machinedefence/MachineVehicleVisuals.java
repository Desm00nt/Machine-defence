package com.desm00nt.machinedefence;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MachineDefence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MachineVehicleVisuals {
    private static final Map<UUID, BlockPos> LAST_ANCHORS = new HashMap<>();

    private MachineVehicleVisuals() {
    }

    @SubscribeEvent
    public static void moveVehicle(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level.isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.level.dimension() != MachineDefence.MACHINE_DEFENCE_LEVEL) return;
        if (player.getXRot() < 85.0F || player.tickCount % 4 != 0) return;

        ServerLevel level = (ServerLevel) player.level;
        BlockPos anchor = new BlockPos((int) Math.floor(player.getX()), 64, (int) Math.floor(player.getZ()));
        BlockPos previous = LAST_ANCHORS.put(player.getUUID(), anchor);
        if (anchor.equals(previous)) return;
        if (previous != null) clearMachine(level, previous);
        placeMachine(level, anchor);
    }

    private static void placeMachine(ServerLevel level, BlockPos anchor) {
        BlockState frame = MachineBlocks.MACHINE_FRAME.get().defaultBlockState();
        BlockState wheel = MachineBlocks.MACHINE_WHEEL.get().defaultBlockState();
        BlockState engine = MachineBlocks.MACHINE_ENGINE.get().defaultBlockState();
        BlockState turret = MachineBlocks.MACHINE_TURRET.get().defaultBlockState();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                level.setBlock(anchor.offset(x, 0, z), frame, 3);
            }
        }
        level.setBlock(anchor.offset(-2, 1, -2), wheel, 3);
        level.setBlock(anchor.offset(2, 1, -2), wheel, 3);
        level.setBlock(anchor.offset(-2, 1, 2), wheel, 3);
        level.setBlock(anchor.offset(2, 1, 2), wheel, 3);
        level.setBlock(anchor.offset(0, 1, 2), engine, 3);
        level.setBlock(anchor.offset(0, 1, -2), turret, 3);
    }

    private static void clearMachine(ServerLevel level, BlockPos anchor) {
        Block[] machineBlocks = {
                MachineBlocks.MACHINE_FRAME.get(),
                MachineBlocks.MACHINE_WHEEL.get(),
                MachineBlocks.MACHINE_ENGINE.get(),
                MachineBlocks.MACHINE_TURRET.get()
        };
        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos pos = anchor.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    for (Block machineBlock : machineBlocks) {
                        if (state.is(machineBlock)) {
                            level.removeBlock(pos, false);
                            break;
                        }
                    }
                }
            }
        }
    }
}
