package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class TreeFallAnimation {

    /*
    This animation has multiple phases.
    Initially, the tree will start slowly toppling over.
    After a short while, it goes over the tipping point and the fall accelerates.
     */
    public static void startAnimation(Block originalBlock, ArrayList<Block> blocks, Player player) {

        /*
        This vector makes sure that the entire tree falls in the same direction from the same reference point
         */
        Vector velocityVector = originalBlock.getLocation().clone().subtract(player.getLocation().clone()).toVector().normalize().setY(0);

        for (Block block : blocks) {

            if (block.getType().equals(Material.AIR)) continue;

            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());

            /*
            Remove original block
             */
            TreeReplant.replaceOriginalBlock(block);

            /*
            Set tipping over effect
            The horizontal velocity going away from the player increases as the Y moves away from the player
             */
            double multiplier = (block.getLocation().getY() - player.getLocation().getY()) * 0.1;

            startPhaseOneAnimation(fallingBlock, velocityVector, multiplier, player);


        }

    }

    /*
    Phase one of the animation, the tree starts slowly tipping over
     */
    private static void startPhaseOneAnimation(FallingBlock fallingBlock, Vector velocityVector, double multiplier, Player player) {

        /*
        Vertical offset so top of the tree sways faster than the base
         */
        fallingBlock.setVelocity(velocityVector.clone().multiply(multiplier));
        fallingBlock.setDropItem(true);
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
                runPhaseTwoAnimation(fallingBlock, player);

            }
        }.runTaskLater(UltimateTimber.plugin, 20);

    }

    /*
    Phase two of the animation, the tree picks up speed until it is on the ground
    For safety's sake, it disintegrates after a 4 seconds
     */
    private static void runPhaseTwoAnimation(FallingBlock fallingBlock, Player player) {

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if (counter < 10)
                    fallingBlock.setVelocity(fallingBlock.getVelocity().multiply(1.3));
                if (counter > 20 * 3 || hasNearbySolidBlock(fallingBlock)) {
                    cancel();
                    TreeLoot.convertFallingBlock(fallingBlock, player);
                    fallingBlock.remove();
                    fallingBlock.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, fallingBlock.getLocation(), 3, 0.2, 0.2, 0.2, 0.05);
                    if (UltimateTimber.plugin.getConfig().getBoolean(DefaultConfig.REPLANT_FROM_LEAVES))
                        TreeReplant.leafFallReplant(fallingBlock);
                    if (UltimateTimber.plugin.getConfig().getBoolean(DefaultConfig.DAMAGE_PLAYERS))
                        TreeEntityDamage.runDamage(fallingBlock);
                    if (UltimateTimber.plugin.getConfig().getBoolean(DefaultConfig.CUSTOM_AUDIO))
                        TreeSounds.fallNoise(fallingBlock, counter);
                }
                counter++;
            }
        }.runTaskTimer(UltimateTimber.plugin, 0, 1);

    }

    /*
    Since the block accelerates as it falls, increase the search radius the longer it has been falling for
     */
    private static boolean hasNearbySolidBlock(FallingBlock fallingBlock) {

        if (!fallingBlock.getLocation().clone().subtract(new Vector(0, 1, 0)).getBlock().getType().equals(Material.AIR))
            return true;

        /*
        Lower comparative intensity by predicting the blocks through which the falling block will pass by by checking
        its velocity
         */
        Location predictedLocation = fallingBlock.getLocation().clone().add(fallingBlock.getVelocity().multiply(2));

        if (predictedLocation.getY() < 1) return true;

        return !predictedLocation.getBlock().getType().equals(Material.AIR);

    }

}
