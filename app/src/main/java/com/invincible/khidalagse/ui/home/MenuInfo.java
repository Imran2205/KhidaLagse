package com.invincible.khidalagse.ui.home;

public class MenuInfo {
    public String name;
    public int price;
    public float ratings;
    public String image_link;

    public MenuInfo(){

    }

    public MenuInfo(String name, int price, float ratings, String image_link){
        this.name = name;
        this.price = price;
        this.ratings = ratings;
        this.image_link = image_link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getRatings() {
        return ratings;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }
}
