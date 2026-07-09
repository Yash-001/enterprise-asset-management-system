package com.yashconsulting.eams.department.service;

import com.yashconsulting.eams.department.dto.DepartmentCreateRequest;
import com.yashconsulting.eams.department.dto.DepartmentResponse;
import com.yashconsulting.eams.department.dto.DepartmentSearchRequest;
import com.yashconsulting.eams.department.dto.DepartmentUpdateRequest;
import com.yashconsulting.eams.department.entity.Department;
import com.yashconsulting.eams.department.mapper.DepartmentMapper;
import com.yashconsulting.eams.department.repository.DepartmentRepository;
import com.yashconsulting.eams.department.specification.DepartmentSpecification;
import com.yashconsulting.eams.exception.DepartmentCodeAlreadyExistsException;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentCreateRequest request) {
        log.info("Creating new department with code: {}", request.getDepartmentCode());

        String departmentCode = request.getDepartmentCode().trim().toUpperCase(Locale.ROOT);
        request.setDepartmentCode(departmentCode);

        if (departmentRepository.existsByDepartmentCode(departmentCode)) {
            throw new DepartmentCodeAlreadyExistsException("Department code " + departmentCode + " is already registered");
        }

        Department department = departmentMapper.toEntity(request);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toResponse(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentUpdateRequest request) {
        log.info("Updating department with ID: {}", id);
        Department department = getDepartmentByIdOrThrow(id);

        departmentMapper.updateEntity(request, department);
        Department updatedDepartment = departmentRepository.save(department);
        return departmentMapper.toResponse(updatedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        log.info("Fetching department with ID: {}", id);
        Department department = getDepartmentByIdOrThrow(id);
        return departmentMapper.toResponse(department);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(Pageable pageable, boolean includeInactive) {
        log.info("Listing all departments with pagination. Include inactive: {}", includeInactive);
        Page<Department> departmentsPage = includeInactive
                ? departmentRepository.findAll(pageable)
                : departmentRepository.findAllByActiveTrue(pageable);
        return departmentsPage.map(departmentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> searchDepartments(DepartmentSearchRequest request) {
        log.info("Searching departments dynamically");
        Specification<Department> spec = DepartmentSpecification.build(request);

        Sort.Direction direction = Sort.Direction.fromString(
                request.getSortDirection() != null ? request.getSortDirection() : "ASC"
        );
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";
        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                Sort.by(direction, sortBy)
        );

        Page<Department> departmentsPage = departmentRepository.findAll(spec, pageable);
        return departmentsPage.map(departmentMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        log.info("Soft deleting department with ID: {}", id);
        Department department = getDepartmentByIdOrThrow(id);
        department.setActive(false);
        departmentRepository.save(department);
    }

    private Department getDepartmentByIdOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
    }
}
