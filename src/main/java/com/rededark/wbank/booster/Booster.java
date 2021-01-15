package com.rededark.wbank.booster;

import com.rededark.wbank.booster.token.BoosterToken;
import com.rededark.wbank.booster.type.BoosterType;
import com.rededark.wbank.event.booster.BoosterUpdateAmountEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Booster {

    private final int id;
    private final String name;

    private final ItemStack icon;
    private final BoosterType type;

    private BoosterToken token;

    public Booster(int id, String name, BoosterToken boosterToken, ItemStack icon, BoosterType type) {
        this.id = id;
        this.name = name;
        this.token = boosterToken;
        this.icon = icon;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public BoosterType getType() {
        return type;
    }

    public void setToken(BoosterToken token) {
        this.token = token;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission("weconomy.booster." + name);
    }

    public float setBooster(Player player, float amount) {
        final BoosterUpdateAmountEvent event = new BoosterUpdateAmountEvent(this, player, amount, token);

        event.call();

        return event.getAmount();
    }
}
