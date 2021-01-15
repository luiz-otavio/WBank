package com.rededark.wbank.booster.token;

import org.bukkit.entity.Player;

public interface BoosterToken {

    float apply(Player player, float current);

}
