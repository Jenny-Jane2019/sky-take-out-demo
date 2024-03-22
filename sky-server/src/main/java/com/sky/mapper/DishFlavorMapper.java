package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 菜品风味批量删除
     *
     * @param ids
     * @return
     */
    void deleteBatch(List<Long> ids);
}
