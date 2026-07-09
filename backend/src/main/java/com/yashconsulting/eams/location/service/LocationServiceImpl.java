package com.yashconsulting.eams.location.service;

import com.yashconsulting.eams.exception.LocationCodeAlreadyExistsException;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.location.dto.LocationCreateRequest;
import com.yashconsulting.eams.location.dto.LocationResponse;
import com.yashconsulting.eams.location.dto.LocationSearchRequest;
import com.yashconsulting.eams.location.dto.LocationUpdateRequest;
import com.yashconsulting.eams.location.entity.Location;
import com.yashconsulting.eams.location.mapper.LocationMapper;
import com.yashconsulting.eams.location.repository.LocationRepository;
import com.yashconsulting.eams.location.specification.LocationSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Override
    @Transactional
    public LocationResponse createLocation(LocationCreateRequest request) {
        log.info("Creating new location with code: {}", request.getLocationCode());

        String locationCode = request.getLocationCode().trim().toUpperCase(Locale.ROOT);
        request.setLocationCode(locationCode);

        if (locationRepository.existsByLocationCode(locationCode)) {
            throw new LocationCodeAlreadyExistsException("Location code " + locationCode + " is already registered");
        }

        Location location = locationMapper.toEntity(request);
        Location savedLocation = locationRepository.save(location);
        return locationMapper.toResponse(savedLocation);
    }

    @Override
    @Transactional
    public LocationResponse updateLocation(Long id, LocationUpdateRequest request) {
        log.info("Updating location with ID: {}", id);
        Location location = getLocationByIdOrThrow(id);

        locationMapper.updateEntity(request, location);
        Location updatedLocation = locationRepository.save(location);
        return locationMapper.toResponse(updatedLocation);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponse getLocationById(Long id) {
        log.info("Fetching location with ID: {}", id);
        Location location = getLocationByIdOrThrow(id);
        return locationMapper.toResponse(location);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LocationResponse> getAllLocations(Pageable pageable, boolean includeInactive) {
        log.info("Listing all locations with pagination. Include inactive: {}", includeInactive);
        Page<Location> locationsPage = includeInactive
                ? locationRepository.findAll(pageable)
                : locationRepository.findAllByActiveTrue(pageable);
        return locationsPage.map(locationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LocationResponse> searchLocations(LocationSearchRequest request) {
        log.info("Searching locations dynamically");
        Specification<Location> spec = LocationSpecification.build(request);

        Sort.Direction direction = Sort.Direction.fromString(
                request.getSortDirection() != null ? request.getSortDirection() : "ASC"
        );
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";
        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                Sort.by(direction, sortBy)
        );

        Page<Location> locationsPage = locationRepository.findAll(spec, pageable);
        return locationsPage.map(locationMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        log.info("Soft deleting location with ID: {}", id);
        Location location = getLocationByIdOrThrow(id);
        location.setActive(false);
        locationRepository.save(location);
    }

    private Location getLocationByIdOrThrow(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + id));
    }
}
