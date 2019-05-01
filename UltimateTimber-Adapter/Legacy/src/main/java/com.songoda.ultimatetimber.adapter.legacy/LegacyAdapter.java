package com.songoda.ultimatetimber.adapter.legacy;

import com.songoda.ultimatetimber.adapter.IBlockData;
import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.adapter.VersionAdapterType;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockType;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import com.songoda.ultimatetimber.utils.Methods;
import com.songoda.ultimatetimber.utils.NMSUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public class LegacyAdapter implements VersionAdapter {

    @Override
    public VersionAdapterType getVersionAdapterType() {
        return VersionAdapterType.LEGACY;
    }

    @Override
    public IBlockData parseBlockDataFromString(String blockDataString) {
        String[] split = blockDataString.split(":");
        Material material = Material.matchMaterial(split[0]);
        List<Byte> data = new ArrayList<>();
        if (split.length > 1) {
            String[] splitData = split[1].split(",");
            for (String dataValue : splitData)
                data.add(Byte.parseByte(dataValue));
        } else {
            data.add((byte)0);
        }
        return new LegacyBlockData(material, data);
    }

    @Override
    public ItemStack parseItemStackFromString(String itemStackString) {
        String[] split = itemStackString.split(":");
        Material material = Material.matchMaterial(split[0]);
        byte data = 0;
        if (split.length > 1) {
            data = Byte.parseByte(split[1]);
        }
        return new ItemStack(material, 1, data);
    }

    @Override
    public Collection<ItemStack> getBlockDrops(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        Set<ItemStack> drops = new HashSet<>();

        IBlockData treeBlockData;
        if (treeBlock.getBlock() instanceof Block) {
            Block block = (Block)treeBlock.getBlock();
            if (block.getType().equals(Material.AIR))
                return drops;
            List<Byte> data = new ArrayList<>();
            data.add(block.getData());
            treeBlockData = new LegacyBlockData(block.getType(), data);
        } else if (treeBlock.getBlock() instanceof FallingBlock) {
            FallingBlock fallingBlock = (FallingBlock)treeBlock.getBlock();
            List<Byte> data = new ArrayList<>();
            data.add(fallingBlock.getBlockData());
            treeBlockData = new LegacyBlockData(fallingBlock.getMaterial(), data);
        } else return drops;

        Set<IBlockData> typedBlockData = treeBlock.getTreeBlockType().equals(TreeBlockType.LOG) ? treeDefinition.getLogBlockData() : treeDefinition.getLeafBlockData();

        IBlockData definitionBlockData = null;
        for (IBlockData blockData : typedBlockData) {
            if (blockData.getMaterial().equals(treeBlockData.getMaterial()) && blockData.getData() == treeBlockData.getData()) {
                definitionBlockData = blockData;
                break;
            }
        }

        if (definitionBlockData == null) {
            for (IBlockData blockData : typedBlockData) {
                if (blockData.getMaterial().equals(treeBlockData.getMaterial())) {
                    definitionBlockData = blockData;
                    break;
                }
            }
        }

        if (definitionBlockData != null && definitionBlockData.isSimilar(treeBlockData))
            drops.add(new ItemStack(definitionBlockData.getMaterial(), 1, definitionBlockData.getData()));

        return drops;
    }

    @Override
    public void applyToolDurability(Player player, int damage) {
        ItemStack tool = this.getItemInHand(player);
        if (tool.getType().getMaxDurability() < 1)
            return;

        int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.DURABILITY);

        int actualDamage = 0;
        for (int i = 0; i < damage; i++)
            if (Methods.checkUnbreakingChance(unbreakingLevel))
                actualDamage++;

        tool.setDurability((short)(tool.getDurability() + actualDamage));

        if (!this.hasEnoughDurability(tool, 1))
            this.removeItemInHand(player);
    }

    @Override
    public boolean hasEnoughDurability(ItemStack tool, int requiredAmount) {
        if (tool.getType().getMaxDurability() <= 1)
            return true;
        return tool.getDurability() + requiredAmount <= tool.getType().getMaxDurability();
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getItemInHand();
    }

    @Override
    public void removeItemInHand(Player player) {
        player.setItemInHand(null);
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, Block block) {
        return location.getWorld().spawnFallingBlock(location, block.getType(), block.getData());
    }

    @Override
    public void configureFallingBlock(FallingBlock fallingBlock) {
        this.toggleGravityFallingBlock(fallingBlock, false);
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(false);
    }

    @Override
    public void toggleGravityFallingBlock(FallingBlock fallingBlock, boolean applyGravity) {
        if (NMSUtil.getVersionNumber() > 9)
            fallingBlock.setGravity(applyGravity);
    }

    @Override
    public void playFallingParticles(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        Collection<ItemStack> blockDrops = this.getBlockDrops(treeDefinition, treeBlock);
        if (!blockDrops.iterator().hasNext())
            return;

        Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
        if (NMSUtil.getVersionNumber() > 8) {
            location.getWorld().spawnParticle(Particle.BLOCK_CRACK, location, 10, blockDrops.iterator().next().getData());
        } else {
            location.getWorld().playEffect(location, Effect.SMOKE, 4);
        }
    }

    @Override
    public void playLandingParticles(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        Collection<ItemStack> blockDrops = this.getBlockDrops(treeDefinition, treeBlock);
        if (!blockDrops.iterator().hasNext())
            return;

        Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
        if (NMSUtil.getVersionNumber() > 8) {
            location.getWorld().spawnParticle(Particle.BLOCK_DUST, location, 10, blockDrops.iterator().next().getData());
        } else {
            location.getWorld().playEffect(location, Effect.SMOKE, 4);
        }
    }

    @Override
    public void playFallingSound(ITreeBlock treeBlock) {
        Location location = treeBlock.getLocation();
        if (NMSUtil.getVersionNumber() > 8) {
            location.getWorld().playSound(location, Sound.BLOCK_CHEST_OPEN, 2F, 0.1F);
        } else {
            location.getWorld().playSound(location, Sound.valueOf("CHEST_OPEN"), 2F, 0.1F);
        }
    }

    @Override
    public void playLandingSound(ITreeBlock treeBlock) {
        Location location = treeBlock.getLocation();
        if (treeBlock.getTreeBlockType().equals(TreeBlockType.LOG)) {
            if (NMSUtil.getVersionNumber() > 8) {
                location.getWorld().playSound(location, Sound.BLOCK_WOOD_FALL, 3F, 0.1F);
            } else {
                location.getWorld().playSound(location, Sound.valueOf("DIG_WOOD"), 3F, 0.1F);
            }
        } else {
            if (NMSUtil.getVersionNumber() > 8) {
                location.getWorld().playSound(location, Sound.BLOCK_GRASS_BREAK, 0.5F, 0.75F);
            } else {
                location.getWorld().playSound(location, Sound.valueOf("DIG_GRASS"), 0.5F, 0.75F);
            }
        }
    }

}
