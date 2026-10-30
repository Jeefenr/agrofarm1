package agrofarm;

import java.util.Objects;

public class TechOperation {
    private final String operationType;
    private String status;
    private Field owner;

    public TechOperation(String operationType) {
        this.operationType = Objects.requireNonNull(operationType, "Тип операції не задано");
        this.status = "NOT_PLANNED";
    }

    public void plan() {
        status = "PLANNED";
        System.out.println("Змодельовано планування технічної операції: " + operationType + ".");
    }

    public String getOperationType() {
        return operationType;
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
