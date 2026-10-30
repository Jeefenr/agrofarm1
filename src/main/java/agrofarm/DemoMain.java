package agrofarm;

import java.util.List;

public class DemoMain {
    private DemoMain() {
    }

    public static void main(String[] args) {
        String season = "2026";
        Field northField = new Field("F-01", "Північне поле");
        Field southField = new Field("F-02", "Південне поле");
        List<Field> fields = List.of(northField, southField);

        TechOperation plowing = new TechOperation("оранка");
        TechOperation sowing = new TechOperation("сівба");
        northField.addOperation(plowing);
        southField.addOperation(sowing);

        Agronomist agronomist = new Agronomist("A-01", "Олена");
        Operator operator = new Operator("O-01", "Петро", new WeatherService(true));
        Storekeeper storekeeper = new Storekeeper("S-01", "Марія");

        System.out.println("=== Планування культури ===");
        agronomist.selectSeason(season);
        agronomist.planCropRotation(northField, season, "пшениця");
        agronomist.planCropRotation(southField, season, "кукурудза");

        System.out.println("\n=== Польова робота ===");
        operator.selectSeason(season);
        FieldWork northWork = operator.planFieldWork(northField, season, plowing);
        operator.recordFieldWork(northWork);
        FieldWork southWork = operator.planFieldWork(southField, season, sowing);
        operator.recordFieldWork(southWork);

        System.out.println("\n=== Облік урожаю ===");
        storekeeper.selectSeason(season);
        storekeeper.recordHarvest(northField, season, "R-001", 42.5);
        storekeeper.recordHarvest(southField, season, "R-002", 37.0);

        System.out.println("\n=== Аналіз урожайності ===");
        agronomist.requestYieldAnalysis(new CropYieldAnalysis(), season, fields);
    }
}
