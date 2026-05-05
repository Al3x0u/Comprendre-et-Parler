package be.hers.pi.comprendre_et_parler.models;

/**
 *  Enumeration for the MissionState
 */
public enum MissionState {
    PENDING(0),
    ACCEPTED(1),
    DENIED(2),
    PROGRESSING(3),
    CANCELED(4);

    private final int value;

    /**
     * @param value represent the new value
     */
    MissionState(int value) {
        this.value = value;
    }

    /**
     * @return this.value
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
}