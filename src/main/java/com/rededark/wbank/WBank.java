package com.rededark.wbank;

import com.rededark.wbank.account.Account;
import com.rededark.wbank.adapter.booster.BoosterAdapter;
import com.rededark.wbank.adapter.text.TextAdapter;
import com.rededark.wbank.booster.Booster;
import com.rededark.wbank.command.BankCommand;
import com.rededark.wbank.composite.BankManager;
import com.rededark.wbank.database.DatabaseProvider;
import com.rededark.wbank.listener.DarkHandler;
import com.rededark.wbank.account.operation.Operation;
import com.rededark.wbank.database.adapter.BankDAO;
import com.rededark.wbank.tooling.listener.ToolingHandler;
import com.rededark.wbank.tooling.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

public final class WBank extends JavaPlugin {

    public static WBank getInstance() {
        return getPlugin(WBank.class);
    }

    private final BankManager bankManager = BankManager.getInstance();
    private final BankDAO bankDao = BankDAO.getInstance();
    private final PluginManager pluginManager = Bukkit.getPluginManager();

    private final AtomicReference<DatabaseProvider> atomicReference = new AtomicReference<>(null);

    @Override
    public void onLoad() {
        if(!getDataFolder().exists()) getDataFolder().mkdirs();

        saveDefaultConfig();

        saveResource("boosters.yml", false);

        final File configuration = new File(getDataFolder(), "config.yml"),
                booster = new File(getDataFolder(), "boosters.yml");

        final FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(configuration),
                target = YamlConfiguration.loadConfiguration(booster);

        TextAdapter.setReference(fileConfiguration);

        atomicReference.set(new DatabaseProvider(getDataFolder().toPath()));

        BankManager.LOCKDOWN.set(fileConfiguration.getBoolean("Lockdown"));

        final ConfigurationSection section = target.getConfigurationSection("Boosters");

        for (String key : section.getKeys(false)) {
            final ConfigurationSection fill = section.getConfigurationSection(key);

            final Booster any = BoosterAdapter.adapt(fill);

            if(any != null) bankManager.put(Booster.class, any.getId(), any);
        }
    }

    @Override
    public void onEnable() {
        setCommand("bank", new BankCommand("booster", "boosters", "operation", "operations"));

        pluginManager.registerEvents(new DarkHandler(), this);
        pluginManager.registerEvents(new ToolingHandler(), this);

        final Operation[] operations = bankDao.getAll();

        for (Operation operation : operations) {
            bankManager.put(Operation.class, operation.getId(), operation);
        }
    }

    @Override
    public void onDisable() {
        for (Account account : bankManager.fill(Account.class)) {
            bankDao.put(account);
        }

        for (Operation operation : bankManager.fill(Operation.class)) {
            if(!operation.isPlace()) bankDao.put(operation);
        }
    }

    public DatabaseProvider getProvider() {
        return atomicReference.get();
    }

    public void setCommand(String name, Command command) {
        Try.of(() -> {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);

            final CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register(name, command);
        });
    }
}
