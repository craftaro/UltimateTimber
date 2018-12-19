package com.songoda.ultimatetimber.treefall;

import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;

class TreeEntityDamage {

    static void runDamage(FallingBlock fallingBlock) {

        for (Entity entity : fallingBlock.getNearbyEntities(0.5, 0.5, 0.5)) {

            if (!(entity instanceof LivingEntity)) continue;

            ((LivingEntity) entity).damage(1);

        }

    }

}
