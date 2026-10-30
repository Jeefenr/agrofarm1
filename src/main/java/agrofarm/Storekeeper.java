package agrofarm;

import java.util.Locale;

public class Storekeeper extends FarmParticipant implements IWorkPlan {
    private String activeSeason;

    public Storekeeper(String participantId, String name) {
        super(participantId, name);
    }

    @Override
    public void selectSeason(String season) {
        activeSeason = season;
    }

    public boolean recordHarvest(Field field, String season, String receiptId, double quantity) {
        String selectedSeason = season == null ? activeSeason : season;
        if (field == null) {
            System.out.println("Надходження відхилено: поле не задано; облік не змінено.");
            return false;
        }
        var rejection = field.validateHarvest(receiptId, selectedSeason, quantity);
        if (rejection.isPresent()) {
            System.out.println("Надходження відхилено: " + rejection.get() + "; облік не змінено.");
            return false;
        }
        boolean recorded = field.recordHarvest(receiptId, selectedSeason, quantity);
        if (recorded) {
            String formattedQuantity = String.format(Locale.forLanguageTag("uk-UA"), "%.2f", quantity);
            System.out.println("Надходження врожаю " + formattedQuantity + " для поля «" + field.getName()
                    + "» за сезон " + selectedSeason + " обліковано.");
        } else {
            System.out.println("Надходження відхилено; облік не змінено.");
        }
        return recorded;
    }
}
