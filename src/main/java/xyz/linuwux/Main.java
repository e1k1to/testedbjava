package xyz.linuwux;


import xyz.linuwux.persistence.migration.MigrationStrategy;
import xyz.linuwux.ui.MainMenu;

import java.sql.SQLException;

import static xyz.linuwux.persistence.config.ConnectionConfig.getConnection;

public class Main {
    public static void main(String[] args) throws SQLException {
        try(var connection = getConnection() ) {
            new MigrationStrategy(connection).executeMigration();
        }
        new MainMenu().execute();
    }
}