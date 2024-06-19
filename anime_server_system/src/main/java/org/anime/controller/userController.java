package org.anime.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.anime.mapper.userMapper;
import org.anime.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
public class userController {

    @Autowired
    private userMapper userMapper;


    @PostMapping("/register")
    public String register(@RequestBody User user){
        return userMapper.insert(user)==1?"注册成功":"注册失败";
    }

}
