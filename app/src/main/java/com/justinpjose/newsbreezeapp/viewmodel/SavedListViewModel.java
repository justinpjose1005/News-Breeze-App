package com.justinpjose.newsbreezeapp.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.justinpjose.newsbreezeapp.model.ArticleModel;
import com.justinpjose.newsbreezeapp.repository.database.RoomDB;

import java.util.List;

public class SavedListViewModel extends ViewModel {
    private static final String TAG = "news_breeze_app";
    private final MutableLiveData<List<ArticleModel.Article>> savedListMutableLiveData;
    private final RoomDB database;

    public SavedListViewModel(Context context) {
        savedListMutableLiveData = new MutableLiveData<>();
        database = RoomDB.getInstance(context);
    }

    public MutableLiveData<List<ArticleModel.Article>> getSavedListObserver() {
        return savedListMutableLiveData;
    }

    public void setSavedArticles() {
        savedListMutableLiveData.postValue(database.articleDao().getAllSavedArticles(true));
    }
}
