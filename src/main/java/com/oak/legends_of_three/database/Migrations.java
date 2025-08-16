package com.oak.legends_of_three.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Migrations {
    private static final String MIGRATION_TABLE = "schema_version";
    private static final String MIGRATION_PATH = "migrations/";

    private static final List<String> MIGRATION_FILES = List.of(
            "V1__Create_users_table.sql"
    );


    public static void runMigrations() throws SQLException, IOException {
        try (Connection conn = Database.getConnection()) {
            createMigrationTableIfNotExists(conn);

            List<String> appliedMigrations = getAppliedMigrations(conn);
            List<String> availableMigrations = getAvailableMigrations();

            for (String migration : availableMigrations) {
                if (!appliedMigrations.contains(migration)) {
                    executeMigration(conn, migration);
                    recordMigration(conn, migration);
                    System.out.println("Migration applied: " + migration);
                }
            }
        }
    }

    private static void createMigrationTableIfNotExists(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + MIGRATION_TABLE + " (" +
                "version VARCHAR(50) PRIMARY KEY," +
                "executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static List<String> getAppliedMigrations(Connection conn) throws SQLException {
        List<String> migrations = new ArrayList<>();
        String sql = "SELECT version FROM " + MIGRATION_TABLE + " ORDER BY version";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                migrations.add(rs.getString("version"));
            }
        }
        return migrations;
    }

    private static List<String> getAvailableMigrations() {
        return new ArrayList<>(MIGRATION_FILES); // Retorna a lista fixa
    }

    private static void executeMigration(Connection conn, String migrationFile) throws SQLException, IOException {
        String sql = readMigrationFile(migrationFile);

        try (Statement stmt = conn.createStatement()) {
            for (String statement : sql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }
        }
    }

    private static String readMigrationFile(String filename) throws IOException {
        try (InputStream in = Migrations.class.getClassLoader()
                .getResourceAsStream(MIGRATION_PATH + filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static void recordMigration(Connection conn, String version) throws SQLException {
        String sql = "INSERT INTO " + MIGRATION_TABLE + " (version) VALUES (?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, version);
            pstmt.executeUpdate();
        }
    }
}