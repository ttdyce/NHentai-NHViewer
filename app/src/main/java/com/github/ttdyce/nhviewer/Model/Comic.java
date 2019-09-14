package com.github.ttdyce.nhviewer.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Comic {
    private int id, numOfPages, numOfFavorites;
    private String mid;
    //included 3 title: eng, jp, pretty
    private String[] titles = new String[3];
    private static final String[] TITLE_INDEXES = {"english", "japanese", "pretty"};

    private Image[] pages;
    private Image cover, thumbnail;
    private int uploadDate;
    private Tag[] tags;

    public Comic() {
    }

    public Comic(JSONObject o) throws JSONException {
        setId(o.getInt("id"));
        setMid(o.getString("media_id"));
        setNumOfPages(o.getInt("num_pages"));
        setUploadDate(o.getInt("upload_date"));
        //3 types of title
        String[] titles = new String[3];
        for (int i = 0; i < 3; i++)
            titles[i] = o.getJSONObject("title").getString(TITLE_INDEXES[i]);
        setTitles(titles);
        //set pages, cover, thumbnail image
        JSONObject imageObj = o.getJSONObject("images");
        //pages
        JSONArray pArr = imageObj.getJSONArray("pages");
        Image[] pages = new Image[pArr.length()];
        for (int i = 0; i < pArr.length(); i++)
        {
            JSONObject pObj = pArr.getJSONObject(i);
            pages[i] = new Image(pObj.getString("t"), pObj.getInt("w"), pObj.getInt("h"));
        }
        setPages(pages);
        //cover
        JSONObject cObj = imageObj.getJSONObject("cover");
        setCover(new Image(cObj.getString("t"), cObj.getInt("w"), cObj.getInt("h")));
        //thumbnail
        JSONObject tObj = imageObj.getJSONObject("thumbnail");
        setThumbnail(new Image(tObj.getString("t"), tObj.getInt("w"), tObj.getInt("h")));

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumOfPages() {
        return numOfPages;
    }

    public void setNumOfPages(int numOfPages) {
        this.numOfPages = numOfPages;
    }

    public int getNumOfFavorites() {
        return numOfFavorites;
    }

    public void setNumOfFavorites(int numOfFavorites) {
        this.numOfFavorites = numOfFavorites;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String[] getTitles() {
        return titles;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public Image[] getPages() {
        return pages;
    }

    public void setPages(Image[] pages) {
        this.pages = pages;
    }

    public Image getCover() {
        return cover;
    }

    public void setCover(Image cover) {
        this.cover = cover;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(int uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Tag[] getTags() {
        return tags;
    }

    public void setTags(Tag[] tags) {
        this.tags = tags;
    }

    //Comic Image
    public class Image {
        String type;
        int width, height;

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
