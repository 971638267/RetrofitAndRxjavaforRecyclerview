package com.gan.base.net.requestbean;

import com.gan.base.net.resultbean.ImagesBean;
import com.gan.base.net.resultbean.PersonBean;
import com.gan.base.net.resultbean.RatingBean;

import java.util.List;

/**
 * Created by gan on 2017/5/18.
 */

public class MovieInfo {

    public RatingBean rating;
    public String title;
    public int collect_count;
    public String original_title;
    public String subtype;
    public String year;
    public ImagesBean images;
    public String alt;
    public String id;
    public List<String> genres;
    public List<PersonBean> casts;
    public List<PersonBean> directors;

    public MovieInfo() {
    }

}
