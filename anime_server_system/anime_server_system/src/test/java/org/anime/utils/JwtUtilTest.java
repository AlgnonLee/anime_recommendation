package org.anime.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//@Test
public class JwtUtilTest {


    @Test
    public void test(){
        System.out.println(JwtUtil.createJWT("abc","def",null));
    }


}