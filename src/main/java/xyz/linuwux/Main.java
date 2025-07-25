package xyz.linuwux;


import xyz.linuwux.persistence.migration.MigrationStrategy;

import java.sql.SQLException;

import static xyz.linuwux.persistence.config.ConnectionConfig.getConnection;

public class Main {
    public static void main(String[] args) throws SQLException {
        try(var connection = getConnection() ) {
            new MigrationStrategy(connection).executeMigration();
        }
    }
}