package org.anime.VController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.anime.pojo.Anime;
import org.anime.pojo.Rating;
import org.anime.pojo.User;
import org.anime.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import javax.servlet.http.HttpSession;
import org.anime.mapper.ratingMapper;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ViewController {

    @Autowired
    private org.anime.mapper.userMapper userMapper;

    @Autowired
    private org.anime.mapper.animeMapper animeMapper;

    @Autowired
    private ratingMapper ratingMapper;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/view/index")
    public String index(HttpSession session) {
        return "index";
    }

    @GetMapping("/view/login")
    public String login() {
        return "login";
    }

    @GetMapping("/view/register")
    public String register() {
        return "register";
    }

    @GetMapping("/view/recommendation")
    public String recommendation(HttpSession session,Model model) {
        Object username = session.getAttribute("username");
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if(redisUtil.getList("result_" + user.getUserId()).size()>0){
            model.addAttribute("exist",true);
        }else
            model.addAttribute("exist",false);
        model.addAttribute("userId", user.getUserId());
        return "recommendation";
    }

    @GetMapping("/view/list")
    public String list() {
        return "list";
    }

    @GetMapping("/view/chat")
    public String chat(HttpSession session,Model model) {
        Object username = session.getAttribute("username");
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        model.addAttribute("nickname", user.getNickname());
        return "chat";
    }

    @GetMapping("/view/anime/{id}")
    public String anime(@PathVariable int id,Model model,HttpSession session) {
        Anime anime = animeMapper.selectById(id);
        model.addAttribute("anime", anime);
        Object username = session.getAttribute("username");
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        Rating rating = ratingMapper.selectOne(new QueryWrapper<Rating>().eq("anime_id", id).eq("user_id", user.getUserId()));
        if(rating!=null){
            model.addAttribute("rating",rating);
            model.addAttribute("exist",true);
        }else
            model.addAttribute("exist",false);
        return "anime";
    }

    @GetMapping("/view/userInfo")
    public String user_info(HttpSession session, Model model) {
        Object username = session.getAttribute("username");
        model.addAttribute("user", userMapper.selectOne(new QueryWrapper<User>().eq("username",username.toString())));
        return "userInfo";
    }

    @GetMapping("/view/sys/animeMG")
    public String sys_animeMG(HttpSession session, Model model) {
        return "/sys/animeMG";
    }

    @GetMapping("/view/sys/login")
    public String sys_login(HttpSession session, Model model) {
        return "/sys/login";
    }

    @GetMapping("/view/sys/userMG")
    public String sys_userMG(){
        return "/sys/userMG";
    }

    @GetMapping("/view/sys/ratingMG")
    public String sys_ratingMG(){
        return "/sys/ratingMG";
    }

}
