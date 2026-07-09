package com.yashconsulting.eams.document.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {

    private Long id;
    private String fileName;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private String referenceType;
    private Long referenceId;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
