package org.anime.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.anime.mapper.userMapper;
import org.anime.pojo.Anime;
import org.anime.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RestController
public class userController {

    @Autowired
    private userMapper userMapper;


    @PostMapping("/register")
    public String register(@RequestBody User user){
        return userMapper.insert(user)==1?"注册成功":"注册失败";
    }

    /**
     *
     * @param user
     * @return
     */

    @PostMapping("/update")
    public String update(@RequestBody User user){
        return userMapper.updateById(user)==1?"更新成功":"请重试";
    }

    @GetMapping("/users")
    private List<User> users(@RequestParam int page) {
        return userMapper.selectPage(new Page<>(page,10),null).getRecords();
    }

    @PostMapping("/user/delete")
    public String delete(@RequestBody User user){
        return userMapper.deleteById(user)>0?"已删除":"删除失败";
    }

}
