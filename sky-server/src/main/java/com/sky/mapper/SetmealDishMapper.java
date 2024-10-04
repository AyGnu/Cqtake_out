package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    @Select("select * from setmeal_dish where dish_id = #{id}")
    boolean findBymealWithdish(Long id);


    List<Long> getSetmealIdsByDishIds(List<Long> ids);


}
