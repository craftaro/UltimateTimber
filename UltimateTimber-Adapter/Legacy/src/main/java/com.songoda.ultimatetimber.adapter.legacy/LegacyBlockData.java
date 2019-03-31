package com.songoda.ultimatetimber.adapter.legacy;

import com.songoda.ultimatetimber.adapter.IBlockData;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class LegacyBlockData implements IBlockData {

    private final Material material;
    private final List<Byte> data;

    public LegacyBlockData(Material material, List<Byte> data) {
        this.material = material;
        this.data = data;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public byte getData() {
        return this.data.get(0);
    }

    @Override
    public boolean isSimilar(IBlockData otherBlockData) {
        if (this.data.size() == 1)
            return this.material.equals(otherBlockData.getMaterial()) && this.getData() == otherBlockData.getData();

        Material blockMaterial = otherBlockData.getMaterial();
        byte blockData = otherBlockData.getData();

        if (!this.material.equals(blockMaterial))
            return false;
        for (byte value : this.data)
            if (value == blockData)
                return true;
        return false;
    }

    @Override
    public boolean isSimilar(Block block) {
        if (this.data.size() == 1)
            return this.material.equals(block.getType()) && this.getData() == block.getData();

        Material blockMaterial = block.getType();
        byte blockData = block.getData();
        if (!this.material.equals(blockMaterial))
            return false;
        for (byte value : this.data)
            if (value == blockData)
                return true;
        return false;
    }

    @Override
    public void setBlock(Block block) {
        block.setType(this.material);
        block.setData(this.data.get(0));
    }

}
