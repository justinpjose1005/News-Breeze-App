package com.justinpjose.newsbreezeapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.justinpjose.newsbreezeapp.R;
import com.justinpjose.newsbreezeapp.adapter.NewsAdapter;
import com.justinpjose.newsbreezeapp.database.RoomDB;
import com.justinpjose.newsbreezeapp.model.ArticleModel;
import com.justinpjose.newsbreezeapp.retorfit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NewsAdapter.ArticleOnClickInterface {
    private List<ArticleModel.Article> articleList = new ArrayList<>();
    private static final String TAG = "Main_Activity";
    private RoomDB mDatabase;
    private NewsAdapter mNewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = RoomDB.getInstance(this);
        SearchView searchView = findViewById(R.id.searchView);
        ImageButton saveButton = findViewById(R.id.saveButton);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SaveActivity.class));
            }
        });

        if (internet_is_connected()) {
            getLatestArticles();
        }
    }

    private void filter(String text) {
        List<ArticleModel.Article> filteredList = new ArrayList<>();
        for (ArticleModel.Article article : articleList) {
            if (article.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(article);
            }
        }
        mNewsAdapter.filterList(filteredList);
    }

    private void getLatestArticles() {
        Call<ArticleModel> call = RetrofitClient.getInstance().getMyApi().getLatestArticles();
        call.enqueue(new Callback<ArticleModel>() {
            @Override
            public void onResponse(Call<ArticleModel> call, Response<ArticleModel> response) {
                ArticleModel model = response.body();
                assert model != null;
                articleList.clear();
                articleList = model.articlesList;
                mDatabase.articleDao().reset();
                for (ArticleModel.Article article : articleList) {
                    String url = article.getImage_url();
                    String title = article.getTitle();
                    String date = article.getDate();
                    String shortDescription = article.getShort_description();
                    String content = article.getContent();
                    String author = article.getAuthor();
                    ArticleModel.Article newArticle = new ArticleModel.Article(url, title, date, shortDescription, content, author, false);
                    mDatabase.articleDao().insert(newArticle);
                }
                initialize_recycler_view(articleList);
            }

            @Override
            public void onFailure(Call<ArticleModel> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
                call.cancel();
            }
        });
    }

    private void initialize_recycler_view(List<ArticleModel.Article> articleList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNewsAdapter = new NewsAdapter(this, articleList, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mNewsAdapter);
    }

    @Override
    public void onArticleClick(int adapterPosition) {
        startActivity(new Intent(this, ReadActivity.class).putExtra("indexPosition", adapterPosition));
    }

    @Override
    public void onArticleSaved(int adapterPosition) {
        List<ArticleModel.Article> articleList = mDatabase.articleDao().getAllArticles();
        ArticleModel.Article article = articleList.get(adapterPosition);
        boolean status;
        status = !article.isSaved();
        String articleTitle = article.getTitle();
        mDatabase.articleDao().update(status, articleTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        articleList.clear();
        articleList = mDatabase.articleDao().getAllArticles();
        initialize_recycler_view(articleList);
    }

    public boolean internet_is_connected() {
        // checks for internet
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        // closes the app
        finish();
        System.exit(0);
    }
}