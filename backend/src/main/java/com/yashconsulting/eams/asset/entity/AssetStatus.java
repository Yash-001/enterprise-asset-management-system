package com.yashconsulting.eams.asset.entity;

import java.util.Set;

public enum AssetStatus {
    AVAILABLE,
    ASSIGNED,
    UNDER_MAINTENANCE,
    RETIRED,
    DISPOSED,
    LOST;

    public boolean isValidTransition(AssetStatus nextStatus) {
        if (this == nextStatus) {
            return true;
        }
        return switch (this) {
            case AVAILABLE -> Set.of(ASSIGNED, UNDER_MAINTENANCE, LOST, RETIRED, DISPOSED).contains(nextStatus);
            case ASSIGNED -> Set.of(AVAILABLE, UNDER_MAINTENANCE, LOST).contains(nextStatus);
            case UNDER_MAINTENANCE -> Set.of(AVAILABLE, RETIRED, DISPOSED, LOST).contains(nextStatus);
            case RETIRED -> Set.of(DISPOSED).contains(nextStatus);
            case LOST -> Set.of(AVAILABLE, RETIRED, DISPOSED).contains(nextStatus);
            case DISPOSED -> false; // Terminal state
        };
    }
}
