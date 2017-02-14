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

    @Column
    private String category = "";

    @Column
    private String medium = "";

    @Column
    private String date = "";

    @Column
    private String size = "";

    @Column
    private String imgLarge = "";

    @Column
    private String imgZoom = "";

    public Artwork() {
    }

    public Artwork(String title){
        super(title);
    }

    public String getArtsyArtworkId() {
        return artsyArtworkId;
    }

    public void setArtsyArtworkId(String artsyArtworkId) {
        this.artsyArtworkId = artsyArtworkId;
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

    public void setUrl() {
        super.setUrl("/artwork?artworkId="+this.getId());
    }

    @Override
    public String toString() {
        return "Artwork{" +
                "artsyArtworkId='" + artsyArtworkId + '\'' +
                ", title='" + this.getTitle() + '\'' +
                ", category='" + category + '\'' +
                ", medium='" + medium + '\'' +
                ", date='" + date + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof Artwork)){
            return false;
        }
        Artwork artwork = (Artwork) obj;

        //Only testing one attribute for Unit Tests
        return artwork.getTitle().equals(this.getTitle());
    }
}
