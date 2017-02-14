package com.theironyard.entities;

import javax.persistence.*;

@Entity
@DiscriminatorValue("video")
public class Video extends Item{
    @Column
    private String videoId;
    @Column
    private String channelTitle;
    @Column
    private String title;
    @Column
    private String snippet;
    @Column
    private String imgThumb;
    @Column
    private String url;

    public Video() {
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
        this.setUrl(videoId);
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    @Override
    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getImgThumb() {
        return imgThumb;
    }

    public void setImgThumb(String imgThumb) {
        this.imgThumb = imgThumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String videoId) {
        this.url = "https://www.youtube.com/watch?v=" + url;
    }
}
