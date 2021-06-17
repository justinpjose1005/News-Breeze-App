package com.justinpjose.newsbreezeapp.viewmodel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.justinpjose.newsbreezeapp.model.ArticleModel;
import com.justinpjose.newsbreezeapp.repository.database.RoomDB;
import com.justinpjose.newsbreezeapp.repository.retorfit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsListViewModel extends ViewModel {
    private static final String TAG = "news_breeze_app";
    private final MutableLiveData<List<ArticleModel.Article>> articleListMutableLiveData;
    private final RoomDB database;
    private static String toastMessage;
    private final Context context;

    public NewsListViewModel(Context context) {
        this.context = context;
        articleListMutableLiveData = new MutableLiveData<>();
        database = RoomDB.getInstance(context);
    }

    public MutableLiveData<List<ArticleModel.Article>> getArticleListObserver() {
        return articleListMutableLiveData;
    }

    public void makeApiCall() {
        if (isInternetAvailable()) {
            Call<ArticleModel> call = RetrofitClient.getInstance().getMyApi().getLatestArticles();
            call.enqueue(new Callback<ArticleModel>() {
                @Override
                public void onResponse(Call<ArticleModel> call, Response<ArticleModel> response) {
                    Log.d(TAG, "onResponse: " + response);
                    ArticleModel articleModel = response.body();
                    assert articleModel != null;
                    emptyTheDatabase();
                    insertArticlesIntoDatabase(articleModel);
                    articleListMutableLiveData.postValue(articleModel.articlesList);
                }

                @Override
                public void onFailure(Call<ArticleModel> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                    articleListMutableLiveData.postValue(null);
                    call.cancel();
                }
            });
        } else {
            setToastMessage("Internet required to update list");
            Toast.makeText(context, getToastMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Methods related to database operations
     */
    private void emptyTheDatabase() {
        Log.d(TAG, "Database has been reset");
        database.articleDao().reset();
    }

    private void insertArticlesIntoDatabase(ArticleModel articleModel) {
        Log.d(TAG, "Articles inserted into database");
        for (ArticleModel.Article article : articleModel.articlesList) {
            database.articleDao().insert(article);
        }
    }

    private List<ArticleModel.Article> getArticleListFromDatabase() {
        Log.d(TAG, "Retrieving articles from database");
        return database.articleDao().getAllArticles();
    }

    /**
     * Methods related toast operations
     */
    public void setToastMessage(String message) {
        toastMessage = message;
    }

    public String getToastMessage() {
        return toastMessage;
    }

    /**
     * Method related to internet availability
     */
    public boolean isInternetAvailable() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method related to search view operations
     */
    public void filterNewsList(String text) {
        if (text == null || text.isEmpty()) {
            this.articleListMutableLiveData.postValue(getArticleListFromDatabase());
        }
        else {
            Log.d(TAG, "Searched for " + text);
            List<ArticleModel.Article> articleList = getArticleListFromDatabase();
            List<ArticleModel.Article> filteredList = new ArrayList<>();
            for (ArticleModel.Article article : articleList) {
                if (article.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(article);
                }
            }
            this.articleListMutableLiveData.postValue(filteredList);
        }
    }

    /**
     * Method to update the articles save state
     */
    public void updateArticlesSaveState(String articleTitle) {
        Log.d(TAG, "Updating articles saved state");
        ArticleModel.Article article = database.articleDao().getTheArticle(articleTitle);
        database.articleDao().update(!article.isSaved(), articleTitle);
    }
}
