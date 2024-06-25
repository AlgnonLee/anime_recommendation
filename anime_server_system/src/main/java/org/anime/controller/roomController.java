package org.anime.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.anime.pojo.Anime;
import org.anime.pojo.Rating;
import org.anime.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
public class roomController {

    @Autowired
    private org.anime.mapper.ratingMapper ratingMapper;

    @Autowired
    private org.anime.mapper.userMapper userMapper;

    @Autowired org.anime.mapper.animeMapper animeMapper;

    @GetMapping("/roomList")
    public List<Integer> roomList(HttpSession session){
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", session.getAttribute("username")));
        List<Rating> ratings = ratingMapper.selectList(new QueryWrapper<Rating>().eq("user_id", user.getUserId()).ge("rating", 6));
        ArrayList<Integer> roomIds = new ArrayList<>();
        for (Rating rating : ratings) {
            roomIds.add(rating.getAnimeId());
        }
        return roomIds;
    }

    @GetMapping("/roomNames")
    public List<String> roomName(HttpSession session){
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", session.getAttribute("username")));
        List<Rating> ratings = ratingMapper.selectList(new QueryWrapper<Rating>().eq("user_id", user.getUserId()).ge("rating", 6));
        ArrayList<String> roomNames = new ArrayList<>();
        for (Rating rating : ratings) {
            roomNames.add(animeMapper.selectById(rating.getAnimeId()).getTitle());
        }
        return roomNames;
    }

}
