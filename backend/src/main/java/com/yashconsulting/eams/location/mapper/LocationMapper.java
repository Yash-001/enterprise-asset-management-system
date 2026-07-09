package com.yashconsulting.eams.location.mapper;

import com.yashconsulting.eams.location.dto.LocationCreateRequest;
import com.yashconsulting.eams.location.dto.LocationResponse;
import com.yashconsulting.eams.location.dto.LocationUpdateRequest;
import com.yashconsulting.eams.location.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toEntity(LocationCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Location.builder()
                .locationCode(request.getLocationCode() != null ? request.getLocationCode().trim() : null)
                .locationName(request.getLocationName() != null ? request.getLocationName().trim() : null)
                .building(request.getBuilding() != null ? request.getBuilding().trim() : null)
                .floor(request.getFloor() != null ? request.getFloor().trim() : null)
                .room(request.getRoom() != null ? request.getRoom().trim() : null)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .build();
    }

    public LocationResponse toResponse(Location entity) {
        if (entity == null) {
            return null;
        }

        return LocationResponse.builder()
                .id(entity.getId())
                .locationCode(entity.getLocationCode())
                .locationName(entity.getLocationName())
                .building(entity.getBuilding())
                .floor(entity.getFloor())
                .room(entity.getRoom())
                .description(entity.getDescription())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntity(LocationUpdateRequest request, Location entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setLocationName(request.getLocationName() != null ? request.getLocationName().trim() : null);
        entity.setBuilding(request.getBuilding() != null ? request.getBuilding().trim() : null);
        entity.setFloor(request.getFloor() != null ? request.getFloor().trim() : null);
        entity.setRoom(request.getRoom() != null ? request.getRoom().trim() : null);
        entity.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        entity.setActive(request.getActive());
    }
}
