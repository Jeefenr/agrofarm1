package agrofarm;

public abstract class FarmParticipant {
    private final String participantId;
    private final String name;

    protected FarmParticipant(String participantId, String name) {
        this.participantId = participantId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public final String getParticipantId() {
        return participantId;
    }

    public abstract void selectSeason(String season);
}
