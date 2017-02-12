package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@DiscriminatorValue("artwork")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artwork extends Item{

    @Column(unique = true)
    @JsonProperty("id")
    private String artsyArtworkId;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column
    private String title;

    @Column
    private String category;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column
    private String medium;

    @Column
    private String date;

    @Column
    private String size;

    @Column
    private String imgThumb;

    @Column
    private String imgLarge;

    @Column
    private String imgZoom;

    public Artwork() {
    }

    public Artwork(String title) {
        this.title = title;
    }

    public String getArtsyArtworkId() {
        return artsyArtworkId;
    }

    public void setArtsyArtworkId(String artsyArtworkId) {
        this.artsyArtworkId = artsyArtworkId;
    }

    public String getTitle() {
        if (title.length() > 30){
            return title.substring(0, 30) + "...";
        }
        return title;
    }

    public void setTitle(String title) {
        if (title.length() > 254) {
            title = title.substring(0, 254);
        }
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category.length() > 254){
            category = category.substring(0, 254);
        }
        this.category = category;
    }

    public String getMedium() {
        if (medium.length() > 30){
            return medium.substring(0, 30) + "...";
        }
        return medium;
    }

    public void setMedium(String medium) {
        if (medium.length()>254) {
            medium = medium.substring(0, 254);
        }
        this.medium = medium;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public String getImgZoom() {
        return imgZoom;
    }

    public void setImgZoom(String imgZoom) {
        this.imgZoom = imgZoom;
    }

    @Override
    public String toString() {
        return "Artwork{" +
                "artsyArtworkId='" + artsyArtworkId + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", medium='" + medium + '\'' +
                ", date='" + date + '\'' +
                ", size='" + size + '\'' +
                ", imgThumb='" + imgThumb + '\'' +
                ", imgLarge='" + imgLarge + '\'' +
                ", imgZoom='" + imgZoom + '\'' +
                '}';
    }
}
