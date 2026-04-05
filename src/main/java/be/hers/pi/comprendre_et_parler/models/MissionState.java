package be.hers.pi.comprendre_et_parler.models;

import java.util.NoSuchElementException;

/**
 *  Enumeration for the MissionState
 */
public enum MissionState {
    PENDING,
    ACCEPTED,
    DENIED,
    REGULAR;

    public static MissionState toMissionState(String character)throws NoSuchElementException {

         return switch(character) {
             case "A" -> ACCEPTED;

             case "R" -> REGULAR;

             case "D" -> DENIED;

             case "H" -> PENDING;

             default -> throw new NoSuchElementException();
         };
    }

    public String toSting(){

        return switch (this){
            case PENDING -> "H";

            case ACCEPTED -> "A";

            case DENIED -> "D";

            case REGULAR -> "R";
        };
    }
}
