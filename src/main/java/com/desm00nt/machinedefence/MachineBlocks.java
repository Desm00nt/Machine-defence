package com.desm00nt.machinedefence;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MachineBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MachineDefence.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MachineDefence.MOD_ID);

    public static final RegistryObject<Block> MACHINE_FRAME = registerBlock("machine_frame",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(6.0F, 8.0F)));
    public static final RegistryObject<Block> MACHINE_WHEEL = registerBlock("machine_wheel",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLACK_CONCRETE).strength(3.0F, 5.0F)));
    public static final RegistryObject<Block> MACHINE_ENGINE = registerBlock("machine_engine",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.FURNACE).strength(4.0F, 6.0F)));
    public static final RegistryObject<Block> MACHINE_TURRET = registerBlock("machine_turret",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DISPENSER).strength(4.0F, 6.0F)));

    private static RegistryObject<Block> registerBlock(String name, java.util.function.Supplier<Block> block) {
        RegistryObject<Block> registered = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(registered.get(), new Item.Properties().tab(net.minecraft.world.item.CreativeModeTab.TAB_BUILDING_BLOCKS)));
        return registered;
    }

    private MachineBlocks() {
    }
}
