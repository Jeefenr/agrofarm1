package agrofarm;

import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public class CropYieldAnalysis {
    public CropYieldAnalysis() {
    }

    public boolean hasDataForSeason(String season, List<Field> fields) {
        if (season == null || season.isBlank() || fields == null) {
            return false;
        }
        return fields.stream()
                .filter(field -> field != null)
                .anyMatch(field -> field.hasHarvestForSeason(season));
    }

    public String analyzeSeason(String season, List<Field> fields) {
        if (!hasDataForSeason(season, fields)) {
            return "Немає облікованих даних урожаю за сезон " + season + ".";
        }

        StringJoiner results = new StringJoiner(System.lineSeparator());
        results.add("Порівняння врожайності за сезон " + season + ":");
        for (Field field : fields) {
            if (field == null) {
                continue;
            }
            field.getHarvestForSeason(season).ifPresent(amount -> results.add(
                    String.format(Locale.forLanguageTag("uk-UA"), "Поле «%s» — %.2f", field.getName(), amount)));
        }
        return results.toString();
    }
}
