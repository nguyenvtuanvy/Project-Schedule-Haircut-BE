package com.example.projectschedulehaircutserver.service.service;

import com.example.projectschedulehaircutserver.dto.ServiceDTO;
import com.example.projectschedulehaircutserver.repository.ServiceRepo;
import com.example.projectschedulehaircutserver.response.ShowAllServiceByComboIdResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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


}
