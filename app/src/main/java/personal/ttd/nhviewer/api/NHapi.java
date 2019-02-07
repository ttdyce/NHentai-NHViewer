package personal.ttd.nhviewer.api;

import android.annotation.SuppressLint;

public class NHapi {
    public static final String userAgent = "NHBooks/Android Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.0.0 Mobile";

    public static final String siteUrl = "https://nhentai.net";
    public static final String mSiteUrl = "https://i.nhentai.net/galleries";
    public static final String comicInfoUrl = "https://nhentai.net/api/gallery/";

    /*e.g. NHapi.getSearchInfoLink("Chinese") + page*/
    public static final String searchInfoUrl = "https://nhentai.net/api/galleries"; // https://nhentai.net/api/galleries/search?query=chinese&page=1
    public static final String thumbInfoUrl = "https://t.nhentai.net/galleries/";//e.g. https://t.nhentai.net/galleries/1234282/thumb.jpg

    public static final String apiSearch = "/search?query=";
    public static final String apiPage = "&page=";
    public static final String apithumb = "/thumb";


    public static String getComicInfoLinkById(String id) {
        return String.format("%s%s", comicInfoUrl, id);
    }

    @SuppressLint("DefaultLocale")
    public static String getSearchInfoLink(String query) {
        return String.format("%s%s%s%s", searchInfoUrl, apiSearch, query, apiPage);
    }

    @SuppressLint("DefaultLocale")
    public static String getThumbLink(String mid, String type) {
        if(type.equals("j"))
        return String.format("%s%s%s.%s", thumbInfoUrl, mid, apithumb, "jpg");
        else
        return String.format("%s%s%s.%s", thumbInfoUrl, mid, apithumb, "png");
    }



    @SuppressLint("DefaultLocale")
    public static String getImageLinkByPage(String mid, String t, int i) {
        String pageLink;

        String type = "png";
        switch (t) {
            case "j":
                type = "jpg";
                break;
        }
        pageLink = String.format("%s/%s/%d.%s", mSiteUrl, mid, i, type);

        return pageLink;
    }


}
