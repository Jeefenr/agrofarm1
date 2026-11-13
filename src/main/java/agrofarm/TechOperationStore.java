package agrofarm;

import java.sql.SQLException;
import java.util.List;

public interface TechOperationStore {
    void save(OperationRecord record) throws SQLException;

    List<OperationRecord> findAll() throws SQLException;
}
