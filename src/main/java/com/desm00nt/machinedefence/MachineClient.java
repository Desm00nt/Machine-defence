package com.desm00nt.machinedefence;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MachineDefence.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MachineClient {
    private MachineClient() {
    }

    @SubscribeEvent
    public static void camera(EntityViewRenderEvent.CameraSetup event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;
        if (minecraft.level.dimension() != MachineDefence.MACHINE_DEFENCE_LEVEL) return;
        if (minecraft.player.getXRot() < 85.0F) return;
        event.setPitch(90.0F);
        event.setRoll(0.0F);
    }
}
