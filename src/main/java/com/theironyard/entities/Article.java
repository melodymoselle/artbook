package com.theironyard.entities;

import javax.persistence.*;

@Entity
@DiscriminatorValue("article")
public class Article extends Item{
//    @Column
//    private String googleCacheId;
    @Column
    private String url;
    @Column
    private String title;
    @Column
    private String snippet;
    @Column
    private String imgThumb;
    @Column
    private String imgLarge;

    public Article() {
    }

    public static class Builder {
        // Required parameters
        private String url;
        private String title;
        private String snippet;
        // Optional parameters
        private String imgThumb = "";
        private String imgLarge = "";

        public Builder url(String val){
            url = val; return this;
        }
        public Builder title(String val){
            title = val; return this;
        }
        public Builder snippet(String val){
            snippet = val; return this;
        }
        public Builder imgThumb(String val){
            imgThumb = val; return this;
        }
        public Builder imgLarge(String val){
            imgLarge = val; return this;
        }

        public Article build(){
            return new Article(this);
        }
    }

    private Article(Builder builder){
        url = builder.url;
        title = builder.title;
        snippet = builder.snippet;
        imgThumb = builder.imgThumb;
        imgLarge = builder.imgLarge;
    }

//    public String getGoogleCacheId() {
//        return googleCacheId;
//    }
//
//    public void setGoogleCacheId(String googleCacheId) {
//        this.googleCacheId = googleCacheId;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getImgThumb() {
        return imgThumb;
    }

    public void setImgThumb(String imgThumb) {
        this.imgThumb = imgThumb;
    }

    public String getImgLarge() {
        return imgLarge;
    }

    public void setImgLarge(String imgLarge) {
        this.imgLarge = imgLarge;
    }

}
