package agrofarm;

import java.util.List;

public class Agronomist extends FarmParticipant implements IWorkPlan {
    private String activeSeason;

    public Agronomist(String participantId, String name) {
        super(participantId, name);
    }

    @Override
    public void selectSeason(String season) {
        activeSeason = season;
    }

    public void planCropRotation(Field field, String season, String crop) {
        if (field == null) {
            System.out.println("Не вдалося запланувати сівозміну: поле не задано.");
            return;
        }
        field.assignCropPlan(season, crop);
    }

    public String requestYieldAnalysis(CropYieldAnalysis analysis, String season, List<Field> fields) {
        String result = analysis.analyzeSeason(season == null ? activeSeason : season, fields);
        System.out.println(result);
        return result;
    }
}
