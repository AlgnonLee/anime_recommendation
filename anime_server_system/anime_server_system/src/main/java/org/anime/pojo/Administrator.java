package org.anime.pojo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("administrators")
public class Administrator {

    @TableId
    private int adminId;
    private String adminname;
    private String passwd;
}
