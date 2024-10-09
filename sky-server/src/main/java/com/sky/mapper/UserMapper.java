package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    Boolean findByOpenId(String openid);


    void register(User user);

    @Select("select * from user where openid = #{openid}")
    User wxLogin(String openid);
}
