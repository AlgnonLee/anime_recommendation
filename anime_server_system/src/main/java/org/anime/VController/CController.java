package org.anime.VController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.anime.pojo.User;
import org.anime.utils.JwtUtil;
import org.anime.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.anime.mapper.userMapper;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CController {

    @Autowired
    private userMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/login")
    public String login(@RequestParam String username,@RequestParam String passwd, HttpServletResponse response) throws IOException {
        QueryWrapper<User> eq = new QueryWrapper<User>().eq("username", username).eq("passwd", passwd);
        Long l = userMapper.selectCount(eq);
        if (l > 0) {
            String login_key = JwtUtil.createJWT(username, "login", null);
            Cookie cookie_name = new Cookie("login_name", username);
            Cookie cookie_key = new Cookie("login_key", login_key);
            cookie_name.setPath("/");
            cookie_name.setMaxAge(60*60);
            cookie_key.setPath("/");
            cookie_key.setMaxAge(60*60);
            response.addCookie(cookie_key);
            response.addCookie(cookie_name);
            redisUtil.set(username,login_key,60*60, TimeUnit.SECONDS);
            return "index";
        } else {
            return "login";
        }
    }

}
