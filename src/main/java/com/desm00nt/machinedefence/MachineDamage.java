package com.desm00nt.machinedefence;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MachineDefence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MachineDamage {
    private static final String ARMOR = "MachineDefenceArmor";

    private MachineDamage() {
    }

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.level().dimension() != MachineDefence.MACHINE_DEFENCE_LEVEL) return;
        if (player.getXRot() < 85.0F || player.tickCount % 20 != 0) return;

        int armor = player.getPersistentData().getInt(ARMOR);
        if (armor <= 0) armor = 100;
        List<Monster> attackers = player.level().getEntitiesOfClass(Monster.class,
                player.getBoundingBox().inflate(2.5D),
                mob -> mob.getPersistentData().getBoolean("MachineDefenceEnemy"));
        if (attackers.isEmpty()) return;

        armor = Math.max(0, armor - attackers.size() * 5);
        player.getPersistentData().putInt(ARMOR, armor);
        player.sendSystemMessage(Component.literal("Machine armor: " + armor + "/100"));
        if (armor == 0) {
            player.sendSystemMessage(Component.literal("The machine was destroyed!"));
            player.hurt(player.level().damageSources().generic(), 1000.0F);
        }
    }
}
