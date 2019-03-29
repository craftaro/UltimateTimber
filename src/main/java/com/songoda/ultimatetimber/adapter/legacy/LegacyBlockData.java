package com.songoda.ultimatetimber.adapter.legacy;

import com.songoda.ultimatetimber.adapter.IBlockData;
import org.bukkit.Material;
import org.bukkit.block.Block;

@SuppressWarnings("deprecation")
public class LegacyBlockData implements IBlockData {

    private final Material material;
    private final byte data;

    public LegacyBlockData(Material material, byte data) {
        this.material = material;
        this.data = data;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public byte getData() {
        return this.data;
    }

    @Override
    public boolean isSimilar(IBlockData otherBlockData) {
        return this.getMaterial().equals(otherBlockData.getMaterial()) && this.getData() == otherBlockData.getData();
    }

    @Override
    public boolean isSimilar(Block block) {
        Material blockMaterial = block.getType();
        byte blockData = block.getData();
        return this.material.equals(blockMaterial) && this.data == blockData;
    }

    @Override
    public void setBlock(Block block) {
        block.setType(this.material);
        // TODO: Break into maven modules so this can use a 1.12.2 jar for compiling instead
        // block.setData(this.data);
    }

}
