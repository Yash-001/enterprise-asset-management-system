package com.yashconsulting.eams.location.specification;

import com.yashconsulting.eams.location.dto.LocationSearchRequest;
import com.yashconsulting.eams.location.entity.Location;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class LocationSpecification {

    private LocationSpecification() {
        // Utility class constructor private instantiation restriction
    }

    public static Specification<Location> hasLocationCode(String locationCode) {
        return (root, query, cb) -> {
            if (locationCode == null || locationCode.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("locationCode")), "%" + locationCode.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Location> hasLocationName(String locationName) {
        return (root, query, cb) -> {
            if (locationName == null || locationName.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("locationName")), "%" + locationName.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Location> hasBuilding(String building) {
        return (root, query, cb) -> {
            if (building == null || building.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("building")), "%" + building.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Location> hasFloor(String floor) {
        return (root, query, cb) -> {
            if (floor == null || floor.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("floor")), "%" + floor.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Location> hasRoom(String room) {
        return (root, query, cb) -> {
            if (room == null || room.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("room")), "%" + room.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Location> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<Location> build(LocationSearchRequest request) {
        if (request == null) {
            return (root, query, cb) -> cb.equal(root.get("active"), true);
        }
        Boolean activeFilter = request.getActive() != null ? request.getActive() : Boolean.TRUE;
        return Specification.allOf(
                hasLocationCode(request.getLocationCode()),
                hasLocationName(request.getLocationName()),
                hasBuilding(request.getBuilding()),
                hasFloor(request.getFloor()),
                hasRoom(request.getRoom()),
                isActive(activeFilter)
        );
    }
}
