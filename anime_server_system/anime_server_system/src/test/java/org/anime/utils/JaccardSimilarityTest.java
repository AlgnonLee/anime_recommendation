package org.anime.utils;

import org.anime.mapper.animeMapper;
import org.anime.pojo.Anime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class JaccardSimilarityTest {

    @Autowired
    private animeMapper animeMapper;

    @Test
    public void testJaccardSimilarity() {

//        System.out.println(JaccardSimilarity.calculateJaccardSimilarity(animeMapper.selectById(1).getGenre(),animeMapper.selectById(1513).getType()));

    }
}