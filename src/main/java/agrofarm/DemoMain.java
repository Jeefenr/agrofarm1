package agrofarm;

import agrofarm.config.FieldIdentityModule;
import agrofarm.persistence.DatabaseModule;
import agrofarm.report.HtmlReportGenerator;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DemoMain {
    private DemoMain() {
    }

    public static void main(String[] args) {
        Path target = Path.of("target");
        try {
            Files.createDirectories(target);
        } catch (IOException e) {
            System.err.println("Не вдалося створити каталог target: " + e.getMessage());
            System.exit(1);
        }

        try {
            runDemo(target);
        } catch (ProvisionException e) {
            System.err.println("Не вдалося ініціалізувати базу даних: " + e.getMessage());
            System.exit(1);
        } catch (ReadFailureException e) {
            System.err.println("Не вдалося прочитати записи технічних операцій: " + e.getCause().getMessage());
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("Помилка роботи з базою даних: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Не вдалося створити HTML-звіт: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void runDemo(Path target) throws SQLException, IOException {
        String season = "2026";
        Injector injector = Guice.createInjector(new DatabaseModule());
        try (Connection ignored = injector.getInstance(Connection.class)) {
            Field northField = createField(injector, "F-01", "Північне поле");
            Field southField = createField(injector, "F-02", "Південне поле");
            List<Field> fields = List.of(northField, southField);

            TechOperation plowing = new TechOperation("OP-F01-PLOWING", "оранка");
            TechOperation sowing = new TechOperation("OP-F02-SOWING", "сівба");
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

            List<String> saveErrors = new ArrayList<>();
            saveOperation(northField, plowing, saveErrors);
            saveOperation(southField, sowing, saveErrors);

            List<OperationRecord> records;
            try {
                records = injector.getInstance(TechOperationStore.class).findAll();
            } catch (SQLException e) {
                throw new ReadFailureException(e);
            }
            new HtmlReportGenerator().generate(target.resolve("operations.html"), records, saveErrors);
        }
    }

    private static Field createField(Injector injector, String fieldId, String name) {
        return injector.createChildInjector(new FieldIdentityModule(fieldId, name)).getInstance(Field.class);
    }

    private static void saveOperation(Field field, TechOperation operation, List<String> saveErrors) {
        try {
            field.saveOperation(operation);
        } catch (SQLException e) {
            String message = "Не вдалося зберегти операцію «" + operation.getOperationId() + "»: " + e.getMessage();
            System.err.println(message);
            saveErrors.add(message);
        }
    }

    private static class ReadFailureException extends SQLException {
        private ReadFailureException(SQLException cause) {
            super(cause);
        }
    }
}
