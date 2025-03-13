package org.anime.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("animes")
@Data
public class Anime {

    @TableId
    private int animeId;
    private String title;
    private String genre;
    private String episodes;
    private String type;
    private float rating;
}
