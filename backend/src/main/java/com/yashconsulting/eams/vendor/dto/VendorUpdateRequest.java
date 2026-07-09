package com.yashconsulting.eams.vendor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body payload to update an existing vendor")
public class VendorUpdateRequest {

    @Schema(description = "Formal name of the vendor company", example = "Metalworks Corp", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Vendor name must not be blank")
    @Size(max = 255, message = "Vendor name must not exceed 255 characters")
    private String vendorName;

    @Schema(description = "Primary contact representative name at the vendor", example = "John Doe")
    @Size(max = 255, message = "Contact person name must not exceed 255 characters")
    private String contactPerson;

    @Schema(description = "Primary contact email address", example = "contact@metalworkscorp.com")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Schema(description = "Primary contact phone number", example = "+1-555-0199")
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;

    @Schema(description = "Vendor street address details", example = "123 Industrial Way")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Schema(description = "City where the vendor resides", example = "Detroit")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Schema(description = "State/province location of the vendor", example = "Michigan")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Schema(description = "Country location of the vendor", example = "United States")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Schema(description = "Postal or zip code location info", example = "48201")
    @Size(max = 50, message = "Postal code must not exceed 50 characters")
    private String postalCode;

    @Schema(description = "Flag indicating whether the vendor is active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Active status must not be null")
    private Boolean active;
}
