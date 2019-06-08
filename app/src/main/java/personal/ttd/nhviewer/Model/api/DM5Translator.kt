package personal.ttd.nhviewer.Model.api

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import personal.ttd.nhviewer.Model.DebugTag
import personal.ttd.nhviewer.Model.ListReturnCallBack
import personal.ttd.nhviewer.Model.comic.Comic
import java.util.HashMap

class DM5Translator {
    companion object {
        internal val BASE_URL = "http://www.dm5.com/manhua-rank/?t=2"


        private fun getComicsByDocument(doc: Document): java.util.ArrayList<Comic> {
            val comics = java.util.ArrayList<Comic>()

            val mangaList = doc.getElementsByClass("mh-item horizontal")

            var count = 1
            for (manga in mangaList) {

                val title = manga.getElementsByClass("title").get(0).getElementsByTag("a").text()
                val styleAttr = manga.getElementsByClass("mh-cover ").get(0).attr("style")
                val thumbLink = styleAttr.substring(styleAttr.indexOf("http://"), styleAttr.indexOf(")"))
                val id = manga.getElementsByClass("title").get(0).getElementsByTag("a").attr("href")

                Log.e("Comic data", "title=%s, thumblink=%s, id=%s".format(title, thumbLink, id));

                val comic = Comic()
                comic.title = title
                comic.thumbLink = thumbLink
                comic.id = id
                //comic.setTotalPage(totalPage);

                //setted comic properties
                Log.e(DebugTag.TAG, String.format("Finished %d gallery, name=%s", count++, title))
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


                val page = NHTranlator.mediaBaseUrl + mid + "/" + i + imgSuffix
                //Log.e("NHMyApi", "page = " + page)
                c.pages.add(page)
            }

            return c
        }

        //call get comicList by document
        fun getComics(context: Context, callback: ListReturnCallBack) {

            val queue = Volley.newRequestQueue(context)
            var doc: Document
            val comics = ArrayList<Comic>()

            val documentRequest = object : StringRequest( //
                    Request.Method.GET, //
                    BASE_URL, //
                    { response ->
                        doc = Jsoup.parse(response)

                        comics.addAll(getComicsByDocument(doc))
                        callback.onResponse(comics)
                    }, //
                    { error ->
                        // Error handling
                        println("Houston we have a problem ... !")
                        error.printStackTrace()
                    }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["User-Agent"] = NHapi.userAgent
                    return headers
                }
            } //

            documentRequest.retryPolicy = DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            // Add the request to the queue...
            queue.add(documentRequest)

            // ... and wait for the document.
            // NOTE: Be aware of user experience here. We don't want to freeze the app...
            queue.addRequestFinishedListener(RequestQueue.RequestFinishedListener<Any> {
                Log.i(DebugTag.TAG, "onRequestFinished: Finished")
            })


        }
    }
}