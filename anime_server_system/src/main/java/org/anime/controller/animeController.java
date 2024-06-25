package org.anime.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.anime.mapper.animeMapper;
import org.anime.pojo.Anime;
import org.anime.utils.JaccardSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class animeController {

    @Autowired
    private animeMapper animeMapper;

    @GetMapping("/animes")
    private List<Anime> animes(int page) {
        return animeMapper.selectPage(new Page<>(page,10),null).getRecords();
    }


    @GetMapping("/animes/random")
    private List<Anime> animesRandom() {
        int randomNumber = (int) (Math.random() * 100);
        List<Anime> records = animeMapper.selectPage(new Page<>(randomNumber, 5), null).getRecords();
        return records;
    }

    @GetMapping("/animes/search")
    private List<Anime> searchAnime(String keyword) {
        QueryWrapper<Anime> liked = new QueryWrapper<Anime>().like("title", keyword);
        return animeMapper.selectList(liked);
    }

    @GetMapping("/animes/insert")
    private String insertAnime(@RequestBody Anime anime) {
        return animeMapper.insert(anime)>0?"添加成功":"失败，请重试";
    }

    @GetMapping("/animes/{id}")
    private Anime getAnimeById(@PathVariable int id) {
        return animeMapper.selectById(id);
    }

    @GetMapping("/animes/liked/{id}")
    private List<Anime> likedAnime(@PathVariable int id) {
        Anime anime1 = animeMapper.selectById(id);
        ArrayList<Anime> arrayList = new ArrayList<>();
        for(Anime anime : animeMapper.selectList(new QueryWrapper<Anime>().le("anime_id", 20000))) {
            if(JaccardSimilarity.calculateJaccardSimilarity(anime1.getTitle(), anime.getGenre())>0.1){
                arrayList.add(anime);
            }
            if(arrayList.size()>=3) {
                break;
            }
        }
        return arrayList;
    }

    @PostMapping("/anime/update")
    private String updateAnime(@RequestBody Anime anime) {
        if(animeMapper.selectCount(new QueryWrapper<Anime>().eq("anime_id",anime.getAnimeId()))>0){
            return animeMapper.updateById(anime)==1?"更新成功":"更新失败";
        }else {
            return animeMapper.insert(anime)>0?"添加成功":"添加失败";
        }

    }

    @PostMapping("/anime/delete")
    private String deleteAnime(@RequestBody Anime anime){
        return animeMapper.deleteById(anime)==1?"删除成功":"错误，请重试";
//        return animeMapper.selectById(anime.getAnimeId())!=null?"删除成功":"错误，请重试";
    }

}
