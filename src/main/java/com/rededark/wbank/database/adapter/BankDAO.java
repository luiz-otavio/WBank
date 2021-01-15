package com.rededark.wbank.database.adapter;

import com.rededark.wbank.WBank;
import com.rededark.wbank.account.Account;
import com.rededark.wbank.database.DatabaseProvider;
import com.rededark.wbank.account.operation.Operation;
import com.rededark.wbank.account.operation.type.OperationType;
import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.OfflinePlayer;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.UUID;

public class BankDAO {

    private static final BankDAO BANK_DAO = new BankDAO();

    public static BankDAO getInstance() {
        return BANK_DAO;
    }

    public Account from(UUID uuid) {
        final DatabaseProvider databaseProvider = WBank.getInstance().getProvider();

        try (ResultSet resultSet = databaseProvider.query("SELECT * FROM account WHERE uuid = '" + uuid.toString() + "'")) {
            if(resultSet.next()) {
                final float amount = resultSet.getFloat("amount");

                final int current = resultSet.getInt("current"),
                        limit = resultSet.getInt("max");

                return new Account(uuid, amount, current, limit);
            }

            final Account account = new Account(uuid, 0, 0, 10000);

            databaseProvider.update("INSERT INTO account VALUES ('" + uuid.toString() +"', 0, 0, 10000)");

            return account;
        } catch (Exception exception) {
            exception.printStackTrace();
        }; return null;
    }

    public Operation[] getAll() {
        final DatabaseProvider databaseProvider = WBank.getInstance().getProvider();

        Operation[] operations = new Operation[0];

        try (ResultSet resultSet = databaseProvider.query("SELECT * FROM operation")) {
            while (resultSet.next()) {
                final String target = resultSet.getString("user");

                final UUID receiver = UUID.fromString(resultSet.getString("uuid")),
                        user = target.equalsIgnoreCase("CONSOLE") ? null : UUID.fromString(target);

                final String type = resultSet.getString("type");
                final long instant = resultSet.getLong("instant");

                final Operation operation = Operation.of()
                        .amount(resultSet.getFloat("amount"))
                        .id(resultSet.getLong("id"))
                        .instant(Instant.ofEpochMilli(instant))
                        .type(OperationType.valueOf(type))
                        .user(user)
                        .receiver(receiver)
                        .isPlaced(true)
                        .to();

                operations = ArrayUtils.add(operations, operation);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }; return operations;
    }

    public void put(Account account) {
        final DatabaseProvider databaseProvider = WBank.getInstance().getProvider();

        final UUID uuid = account.getUUID();

        databaseProvider.update("UPDATE account SET amount = " + account.getAmount() + ", current = " + account.getCurrent() + ", max = " + account.getLimit() + " WHERE uuid = '" + uuid.toString() + "'");
    }

    public void put(Operation operation) {
        final DatabaseProvider databaseProvider = WBank.getInstance().getProvider();

        final OfflinePlayer receiver = operation.getReceiver(),
                user = operation.getUser();

        final String uuid = receiver.getUniqueId().toString(),
                target = user == null ? "CONSOLE" : user.getUniqueId().toString();

        databaseProvider.update("INSERT INTO operation VALUES (" +
                "" + operation.getId() + "," +
                " '" + uuid + "'," +
                " '" + target +"'," +
                " " + operation.getAmount() + "," +
                " '" + operation.getType().name() + "'," +
                " " + operation.getInstant().toEpochMilli() + ")");
    }
}
