package com.example.projectschedulehaircutserver.service.combo;

import com.example.projectschedulehaircutserver.dto.ComboDTO;

import java.util.Set;

public interface ComboService {
    Set<ComboDTO> findAllCombo();

    Set<ComboDTO> findAllComboByCategoryId(Integer categoryId);
}
