package github.ttdyce.nhviewer.Model.comic;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Comic implements Parcelable {

    public static final Creator<Comic> CREATOR = new Creator<Comic>() {
        @Override
        public Comic createFromParcel(Parcel in) {
            return new Comic(in);
        }

        @Override
        public Comic[] newArray(int size) {
            return new Comic[size];
        }
    };
    private String id;
    private String title;
    private String thumbLink;
    private int totalPage;
    private ArrayList<String> pages = new ArrayList<>();
    private String mid;
    private String pageTypes;
    private int seenPage;

    public Comic() {
    }

    protected Comic(Parcel in) {
        title = in.readString();
        thumbLink = in.readString();
        totalPage = in.readInt();
        id = in.readString();
        mid = in.readString();
        pages = in.createStringArrayList();
        pageTypes = in.readString();
        seenPage = in.readInt();
    }

    public Comic(String id, String title, String thumblink) {
        this.id = id;
        this.title = title;
        this.thumbLink = thumblink;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(thumbLink);
        dest.writeInt(totalPage);
        dest.writeString(id);
        dest.writeString(mid);
        dest.writeStringList(pages);
        dest.writeString(pageTypes);
        dest.writeInt(seenPage);
    }

    public void addPage(String str) {
        pages.add(str);
    }

    public void addAllPages(ArrayList<String> allPages) {
        pages.addAll(allPages);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public ArrayList<String> getPages() {
        return pages;
    }

    public void setPages(ArrayList<String> pages) {
        this.pages = pages;
    }

    public String getThumbLink() {
        return thumbLink;
    }

    public void setThumbLink(String thumbLink) {
        this.thumbLink = thumbLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getPageTypes() {
        return pageTypes;
    }

    public void setPageTypes(String pageTypes) {
        this.pageTypes = pageTypes;
    }

    public int getSeenPage() {
        return seenPage;
    }

    public void setSeenPage(int seenPage) {
        this.seenPage = seenPage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
