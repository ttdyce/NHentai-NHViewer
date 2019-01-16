package personal.ttd.nhviewer.api

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Adapter
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import personal.ttd.nhviewer.DebugTag.TAG
import personal.ttd.nhviewer.api.NHapi.userAgent
import personal.ttd.nhviewer.comic.Comic
import personal.ttd.nhviewer.file.FeedReaderContract
import personal.ttd.nhviewer.file.Storage
import java.util.HashMap

class MyApi {
    //static methods
    companion object {
        fun getPages(mid:String, types:String, totalPage:Int):ArrayList<String>{
            val pages:ArrayList<String> = ArrayList()

            for(i in 1 until totalPage){
                pages.add(NHapi.getPictureLinkByPage(mid, types[i].toString(), i))
            }

            return pages
        }

        fun getThumbLink(mid:String, type:String):String{
            val t = type.get(0).toString()

            return NHapi.getThumbLink(mid, t);
        }

        fun getComicInfoLink(id:String):String{
            return NHapi.getComicInfoLinkById(id)
        }

        ///TODO API unusable -20190115
        /*
            using comicid
            => comicInfoLink
            => request( comicInfoLink )
            <= JSON response
            => comic : Comic > return
         */
        private fun setComicById(comic:Comic, id: String, context: Context) {
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
                                comic.addPage(NHapi.getPictureLinkByPage(mid, type, i))
                                Log.i(TAG, String.format("onResponse: mid: %s, totalPage: %s, type: %s", mid, totalPage, type))
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }


                    }, { error -> Log.i(TAG, String.format("Error: fetching comic pages"))

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

        fun getHistory(context: Context) : List<Comic>{
            return Storage.getAllRows(context, FeedReaderContract.FeedEntry.TABLE_HISTORY, FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATE_TIME)
        }

        fun downloadComic(id: Int) {

        }

        fun getMainPageComics(page:Int):ArrayList<Comic> {
            var comics:ArrayList<Comic> = ArrayList()

            var url = NHapi.getSearchInfoLink("Chinese") + page


            return comics
        }

        //only for transfering data
        fun updateFromJson(context: Context, id:Int) {
            var c: Comic
            c = Comic()

            addToCollection(context, c)
        }

    }
}