package com.yashconsulting.eams.document.repository;

import com.yashconsulting.eams.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    Optional<Document> findByIdAndActiveTrue(Long id);

    Page<Document> findAllByActiveTrue(Pageable pageable);

    List<Document> findAllByReferenceTypeAndReferenceIdAndActiveTrue(String referenceType, Long referenceId);

    Page<Document> findAllByReferenceTypeAndReferenceIdAndActiveTrue(String referenceType, Long referenceId, Pageable pageable);
}
