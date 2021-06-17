package com.justinpjose.newsbreezeapp.repository.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.justinpjose.newsbreezeapp.model.ArticleModel;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ArticleDao {
    //Insert articles
    @Insert(onConflict = REPLACE)
    void insert(ArticleModel.Article article);

    //Update query
    @Query("UPDATE articles SET saved = :status WHERE title = :articleTitle")
    void update(boolean status, String articleTitle);

    //Delete all articles
    @Query("DELETE FROM articles")
    void reset();

    //Get a single articles
    @Query("SELECT * FROM articles WHERE title = :articleTitle")
    ArticleModel.Article getTheArticle(String articleTitle);

    //Get all articles
    @Query("SELECT * FROM articles")
    List<ArticleModel.Article> getAllArticles();

    //Get saved articles
    @Query("SELECT * FROM articles WHERE saved = :status")
    List<ArticleModel.Article> getAllSavedArticles(boolean status);
}