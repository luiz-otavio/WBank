package com.rededark.wbank.container.account;

import com.google.common.collect.Lists;
import com.rededark.wbank.booster.Booster;
import com.rededark.wbank.container.BankMenu;
import com.rededark.wbank.WBank;
import com.rededark.wbank.account.Account;
import com.rededark.wbank.account.operation.Operation;
import com.rededark.wbank.adapter.text.TextAdapter;
import com.rededark.wbank.composite.BankManager;
import com.rededark.wbank.container.booster.BoosterMenu;
import com.rededark.wbank.container.operation.OperationMenu;
import com.rededark.wbank.tooling.container.icon.Icon;
import com.rededark.wbank.tooling.container.size.Size;
import com.rededark.wbank.tooling.item.ItemBuilder;
import com.rededark.wbank.tooling.util.paginator.Paginator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class AccountMenu extends BankMenu {

    private final BankManager bankManager = BankManager.getInstance();

    public AccountMenu(Account account, Player player) {
        super("Conteúdo bancario.", Size.FIVE_LINES);

        final List<String> info = Lists.newArrayList(" ",
                " §7Esse é seu conteúdo bancario.",
                " §7Todos seus trainings points são armazenados aqui.",
                " §7Complete missões para ganhar tranings points.",
                " ",
                " §eSeu limite atual: $" + String.format("%,d", account.getLimit()),
                " §eSua quantia de uso: $" + String.format("%,d", account.getCurrent()),
                " ");

        if(account.getAmount() <= 0) {
            info.add(" §cSeu armazem está vázio para converter!");
        } else {
            info.add(" §7Armazem: $" + String.format("%,.2f", account.getAmount()));
        }

        info.add(" ");


        Icon.of(21)
                .stack(() -> new ItemBuilder(Material.SKULL_ITEM, 1, 3)
                        .head(player)
                        .name("§aInformações.")
                        .lore(info)
                        .build())
                .handle((icon, event) -> player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.5F, 0.1F)).build(this);

        Icon.of(20)
                .stack(() -> new ItemBuilder(Material.EMERALD_BLOCK)
                        .name("§aTransições")
                        .lore(
                            " ",
                            " §7Todas alterações em sua conta é armazenada.",
                            " §7Acesse suas transações para ler suas alterações.",
                            " ",
                            " §aClique com botão esquerdo para acessar!",
                            " ")
                        .build())
                .handle((icon, event) -> {
                    final Paginator<Operation> paginator = new Paginator<>(21, account.getOperations());

                    player.closeInventory();

                    new OperationMenu(account, player, paginator, 0, false).open(player);
                }).build(this);

        Icon.of(22)
                .stack(() -> new ItemBuilder(Material.EXP_BOTTLE)
                        .name("§aBoosters")
                        .lore(
                            " ",
                            " §7Boosters são preciosos!",
                            " §7Com boosters você aumenta sua quantia de pontos.",
                            " ",
                            " §aClique com botão esquerdo para acessar!",
                            " "
                        ).build())
                .handle((icon, event) -> {
                    final Paginator<Booster> boosters = new Paginator<>(21, account.getBoosters());

                    player.closeInventory();

                    new BoosterMenu(account, player, boosters, 0).open(player);
                }).build(this);

        Icon.of(24)
                .stack(() -> new ItemBuilder(Material.SIGN)
                        .name("§aClique para reivindicar seu armazem.")
                        .build())
                .handle((icon, event) -> {
                    player.closeInventory();

                    if(account.isFull()) {
                        player.sendMessage("§c§lERRO! §cVocê já atingiu seu limite!");
                        return;
                    }

                    if(player.hasMetadata("Collect")) {
                        player.sendMessage(TextAdapter.accept("onMetadata", player));
                        return;
                    }

                    player.setMetadata("Collect", new FixedMetadataValue(WBank.getInstance(), account));

                    player.sendMessage(new String[] {
                            " ",
                            " §a§lINFO: §aVocê entrou no modo coleta.",
                            " §a§lINFO: §aDigite a quantia para coletar.",
                            " §c§lAVISO: §cPara cancelar essa sessão digite: §fcancelar!",
                            " "
                    });
                }).build(this);
    }
}
