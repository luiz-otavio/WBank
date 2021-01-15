package com.rededark.wbank.container.booster;

import com.rededark.wbank.account.Account;
import com.rededark.wbank.booster.Booster;
import com.rededark.wbank.container.BankMenu;
import com.rededark.wbank.container.account.AccountMenu;
import com.rededark.wbank.tooling.container.icon.Icon;
import com.rededark.wbank.tooling.container.size.Size;
import com.rededark.wbank.tooling.item.ItemBuilder;
import com.rededark.wbank.tooling.util.paginator.Paginator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BoosterMenu extends BankMenu {

    public BoosterMenu(Account account, Player player, Paginator<Booster> paginator, int page) {
        super("Boosters", Size.FIVE_LINES);

        Icon.of(40)
                .stack(() -> new ItemBuilder(Material.ANVIL)
                        .name("§aClique para voltar ao menu principal")
                        .build())
                .handle((icon, event) -> {
                    player.closeInventory();

                    AccountMenu accountMenu = new AccountMenu(account, player);

                    accountMenu.open(player);
                }).build(this);


        if(paginator.isEmpty()) {
            Icon.of(22)
                    .stack(() -> new ItemBuilder(Material.TNT)
                            .name("§cOops! Não há boosters em sua conta.")
                            .build())
                    .handle((icon, event) -> player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0.5F, 0.1F))
                    .build(this); return;
        }

        int index = 0;

        for (Booster booster : paginator.getPage(page)) {
            Icon.of(CONTENT[index++])
                    .stack(booster::getIcon)
                    .build(this);
        }

        if(paginator.exists(page + 1)) {
            Icon.of(25)
                    .stack(() -> new ItemBuilder(Material.ARROW)
                            .name("§aClique para acessar a proxima pagina.")
                            .build())
                    .handle(((icon, event) -> new BoosterMenu(account, player, paginator, page + 1).open(player)))
                    .build(this);
        }

        if(paginator.exists(page - 1)) {
            Icon.of(19)
                    .stack(() -> new ItemBuilder(Material.ARROW)
                            .name("§aClique para acessar a pagina anterior.")
                            .build())
                    .handle((icon, event) -> new BoosterMenu(account, player, paginator, page - 1).open(player))
                    .build(this);
        }
    }
}
