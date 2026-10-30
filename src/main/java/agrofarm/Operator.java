package agrofarm;

public class Operator extends FarmParticipant implements IWorkPlan {
    private String activeSeason;
    private final WeatherService weatherService;

    public Operator(String participantId, String name, WeatherService weatherService) {
        super(participantId, name);
        this.weatherService = weatherService;
    }

    @Override
    public void selectSeason(String season) {
        activeSeason = season;
    }

    public FieldWork planFieldWork(Field field, String season, TechOperation operation) {
        String selectedSeason = season == null ? activeSeason : season;
        System.out.println("Механізатор планує польову роботу на сезон "
                + (selectedSeason == null ? "(не задано)" : selectedSeason) + ".");
        return new FieldWork(field, selectedSeason, operation, weatherService);
    }

    public void recordFieldWork(FieldWork work) {
        if (work == null) {
            System.out.println("Не вдалося зафіксувати польову роботу: роботу не задано.");
            return;
        }
        work.complete();
    }
}
