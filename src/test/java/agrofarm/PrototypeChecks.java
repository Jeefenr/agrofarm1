package agrofarm;

import java.util.List;

public class PrototypeChecks {
    private PrototypeChecks() {
    }

    public static void main(String[] args) {
        checkSuccessfulFieldWork();
        checkIncompleteAndForeignFieldWork();
        checkMissingForecast();
        checkHarvestValidationAndDuplicates();
        checkSeasonalAnalysis();
        System.out.println("Усі перевірки прототипу успішно пройдено.");
    }

    private static void checkSuccessfulFieldWork() {
        Field field = new Field("F-01", "Тестове поле");
        TechOperation operation = new TechOperation("сівба");
        field.addOperation(operation);
        FieldWork work = new FieldWork(field, "2026", operation, new WeatherService(true));

        check(work.getStatus() == FieldWorkStatus.PLANNED, "Початковий стан має бути PLANNED.");
        check(work.complete(), "Коректна польова робота має завершитися.");
        check(work.getStatus() == FieldWorkStatus.COMPLETED, "Стан має перейти до COMPLETED.");
        check(!work.complete(), "Повторне завершення не має створювати новий перехід.");
    }

    private static void checkIncompleteAndForeignFieldWork() {
        Field field = new Field("F-01", "Перше поле");
        Field otherField = new Field("F-02", "Друге поле");
        TechOperation operation = new TechOperation("обробіток");
        otherField.addOperation(operation);
        checkRejectedWork(new FieldWork(null, "2026", operation, new WeatherService(true)), "без поля");
        checkRejectedWork(new FieldWork(field, " ", null, new WeatherService(true)), "без сезону й операції");
        checkRejectedWork(new FieldWork(field, "2026", null, new WeatherService(true)), "без операції");
        checkRejectedWork(new FieldWork(field, "2026", operation, new WeatherService(true)), "з чужою операцією");
        boolean rejectedSecondOwner = false;
        try {
            field.addOperation(operation);
        } catch (IllegalStateException expected) {
            rejectedSecondOwner = true;
        }
        check(rejectedSecondOwner, "Операцію не можна передати у власність другого поля.");
    }

    private static void checkRejectedWork(FieldWork work, String context) {
        check(!work.complete(), "Робота " + context + " має бути відхилена.");
        check(work.getStatus() == FieldWorkStatus.PLANNED, "Відхилена робота має лишитися запланованою.");
    }

    private static void checkMissingForecast() {
        Field field = new Field("F-03", "Поле без прогнозу");
        TechOperation operation = new TechOperation("збирання");
        field.addOperation(operation);
        FieldWork work = new FieldWork(field, "2026", operation, new WeatherService(false));
        check(work.complete(), "Відсутність прогнозу не має блокувати роботу.");
        check(work.getStatus() == FieldWorkStatus.COMPLETED, "Робота без прогнозу має завершитися.");
    }

    private static void checkHarvestValidationAndDuplicates() {
        Field field = new Field("F-01", "Тестове поле");
        Storekeeper storekeeper = new Storekeeper("S-01", "Тестовий комірник");
        storekeeper.selectSeason("2026");

        check(storekeeper.recordHarvest(field, "2026", "R-001", 12.5), "Додатне надходження має бути прийняте.");
        double initial = field.getHarvestForSeason("2026").orElseThrow();
        check(!storekeeper.recordHarvest(field, "2026", "R-002", 0), "Нульову кількість треба відхилити.");
        check(!storekeeper.recordHarvest(field, "2026", "R-003", -2), "Від'ємну кількість треба відхилити.");
        check(!storekeeper.recordHarvest(field, "2026", "R-006", Double.NaN),
                "Невизначену кількість треба відхилити.");
        check(!storekeeper.recordHarvest(field, "2026", "R-007", Double.POSITIVE_INFINITY),
                "Нескінченну кількість треба відхилити.");
        check(!storekeeper.recordHarvest(field, "2026", "R-001", 13), "Повторний receiptId треба відхилити.");
        check(!field.recordHarvest("R-004", "2026", 12.5, "інша примітка"),
                "Повтор поля, сезону й кількості треба відхилити незалежно від примітки.");
        check(!storekeeper.recordHarvest(field, " ", "R-005", 1), "Невизначений сезон треба відхилити.");
        check(field.getHarvestForSeason("2026").orElseThrow() == initial,
                "Відхилені надходження не мають змінювати сезонну суму.");
    }

    private static void checkSeasonalAnalysis() {
        Field harvested = new Field("F-01", "Поле з урожаєм");
        Field withoutHarvest = new Field("F-02", "Поле без урожаю");
        harvested.recordHarvest("R-001", "2026", 12.5);
        harvested.recordHarvest("R-002", "2025", 8.0);
        CropYieldAnalysis analysis = new CropYieldAnalysis();

        check(analysis.hasDataForSeason("2026", List.of(harvested, withoutHarvest)),
                "Аналіз має знаходити дані вибраного сезону.");
        String result = analysis.analyzeSeason("2026", List.of(harvested, withoutHarvest));
        check(result.contains("Поле з урожаєм") && result.contains("12,50"),
                "Аналіз має показати поле та суму вибраного сезону.");
        check(!result.contains("Поле без урожаю") && !result.contains("8,00"),
                "Аналіз не має включати поля без даних або інший сезон.");
        check(!analysis.hasDataForSeason("2024", List.of(harvested)),
                "Сезон без надходжень має бути позначений як відсутній.");
        check(analysis.analyzeSeason("2024", List.of(harvested)).contains("Немає облікованих даних"),
                "Порожній сезон не має повертати нульову врожайність.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
