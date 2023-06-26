package com.craftaro.ultimatetimber.database.migrations;

import com.craftaro.core.database.DataMigration;
import com.craftaro.core.database.DatabaseConnector;
import com.craftaro.core.third_party.org.jooq.impl.SQLDataType;

/**
 * Creates the database tables
 */
public class _1_CreateTables extends DataMigration {

    public _1_CreateTables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, String tablePrefix) {
        connector.connectDSL(context -> context.createTableIfNotExists(tablePrefix + "players")
                .column("uuid", SQLDataType.VARCHAR(36))
                .column("chopping_enabled", SQLDataType.BOOLEAN)
                .column("last_chopping_use", SQLDataType.BIGINT)
                .primaryKey("uuid")
                .execute());
    }
}