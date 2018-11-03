package com.songoda.epictimber;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeFallHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTreeBreak(BlockBreakEvent event) {

        if (event.isCancelled()) return;
        parseTree(event.getBlock(), event.getPlayer());


    }

    /*
    Incorporate all checks that would disqualify this event from happening
     */
    private static boolean eventIsValid(BlockBreakEvent event) {
        /*
        General catchers
         */
        if (event.isCancelled()) return false;
        if (!validMaterials.contains(event.getBlock().getType())) return false;
        /*
        Config-based catchers
         */
        if (DefaultConfig.configuration.getBoolean(DefaultConfig.CREATIVE_DISALLOWED) &&
                event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return false;
        return !DefaultConfig.configuration.getBoolean(DefaultConfig.AXES_ONLY) ||
                (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STONE_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE));
    }

    /*
    Used to check if a tree is a tree
     */
    private static List<Material> validMaterials = new ArrayList<>(Arrays.asList(
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG,
            Material.OAK_LOG,
            Material.SPRUCE_LOG
    ));

    /*
    Used to limit the blocks that constitute a tree
     */
    private static List<Material> validSurroundingMaterials = new ArrayList<>(Arrays.asList(
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES,
            Material.COCOA_BEANS
    ));

    private static void parseTree(Block block, Player player) {
        /*
        Check if material is valid
         */
        if (!validMaterials.contains(block.getType())) return;

        ArrayList<Block> blocks = new ArrayList<>();

        boolean containsSecondaryBlock = false;

        /*
        Check if block is surrounded by air (bottom blocks are)
        Every third block expand one block laterally until the main block line reaches air or the max height is reached
         */
        int offset = 1;
        int maxHeight = 31; //make configurable

        for (int i = 0; i < maxHeight; i++) {

            if ((i + 1) % 3 == 0)
                offset++;

            /*
            This expands the inverted search pyramid vertically
             */
            int xOffset = -offset;
            for (int j = xOffset - 1; j < offset; j++) {

                int zOffset = -offset;

                for (int k = zOffset - 1; k < offset; k++) {

                    Block thisBlock = block.getLocation().clone().add(new Vector(xOffset, i, zOffset)).getBlock();

                    /*
                    This exclusion list should include everything you may find near trees as to not invalidate trees
                    in natural forests and such
                    Construction blocks aren't included because that would bust buildings.
                     */
                    if (!thisBlock.getType().equals(block.getType()) &&
                            !validSurroundingMaterials.contains(thisBlock.getType()) &&
                            !thisBlock.getType().equals(Material.AIR) &&
                            !thisBlock.getType().equals(Material.VINE) &&
                            !thisBlock.getType().equals(Material.ROSE_BUSH) &&
                            !thisBlock.getType().equals(Material.ORANGE_TULIP) &&
                            !thisBlock.getType().equals(Material.PINK_TULIP) &&
                            !thisBlock.getType().equals(Material.RED_TULIP) &&
                            !thisBlock.getType().equals(Material.WHITE_TULIP) &&
                            !thisBlock.getType().equals(Material.TALL_GRASS) &&
                            !thisBlock.getType().equals(Material.FERN) &&
                            !thisBlock.getType().equals(Material.LARGE_FERN) &&
                            !thisBlock.getType().equals(Material.DEAD_BUSH) &&
                            !thisBlock.getType().equals(Material.BROWN_MUSHROOM) &&
                            !thisBlock.getType().equals(Material.RED_MUSHROOM))
                        return;

                    if (validSurroundingMaterials.contains(thisBlock.getType()))
                        containsSecondaryBlock = true;

                    if (!thisBlock.getType().equals(Material.AIR))
                        blocks.add(thisBlock);

                    zOffset++;

                }

                xOffset++;

            }

            if (block.getLocation().clone().add(new Vector(0, i, 0)).getBlock().getType().equals(Material.AIR))
                if (i > 1)
                    break;
                else
                    return;

        }

        /*
        If there are no leaves, don't see it as a tree
         */
        if (!containsSecondaryBlock) return;

        toppleAnimation(block, blocks, player);

    }

    private static void toppleAnimation(Block originalBlock, ArrayList<Block> blocks, Player player) {

        Vector velocityVector = originalBlock.getLocation().clone().subtract(player.getLocation().clone()).toVector().normalize().setY(0);

        for (Block block : blocks) {

            if (block.getType().equals(Material.AIR)) continue;

            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());

            /*
            Set tipping over effect
            The horizontal velocity going away from the player increases as the Y moves away from the player
             */
            block.setType(Material.AIR);
            double multiplier = (block.getLocation().getY() - player.getLocation().getY()) * 0.1;
            fallingBlock.setVelocity(velocityVector.clone().multiply(multiplier));
            fallingBlock.setDropItem(true);
            fallingBlock.setGravity(false);
            new BukkitRunnable() {
                @Override
                public void run() {
                    fallingBlock.setGravity(true);
                }
            }.runTaskLater(EpicTimber.plugin, 5);
            new BukkitRunnable() {
                @Override
                public void run() {
                    fallingBlock.setVelocity(fallingBlock.getVelocity().subtract(new Vector(0, 0.05, 0)));
                    if (hasNearbySolidBlock(fallingBlock)) {
                        cancel();
                        fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(fallingBlock.getBlockData().getMaterial(), 1));
                        fallingBlock.remove();
                        fallingBlock.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, fallingBlock.getLocation(), 3, 0.2, 0.2, 0.2, 0.05);
                    }
                }
            }.runTaskTimer(EpicTimber.plugin, 0, 1);

        }

    }


    /*
    Since the block accelerates as it falls, increase the search radius the longer it has been falling for
     */
    private static boolean hasNearbySolidBlock(FallingBlock fallingBlock) {

        if (!fallingBlock.getLocation().subtract(new Vector(0, 1, 0)).getBlock().getType().equals(Material.AIR))
            return true;

        /*
        Lower comparative intensity by predicting the blocks through which the falling block will pass by by checking
        its velocity
         */
        Location predictedLocation = fallingBlock.getLocation().clone().add(fallingBlock.getVelocity());

        return !predictedLocation.getBlock().getType().equals(Material.AIR);

    }


}
