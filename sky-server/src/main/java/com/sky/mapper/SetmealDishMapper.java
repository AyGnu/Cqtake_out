package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    @Select("select * from setmeal_dish where dish_id = #{id}")
    boolean findBymealWithdish(Long id);


    List<Long> getSetmealIdsByDishIds(List<Long> ids);



//    @AutoFill(value = OperationType.INSERT)
    void add(@Param("setmealDishes")List<SetmealDish> setmealDishes, @Param("ide") Long id);

    void deleteAll(Long id);

    @Select("select * from setmeal_dish where dish_id = #{id}")
    List<SetmealDish> findBySetmealId(Long id);




}
