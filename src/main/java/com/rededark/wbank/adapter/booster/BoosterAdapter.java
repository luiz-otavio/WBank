package com.rededark.wbank.adapter.booster;

import com.rededark.wbank.adapter.item.ItemAdapter;
import com.rededark.wbank.booster.Booster;
import com.rededark.wbank.booster.type.BoosterType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class BoosterAdapter {

    public static Booster adapt(ConfigurationSection section) {
        final int id = section.getInt("ID"),
                amount = section.getInt("Amount");
        final String name = section.getString("Name");

        final ItemStack itemStack = ItemAdapter.toItem(section.getConfigurationSection("Icon"));

        final BoosterType boosterType = BoosterType.from(section.getString("Type"));

        if(boosterType == null) return null;

        return new Booster(id, name, ((player, current) -> {
            switch (boosterType) {
                case PERCENTAGE:
                    return current + ((current / 100) * amount);
                case MULTIPLY:
                    return current * amount;
                case SUM:
                    return current + amount;
            }; return current;
        }), itemStack, boosterType);
    }

}
