package org.anime.pojo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ratings")
public class Rating {

    @TableId
    private int ratingId;
    private int userId;
    private int animeId;
    private int rating;
    private String comment;
}
