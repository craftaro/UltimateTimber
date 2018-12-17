package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class TreeFallAnimation implements Listener {

    /*
    This field gets updated based on player permissions, doubles loot from trees
     */
    private boolean hasBonusLoot;

    public boolean hasBonusLoot() {
        return this.hasBonusLoot;
    }

    private void setHasBonusLoot(boolean bool) {
        this.hasBonusLoot = bool;
    }

    /*
    If a player's tool has the silk touch enchantment, it changes the loot table
     */
    private boolean hasSilkTouch;

    public boolean hasSilkTouch() {
        return this.hasSilkTouch;
    }

    private void setHasSilkTouch(boolean bool) {
        this.hasSilkTouch = bool;
    }

    /*
    This field stores every falling block in this instance of the animation
    This list is also used to identify if a falling block is a part of an animation
     */
    private ArrayList<FallingBlock> fallingBlocks = new ArrayList<>();

    public boolean isFallingTreeBlock(FallingBlock fallingBlock) {
        return this.fallingBlocks.contains(fallingBlock);
    }

    private void registerFallingBlock(FallingBlock fallingBlock) {
        this.fallingBlocks.add(fallingBlock);
    }

    private void unregisterFallingBlock(FallingBlock fallingBlock) {
        this.fallingBlocks.remove(fallingBlock);
    }

    private ArrayList<FallingBlock> getAllFallingBlocks() {
        return this.fallingBlocks;
    }

    /*
    Register all instances of falling trees.
     */
    public static ArrayList<TreeFallAnimation> treeFallAnimationInstances = new ArrayList<>();

    public boolean isInTreeFallInstance(FallingBlock fallingBlock) {
        for (TreeFallAnimation treeFallAnimation : treeFallAnimationInstances)
            if (treeFallAnimation.isFallingTreeBlock(fallingBlock))
                return true;
        return false;
    }

    public TreeFallAnimation getTreeFallAnimation(FallingBlock fallingBlock) {
        for (TreeFallAnimation treeFallAnimation : treeFallAnimationInstances)
            if (treeFallAnimation.isFallingTreeBlock(fallingBlock))
                return treeFallAnimation;
        return null;
    }

    private void registerTreeFallInstance() {
        treeFallAnimationInstances.add(this);
    }

    private void unregisterTreeFallAnimation() {
        if (this.fallingBlocks.isEmpty())
            treeFallAnimationInstances.remove(this);
    }

    /*
    This animation has multiple phases.
    Initially, the tree will start slowly toppling over.
    After a short while, it goes over the tipping point and the fall accelerates.
     */
    public void startAnimation(Block originalBlock, HashSet<Block> blocks, Player player) {
        /*
        This vector makes sure that the entire tree falls in the same direction from the same reference point
         */
        Vector velocityVector = originalBlock.getLocation().clone().subtract(player.getLocation().clone()).toVector().normalize().setY(0);

        registerTreeFallInstance();
        setHasBonusLoot(player.hasPermission("ultimatetimber.bonusloot"));

        /*
        Register private properties
         */
        if (player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.STONE_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE))
            if (player.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH))
                setHasSilkTouch(true);
            else
                setHasSilkTouch(false);
        else
            setHasSilkTouch(false);

        for (Block block : blocks) {

            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().clone().add(0.5,0,0.5), block.getBlockData());
            fallingBlock.setDropItem(false);


            registerFallingBlock(fallingBlock);

            /*
            Dropping air causes some issues
             */
            if (block.getType().equals(Material.AIR)) continue;

                        /*
            Remove original block
             */
            TreeReplant.replaceOriginalBlock(block);

            /*
            Set tipping over effect
            The horizontal velocity going away from the player increases as the Y moves away from the player
             */
            double multiplier = (block.getLocation().getY() - player.getLocation().getY()) * 0.1;

            startPhaseOneAnimation(fallingBlock, velocityVector, multiplier);

        }

    }

    /*
    Phase one of the animation, the tree starts slowly tipping over
     */
    private void startPhaseOneAnimation(FallingBlock fallingBlock, Vector velocityVector, double multiplier) {

        /*
        Vertical offset so top of the tree sways faster than the base
         */
        fallingBlock.setVelocity(velocityVector.clone().multiply(multiplier));
        /*
        No gravity helps with the initial surrounding block detection (somehow) and with the initial trunk rigidity aspect
        required for the effect to look convincing
         */
        fallingBlock.setGravity(false);

        fallingBlock.setVelocity(fallingBlock.getVelocity().multiply(0.2));

        new BukkitRunnable() {
            @Override
            public void run() {

                fallingBlock.setGravity(true);

                /*
                Phase 2 has to be launched from here as to not override effects
                 */
                runPhaseTwoAnimation(fallingBlock);

            }
        }.runTaskLater(UltimateTimber.getInstance(), 20);

    }

    /*
    Phase two of the animation, the tree picks up speed until it is on the ground
    For safety's sake, it disintegrates after a 4 seconds
     */
    private void runPhaseTwoAnimation(FallingBlock fallingBlock) {
        UltimateTimber plugin = UltimateTimber.getInstance();
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {

                if (!fallingBlock.isValid()) {
                    cancel();
                    return;
                }

                /*
                Safeguard to prevent errors that come from glitchy Minecraft behavior
                 */
                if (counter > 20 * 3) {
                    runFallingBlockImpact(fallingBlock);
                    cancel();
                }

                if (counter < 10)
                    fallingBlock.setVelocity(fallingBlock.getVelocity().multiply(1.3));

                counter++;
            }
        }.runTaskTimer(plugin, 0, 1);

    }

    /*
    Catch tree blocks falling down
     */
    @EventHandler
    public void blockDrop(EntityChangeBlockEvent event) {

        if (!(event.getEntity() instanceof FallingBlock)) return;
        if (!isInTreeFallInstance((FallingBlock) event.getEntity())) return;

        event.setCancelled(true);

        FallingBlock fallingBlock = (FallingBlock) event.getEntity();

        runFallingBlockImpact(fallingBlock);

    }

    private void runFallingBlockImpact(FallingBlock fallingBlock) {

        TreeFallAnimation treeFallAnimation = getTreeFallAnimation(fallingBlock);
        treeFallAnimation.unregisterFallingBlock(fallingBlock);
        if (treeFallAnimation.getAllFallingBlocks().isEmpty())
            unregisterTreeFallAnimation();

        UltimateTimber plugin = UltimateTimber.getInstance();
        FileConfiguration fileConfiguration = plugin.getConfig();

        /*
        Run block fall aftermath
         */
        TreeLoot.convertFallingBlock(fallingBlock, treeFallAnimation.hasBonusLoot(), treeFallAnimation.hasSilkTouch());
        if (UltimateTimber.getInstance().getConfig().getBoolean(DefaultConfig.REPLANT_FROM_LEAVES))
            TreeReplant.leafFallReplant(fallingBlock);
        if (fileConfiguration.getBoolean(DefaultConfig.DAMAGE_PLAYERS))
            TreeEntityDamage.runDamage(fallingBlock);
        if (fileConfiguration.getBoolean(DefaultConfig.CUSTOM_AUDIO))
            TreeSounds.fallNoise(fallingBlock);

        fallingBlock.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, fallingBlock.getLocation(), 3, 0.2, 0.2, 0.2, 0.05);

        /*
        Make sure the falling block gets culled
         */
        fallingBlock.remove();

    }

}
