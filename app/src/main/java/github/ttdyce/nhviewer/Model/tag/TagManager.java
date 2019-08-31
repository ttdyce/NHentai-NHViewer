package github.ttdyce.nhviewer.Model.tag;

import android.content.SharedPreferences;

import java.util.LinkedHashSet;
import java.util.Set;

import github.ttdyce.nhviewer.Controller.fragment.TagFragment;

public class TagManager {

    public static Set<String> getTagAll(SharedPreferences pref) {
        /*has to be a new object, otherwise unable to modify String set*/
        return new LinkedHashSet<>(pref.getStringSet(TagFragment.KEY_PREF_TAG, new LinkedHashSet<>()));
    }

    public static boolean addTag(String tagName, SharedPreferences pref) {
        if(tagName.length() < 1)
            return false;

        Set<String> tags = getTagAll(pref);
        SharedPreferences.Editor editor = pref.edit();

        tags.add(tagName);
        editor.putStringSet(TagFragment.KEY_PREF_TAG, tags);
        editor.apply();
        return true;
    }

    public static Set<String> getTagRequired(SharedPreferences pref) {
        Set<String> tags = getTagAll(pref), requiredTags = new LinkedHashSet<>();

        for (String t : tags) {
            if (t.length() > 0 && t.charAt(0) != '-')
                requiredTags.add(t);
        }

        return requiredTags;
    }

    public static Set<String> getTagFiltered(SharedPreferences pref) {
        Set<String> tags = getTagAll(pref), filteredTags = new LinkedHashSet<>();
        for (String t : tags) {
            if (t.length() > 0 && t.charAt(0) == '-')
                filteredTags.add(t);
        }

        return filteredTags;
    }

    public static Boolean removeTag(String tag, SharedPreferences pref) {
        Set<String> tags = getTagAll(pref);
        SharedPreferences.Editor editor = pref.edit();
        Boolean removed = tags.remove(tag);

        editor.putStringSet(TagFragment.KEY_PREF_TAG, tags);
        editor.apply();

        return removed;
    }
}
