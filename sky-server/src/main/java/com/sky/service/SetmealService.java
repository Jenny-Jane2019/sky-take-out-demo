package com.sky.service;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.enumeration.OperationType;

public interface SetmealService {
    @AutoFill(value= OperationType.INSERT)
    void saveWithDish(SetmealDTO setmealDTO);

}
