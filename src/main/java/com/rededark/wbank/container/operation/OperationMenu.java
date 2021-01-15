package com.rededark.wbank.container.operation;

import com.rededark.wbank.container.BankMenu;
import com.rededark.wbank.container.account.AccountMenu;
import com.rededark.wbank.account.Account;
import com.rededark.wbank.account.operation.Operation;
import com.rededark.wbank.account.operation.type.OperationType;
import com.rededark.wbank.tooling.container.icon.Icon;
import com.rededark.wbank.tooling.container.size.Size;
import com.rededark.wbank.tooling.item.ItemBuilder;
import com.rededark.wbank.tooling.util.paginator.Paginator;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OperationMenu extends BankMenu {

    private static final SimpleDateFormat PATTERN = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

    public OperationMenu(Account account, Player player, Paginator<Operation> paginator, int page, boolean isAdmin) {
        super("Transações", Size.FIVE_LINES);

        if(!isAdmin) {
            Icon.of(40)
                    .stack(() -> new ItemBuilder(Material.ANVIL)
                            .name("§aClique para voltar ao menu principal")
                            .build())
                    .handle((icon, event) -> {
                        player.closeInventory();

                        AccountMenu accountMenu = new AccountMenu(account, player);

                        accountMenu.open(player);
                    }).build(this);
        }

        if(paginator.isEmpty()) {
            Icon.of(22)
                    .stack(() -> new ItemBuilder(Material.TNT)
                            .name("§cOops! Não há transações nessa conta.")
                            .build())
                    .handle((icon, event) -> player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0.5F, 0.1F))
                    .build(this); return;
        }; int index = 0;

        for (Operation operation : paginator.getPage(page)) {
            final OperationType operationType = operation.getType();

            int data = 0;

            switch (operationType) {
                case COLLECT:
                    data = 1; break;
                case UPDATE:
                    data = 9; break;
                case DECREMENT:
                    data = 14; break;
                case INCREMENT:
                    data = 5; break;
            }; final ItemBuilder itemBuilder = new ItemBuilder(Material.WOOL, 1, data)
                    .name("§aOperação: §f" + operation.getId());

            final Date date = new Date(operation.getInstant().toEpochMilli());

            final OfflinePlayer user = operation.getUser();

            Icon.of(CONTENT[index++])
                    .stack(() -> itemBuilder
                    .lore(
                            " ",
                            " §fTipo: §7" + operationType.getPrefix(),
                            " §fHorário: §7" + PATTERN.format(date),
                            " ",
                            " §fEnvio: §7" + (user != null ? user.getName() : "Console"),
                            " §fQuantia: §7$" + String.format("%,.2f", operation.getAmount()),
                            " "
                    ).build())
                    .build(this);
        }

        if(paginator.exists(page + 1)) {
            Icon.of(25)
                    .stack(() -> new ItemBuilder(Material.ARROW)
                            .name("§aClique para acessar a proxima pagina.")
                            .build())
                    .handle(((icon, event) -> new OperationMenu(account, player, paginator, page + 1, isAdmin).open(player)))
                    .build(this);
        }

        if(paginator.exists(page - 1)) {
            Icon.of(19)
                    .stack(() -> new ItemBuilder(Material.ARROW)
                            .name("§aClique para acessar a pagina anterior.")
                            .build())
                    .handle((icon, event) -> new OperationMenu(account, player, paginator, page - 1, isAdmin).open(player))
                    .build(this);
        }
    }
}
