package org.anime.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.anime.mapper.ratingMapper;
import org.anime.pojo.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ratingController {

    @Autowired
    private ratingMapper ratingMapper;


    @PostMapping("/ratings/{id}")
    public List<Rating> rating(@PathVariable int id) {
        QueryWrapper<Rating> ratingQueryWrapper = new QueryWrapper<>();
        ratingQueryWrapper.eq("rating_id",id);
        return ratingMapper.selectList(ratingQueryWrapper);
    }

    @PostMapping("/ratings/insert")
    public String ratingInsert(@RequestBody Rating rating) {
        int animeId = rating.getAnimeId();
        int userId = rating.getUserId();
        QueryWrapper<Rating> eq = new QueryWrapper<Rating>().eq("anime_id", animeId).eq("user_id", userId);
        if(ratingMapper.selectCount(eq)>0) {
            return ratingMapper.update(rating,eq)==1?"评论更新成功":"失败，请重试";
        }else {
            return ratingMapper.insert(rating)==1?"评论成功":"失败，请重新评论";
        }
    }


}
