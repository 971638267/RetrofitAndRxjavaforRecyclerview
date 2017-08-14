package com.gan.base.banner;

/**
 * Created by Administrator on 2015/8/17.
 */
public class BannerData {

    private int Id;
    private String Title;
    private String url;
    private String Image;
    private int PositionId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //region getter and setter
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getPositionId() {
        return PositionId;
    }

    public void setPositionId(int positionId) {
        PositionId = positionId;
    }
    //endregion
}
