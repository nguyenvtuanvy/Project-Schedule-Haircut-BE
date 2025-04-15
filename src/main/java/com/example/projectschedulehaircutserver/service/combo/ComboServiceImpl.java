package com.example.projectschedulehaircutserver.service.combo;

import com.example.projectschedulehaircutserver.dto.ComboDTO;
import com.example.projectschedulehaircutserver.repository.ComboRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ComboServiceImpl implements ComboService{
    private final ComboRepo comboRepo;

    @Override
    public Set<ComboDTO> findAllCombo() {
        return comboRepo.findAllCombo();
    }

    @Override
    public Set<ComboDTO> findAllComboByCategoryId(Integer categoryId) {
        return comboRepo.findAllComboByCategoryId(categoryId).stream()
                .sorted(Comparator.comparing(ComboDTO::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
