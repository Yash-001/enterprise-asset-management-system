package com.yashconsulting.eams.location.service;

import com.yashconsulting.eams.location.dto.LocationCreateRequest;
import com.yashconsulting.eams.location.dto.LocationResponse;
import com.yashconsulting.eams.location.dto.LocationSearchRequest;
import com.yashconsulting.eams.location.dto.LocationUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationService {

    LocationResponse createLocation(LocationCreateRequest request);

    LocationResponse updateLocation(Long id, LocationUpdateRequest request);

    LocationResponse getLocationById(Long id);

    Page<LocationResponse> getAllLocations(Pageable pageable, boolean includeInactive);

    Page<LocationResponse> searchLocations(LocationSearchRequest request);

    void deleteLocation(Long id);
}
