package org.anime.VController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.anime.pojo.Administrator;
import org.anime.pojo.Anime;
import org.anime.pojo.User;
import org.anime.utils.JwtUtil;
import org.anime.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.anime.mapper.userMapper;

@Controller
public class CController {

    @Autowired
    private userMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private org.anime.mapper.animeMapper animeMapper;

    @Autowired
    private org.anime.mapper.administratorMapper administratorMapper;

    /**
     *
     * @param username
     * @param passwd
     * @param response
     * @param session
     * @return
     * @throws IOException
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,@RequestParam String passwd, HttpServletResponse response, HttpSession session) throws IOException {
        QueryWrapper<User> eq = new QueryWrapper<User>().eq("username", username).eq("passwd", passwd);
        Long l = userMapper.selectCount(eq);
        if (l > 0) {
            String login_key = JwtUtil.createJWT(username, "login", null);
            Cookie cookie_name = new Cookie("login_name", username);
            Cookie cookie_key = new Cookie("login_key", login_key);
            cookie_name.setPath("/");
//            cookie_name.setMaxAge(60*60);
            cookie_key.setPath("/");
//            cookie_key.setMaxAge(60*60);
            response.addCookie(cookie_key);
            response.addCookie(cookie_name);
            redisUtil.set(username,login_key,60*60, TimeUnit.SECONDS);
            session.setAttribute("username",username);
            return "index";
        } else {
            return "login";
        }
    }

    @ResponseBody
    @GetMapping("/rcminfo/{id}")
    public ArrayList<Anime> rcminfo(HttpSession session,@PathVariable int id) {
        List<String> results = redisUtil.getList("result_" + id);
        ArrayList<Anime> arrayList = new ArrayList<>();
        for (String result : results) {
            Anime anime = animeMapper.selectById(Integer.parseInt(result));
            if(anime != null) {
                arrayList.add(anime);
            }
        }
        return arrayList;
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam String username,@RequestParam String passwd) throws IOException {
        QueryWrapper<Administrator> eq = new QueryWrapper<Administrator>().eq("adminname", username).eq("passwd", passwd);
        Long l = administratorMapper.selectCount(eq);
        if (l > 0) {
            return "sys/animeMG";
        } else {
            return "sys/login";
        }
    }

}
