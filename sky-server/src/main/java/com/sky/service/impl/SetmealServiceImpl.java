package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service

public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    @Transactional
    public void add(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.add(setmeal);
        Long id = setmeal.getId();
        setmealDishMapper.add(setmealDTO.getSetmealDishes(),id);

    }

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
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
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        long total = page.getTotal();
        List<SetmealVO> result = page.getResult();
        result.forEach(setmealVO -> {
            setmealVO.setCategoryName(categoryMapper.getById(setmealVO.getCategoryId()));
        });
        return new PageResult(total,result);
    }




    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        setmealDishMapper.deleteAll(setmealDTO.getId());
        setmealDishMapper.add(setmealDTO.getSetmealDishes(),setmeal.getId());
    }

    @Override
    public SetmealVO findById(Long id) {
       Setmeal setmeal =  setmealMapper.findById(id);
       List<SetmealDish> setmealDishs  = setmealDishMapper.findBySetmealId(id);
       SetmealVO setmealVO = new SetmealVO();
       BeanUtils.copyProperties(setmeal,setmealVO);
       setmealVO.setSetmealDishes(setmealDishs);
       return setmealVO;
    }

    @Override
    public void startOrStopSetmeal(Integer status, Long id) {
        setmealMapper.startOrStopSetmeal(status,id);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {

        setmealMapper.deleteBatch(ids);
        for (Long id:ids){
            setmealDishMapper.deleteAll(id);
        }

    }

}
