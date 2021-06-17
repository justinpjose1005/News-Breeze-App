package com.justinpjose.newsbreezeapp.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.justinpjose.newsbreezeapp.model.ArticleModel;
import com.justinpjose.newsbreezeapp.repository.database.RoomDB;

import java.util.List;

public class ReadViewModel extends ViewModel {
    private static final String TAG = "news_breeze_app";
    private final String articleTitle;
    private final RoomDB database;
    private final MutableLiveData<ArticleModel.Article> articleMutableLiveData;
    private final boolean fromSavedListActivity;

    public ReadViewModel(Context context, String articleTitle, boolean fromSavedListActivity) {
        articleMutableLiveData = new MutableLiveData<>();
        this.articleTitle = articleTitle;
        this.fromSavedListActivity = fromSavedListActivity;
        database = RoomDB.getInstance(context);
    }

    public MutableLiveData<ArticleModel.Article> getArticleObserver() {
        return articleMutableLiveData;
    }

    public void getCurrentArticle() {
        articleMutableLiveData.postValue(findArticle());
    }

    private ArticleModel.Article findArticle() {
        List<ArticleModel.Article> articleList;
        if (fromSavedListActivity) {
            articleList = database.articleDao().getAllSavedArticles(true);
            for (ArticleModel.Article article : articleList) {
                if (article.getTitle().equals(articleTitle)) {
                    return article;
                }
            }
        }
        articleList = database.articleDao().getAllArticles();
        for (ArticleModel.Article article : articleList) {
            if (article.getTitle().equals(articleTitle)) {
                return article;
            }
        }
        return null;
    }

    /**
     * Method to update the articles save state
     */
    public void updateArticlesSaveState(String articleTitle) {
        ArticleModel.Article article = database.articleDao().getTheArticle(articleTitle);
        database.articleDao().update(!article.isSaved(),article.getTitle());
        Log.d(TAG, "Updating articles saved state to " + !article.isSaved());
    }
}
