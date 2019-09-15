package com.github.ttdyce.nhviewer.Model.Comic;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Comic {
    private int id;
    @SerializedName("num_pages")
    private int numOfPages;

    @SerializedName("num_favorites")
    private int numOfFavorites;

    @SerializedName("media_id")
    private String mid;

    //included 3 title: eng, jp, pretty
    private Title title;
    //included 3 images: pages, cover, thumbnail
    private Images images;

    @SerializedName("upload_date")
    private int uploadDate;
    private Tag[] tags;

    public Comic() {
    }

    public int getId() {
        return id;
    }

    public int getNumOfPages() {
        return numOfPages;
    }

    public int getNumOfFavorites() {
        return numOfFavorites;
    }

    public String getMid() {
        return mid;
    }

    public Title getTitle() {
        return title;
    }

    public Images getImages() {
        return images;
    }

    public int getUploadDate() {
        return uploadDate;
    }

    public Tag[] getTags() {
        return tags;
    }

    /*Inner classes*/

    //Comic Title
    public class Title {
        String english, japanese, pretty;

        @NonNull
        @Override
        public String toString() {
            return "\nPretty: " + pretty;
        }
    }

    public class Images {
        Image[] pages;
        Image cover;
        Image thumbnail;
    }

    //Comic Image
    public class Image {
        @SerializedName("t")
        String type;
        @SerializedName("w")
        int width;
        @SerializedName("h")
        int height;

        public Image(String type, int width, int height) {
            this.type = type;
            this.width = width;
            this.height = height;
        }
    }

    //Comic Tag
    public class Tag {
        int id, count;
        String type, name, url;

        public Tag(int id, int count, String type, String name, String url) {
            this.id = id;
            this.count = count;
            this.type = type;
            this.name = name;
            this.url = url;
        }
    }
}
