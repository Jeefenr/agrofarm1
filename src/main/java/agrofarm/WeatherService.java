package agrofarm;

import java.util.Optional;

public class WeatherService {
    private final boolean forecastAvailable;

    public WeatherService(boolean forecastAvailable) {
        this.forecastAvailable = forecastAvailable;
    }

    public Optional<String> getModeledContext(String fieldId, String season) {
        if (!forecastAvailable || fieldId == null || season == null || season.isBlank()) {
            return Optional.empty();
        }
        return Optional.of("Модельований погодний контекст для сезону " + season);
    }
}
