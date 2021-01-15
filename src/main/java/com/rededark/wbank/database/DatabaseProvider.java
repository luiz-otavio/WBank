package com.rededark.wbank.database;

import java.io.File;
import java.nio.file.Path;
import java.sql.*;
import java.util.concurrent.ForkJoinPool;

public class DatabaseProvider {

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(5);

    private Connection connection;
    private Statement statement;

    public DatabaseProvider(Path path) {
        final File target = new File(path.toFile(), "database.db");

        final String address = "jdbc:sqlite:" + target;

        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(address);

            statement = connection.createStatement();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            update("CREATE TABLE IF NOT EXISTS account(uuid CHAR(36), amount FLOAT, current INT, max INT)");

            update("CREATE TABLE IF NOT EXISTS operation(id LONG, uuid CHAR(36), user CHAR(36), amount FLOAT, type VARCHAR(15), instant LONG)");
        }
    }

    public void update(String query) {
        FORK_JOIN_POOL.execute(() -> {
            try {
                statement.executeUpdate(query);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public ResultSet query(String target) {
        return FORK_JOIN_POOL.submit(() -> {
            try {
                return statement.executeQuery(target);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }; return null;
        }).join();
    }


}
