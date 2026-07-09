package com.yashconsulting.eams.department.service;

import com.yashconsulting.eams.department.dto.DepartmentCreateRequest;
import com.yashconsulting.eams.department.dto.DepartmentResponse;
import com.yashconsulting.eams.department.dto.DepartmentSearchRequest;
import com.yashconsulting.eams.department.dto.DepartmentUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    DepartmentResponse createDepartment(DepartmentCreateRequest request);

    DepartmentResponse updateDepartment(Long id, DepartmentUpdateRequest request);

    DepartmentResponse getDepartmentById(Long id);

    Page<DepartmentResponse> getAllDepartments(Pageable pageable, boolean includeInactive);

    Page<DepartmentResponse> searchDepartments(DepartmentSearchRequest request);

    void deleteDepartment(Long id);
}
