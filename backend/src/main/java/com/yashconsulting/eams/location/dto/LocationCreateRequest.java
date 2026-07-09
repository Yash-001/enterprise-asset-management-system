package com.yashconsulting.eams.location.dto;

import com.yashconsulting.eams.user.validation.NoLeadingTrailingWhitespace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload to register a new location")
public class LocationCreateRequest {

    @Schema(description = "Unique code identifying the location", example = "LOC-HQ-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Location code must not be blank")
    @Size(max = 100, message = "Location code must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Location code must not contain leading or trailing spaces")
    private String locationCode;

    @Schema(description = "Name of the location", example = "HQ Conference Room 1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Location name must not be blank")
    @Size(max = 100, message = "Location name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Location name must not contain leading or trailing spaces")
    private String locationName;

    @Schema(description = "Building name where the location resides", example = "Main Tower")
    @Size(max = 100, message = "Building name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Building name must not contain leading or trailing spaces")
    private String building;

    @Schema(description = "Floor number of the location", example = "4th Floor")
    @Size(max = 50, message = "Floor must not exceed 50 characters")
    @NoLeadingTrailingWhitespace(message = "Floor must not contain leading or trailing spaces")
    private String floor;

    @Schema(description = "Room number of the location", example = "Room 402")
    @Size(max = 50, message = "Room must not exceed 50 characters")
    @NoLeadingTrailingWhitespace(message = "Room must not contain leading or trailing spaces")
    private String room;

    @Schema(description = "Detailed description of the location", example = "Main executive meeting room")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Flag indicating whether location is active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    @Builder.Default
    private Boolean active = true;
}
