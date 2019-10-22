package com.github.ttdyce.nhviewer.model.comic;

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

    public Comic(int id) {
        this.id = id;
    }

    public Comic(int id, String mid, Title title, int numOfPages, String[] pageTypes) {
        this.id = id;
        this.mid = mid;
        this.title = title;
        this.numOfPages = numOfPages;
        this.setPageTypes(pageTypes);
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

    public String[] getPageTypes() {
        String[] types = new String[numOfPages];
        Image[] pages = getImages().getPages();

        for (int i = 0; i < types.length; i++) {
            types[i] = pages[i].getType();
        }

        return types;
    }

    public void setPageTypes(String [] pageTypes) {
        if(getImages() == null)
            images = new Images();

        Images images = getImages();
        Image[] pages = images.getPages();
        Image thumbnail = images.getThumbnail();

        for (int i = 0; i < pageTypes.length; i++) {
            pages[i].type = pageTypes[i];
        }
        // TODO: 2019/9/28 Hardcoded thumbnail type, using first page of inner page type
        thumbnail.type = pageTypes[0];
    }

    /*Inner classes*/

    //Comic Title
    public static class Title {
        String english, japanese, pretty;

        public Title(String english) {
            this.english = english;
        }

        @NonNull
        @Override
        public String toString() {
            return english;
        }
    }

    public class Images {
        Image[] pages;
        Image cover;
        Image thumbnail;

        public Images() {
            pages = new Image[numOfPages];
            for (int i = 0; i < pages.length; i++)
                pages[i] = new Image();

            cover = new Image();
            thumbnail = new Image();
        }

        public Image[] getPages() {
            return pages;
        }

        public Image getCover() {
            return cover;
        }

        public Image getThumbnail() {
            return thumbnail;
        }
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

        public Image() {

        }

        public String getType() {
            return type;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
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
