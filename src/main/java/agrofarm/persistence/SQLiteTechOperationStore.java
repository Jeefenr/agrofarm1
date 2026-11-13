package agrofarm.persistence;

import agrofarm.OperationRecord;
import agrofarm.TechOperationStore;
import com.google.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteTechOperationStore implements TechOperationStore {
    private final Connection connection;

    @Inject
    public SQLiteTechOperationStore(Connection connection) throws SQLException {
        this.connection = connection;
        createSchema();
    }

    @Override
    public void save(OperationRecord record) throws SQLException {
        String sql = """
                INSERT INTO tech_operations (operation_id, operation_type, status, field_id)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(operation_id) DO UPDATE SET
                    operation_type = excluded.operation_type,
                    status = excluded.status,
                    field_id = excluded.field_id
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, record.operationId());
            statement.setString(2, record.operationType());
            statement.setString(3, record.status());
            statement.setString(4, record.fieldId());
            statement.executeUpdate();
        }
    }

    @Override
    public List<OperationRecord> findAll() throws SQLException {
        List<OperationRecord> records = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("""
                     SELECT operation_id, operation_type, status, field_id
                     FROM tech_operations
                     ORDER BY operation_id
                     """)) {
            while (resultSet.next()) {
                records.add(new OperationRecord(
                        resultSet.getString("operation_id"),
                        resultSet.getString("operation_type"),
                        resultSet.getString("status"),
                        resultSet.getString("field_id")));
            }
        }
        return List.copyOf(records);
    }

    private void createSchema() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS tech_operations (
                        operation_id TEXT PRIMARY KEY NOT NULL,
                        operation_type TEXT NOT NULL,
                        status TEXT NOT NULL,
                        field_id TEXT NOT NULL
                    )
                    """);
        }
    }
}
