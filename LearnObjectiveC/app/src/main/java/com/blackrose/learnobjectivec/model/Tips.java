package com.blackrose.learnobjectivec.model;

/**
 * Created by mrnoo on 20/04/2016.
 */
public class Tips {
    private String image;
    private String text;
    private String title;

    public Tips() {
    }

    public Tips(String image, String text, String title) {
        this.image = image;
        this.text = text;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Tips{" +
                "image='" + image + '\'' +
                ", text='" + text + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
