package com.rededark.wbank.adapter.item;

import com.rededark.wbank.tooling.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ItemAdapter {

    public static ItemStack toItem(ConfigurationSection section) {
        final String type = section.getString("Type");

        final int amount = section.getInt("Amount");

        if (type.equals("373")) {
            final int effect = section.getInt("Effect");

            final int level = section.getInt("Level");

            final ItemStack potion = new Potion(PotionType.getByEffect(PotionEffectType.getById(effect)), level).toItemStack(amount);

            return append(section, new ItemBuilder(potion));
        }

        if (type.contains(":")) {
            final String[] split = type.split(":");

            final int id = Integer.parseInt(split[0]);
            final short data = Short.parseShort(split[1]);

            return append(section, new ItemBuilder(Material.getMaterial(id), amount, data));
        }

        return append(section, new ItemBuilder(Material.getMaterial(Integer.parseInt(type)), amount));
    }

    private static ItemStack append(ConfigurationSection section, ItemBuilder builder) {
        if (section.contains("Name")) builder.name(section.getString("Name"));

        if (section.contains("Lore")) builder.lore(section.getStringList("Lore"));

        if (section.contains("Enchant")) {
            for (String target : section.getConfigurationSection("Enchant").getKeys(false)) {
                final Enchantment enchantment = Enchantment.getById(Integer.parseInt(target));

                final int level = section.getInt("Enchant." + target);

                builder.enchant(enchantment, level);
            }
        }

        return builder.build();
    }
}
