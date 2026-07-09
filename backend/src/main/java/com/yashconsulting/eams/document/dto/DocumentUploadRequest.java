package com.yashconsulting.eams.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentUploadRequest {

    @NotBlank(message = "Reference type is required")
    private String referenceType;

    @NotNull(message = "Reference ID is required")
    private Long referenceId;
}
