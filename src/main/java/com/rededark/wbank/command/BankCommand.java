package com.rededark.wbank.command;

import com.google.common.collect.ImmutableMap;
import com.rededark.wbank.adapter.text.TextAdapter;
import com.rededark.wbank.booster.Booster;
import com.rededark.wbank.composite.BankManager;
import com.rededark.wbank.container.account.AccountMenu;
import com.rededark.wbank.container.operation.OperationMenu;
import com.rededark.wbank.account.Account;
import com.rededark.wbank.account.operation.Operation;
import com.rededark.wbank.account.operation.type.OperationType;
import com.rededark.wbank.container.booster.BoosterMenu;
import com.rededark.wbank.tooling.util.paginator.Paginator;
import net.minecraft.util.org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class BankCommand extends Command {

    private final BankManager bankManager = BankManager.getInstance();
    private final AtomicBoolean lockdown = BankManager.LOCKDOWN;

    private final Map<Object, Object> operationTypes = ImmutableMap.builder()
            .put("add", OperationType.INCREMENT)
            .put("set", OperationType.UPDATE)
            .put("remove", OperationType.DECREMENT)
            .put("setlimit", OperationType.UPDATE_LIMIT)
            .put("addlimit", OperationType.INCREMENT_LIMIT)
            .put("removelimit", OperationType.DECREMENT_LIMIT)
            .build();

    public BankCommand(String... aliases) {
        super("bank", "Gerencie seu armazem de training points.", "/bank", Arrays.asList(aliases));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("bank")) {
            if (sender instanceof Player && args.length == 0) {
                final Player player = (Player) sender;

                final Account account = bankManager.searchBy(Account.class, player.getUniqueId());

                new AccountMenu(account, player).open(player);

                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(new String[]{
                        " ",
                        " §a/bank add <Name|All> <Amount>",
                        " §a/bank doar <Name> <Amount>",
                        " §a/bank remove <Name|All> <Amount>",
                        " §a/bank set <Name|All> <Amount>",

                        " §a/bank addlimit <Name|All> <Amount>",
                        " §a/bank setlimit <Name|All <Amount>",
                        " §a/bank removelimit <Name|All> <Amount>",

                        " §a/bank lockdown <true/false>",
                        " "
                });
                return true;
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("lockdown")) {
                if (!sender.hasPermission("bank.admin") && sender instanceof Player) {
                    sender.sendMessage(TextAdapter.accept("onPermission", (Player) sender)); return false;
                } final boolean target = BooleanUtils.toBooleanObject(args[1]);

                final boolean current = lockdown.get();

                if(current == target) {
                    sender.sendMessage("§c§lERRO! §cO estado do lockdown já é o mesmo."); return false;
                }; final String[] message = TextAdapter.accept(target ? "onLockdown" : "onDeslockdown")
                        .toArray(new String[0]);

                lockdown.set(target);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(message);
                }; return true;
            }

            if (args.length == 3) {
                if (lockdown.get()) return false;

                if (!NumberUtils.isNumber(args[2])) {
                    sender.sendMessage("§c§lERRO! §cIsso não é um número."); return false;
                }; final float number = Float.parseFloat(args[2]);

                if (args[0].equalsIgnoreCase("doar") && sender instanceof Player) {
                    final Player target = Bukkit.getPlayerExact(args[1]),
                            player = (Player) sender;

                    if (target == null) {
                        player.sendMessage("§c§lERRO! §cEsse jogador não existe.");
                        return false;
                    }

                    if (target.getUniqueId().compareTo(player.getUniqueId()) == 0) {
                        player.sendMessage("§c§lERRO! §cEsse jogador não pode ser você!");
                        return false;
                    }; final Account account = bankManager.searchBy(Account.class, player.getUniqueId());

                    if (account.getAmount() < number) {
                        player.sendMessage("§c§lERRO! §cEssa quantia é muito superior à seu valor no armazem.");
                    }; final Operation operation = Operation.of()
                            .receiver(target.getUniqueId())
                            .amount(number)
                            .type(OperationType.PAYOFF)
                            .user(player.getUniqueId())
                            .to();

                    operation.queue();

                    sender.sendMessage(TextAdapter.accept("onOperation", player));

                    return true;
                }

                if (!sender.hasPermission("bank.admin") && sender instanceof Player) {
                    sender.sendMessage(TextAdapter.accept("onPermission", (Player) sender)); return false;
                }

                OperationType operationType = null;

                for (Map.Entry<Object, Object> entry : operationTypes.entrySet()) {
                    final String text = (String) entry.getKey();

                    if(args[0].equalsIgnoreCase(text)) {
                        operationType = (OperationType) entry.getValue(); break;
                    }
                }

                if (operationType == null) {
                    sender.sendMessage("§c§lERRO! §cAlgo está errado com esse comando...");
                    return false;
                }

                if (args[1].equalsIgnoreCase("all")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        final Operation operation = Operation.of()
                                .receiver(player.getUniqueId())
                                .type(operationType)
                                .amount(number)
                                .to();

                        operation.queue();
                    }

                    sender.sendMessage(TextAdapter.accept("onOperation", null));

                    return true;
                }; final Player player = Bukkit.getPlayerExact(args[1]);

                if (player == null) {
                    sender.sendMessage("§c§lERRO! §cEsse jogador não existe.");
                    return false;
                }; final Operation operation = Operation.of()
                        .receiver(player.getUniqueId())
                        .type(operationType)
                        .amount(number)
                        .to();

                operation.queue();

                sender.sendMessage(TextAdapter.accept("onOperation", null));

                return true;
            }
        }

        if (label.startsWith("booster")) {
            if (args.length == 0 && sender instanceof Player) {
                final Player player = (Player) sender;

                final Account account = bankManager.searchBy(Account.class, player.getUniqueId());

                new BoosterMenu(account, player, new Paginator<>(21, account.getBoosters()), 0).open(player);

                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(new String[]{
                        " ",
                        " §a/booster fill <Name>",
                        " §a/booster add <Name|All> <ID>",
                        " §a/booster remove <Name|All> <ID>",
                        " "
                });
                return true;
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("fill")) {
                final Player player = Bukkit.getPlayerExact(args[1]);

                if (player == null) {
                    sender.sendMessage("§c§lERRO! §cNão existe um jogador com esse nome!"); return false;
                }; final Account account = bankManager.searchBy(Account.class, player.getUniqueId());

                sender.sendMessage(" ");

                sender.sendMessage(" §7Boosters de " + player.getName());

                for (Booster booster : account.getBoosters()) {
                    sender.sendMessage("  * " + booster.getName());
                }; sender.sendMessage(" ");

                return true;
            }
        }

        if (label.startsWith("operation")) {
            if (args.length == 0) {
                final Player player = (Player) sender;

                final Account account = bankManager.searchBy(Account.class, player.getUniqueId());

                new OperationMenu(account, player, new Paginator<>(21, account.getOperations()), 0, false).open(player);

                return true;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(new String[]{
                            " ",
                            " §a/operation <Name>",
                            " "
                    });
                } else {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§c§lERRO! §cComando habilitado somente para in-game."); return false;
                    }; final Player reader = (Player) sender,
                            player = Bukkit.getPlayerExact(args[0]);

                    if (player == null) {
                        sender.sendMessage("§c§lERRO! §cEsse jogador não existe."); return false;
                    }; final Account account = bankManager.searchBy(Account.class, player.getUniqueId());

                    new OperationMenu(account, player, new Paginator<>(21, account.getOperations()), 0, true).open(reader);
                }
            }; return true;
        }; return false;
    }
}
