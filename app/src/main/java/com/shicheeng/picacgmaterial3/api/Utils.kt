package com.shicheeng.picacgmaterial3.api

import android.annotation.SuppressLint
import androidx.preference.PreferenceManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.MyApp
import okhttp3.CacheControl
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * 灵感来自
 * see [https://github.com/tachiyomiorg/tachiyomi-extensions/tree/master/src/zh/picacomic]
 */
class Utils {

    private val baseUrl = "https://picaapi.picacomic.com"
    private val DEFAULT_CACHE_CONTROL = CacheControl.Builder()
        .maxAge(3, TimeUnit.SECONDS)
        .build()

    private val manager by lazy {
        PreferenceManager.getDefaultSharedPreferences(MyApp.contextBase)
    }

    private val basicHeaders = mapOf(
        "api-key" to "C69BAF41DA5ABD1FFEDC6D2FEA56B",
        "app-channel" to manager.getString("APP_CHANNEL", "2")!!,
        "app-version" to "2.2.1.3.3.4",
        "app-uuid" to "defaultUuid",
        "app-platform" to "android",
        "app-build-version" to "45",
        "User-Agent" to "okhttp/3.8.1",
        "accept" to "application/vnd.picacomic.com.v1+json",
        "image-quality" to manager.getString("IMAGE_QUALITY", "original")!!,
        "Content-Type" to "application/json; charset=UTF-8", // must be exactly matched!
    )


    /**
     * 0 - 默认
     * 1 - 新到旧
     * 2 - 旧到新
     * 3 - 最多爱心
     * 4 - 最多指名
     */
    val order = mapOf(
        0 to "ua",
        1 to "dd",
        2 to "da",
        3 to "ld",
        4 to "vd"
    )

    enum class RankKey {
        H24,
        D7,
        D30
    }


    val urlOnRank = mapOf(
        Pair(RankKey.H24, "/comics/leaderboard?tt=H24&ct=VC"),
        Pair(RankKey.D7, "/comics/leaderboard?tt=D7&ct=VC"),
        Pair(RankKey.D30, "/comics/leaderboard?tt=D30&ct=VC"),
    )


    private fun encrypt(url: String, time: Long, method: String, nonce: String): String {
        val hmacSha256Key = Common.secretKey
        val apiKey = basicHeaders["api-key"]
        val path = url.substringAfter("$baseUrl/")
        val raw = "$path$time$nonce${method}$apiKey".lowercase(Locale.ROOT)
        return hmacSHA256(hmacSha256Key, raw).convertToString()
    }

    private fun picaHeaders(url: String, method: String = "GET", token: String? = null): Headers {
        val time = System.currentTimeMillis() / 1000
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val nonce = (1..32).map { allowedChars.random() }.joinToString("")
        val signature = encrypt(url, time, method, nonce)

        return basicHeaders.toMutableMap().apply {
            put("nonce", nonce)
            put("time", time.toString())
            put("signature", signature)
            if (!url.endsWith("/auth/sign-in")) {
                put("authorization", token!!)   // avoid recursive call
            }
        }.toHeaders()
    }

    /**
     * 获取漫画信息
     *
     * @param comicId 漫画的id
     */
    fun getComicInfo(comicId: String, token: String): String {
        val url = "https://picaapi.picacomic.com/comics/$comicId"
        val client = OkHttpClient()

        val request = Requset().GET(url, picaHeaders(url, "GET", token))
        val response = client.newCall(request).execute()
        //Log.d("TAG_SETTING_C", "getComicInfo: ${request.headers}")
        return response.body!!.string()
    }

    /**
     * 获取漫画图片
     * @param comicID 漫画id
     * @param order 漫画章节id
     * @param page 漫画页数
     * @param token Token
     */
    fun getComicPicture(comicID: String, order: Int, page: Int, token: String): String {
        val url = "https://picaapi.picacomic.com/comics/$comicID/order/$order/pages?page=$page"
        val client = OkHttpClient.Builder().connectTimeout(Duration.ofMinutes(3)).build()
        val request = Requset().GET(url, picaHeaders(url, "GET", token))
        val response = client.newCall(request).execute()
        return response.body!!.string()
    }

    /**
     * 获取漫画章节
     * @param comicID 漫画Id
     * @param page 页数
     * @param token TOKEN
     */
    fun getComicEps(comicID: String, page: Int, token: String): String {
        val url = "https://picaapi.picacomic.com/comics/$comicID/eps?page=$page"
        val client = OkHttpClient()
        val request = Requset().GET(url, picaHeaders(url, "GET", token))
        val response = client.newCall(request).execute()

        return response.body!!.string()
    }

    /**
     * 获取漫画分区
     *
     * @param page: String 分页，从1开始
     * @param c: String  categories里面的title，如"嗶咔漢化"
     * @param s: String  排序依据
     * @param token:String Token 字符串
     */
    fun getComics(page: String, c: String, s: String, token: String): String {
        val client = OkHttpClient.Builder().build()

        val url = "https://picaapi.picacomic.com/comics?page=$page&c=${
            URLEncoder.encode(c,
                "utf-8")
        }&s=$s"

        val request =
            Request.Builder()
                .url(url)
                .get()
                .headers(picaHeaders(url, "GET", token))
                .cacheControl(DEFAULT_CACHE_CONTROL)
                .build()
        //Log.d("TAGAA", "getComics: ${request.body}")

        val response = client.newCall(request).execute()
        return response.body!!.string()
    }

    fun getComicComments(comicID: String, page: Int, token: String): String {
        val url = "https://picaapi.picacomic.com/comics/${comicID}/comments?page=$page"
        val client = OkHttpClient.Builder().build()
        val request = Requset().GET(url, picaHeaders(url, "GET", token))
        val response = client.newCall(request).execute()

        return response.body!!.string()
    }

    /**
     * 获取热搜
     */
    fun getComicKeyWord(token: String): String {
        val url = "https://picaapi.picacomic.com/keywords"
        val client = OkHttpClient()

        val request = Requset().GET(url, picaHeaders(url, "GET", token))
        val response = client.newCall(request).execute()

        return response.body!!.string()
    }

    /**
     * 漫画搜索
     * @param category 合集
     * @param keyWord 关键字
     * @param sort 分类
     * @param page 页数
     * */
    fun searchComic(
        category: String? = null,
        keyWord: String,
        sort: String? = null,
        page: Int,
        token: String,
    ): String {
        val url = "https://picaapi.picacomic.com/comics/advanced-search?page=$page"
        val client = OkHttpClient()
        val postData = JsonObject().apply {
            addProperty("categories", category)
            addProperty("keyword", keyWord)
            addProperty("sort", sort)
        }.asJsonObject.toString().toRequestBody("application/json; charset=UTF-8".toMediaType())

        val request = Requset().POST(url, picaHeaders(url, "POST", token), postData)
        val response = client.newCall(request).execute()

        return response.body!!.string()
    }

    /**
     * 获取个人页面
     * @param token Token
     */
    fun getUserProfile(token: String): String {
        val client = OkHttpClient.Builder().build()
        val url = "$baseUrl/users/profile"
        val request = Requset().GET(url, picaHeaders(url, "GET", token))

        val response = client.newCall(request).execute()
        return response.body!!.string()
    }

    /**
     * 获取我收藏的漫画
     * @param pageNum 页数
     */
    fun getMyFavoriteBooks(pageNum: Int, token: String): String {
        val uri = String.format("$baseUrl/users/favourite?page=%d&s=dd", pageNum)
        val client = OkHttpClient.Builder().build()

        val request =
            Request.Builder().url(uri).get().headers(picaHeaders(uri, "GET", token))
                .cacheControl(DEFAULT_CACHE_CONTROL)
                .build()

        val response = client.newCall(request).execute()
        return response.body!!.string()
    }


    /**
     * 漫画分类图
     * @param fileServer 头部URL
     * @param path 尾部url
     */
    fun getComicCategoryImage(fileServer: String, path: String): String {

        return if (fileServer == "https://storage1.picacomic.com") {
            "$fileServer/static/$path"
        } else {
            "$fileServer$path"
        }
    }

    /**
     * 获取bitmap
     */
    fun getImageBitmap(url: String): InputStream {
        val client = OkHttpClient.Builder().build()

        val request = Request.Builder().url(url).get()
            .cacheControl(DEFAULT_CACHE_CONTROL)
            .build()
        val response = client.newCall(request).execute()

        return response.body!!.byteStream()
    }

    /**
     * 获取排行漫画
     *
     * @param urlOnRank 链接
     * @param token Token
     */
    fun getRankComic(urlOnRank: String, token: String): String {
        val url = "$baseUrl$urlOnRank"
        val client = OkHttpClient.Builder().build()
        val request = Requset().GET(url, picaHeaders(url, "GET", token))
        val response = client.newCall(request).execute()

        val a = response.body!!.string()
        if (JsonParser.parseString(a)
                .asJsonObject.get("message")
                .asString.equals("unauthorized")
        ) {
            throw LoginFException("登录过期")
        }

        return a
    }


    /**
     * 获取漫画分区
     */
    fun getComicsCategory(token: String): String {
        val client = OkHttpClient.Builder().build()
        val url = "$baseUrl/categories"
        val request = Requset().GET(url, picaHeaders(url, "GET", token))
        val response = client.newCall(request).execute()
        val a = response.body!!.string()


        return a
    }

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     */
    fun getToken(username: String, password: String): String {
        val client =
            OkHttpClient().newBuilder().hostnameVerifier { _, _ -> true }.build()

        val url = "$baseUrl/auth/sign-in"
        val jsonObject = JsonObject().apply {
            addProperty("email", username)
            addProperty("password", password)
        }.asJsonObject.toString().toRequestBody("application/json; charset=UTF-8".toMediaType())
        //println(body.contentType())
        val request =
            Request.Builder().url(url).post(jsonObject).removeHeader("User-Agent")
                .headers(picaHeaders(url, "POST"))
                .cacheControl(DEFAULT_CACHE_CONTROL).build()

        val response = client.newCall(request).execute()
        //Log.d("TAG_RES", "getToken: ${response.body!!.string()}")
        return response.body!!.string()
    }

    companion object {

        fun getComicRankImage(fileServer: String, path: String): String {
            return "$fileServer/static/$path"
        }

        @SuppressLint("SimpleDateFormat")
        fun formatTime(time: String): String {
            val date = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
            val instant: Instant = Instant.parse(time)
            val timeS: Long = instant.toEpochMilli()
            return date.format(timeS)
        }

        fun unauthorizedCheck(string: String): Boolean {
            return JsonParser.parseString(string)
                .asJsonObject.get("message")
                .asString.equals("unauthorized")
        }


        const val preferenceFileKey = "com.shihcheeng.picacgmaterial3.PREFERENCE_FILE_KEY"

        const val key = "KEY_TOKEN"
        const val ERROR_TIME_OUT = 1111
        const val ERROR_LOADING = 2222

    }

}




