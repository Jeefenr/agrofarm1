package agrofarm;

import agrofarm.config.FieldIdentityModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldPersistenceTest {
    @Test
    void injectsStoreAndPersistsOwningFieldId() throws SQLException {
        RecordingStore store = new RecordingStore();
        Field field = createField(store);
        TechOperation operation = new TechOperation("OP-1", "оранка");
        field.addOperation(operation);

        field.saveOperation(operation);

        assertEquals(List.of(new OperationRecord("OP-1", "оранка", "NOT_PLANNED", "F-07")), store.records);
    }

    @Test
    void propagatesSaveFailureToCaller() {
        TechOperationStore failingStore = new TechOperationStore() {
            @Override
            public void save(OperationRecord record) throws SQLException {
                throw new SQLException("database unavailable");
            }

            @Override
            public List<OperationRecord> findAll() {
                return List.of();
            }
        };
        Field field = createField(failingStore);
        TechOperation operation = new TechOperation("OP-1", "оранка");
        field.addOperation(operation);

        assertThrows(SQLException.class, () -> field.saveOperation(operation));
    }

    @Test
    void retainsExplicitOperationIdWhenStatusChanges() {
        TechOperation operation = new TechOperation("OP-STABLE", "оранка");

        operation.plan();

        assertEquals("OP-STABLE", operation.getOperationId());
        assertEquals("PLANNED", operation.getStatus());
    }

    private Field createField(TechOperationStore store) {
        Injector parent = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(TechOperationStore.class).toInstance(store);
            }
        });
        return parent.createChildInjector(new FieldIdentityModule("F-07", "Тестове поле"))
                .getInstance(Field.class);
    }

    private static class RecordingStore implements TechOperationStore {
        private final List<OperationRecord> records = new ArrayList<>();

        @Override
        public void save(OperationRecord record) {
            records.add(record);
        }

        @Override
        public List<OperationRecord> findAll() {
            return List.copyOf(records);
        }
    }
}
