package com.yashconsulting.eams.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing a location profile")
public class LocationResponse {

    @Schema(description = "Unique database auto-increment ID", example = "1")
    private Long id;

    @Schema(description = "Unique code identifying the location", example = "LOC-HQ-01")
    private String locationCode;

    @Schema(description = "Name of the location", example = "HQ Conference Room 1")
    private String locationName;

    @Schema(description = "Building name where the location resides", example = "Main Tower")
    private String building;

    @Schema(description = "Floor number of the location", example = "4th Floor")
    private String floor;

    @Schema(description = "Room number of the location", example = "Room 402")
    private String room;

    @Schema(description = "Detailed description of the location", example = "Main executive meeting room")
    private String description;

    @Schema(description = "Location active status flag", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the record was created", example = "2026-07-09T20:26:36")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the record was last updated", example = "2026-07-09T20:26:36")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created the record", example = "admin@eams.com")
    private String createdBy;

    @Schema(description = "User who last updated the record", example = "admin@eams.com")
    private String updatedBy;
}
