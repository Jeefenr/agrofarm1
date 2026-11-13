package agrofarm;

import java.util.UUID;

public class TechOperation {
    private final String operationId;
    private final String operationType;
    private String status;
    private Field owner;

    public TechOperation(String operationType) {
        this(UUID.randomUUID().toString(), operationType);
    }

    public TechOperation(String operationId, String operationType) {
        if (operationId == null || operationId.isBlank()) {
            throw new IllegalArgumentException("Ідентифікатор операції не може бути порожнім.");
        }
        this.operationId = operationId;
        if (operationType == null || operationType.isBlank()) {
            throw new IllegalArgumentException("Тип операції не може бути порожнім.");
        }
        this.operationType = operationType;
        this.status = "NOT_PLANNED";
    }

    public void plan() {
        status = "PLANNED";
        System.out.println("Змодельовано планування технічної операції: " + operationType + ".");
    }

    public String getOperationType() {
        return operationType;
    }

    public String getOperationId() {
        return operationId;
    }

    public String getStatus() {
        return status;
    }

    void assignTo(Field field) {
        if (owner != null && owner != field) {
            throw new IllegalStateException("Технічна операція вже належить іншому полю.");
        }
        owner = field;
    }

    boolean belongsTo(Field field) {
        return owner == field;
    }
}
