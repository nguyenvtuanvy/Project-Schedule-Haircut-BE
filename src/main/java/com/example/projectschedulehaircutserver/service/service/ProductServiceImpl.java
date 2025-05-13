package com.example.projectschedulehaircutserver.service.service;

import com.example.projectschedulehaircutserver.dto.ServiceDTO;
import com.example.projectschedulehaircutserver.repository.ServiceRepo;
import com.example.projectschedulehaircutserver.response.ServiceManagementResponse;
import com.example.projectschedulehaircutserver.response.ShowAllServiceByComboIdResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ServiceRepo serviceRepo;
    @Override
    public Set<ServiceDTO> findAllService() {
        return serviceRepo.findAllService();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ShowAllServiceByComboIdResponse> findAllServiceByComboId(Integer id) {
        List<Object[]> objects = serviceRepo.findAllServiceByComboId(id);

        return objects.stream()
                .map(ShowAllServiceByComboIdResponse::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ServiceDTO> findAllServiceByCategoryId(Integer categoryId) {
        return serviceRepo.findAllServiceByCategoryId(categoryId).stream()
                .sorted(Comparator.comparing(ServiceDTO::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<ServiceManagementResponse> getAllServices() {
        try {
            Set<ServiceManagementResponse> result = serviceRepo.getAllServices();
            if(result.isEmpty()) {
                throw new NoSuchElementException("Danh sách khách hàng trống");
            }
            return result;
        } catch (RuntimeException ex) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu");
        }
    }


}
