package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    void addCategory(CategoryDTO categoryDTO);

    List<Category> findByType(Integer type);

    PageResult pageCategory(CategoryPageQueryDTO categoryPageQueryDTO);

    void deleteById(Integer id);

    void startOrOff(Integer status, Long id);

    void update(CategoryDTO categoryDTO);
}
