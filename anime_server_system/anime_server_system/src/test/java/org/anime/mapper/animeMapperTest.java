package org.anime.mapper;

import org.anime.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
@SpringBootTest
class animeMapperTest {
    @Autowired
    private animeMapper animeMapper;
    @Autowired
    private userMapper userMapper;

    @Test
    public void test(){
//        QueryWrapper<anime> animeQueryWrapper = new QueryWrapper<>();
//        animeQueryWrapper.eq("id",1);
//        animeMapper.selectList(animeQueryWrapper);
//        System.out.println(animeMapper.selectById(1));
        User user = new User();
        user.setUserId(0);
        user.setAge(21);
        user.setGender("male");
        user.setUsername("algnonlee");
        user.setPasswd("bzjxjy11");
        user.setNickname("knightofhanoi");
//        System.out.println(userMapper.insert(user));
        System.out.println(userMapper.selectById(1));;
    }
}