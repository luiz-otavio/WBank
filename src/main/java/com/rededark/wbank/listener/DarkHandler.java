package com.rededark.wbank.listener;

import com.rededark.wbank.WBank;
import com.rededark.wbank.account.Account;
import com.rededark.wbank.account.operation.Operation;
import com.rededark.wbank.account.operation.type.OperationType;
import com.rededark.wbank.adapter.text.TextAdapter;
import com.rededark.wbank.booster.Booster;
import com.rededark.wbank.booster.token.BoosterToken;
import com.rededark.wbank.composite.BankManager;
import com.rededark.wbank.database.adapter.BankDAO;
import com.rededark.wbank.event.account.AccountAttachAmountEvent;
import com.rededark.wbank.event.account.AccountUpdateAmountEvent;
import com.rededark.wbank.event.account.AccountUpdateLimitEvent;
import com.rededark.wbank.event.booster.BoosterUpdateAmountEvent;
import com.rededark.wbank.event.operation.OperationCompleteEvent;
import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class DarkHandler implements Listener {

    private final BankManager bankManager = BankManager.getInstance();
    private final BankDAO bankDao = BankDAO.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        if(bankManager.searchBy(Account.class, uuid) != null) return;

        final Account account = bankDao.from(uuid);

        bankManager.put(Account.class, uuid, account);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onOperationComplete(OperationCompleteEvent event) {
        if(event.isCancelled()) return;

        final Operation operation = event.getOperation();
        final Account receiver = event.getReceiver(),
                user = event.getUser();

        float amount = operation.getAmount();
        final OperationType type = operation.getType();

        bankManager.put(Operation.class, operation.getId(), operation);

        switch (type) {
            case PAYOFF:
                new AccountUpdateAmountEvent(receiver, operation, user.getAmount() - amount).call();

                new AccountUpdateAmountEvent(receiver, operation, receiver.getAmount() + amount).call();

                break;
            case INCREMENT:
                final Player player = receiver.getPlayer();

                for (Booster booster : receiver.getBoosters()) {
                    amount = booster.setBooster(player, amount);
                }

                new AccountUpdateAmountEvent(receiver, operation, receiver.getAmount() + amount).call();
                break;
            case DECREMENT:
                new AccountUpdateAmountEvent(receiver, operation, receiver.getAmount() - amount).call();
                break;
            case UPDATE:
                new AccountUpdateAmountEvent(receiver, operation, amount).call();
                break;
            case COLLECT:
                new AccountAttachAmountEvent(receiver, operation, amount).call();
                break;
            default:
                new AccountUpdateLimitEvent(receiver, type, (int) amount).call();
                break;
        };

        if(user == null) return;

        final Player player = user.getPlayer();

        player.sendMessage(TextAdapter.accept("onComplete", player));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onAccountUpdateLimit(AccountUpdateLimitEvent event) {
        if(event.isCancelled()) return;

        final Account account = event.getAccount();
        final int limit = event.getLimit();

        switch (event.getType()) {
            case INCREMENT_LIMIT:
                account.setLimit(account.getLimit() + limit);
                break;
            case DECREMENT_LIMIT:
                account.setLimit(account.getLimit() - limit);
                break;
            case UPDATE_LIMIT:
                account.setLimit(limit);
                break;
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    private void onAccountUpdateAmount(AccountUpdateAmountEvent event) {
        if(event.isCancelled()) return;
        
        final Account account = event.getAccount();
        final float amount = event.getAmount();
        final Operation operation = event.getOperation();

        final Player player = account.getPlayer();
        final OperationType operationType = operation.getType();

        if(player != null) player.sendMessage(TextAdapter.accept("Query." + operationType.getPrefix(), player));

        account.setAmount(amount < 0 ? 0 : amount);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    private void onAccountAttachAmount(AccountAttachAmountEvent event) {
        if(event.isCancelled()) return;

        final Account account = event.getAccount();

        final Player player = account.getPlayer();
        final NBTManager nbtManager = PowerNBT.getApi();

        final NBTCompound compound = nbtManager.read(player),
                persisted = compound.compound("ForgeData")
                        .compound("PlayerPersisted");

        final int current = persisted.getInt("jrmcTpint");

        float amount = event.getAmount();

        if(current >= 1000000000) {
            player.sendMessage(TextAdapter.accept("onMax", player)); return;
        }

        final int base = (int) (current + Math.ceil(amount));

        if(base > 1000000000) amount += 1000000000 - base;

        account.setAmount(account.getAmount() - amount);
        account.setCurrent((int) (account.getCurrent() + amount));

        player.sendMessage(TextAdapter.accept("Query.Collect", player));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "jrmctp " + Math.toIntExact((long) amount) + " " + player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onBoosterUpdateAmount(BoosterUpdateAmountEvent event) {
        if(event.isCancelled()) return;

        final Player player = event.getPlayer();
        final BoosterToken boosterToken = event.getToken();
        final float amount = event.getAmount();

        event.setAmount(boosterToken.apply(player, amount));
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String message = event.getMessage();

        final WBank plugin = WBank.getInstance();

        if (!player.hasMetadata("Collect")) return;

        event.setCancelled(true);

        if (message.equalsIgnoreCase("cancelar")) {
            player.removeMetadata("Collect", plugin);

            player.sendMessage("§a§lBANCO: §aSua sessão de armazem finalizou.");

            return;
        }

        if (!NumberUtils.isNumber(message)) {
            player.sendMessage("§c§lERRO! §cIsso não é um número."); return;
        }

        final float number = Float.parseFloat(message);

        final Account account = bankManager.searchBy(Account.class, player.getUniqueId());

        final int current = account.getCurrent(),
                limit = account.getLimit();

        if((current + number) > limit) {
            player.sendMessage("§c§lERRO! §cEsse número ultrapassa seu limite!"); return;
        }

        if (number > account.getAmount()) {
            player.sendMessage("§c§lERRO! §cEssa quantinha é superior à seu valor no armazem."); return;
        }

        player.removeMetadata("Collect", plugin);

        final Operation operation = Operation.of()
                .amount(number)
                .type(OperationType.COLLECT)
                .receiver(player.getUniqueId())
                .to();

        operation.queue();
    }
}
