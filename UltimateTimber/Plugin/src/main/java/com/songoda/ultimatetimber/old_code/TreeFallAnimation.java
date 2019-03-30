//package com.songoda.ultimatetimber.old_code;
//
//import com.songoda.ultimatetimber.UltimateTimber;
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
//import org.bukkit.Particle;
//import org.bukkit.block.Block;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.enchantments.Enchantment;
//import org.bukkit.entity.FallingBlock;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.entity.EntityChangeBlockEvent;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.util.Vector;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Random;
//import java.util.Set;
//
//public class TreeFallAnimation implements Listener, Runnable {
//
//    /*
//    Register all instances of falling trees.
//     */
//    private static ArrayList<TreeFallAnimation> treeFallAnimationInstances = new ArrayList<>();
//    private static Random random = new Random();
//    /*
//    This field gets updated based on player permissions, doubles loot from trees
//     */
//    private boolean hasBonusLoot;
//    /*
//    If a player's tool has the silk touch enchantment, it changes the loot table
//     */
//    private boolean hasSilkTouch;
//    /*
//    This field stores every falling block in this instance of the animation
//    This list is also used to identify if a falling block is a part of an animation
//     */
//    private ArrayList<FallingBlock> fallingBlocks = new ArrayList<>();
//
//    /*
//    The ID of the task that manages the falling block detection
//     */
//    private int fallingBlockTaskId;
//
//    private boolean hasBonusLoot() {
//        return this.hasBonusLoot;
//    }
//
//    private void setHasBonusLoot(boolean bool) {
//        this.hasBonusLoot = bool;
//    }
//
//    private boolean hasSilkTouch() {
//        return this.hasSilkTouch;
//    }
//
//    private void setHasSilkTouch(boolean bool) {
//        this.hasSilkTouch = bool;
//    }
//
//    private boolean isFallingTreeBlock(FallingBlock fallingBlock) {
//        return this.fallingBlocks.contains(fallingBlock);
//    }
//
//    private void registerFallingBlock(FallingBlock fallingBlock) {
//        this.fallingBlocks.add(fallingBlock);
//    }
//
//    private void unregisterFallingBlock(FallingBlock fallingBlock) {
//        this.fallingBlocks.remove(fallingBlock);
//    }
//
//    private ArrayList<FallingBlock> getAllFallingBlocks() {
//        return this.fallingBlocks;
//    }
//
//    private boolean isInTreeFallInstance(FallingBlock fallingBlock) {
//        for (TreeFallAnimation treeFallAnimation : treeFallAnimationInstances)
//            if (treeFallAnimation.isFallingTreeBlock(fallingBlock))
//                return true;
//        return false;
//    }
//
//    private TreeFallAnimation getTreeFallAnimation(FallingBlock fallingBlock) {
//        for (TreeFallAnimation treeFallAnimation : treeFallAnimationInstances)
//            if (treeFallAnimation.isFallingTreeBlock(fallingBlock))
//                return treeFallAnimation;
//        return null;
//    }
//
//    private void registerTreeFallInstance() {
//        treeFallAnimationInstances.add(this);
//    }
//
//    private void unregisterTreeFallAnimation() {
//        if (this.fallingBlocks.isEmpty()) {
//            treeFallAnimationInstances.remove(this);
//            Bukkit.getScheduler().cancelTask(this.fallingBlockTaskId);
//        }
//    }
//
//    /*
//    This is used to detect after the falling tree blocks have hit the ground
//    Using the event was too unreliable since multiple entities could hit the ground at the same time
//     */
//    @Override
//    public void run() {
//        Set<FallingBlock> groundedBlocks = new HashSet<>();
//        for (FallingBlock fallingBlock : this.fallingBlocks)
//            if (fallingBlock.isDead())
//                groundedBlocks.add(fallingBlock);
//        for (FallingBlock fallingBlock : groundedBlocks)
//            runFallingBlockImpact(fallingBlock);
//    }
//
//    /*
//    This animation has multiple phases.
//    Initially, the tree will start slowly toppling over.
//    After a short while, it goes over the tipping point and the fall accelerates.
//     */
//    void startAnimation(Block originalBlock, HashSet<Block> blocks, Player player) {
//        /*
//        This vector makes sure that the entire tree falls in the same direction from the same reference point
//         */
//        Vector velocityVector = originalBlock.getLocation().clone().subtract(player.getLocation().clone()).toVector().normalize().setY(0);
//
//        registerTreeFallInstance();
//        setHasBonusLoot(player.hasPermission("ultimatetimber.bonusloot"));
//
//        /*
//        Register private properties
//         */
//        if (player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) ||
//                player.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_AXE) ||
//                player.getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE) ||
//                player.getInventory().getItemInMainHand().getType().equals(Material.STONE_AXE) ||
//                player.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE))
//            if (player.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH))
//                setHasSilkTouch(true);
//            else
//                setHasSilkTouch(false);
//        else
//            setHasSilkTouch(false);
//
//        for (Block block : blocks) {
//
//            /*
//            Dropping air causes some issues
//             */
//            if (block.getType().equals(Material.AIR)) continue;
//
//            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().clone().add(0.5, 0, 0.5), block.getBlockData());
//            fallingBlock.setDropItem(false);
//            registerFallingBlock(fallingBlock);
//
//            /*
//            Remove original block
//             */
//            TreeReplant.replaceOriginalBlock(block);
//
//            /*
//            Set tipping over effect
//            The horizontal velocity going away from the player increases as the Y moves away from the player
//             */
//            double multiplier = (block.getLocation().getY() - player.getLocation().getY()) * 0.1;
//
//            startPhaseOneAnimation(fallingBlock, velocityVector, multiplier);
//
//        }
//
//        /*
//        Kick off a task for detecting when the falling blocks have hit the ground
//         */
//        this.fallingBlockTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(UltimateTimber.getInstance(), this, 0, 1);
//
//    }
//
//    /*
//    Phase one of the animation, the tree starts slowly tipping over
//     */
//    private void startPhaseOneAnimation(FallingBlock fallingBlock, Vector velocityVector, double multiplier) {
//
//        /*
//        Vertical offset so top of the tree sways faster than the base
//         */
//        fallingBlock.setVelocity(velocityVector.clone().multiply(multiplier));
//        /*
//        No gravity helps with the initial surrounding block detection (somehow) and with the initial trunk rigidity aspect
//        required for the effect to look convincing
//         */
//        fallingBlock.setGravity(false);
//
//        fallingBlock.setVelocity(fallingBlock.getVelocity().multiply(0.2));
//
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//
//                fallingBlock.setGravity(true);
//
//                /*
//                Phase 2 has to be launched from here as to not override effects
//                 */
//                runPhaseTwoAnimation(fallingBlock);
//
//            }
//        }.runTaskLater(UltimateTimber.getInstance(), 20);
//
//    }
//
//    /*
//    Phase two of the animation, the tree picks up speed until it is on the ground
//    For safety's sake, it disintegrates after a 4 seconds
//     */
//    private void runPhaseTwoAnimation(FallingBlock fallingBlock) {
//        UltimateTimber plugin = UltimateTimber.getInstance();
//        new BukkitRunnable() {
//            int counter = 0;
//
//            @Override
//            public void run() {
//
//                if (!fallingBlock.isValid()) {
//                    cancel();
//                    return;
//                }
//
//                /*
//                Safeguard to prevent errors that come from glitchy Minecraft behavior
//                 */
//                if (counter > 20 * 3) {
//                    runFallingBlockImpact(fallingBlock);
//                    cancel();
//                }
//
//                if (counter < 10)
//                    fallingBlock.setVelocity(fallingBlock.getVelocity().multiply(1.3));
//
//                counter++;
//            }
//        }.runTaskTimer(plugin, 0, 1);
//
//    }
//
//    /*
//    Catch tree blocks falling down and prevent them from interacting with the ground
//     */
//    @EventHandler
//    public void blockDrop(EntityChangeBlockEvent event) {
//        if (!(event.getEntity() instanceof FallingBlock)) return;
//        FallingBlock fallingBlock = (FallingBlock) event.getEntity();
//        if (!isInTreeFallInstance(fallingBlock)) return;
//
//        if (UltimateTimber.getInstance().getConfig().getBoolean(DefaultConfig.SCATTER_FALLEN_BLOCKS)) {
//            boolean isLeaf = fallingBlock.getBlockData().getMaterial().name().endsWith("LEAVES");
//            if (!isLeaf || (isLeaf && random.nextDouble() > 0.5)) { // Only let about half the leafs turn back into blocks
//                getTreeFallAnimation(fallingBlock).unregisterFallingBlock(fallingBlock);
//                return;
//            }
//        }
//
//        event.setCancelled(true);
//    }
//
//    private void runFallingBlockImpact(FallingBlock fallingBlock) {
//
//        TreeFallAnimation treeFallAnimation = getTreeFallAnimation(fallingBlock);
//        treeFallAnimation.unregisterFallingBlock(fallingBlock);
//        if (treeFallAnimation.getAllFallingBlocks().isEmpty())
//            unregisterTreeFallAnimation();
//
//        FileConfiguration fileConfiguration = UltimateTimber.getInstance().getConfig();
//
//        /*
//        Run block fall aftermath
//         */
//        TreeLoot.dropTreeLoot(fallingBlock.getBlockData(), fallingBlock.getLocation(), treeFallAnimation.hasBonusLoot(), treeFallAnimation.hasSilkTouch());
//        if (UltimateTimber.getInstance().getConfig().getBoolean(DefaultConfig.REPLANT_FROM_LEAVES))
//            TreeReplant.leafFallReplant(fallingBlock);
//        if (fileConfiguration.getBoolean(DefaultConfig.DAMAGE_PLAYERS))
//            TreeEntityDamage.runDamage(fallingBlock);
//        if (fileConfiguration.getBoolean(DefaultConfig.CUSTOM_AUDIO))
//            TreeSounds.fallNoise(fallingBlock);
//
//        fallingBlock.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, fallingBlock.getLocation(), 3, 0.2, 0.2, 0.2, 0.05);
//
//        /*
//        Make sure the falling block gets culled
//         */
//        fallingBlock.remove();
//
//    }
//
//}
