package com.songoda.ultimatetimber.adapter.current;

import com.songoda.ultimatetimber.adapter.IBlockData;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CurrentBlockData implements IBlockData {

    private final Material material;

    public CurrentBlockData(Material material) {
        this.material = material;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public byte getData() {
        return 0;
    }

    @Override
    public boolean isSimilar(IBlockData otherBlockData) {
        return this.material.equals(otherBlockData.getMaterial());
    }

    @Override
    public boolean isSimilar(Block block) {
        return this.material.equals(block.getType());
    }

}
