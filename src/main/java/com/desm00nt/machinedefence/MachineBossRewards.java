package com.desm00nt.machinedefence;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MachineDefence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MachineBossRewards {
    private MachineBossRewards() {
    }

    @SubscribeEvent
    public static void reward(LivingDeathEvent event) {
        if (!event.getEntity().getPersistentData().getBoolean("MachineDefenceBoss")) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        player.addItem(new ItemStack(Items.GOLD_NUGGET, 20));
        int dna = player.getPersistentData().getInt("MachineDefenceDNA") + 5;
        player.getPersistentData().putInt("MachineDefenceDNA", dna);
        player.sendSystemMessage(Component.literal("Boss defeated! Gold +20, DNA +5 (total " + dna + ")"));
    }
}
