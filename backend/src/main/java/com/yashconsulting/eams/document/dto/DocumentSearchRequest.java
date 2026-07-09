package com.yashconsulting.eams.document.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentSearchRequest {

    private String referenceType;
    private Long referenceId;
    private String contentType;
    private String originalFileName;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "uploadedAt";

    @Builder.Default
    private String sortDirection = "DESC";
}
