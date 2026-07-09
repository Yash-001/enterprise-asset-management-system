package com.yashconsulting.eams.vendor.service;

import com.yashconsulting.eams.exception.VendorCodeAlreadyExistsException;
import com.yashconsulting.eams.vendor.dto.VendorCreateRequest;
import com.yashconsulting.eams.vendor.entity.Vendor;
import com.yashconsulting.eams.vendor.mapper.VendorMapper;
import com.yashconsulting.eams.vendor.repository.VendorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorServiceImplUnitTest {

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private VendorMapper vendorMapper;

    @InjectMocks
    private VendorServiceImpl vendorService;

    @Test
    void createVendor_whenCodeExists_thenThrowsVendorCodeAlreadyExistsException() {
        VendorCreateRequest request = VendorCreateRequest.builder()
                .vendorCode("VEN-001")
                .vendorName("Test Vendor")
                .build();

        when(vendorRepository.existsByVendorCode("VEN-001")).thenReturn(true);

        assertThrows(VendorCodeAlreadyExistsException.class, () ->
                vendorService.createVendor(request));

        verify(vendorRepository, never()).save(any());
    }
}
