package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController

@Api(tags = "分类相关接口")
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired

    public CategoryService categoryService;

    @PostMapping
    @ApiOperation("增加分类")
    public Result addCategory(CategoryDTO categoryDTO){
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }
    @GetMapping("/list")
    @ApiOperation("根据分类查询类型")
    public Result<List<Category>> findByType(Integer type){
        log.info("进入controller");
        List<Category>  category  = (List<Category>) categoryService.findByType(type);
        return Result.success(category);
    }
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult  pageResult= categoryService.pageCategory(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
    @DeleteMapping({"/{id}"})
    @ApiOperation("根据ID删除")
    public Result deleteById(@PathVariable Integer id){
        categoryService.deleteById(id);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用分类")
    public Result startOrOff(@PathVariable Integer status,Long id){

        categoryService.startOrOff(status,id);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改分类")
    public Result<Category> Update(@RequestBody CategoryDTO categoryDTO){
      categoryService.update(categoryDTO);
        return Result.success();
    }


}
