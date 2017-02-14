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

    @Override
    public void setUrl(String videoId) {
        this.url = "https://www.youtube.com/watch?v=" + url;
    }
}
