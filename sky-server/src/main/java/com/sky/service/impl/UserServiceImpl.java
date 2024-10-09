package com.sky.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String code = userLoginDTO.getCode();
        Map<String,String> map = new HashMap<>();

        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String s = HttpClientUtil.doGet("https://api.weixin.qq.com/sns/jscode2session", map);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String,String> map1 = objectMapper.readValue(s, Map.class);

            String sessionKey = map1.get("session_key");
            String openid = map1.get("openid");
            if(openid==null){
                throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
            }
           Boolean isRegister =  userMapper.findByOpenId(openid);
            if(isRegister==null){
              User user =  User.builder()
                        .openid(openid)
                        .createTime(LocalDateTime.now())
                                .build();
                userMapper.register(user);
            }
           User user =  userMapper.wxLogin(openid);
            return user;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
}
