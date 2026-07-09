package com.yashconsulting.eams.vendor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing vendor details")
public class VendorResponse {

    @Schema(description = "Unique database auto-increment ID", example = "1")
    private Long id;

    @Schema(description = "Unique alphanumeric identifying code for the vendor", example = "VEN-MET-001")
    private String vendorCode;

    @Schema(description = "Formal name of the vendor company", example = "Metalworks Corp")
    private String vendorName;

    @Schema(description = "Primary contact representative name", example = "John Doe")
    private String contactPerson;

    @Schema(description = "Primary contact email address", example = "contact@metalworkscorp.com")
    private String email;

    @Schema(description = "Primary contact phone number", example = "+1-555-0199")
    private String phone;

    @Schema(description = "Vendor street address", example = "123 Industrial Way")
    private String address;

    @Schema(description = "City location", example = "Detroit")
    private String city;

    @Schema(description = "State location", example = "Michigan")
    private String state;

    @Schema(description = "Country location", example = "United States")
    private String country;

    @Schema(description = "Postal code", example = "48201")
    private String postalCode;

    @Schema(description = "Flag indicating whether the vendor is active", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the vendor record was created", example = "2026-07-09T20:44:36")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the vendor record was last updated", example = "2026-07-09T20:44:36")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created the record", example = "admin@eams.com")
    private String createdBy;

    @Schema(description = "User who last updated the record", example = "admin@eams.com")
    private String updatedBy;
}
