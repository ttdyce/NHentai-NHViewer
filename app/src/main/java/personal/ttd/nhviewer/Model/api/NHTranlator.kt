package personal.ttd.nhviewer.Model.api

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import personal.ttd.nhviewer.Model.DebugTag.TAG
import personal.ttd.nhviewer.Model.Saver.file.FeedReaderContract
import personal.ttd.nhviewer.Model.Saver.file.Storage
import personal.ttd.nhviewer.Model.ListReturnCallBack
import personal.ttd.nhviewer.Model.api.NHapi.userAgent
import personal.ttd.nhviewer.Model.comic.Comic
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.dropLastWhile
import kotlin.collections.set
import kotlin.collections.toTypedArray

class NHTranlator {
    //static methods
    companion object {
        val suffixSortPopular = NHapi.Suffix.sortPopular
        val baseUrl = "https://nhentai.net/"
        val baseUrlLanguage = "https://nhentai.net/language/"
        val baseUrlChinese = "https://nhentai.net/language/chinese/"
        val comicBaseUrl = "https://nhentai.net/g/"
        val mediaBaseUrl = "https://i.nhentai.net/galleries/"//https://i.nhentai.net/galleries/1347646/1.jpg
        val searchBaseUrl = "https://nhentai.net/search/?q="//add &sort=popular for sorting
        private val pagePrefix = "?page="
        private val pagePrefixSearch = "&page="

        fun getPages(mid: String, types: String, totalPage: Int): ArrayList<String> {
            val pages: ArrayList<String> = ArrayList()

            for (i in 1 until totalPage) {
                pages.add(NHapi.getImageLinkByPage(mid, types[i].toString(), i))
            }

            return pages
        }

        fun getThumbLink(mid: String, type: String): String {
            val t = type.get(0).toString()

            return NHapi.getThumbLink(mid, t)
        }

        fun getComicInfoLink(id: String): String {
            return NHapi.getComicInfoLinkById(id)
        }

        private fun getComicsByDocument(doc: Document): java.util.ArrayList<Comic> {
            val comics = java.util.ArrayList<Comic>()

            val galleries = doc.getElementsByClass("gallery")

            var count = 1
            for (gallery in galleries) {

                val title = gallery.getElementsByTag("div").get(0).text()
                val thumbLink = gallery.getElementsByTag("img").attr("data-src")
                val id = gallery.getElementsByTag("a").attr("href").split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[2]
                var mid = ""
                if (thumbLink != "")///todo 20190602: some comic may not have "data-src", and resulted in empty thumbnail and mid
                    mid = thumbLink.split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[thumbLink.split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray().size - 2]
                //int totalPage = gallery.getElementById("thumbnail-container").childNodeSize();

                val comic = Comic()
                comic.title = title
                comic.thumbLink = thumbLink
                comic.id = id
                comic.mid = mid
                //comic.setTotalPage(totalPage);

                //setted comic properties
                Log.e(TAG, String.format("Finished %d gallery, name=%s", count++, title))
                //Log.i(TAG, "setList: totalPage: " + totalPage);

                comics.add(comic)

            }
            return comics
        }

        private fun getComicByDocument(doc: Document): Comic {
            val c = Comic()

            val thumbContainer = doc.getElementById("thumbnail-container")
            val thumbLink1 = thumbContainer.child(0).getElementsByTag("img")[0].attr("data-src")
            val mid = thumbLink1.split("/")[thumbLink1.split("/").size - 2]
            val totalPage = thumbContainer.children().size
            var pageTypes = ""
            //Log.e("NHMyApi", "totalpage = " + totalPage)
            //Log.e("NHMyApi", "thumbLink1 = " + thumbLink1 + " mid  = " + mid)

            for (element: Element in thumbContainer.children()) {
                val pageType: Char

                val thumbLink = element.getElementsByTag("img")[0].attr("data-src")
                val imgType = thumbLink.split(".")[thumbLink.split(".").size - 1]

                pageType = imgType[0]
                pageTypes += pageType
            }

            c.mid = mid
            c.totalPage = totalPage
            c.pageTypes = pageTypes

            for (i in 1..totalPage) {
                var imgSuffix = ".jpg"

                when (c.pageTypes[i - 1]) {
                    'j' -> imgSuffix = ".jpg"
                    'p' -> imgSuffix = ".png"
                }


                val page = mediaBaseUrl + mid + "/" + i + imgSuffix
                //Log.e("NHMyApi", "page = " + page)
                c.pages.add(page)
            }


//            var count = 1
//            for (gallery in galleries) {
//
//                val title = gallery.getElementsByTag("div").get(0).text()
//                val thumbLink = gallery.getElementsByTag("img").attr("data-src")
//                val id = gallery.getElementsByTag("a").attr("href").split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[2]
//                val mid = thumbLink.split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[thumbLink.split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray().size - 2]
//                //int totalPage = gallery.getElementById("thumbnail-container").childNodeSize();
//
//                val comic = Comic()
//                comic.title = title
//                comic.thumbLink = thumbLink
//                comic.id = id
//                comic.mid = mid
//                //comic.setTotalPage(totalPage);
//
//                //setted comic properties
//                Log.e(TAG, String.format("Finished %d gallery, name=%s", count++, title))
//                //Log.i(TAG, "setList: totalPage: " + totalPage);
//
//            }
            return c
        }

        //call get comicList by document
        fun getComicsBySite(baseUrl: String, page: String, context: Context, callback: ListReturnCallBack) {

            val prefix: String

            val queue = Volley.newRequestQueue(context)
            var doc: Document
            val comics = ArrayList<Comic>()

            if (baseUrl.contains(searchBaseUrl))
                prefix = pagePrefixSearch
            else
                prefix = pagePrefix

            Log.i("NHT", "loaded url: " + baseUrl + prefix + page)

            val documentRequest = object : StringRequest( //
                    Request.Method.GET, //
                    baseUrl + prefix + page, //
                    { response ->
                        doc = Jsoup.parse(response)

                        comics.addAll(getComicsByDocument(doc))
                        callback.onResponse(comics)
                    }, //
                    { error ->
                        // Error handling
                        error.printStackTrace()
                    }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["User-Agent"] = userAgent
                    return headers
                }
            } //

            // Add the request to the queue...
            queue.add(documentRequest)

            // ... and wait for the document.
            // NOTE: Be aware of user experience here. We don't want to freeze the app...
            queue.addRequestFinishedListener(RequestQueue.RequestFinishedListener<Any> {
                Log.i(TAG, "onRequestFinished: Finished")
            })


        }

        fun getComicById(comicid: String, context: Context, callback: ListReturnCallBack) {

            val queue = Volley.newRequestQueue(context)
            var doc: Document
            val comics = ArrayList<Comic>()

            val documentRequest = object : StringRequest( //
                    Request.Method.GET, //
                    comicBaseUrl + comicid, //
                    { response ->
                        //Log.i(TAG, "onResponse: " + response);
                        doc = Jsoup.parse(response)

                        //Log.e("nhcomicsize", "entering comicList loop, comicList size:" + comicList.size)
                        comics.add(getComicByDocument(doc))
                        callback.onResponse(comics)
                    }, //
                    { error ->
                        // Error handling
                        error.printStackTrace()
                    }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["User-Agent"] = userAgent
                    return headers
                }
            } //

            // Add the request to the queue...
            queue.add(documentRequest)

            // ... and wait for the document.
            // NOTE: Be aware of user experience here. We don't want to freeze the app...
            queue.addRequestFinishedListener(RequestQueue.RequestFinishedListener<Any> {
                Log.i(TAG, "onRequestFinished: Finished")
            })
        }


        fun getComicByMid(mid: String, applicationContext: Context): Comic {
            return Comic();
        }


        /*
        * putting everything needed into database
        */
        fun addToCollection(context: Context, c: Comic) {
            Storage.insertTableCollection(context, c.id)
            Storage.insertTableComic(context, c)
            Storage.insertTableInnerPage(context, c)
        }


        /*
        * History part
        * */
        fun addToHistory(context: Context, c: Comic, p: Int) {
            Storage.insertTableHistory(context, c.id, p)

        }

        fun updateHistory(context: Context, c: Comic, p: Int) {
            Storage.updateTableHistory(context, c.id, p)

        }

        fun getHistoryComicList(context: Context): List<Comic> {
            return Storage.getAllRows(context, FeedReaderContract.FeedEntry.TABLE_HISTORY, FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATE_TIME)
        }


        ///TODO API unusable -20190115
        /*
            using comicid
            => comicInfoLink
            => request( comicInfoLink )
            <= JSON response
            => comic : Comic > return
         */
        private fun setComicById(comic: Comic, id: String, context: Context) {
            val comicInfoLink = getComicInfoLink(id)
            val queue = Volley.newRequestQueue(context)

            //request for nh's json
            val jsonObjReq = object : JsonObjectRequest(Request.Method.GET,
                    comicInfoLink, null,
                    { response ->
                        //fetching comic pages
                        var mid: String
                        var type: String
                        var totalPage = 0
                        try {
                            mid = response.getString("media_id")
                            totalPage = response.getInt("num_pages")

                            Log.i(TAG, String.format("onResponse: mid: %s, totalPage: %s", mid, totalPage))
                            comic.mid = mid
                            comic.totalPage = totalPage
                            //Log.i(TAG, String.format("onResponse: mid: %s, totalPage: %s, type: %s", mid, totalPage, type))
                            for (i in 1..totalPage) {
                                type = response.getJSONObject("images").getJSONArray("pages").getJSONObject(i - 1).getString("t")
                                comic.addPage(NHapi.getImageLinkByPage(mid, type, i))
                                Log.i(TAG, String.format("onResponse: mid: %s, totalPage: %s, type: %s", mid, totalPage, type))
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }


                    }, { error ->
                Log.i(TAG, String.format("Error: fetching comic pages"))

            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["User-Agent"] = NHapi.userAgent
                    return headers
                }
            }
            // Add above request to queue...
            queue.add<JSONObject>(jsonObjReq)
            queue.addRequestFinishedListener<Any> { request ->
                Log.i(TAG, "onRequestFinished: Finished")

            }
        }

    }
}