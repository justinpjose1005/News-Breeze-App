package com.justinpjose.newsbreezeapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.justinpjose.newsbreezeapp.R;
import com.justinpjose.newsbreezeapp.adapter.NewsListAdapter;
import com.justinpjose.newsbreezeapp.model.ArticleModel;
import com.justinpjose.newsbreezeapp.viewmodel.NewsListViewModel;

import java.util.ArrayList;
import java.util.List;

public class NewsListActivity extends AppCompatActivity implements NewsListAdapter.ArticleOnClickInterface {
    private final List<ArticleModel.Article> articleList = new ArrayList<>();
    private static final String TAG = "news_breeze_app";
    private NewsListAdapter adapter;
    private NewsListViewModel viewModel;
    private ProgressBar progressBar;
    private View separator;
    private static boolean freshStart = true;
    private static String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchView searchView = findViewById(R.id.searchView);
        ImageButton saveButton = findViewById(R.id.saveButton);
        separator = findViewById(R.id.separator);
        separator.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsListAdapter(this, articleList, this);
        recyclerView.setAdapter(adapter);

        viewModel = new NewsListViewModel(this);
        viewModel.getArticleListObserver().observe(this, new Observer<List<ArticleModel.Article>>() {
            @Override
            public void onChanged(List<ArticleModel.Article> articles) {
                if (articles != null) {
                    adapter.filterList(articles);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    separator.setVisibility(View.VISIBLE);
                }
            }
        });
        viewModel.makeApiCall();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                keyword = newText;
                viewModel.filterNewsList(newText);
                return false;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SavedListActivity.class));
            }
        });
    }

    @Override
    public void onArticleClick(String articleTitle) {
        startActivity(new Intent(this, ReadActivity.class).putExtra("articleTitle", articleTitle).putExtra("fromSavedListActivity", false));
    }

    @Override
    public void onArticleSaved(String articleTitle) {
        viewModel.updateArticlesSaveState(articleTitle);
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "===========MainActivity.java===========");
        super.onResume();
        if (!freshStart) {
            viewModel.filterNewsList(keyword);
        } else {
            Log.e(TAG, "First Launch");
            freshStart = false;
        }
    }

    @Override
    public void onBackPressed() {
        // closes the app
        finish();
        System.exit(0);
    }
}