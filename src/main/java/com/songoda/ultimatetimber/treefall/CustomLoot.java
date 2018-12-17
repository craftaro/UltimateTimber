package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CustomLoot {

    /*
    This is a very simple config parser for items
    Each item is a new line in a list
    Each line includes the keywords "Material:" and "Chance:" seperated by a ","
    The chance is a percentage
    It throws specific errors on startup when an invalid configuration is detected
     */
    private static HashMap<ItemStack, Double> itemMap = new HashMap<>();

    public static void doCustomItemDrop(Location location) {
        for (ItemStack itemStack : itemMap.keySet())
            if ((ThreadLocalRandom.current().nextDouble()) < itemMap.get(itemStack) / 100)
                location.getWorld().dropItem(location, itemStack);
    }

    public static void initializeCustomItems() {

        itemMap.clear();

        FileConfiguration fileConfiguration = UltimateTimber.getInstance().getConfig();

        List<String> arrayList = (List<String>) fileConfiguration.getList(DefaultConfig.CUSTOM_LOOT_LIST);

        for (String string : arrayList) {

            Material material = null;
            double chance = 0;

            String materialString = string.split(",")[0].replace("Material:", "");
            try {
                material = Material.valueOf(materialString);
            } catch (Exception ex) {
                Bukkit.getLogger().warning("[UltimateTimber] Warning: " + materialString + " is not a valid material name.");
            }

            String chanceString = string.split(",")[1].replace("Chance:", "");
            try {

                chance = Double.parseDouble(chanceString);
            } catch (Exception ex) {
                Bukkit.getLogger().warning("[UltimateTimber] Warning: " + chanceString + " is not a valid chance.");
            }

            if (material == null || chance == 0) continue;

            ItemStack itemStack = new ItemStack(material);
            itemMap.put(itemStack, chance);

        }

    }

}
