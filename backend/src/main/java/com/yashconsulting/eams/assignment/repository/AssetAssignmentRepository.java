package com.yashconsulting.eams.assignment.repository;

import com.yashconsulting.eams.assignment.entity.AssetAssignment;
import com.yashconsulting.eams.assignment.entity.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetAssignmentRepository extends JpaRepository<AssetAssignment, Long>, JpaSpecificationExecutor<AssetAssignment> {

    boolean existsByAssetIdAndStatus(Long assetId, AssignmentStatus status);
}
