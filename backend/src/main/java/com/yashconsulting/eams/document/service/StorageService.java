package com.yashconsulting.eams.document.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction for file storage backends.
 * Implement this interface to support local filesystem, AWS S3, Azure Blob, GCS, etc.
 */
public interface StorageService {

    /**
     * Stores a file and returns the relative storage path.
     *
     * @param file          the uploaded multipart file
     * @param referenceType the entity type this file is linked to (e.g., ASSET, WORK_ORDER)
     * @param referenceId   the ID of the linked entity
     * @return the relative path where the file was stored
     */
    String store(MultipartFile file, String referenceType, Long referenceId);

    /**
     * Loads a file as a Resource for download.
     *
     * @param storagePath the relative storage path
     * @return the file resource
     */
    Resource load(String storagePath);

    /**
     * Deletes a file from storage.
     *
     * @param storagePath the relative storage path
     */
    void delete(String storagePath);
}
