package agrofarm;

public record OperationRecord(String operationId, String operationType, String status, String fieldId) {
    public OperationRecord {
        requireText(operationId, "Ідентифікатор операції");
        requireText(operationType, "Тип операції");
        requireText(status, "Стан операції");
        requireText(fieldId, "Ідентифікатор поля");
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " не може бути порожнім.");
        }
    }
}
