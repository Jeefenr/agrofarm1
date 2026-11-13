package agrofarm.persistence;

import agrofarm.OperationRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLiteTechOperationStoreTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void createsSchemaAndStoresEveryRecordField() throws SQLException {
        Path database = temporaryDirectory.resolve("operations.db");
        try (Connection connection = open(database)) {
            SQLiteTechOperationStore store = new SQLiteTechOperationStore(connection);
            store.save(new OperationRecord("OP-1", "оранка", "PLANNED", "F-01"));

            List<OperationRecord> records = store.findAll();
            assertEquals(List.of(new OperationRecord("OP-1", "оранка", "PLANNED", "F-01")), records);
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'tech_operations'")) {
                assertTrue(resultSet.next());
            }
        }
    }

    @Test
    void returnsEmptyListForEmptyDatabase() throws SQLException {
        try (Connection connection = open(temporaryDirectory.resolve("empty.db"))) {
            SQLiteTechOperationStore store = new SQLiteTechOperationStore(connection);

            assertEquals(List.of(), store.findAll());
        }
    }

    @Test
    void dataSurvivesReopeningDatabase() throws SQLException {
        Path database = temporaryDirectory.resolve("persistent.db");
        OperationRecord expected = new OperationRecord("OP-1", "сівба", "PLANNED", "F-02");
        try (Connection connection = open(database)) {
            new SQLiteTechOperationStore(connection).save(expected);
        }

        try (Connection connection = open(database)) {
            assertEquals(List.of(expected), new SQLiteTechOperationStore(connection).findAll());
        }
    }

    @Test
    void upsertUpdatesExistingOperationWithoutDuplicate() throws SQLException {
        try (Connection connection = open(temporaryDirectory.resolve("upsert.db"))) {
            SQLiteTechOperationStore store = new SQLiteTechOperationStore(connection);
            store.save(new OperationRecord("OP-1", "оранка", "PLANNED", "F-01"));
            store.save(new OperationRecord("OP-1", "культивація", "COMPLETED", "F-02"));

            assertEquals(List.of(new OperationRecord("OP-1", "культивація", "COMPLETED", "F-02")),
                    store.findAll());
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM tech_operations WHERE operation_id = ?")) {
                statement.setString(1, "OP-1");
                try (ResultSet resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(1, resultSet.getInt(1));
                }
            }
        }
    }

    private Connection open(Path database) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + database);
    }
}
