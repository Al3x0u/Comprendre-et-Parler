package be.hers.pi.comprendre_et_parler.models;

/**
 * Enumeration for the MissionState
 */
public enum MissionState {
    PENDING(0),
    ACCEPTED(1),
    DENIED(2),
    PROGRESSING(3),
    REGULAR(4),
    CANCELED(5);

    private final int value;

    /**
     * Constructor of a MissionState object
     * @param value represents the id value the mission state
     */
    MissionState(int value) {
        this.value = value;
    }

    /**
     * @return the integer value associated with this MissionState
     */
    public int getValue() {
        return value;
    }

    /**
     * Return the MissionState associated with the given integer value
     * @param value the integer value to convert
     * @return the MissionState associated with the given value
     * @throws IllegalArgumentException if no MissionState is associated with the given value
     */
    public static MissionState fromValue(int value) {
        for (MissionState state : values()) {
            if (state.value == value)
                return state;
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    /**
     * Return the MissionState assosciated with the given status
     * @param status the status to convert
     * @return the MissionState associated with the given value
     * @throws IllegalArgumentException if no MissionState is associated with the given string
     */
    public static MissionState fromDisplayStatus(String status) {
        return switch (status) {
            case "Acceptée"        -> ACCEPTED;
            case "En attente"      -> PENDING;
            case "Refusée"         -> DENIED;
            case "Annulée"         -> CANCELED;
            case "Horaire de base" -> REGULAR;
            default -> throw new IllegalArgumentException("Unknown value: " + status);
        };
    }
}