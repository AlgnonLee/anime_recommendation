package org.anime.pojo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("users")
@Data
public class User {

    @TableId
    private int userId;
    private String username;
    private String passwd;
    private String nickname;
    private String gender;
    private int age;
}
