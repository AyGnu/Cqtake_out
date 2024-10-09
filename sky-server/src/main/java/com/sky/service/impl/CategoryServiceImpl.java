package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
//import jdk.jpackage.internal.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    public CategoryMapper categoryMapper;
    @Autowired
    public DishMapper dishMapper;
    @Autowired
    public SetmealMapper setmealMapper;
    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
//        BeanUtils.copyProperties(employeeDTO,employee);
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(StatusConstant.DISABLE);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());

        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.addCategory(category);
    }

    @Override
    public List<Category> findByType(Integer type) {
//        log.info("查找",type);
        CategoryPageQueryDTO category1 = new CategoryPageQueryDTO();
        category1.setType(type);


        List<Category> category  =  categoryMapper.pageQuery(category1);
        return category;
    }

    @Override
    public PageResult pageCategory(CategoryPageQueryDTO categoryPageQueryDTO) {
        /**
         *       PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
         *
         *         Page<Employee> page  = employeeMapper.pageQuery(employeePageQueryDTO);
         *
         *         long total = page.getTotal();
         *         List<Employee> result = page.getResult();
         *
         *         return new PageResult(total,result);
         *
         */
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> page  = categoryMapper.pageQuery(categoryPageQueryDTO);
        long total = page.getTotal();
        List<Category> result = page.getResult();
        return new PageResult(total,result);
    }

    @Override
    public void deleteById(Integer id) {
        Integer count =  dishMapper.countByCategoryId(id);
        if(count>0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        count = setmealMapper.countByCategoryId(id);
        if(count>0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        categoryMapper.deleteById(id);
    }

    @Override
    public void startOrOff(Integer status, Long id) {
        Category category = new Category().builder()
                        .status(status)
                                .id(id).build();
        categoryMapper.update(category);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
//        BeanUtils.copyProperties(employeeDTO,employee);
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(StatusConstant.DISABLE);
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(category);
    }
}
