package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingMapper;
import com.sky.properties.JwtProperties;
import com.sky.service.ShoppingCartService;
import com.sky.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingMapper shoppingMapper;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {

//         = (List<ShoppingCart>) new ShoppingCart();

        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> lists = shoppingMapper.list(shoppingCart);

        if(lists!=null&&lists.size()>0){
            ShoppingCart shoppingCart1 = lists.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
        shoppingMapper.updateNumberById(shoppingCart1);
        }else {

            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();
            if(dishId!=null){
                //本次添加的是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

            }else {
                //本次添加的是套餐
                Setmeal setmeal = setmealMapper.findById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingMapper.add(shoppingCart);

            }
        }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> list = shoppingMapper.list(shoppingCart);
        return list;
    }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingMapper.clean(userId);
    }

    @Override
    public void deleteOne(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(userId);
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        shoppingMapper.deleteOne(shoppingCart);
    }
}
