package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Insert("insert into category(id,type,name,sort,status,create_time,update_time,create_user,update_user) "+
            "values " +
            "(#{id},#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void addCategory(Category category);

    @Select("select * from category where type =#{type}")
    List<Category> findByType(Integer type);

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    @Delete("delete from category where id=#{id}")
    void deleteById(Integer id);

    void update(Category category);

    @Select("select name from category where id=#{categoryID}")
    String getById(Long categoryId);
}
