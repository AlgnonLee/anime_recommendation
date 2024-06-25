package org.anime.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.anime.mapper.ratingMapper;
import org.anime.pojo.Rating;
import org.anime.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class ratingController {

    @Autowired
    private ratingMapper ratingMapper;

    @Autowired
    private org.anime.mapper.userMapper userMapper;


    @GetMapping("/ratings/{id}")
    public List<Rating> rating(@PathVariable int id) {
        QueryWrapper<Rating> ratingQueryWrapper = new QueryWrapper<>();
        ratingQueryWrapper.eq("anime_id",id);
        return ratingMapper.selectList(ratingQueryWrapper);
    }

    @PostMapping("/ratings/insert")
    public String ratingInsert(@RequestBody Rating rating, HttpSession session) {
        int animeId = rating.getAnimeId();
        Object username = session.getAttribute("username");
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        rating.setUserId(user.getUserId());
        QueryWrapper<Rating> eq = new QueryWrapper<Rating>().eq("anime_id", animeId).eq("user_id", user.getUserId());
        if(ratingMapper.selectCount(eq)>0) {
            return ratingMapper.update(rating,eq)==1?"评论更新成功":"失败，请重试";
        }else {
            return ratingMapper.insert(rating)==1?"评论成功":"失败，请重新评论";
        }
    }

    @GetMapping("/ratings")
    public List<Rating> ratings(@RequestParam int page) {
        return ratingMapper.selectPage(new Page<Rating>(page,10), null).getRecords();
    }

    @PostMapping("/ratings/delete")
    public String ratingDelete(@RequestBody Rating rating) {
        return ratingMapper.deleteById(rating)>0?"删除成功":"删除失败";
    }

}
