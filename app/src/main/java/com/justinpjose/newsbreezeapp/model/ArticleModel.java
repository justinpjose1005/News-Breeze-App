package com.justinpjose.newsbreezeapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArticleModel {
    @SerializedName("status")
    private final String status;
    @SerializedName("totalResults")
    private final String totalResults;
    @SerializedName("articles")
    public List<Article> articlesList = null;

    @Entity(tableName = "articles")
    public static class Article {
        @PrimaryKey(autoGenerate = true)
        private int ID;
        @SerializedName("urlToImage")
        private String image_url;
        @SerializedName("title")
        private String title;
        @SerializedName("publishedAt")
        private String date;
        @SerializedName("description")
        private String short_description;
        @SerializedName("content")
        private String content;
        @SerializedName("author")
        private String author;
        private boolean saved;

        public Article(String image_url, String title, String date, String short_description, String content, String author, boolean saved) {
            this.image_url = image_url;
            this.title = title;
            this.date = date;
            this.short_description = short_description;
            this.content = content;
            this.author = author;
            this.saved = saved;
        }

        public boolean isSaved() {
            return saved;
        }

        public void setSaved(boolean saved) {
            this.saved = saved;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getShort_description() {
            return short_description;
        }

        public void setShort_description(String short_description) {
            this.short_description = short_description;
        }
    }

    public ArticleModel(String status, String totalResults) {
        this.status = status;
        this.totalResults = totalResults;
    }
}
