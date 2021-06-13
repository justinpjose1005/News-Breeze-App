package com.justinpjose.newsbreezeapp.retorfit;

import com.justinpjose.newsbreezeapp.model.ArticleModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    String BASE_URL = "https://newsapi.org/v2/";

    @GET("top-headlines?country=in&apiKey=1fa8833a02df4bf0b295861dfc0ea35b")
    Call<ArticleModel> getLatestArticles();
}
