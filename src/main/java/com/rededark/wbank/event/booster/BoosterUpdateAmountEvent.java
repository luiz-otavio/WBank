package com.rededark.wbank.event.booster;

import com.rededark.wbank.booster.Booster;
import com.rededark.wbank.booster.token.BoosterToken;
import com.rededark.wbank.event.DarkEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BoosterUpdateAmountEvent extends DarkEvent {

    private final Booster booster;
    private final UUID uuid;

    private BoosterToken token;
    private float amount;

    public BoosterUpdateAmountEvent(Booster booster, Player player, float amount, BoosterToken token) {
        this.booster = booster;
        this.uuid = player.getUniqueId();
        this.token = token;
        this.amount = amount;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Booster getBooster() {
        return booster;
    }

    public BoosterToken getToken() {
        return token;
    }

    public UUID getUUID() {
        return uuid;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setToken(BoosterToken token) {
        this.token = token;
    }
}
