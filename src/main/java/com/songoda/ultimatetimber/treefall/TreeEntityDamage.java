package com.songoda.ultimatetimber.treefall;

import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;

public class TreeEntityDamage {

    public static void runDamage(FallingBlock fallingBlock) {

        for (Entity entity : fallingBlock.getNearbyEntities(0.5, 0.5, 0.5)) {

            if (entity instanceof LivingEntity) {

                ((LivingEntity) entity).damage(1);

            }

        }

    }

}
