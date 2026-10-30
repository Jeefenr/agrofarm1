package agrofarm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

public class Field {
    private final String fieldId;
    private final String name;
    private final Map<String, String> cropBySeason = new LinkedHashMap<>();
    private final List<TechOperation> operations = new ArrayList<>();
    private final Map<String, Double> harvestBySeason = new LinkedHashMap<>();
    private final Set<String> recordedReceiptIds = new HashSet<>();
    private final Set<String> recordedHarvestKeys = new HashSet<>();

    public Field(String fieldId, String name) {
        if (fieldId == null || fieldId.isBlank()) {
            throw new IllegalArgumentException("Ідентифікатор поля не може бути порожнім.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Назва поля не може бути порожньою.");
        }
        this.fieldId = fieldId;
        this.name = name;
    }

    public void assignCropPlan(String season, String crop) {
        if (season == null || season.isBlank() || crop == null || crop.isBlank()) {
            System.out.println("Не вдалося запланувати культуру: потрібно вказати сезон і культуру.");
            return;
        }
        cropBySeason.put(season, crop);
        System.out.println("Для поля «" + name + "» на сезон " + season
                + " заплановано культуру «" + crop + "».");
    }

    public void addOperation(TechOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("Технічну операцію не задано.");
        }
        operation.assignTo(this);
        if (!operations.contains(operation)) {
            operations.add(operation);
        }
    }

    public boolean hasOperation(TechOperation operation) {
        return operation != null && operation.belongsTo(this) && operations.contains(operation);
    }

    public boolean recordHarvest(String receiptId, String season, double quantity) {
        return recordHarvest(receiptId, season, quantity, null);
    }

    public boolean recordHarvest(String receiptId, String season, double quantity, String sourceNote) {
        if (validateHarvest(receiptId, season, quantity).isPresent()) {
            return false;
        }
        recordedReceiptIds.add(receiptId);
        recordedHarvestKeys.add(harvestKey(season, quantity));
        harvestBySeason.merge(season, quantity, Double::sum);
        return true;
    }

    public Optional<String> validateHarvest(String receiptId, String season, double quantity) {
        if (season == null || season.isBlank()) {
            return Optional.of("не вказано сезон");
        }
        if (receiptId == null || receiptId.isBlank()) {
            return Optional.of("не вказано ідентифікатор квитанції");
        }
        if (!Double.isFinite(quantity) || quantity <= 0) {
            return Optional.of("кількість має бути додатною");
        }
        if (recordedReceiptIds.contains(receiptId)) {
            return Optional.of("ідентифікатор квитанції вже обліковано");
        }
        if (recordedHarvestKeys.contains(harvestKey(season, quantity))) {
            return Optional.of("таке надходження поля за сезон і кількістю вже обліковано");
        }
        return Optional.empty();
    }

    public OptionalDouble getHarvestForSeason(String season) {
        Double amount = harvestBySeason.get(season);
        return amount == null ? OptionalDouble.empty() : OptionalDouble.of(amount);
    }

    public boolean hasHarvestForSeason(String season) {
        return harvestBySeason.containsKey(season);
    }

    public String getFieldId() {
        return fieldId;
    }

    public String getName() {
        return name;
    }

    private String harvestKey(String season, double quantity) {
        return fieldId + "\u0000" + season + "\u0000" + Double.toHexString(quantity);
    }
}
