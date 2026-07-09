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
@Schema(description = "Request body payload to update location details")
public class LocationUpdateRequest {

    @Schema(description = "Updated name of the location", example = "HQ Conference Room 1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Location name must not be blank")
    @Size(max = 100, message = "Location name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Location name must not contain leading or trailing spaces")
    private String locationName;

    @Schema(description = "Updated building name where the location resides", example = "Main Tower")
    @Size(max = 100, message = "Building name must not exceed 100 characters")
    @NoLeadingTrailingWhitespace(message = "Building name must not contain leading or trailing spaces")
    private String building;

    @Schema(description = "Updated floor number of the location", example = "4th Floor")
    @Size(max = 50, message = "Floor must not exceed 50 characters")
    @NoLeadingTrailingWhitespace(message = "Floor must not contain leading or trailing spaces")
    private String floor;

    @Schema(description = "Updated room number of the location", example = "Room 402")
    @Size(max = 50, message = "Room must not exceed 50 characters")
    @NoLeadingTrailingWhitespace(message = "Room must not contain leading or trailing spaces")
    private String room;

    @Schema(description = "Updated detailed description of the location", example = "Main executive meeting room")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Updated active status flag", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    private Boolean active;
}
