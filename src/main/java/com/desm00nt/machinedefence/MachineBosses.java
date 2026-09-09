package com.desm00nt.machinedefence;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Husk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MachineDefence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MachineBosses {
    private MachineBosses() {
    }

    @SubscribeEvent
    public static void spawnBoss(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.level().dimension() != MachineDefence.MACHINE_DEFENCE_LEVEL) return;
        if (player.getXRot() < 85.0F || player.tickCount % 600 != 0) return;

        Husk boss = EntityType.HUSK.spawn((net.minecraft.server.level.ServerLevel) player.level(), null, null,
                new BlockPos((int) player.getX(), 65, (int) player.getZ() + 14),
                MobSpawnType.EVENT, true, false);
        if (boss == null) return;

        if (boss.getAttribute(Attributes.MAX_HEALTH) != null) {
            boss.getAttribute(Attributes.MAX_HEALTH).setBaseValue(60.0D);
            boss.setHealth(60.0F);
        }
        boss.setCustomName(Component.literal("Machine Defence Boss"));
        boss.setCustomNameVisible(true);
        boss.getPersistentData().putBoolean("MachineDefenceEnemy", true);
        boss.getPersistentData().putBoolean("MachineDefenceBoss", true);
        player.sendSystemMessage(Component.literal("Boss incoming! Defeat it for extra gold and DNA."));
    }
}
