package personal.ttd.nhviewer.Model.api;

import java.util.Locale;

//for api package
class NHapi {
    public static final String userAgent = "NHBooks/Android Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.0.0 Mobile";

    public static String getComicInfoLinkById(String id) {
        return String.format(Locale.ENGLISH, "%s%s", Url.comicInfo, id);
    }

    public static String getSearchInfoLink(String query) {
        return String.format(Locale.ENGLISH, "%s%s%s%s", Url.searchInfo, Suffix.search, query, Suffix.page);
    }

    public static String getThumbLink(String mid, String type) {
        if (type.equals("j"))
            return String.format(Locale.ENGLISH, "%s%s%s.%s", Url.thumbInfo, mid, Suffix.thumb, "jpg");
        else
            return String.format(Locale.ENGLISH, "%s%s%s.%s", Url.thumbInfo, mid, Suffix.thumb, "png");
    }

    public static String getImageLinkByPage(String mid, String t, int i) {
        String pageLink;

        String type = "png";
        switch (t) {
            case "j":
                type = "jpg";
                break;
        }
        pageLink = String.format(Locale.ENGLISH, "%s/%s/%d.%s", Url.mSite, mid, i, type);

        return pageLink;
    }

    public class Url {
        public static final String site = "https://nhentai.net";
        public static final String mSite = "https://i.nhentai.net/galleries";
        public static final String comicInfo = "https://nhentai.net/api/gallery/";
        /*e.g. NHapi.getSearchInfoLink("Chinese") + page*/
        public static final String searchInfo = "https://nhentai.net/api/galleries"; // https://nhentai.net/api/galleries/search?query=chinese&page=1
        public static final String thumbInfo = "https://t.nhentai.net/galleries/";//e.g. https://t.nhentai.net/galleries/1234282/thumb.jpg
    }

    public class Suffix {
        public static final String search = "/search?query=";
        public static final String page = "&page=";
        public static final String thumb = "/thumb";
        public static final String sortPopular = "&sort=popular";
    }


}
