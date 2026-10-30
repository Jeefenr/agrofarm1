package agrofarm;

import java.util.Optional;

public class FieldWork {
    private final Field field;
    private final String season;
    private final TechOperation operation;
    private final WeatherService weatherService;
    private FieldWorkStatus status;

    public FieldWork(Field field, String season, TechOperation operation, WeatherService weatherService) {
        this.field = field;
        this.season = season;
        this.operation = operation;
        this.weatherService = weatherService;
        this.status = FieldWorkStatus.PLANNED;
    }

    public boolean complete() {
        if (status != FieldWorkStatus.PLANNED) {
            System.out.println("Польову роботу вже виконано.");
            return false;
        }
        if (field == null) {
            return reject("не задано поле");
        }
        if (season == null || season.isBlank()) {
            return reject("не задано сезон");
        }
        if (operation == null) {
            return reject("не задано тип операції");
        }
        if (!field.hasOperation(operation)) {
            return reject("операція не належить вибраному полю");
        }

        Optional<String> modeledContext = weatherService == null
                ? Optional.empty()
                : weatherService.getModeledContext(field.getFieldId(), season);
        if (modeledContext.isPresent()) {
            System.out.println(modeledContext.get() + ".");
        } else {
            System.out.println("Модельований прогноз недоступний; фактичні погодні умови не визначено.");
        }
        operation.plan();
        status = FieldWorkStatus.COMPLETED;
        System.out.println("Польову роботу для поля «" + field.getName()
                + "» завершено; стан: ВИКОНАНО.");
        return true;
    }

    public FieldWorkStatus getStatus() {
        return status;
    }

    private boolean reject(String reason) {
        System.out.println("Не вдалося завершити польову роботу: " + reason
                + ". Стан лишається ЗАПЛАНОВАНО.");
        return false;
    }
}
