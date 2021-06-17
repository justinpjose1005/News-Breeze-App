package com.justinpjose.newsbreezeapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.justinpjose.newsbreezeapp.R;
import com.justinpjose.newsbreezeapp.adapter.SavedListAdapter;
import com.justinpjose.newsbreezeapp.repository.database.RoomDB;
import com.justinpjose.newsbreezeapp.model.ArticleModel;
import com.justinpjose.newsbreezeapp.viewmodel.SavedListViewModel;

import java.util.ArrayList;
import java.util.List;

public class SavedListActivity extends AppCompatActivity implements SavedListAdapter.ArticleInterface {
    List<ArticleModel.Article> savedArticleList = new ArrayList<>();
    private static final String TAG = "news_breeze_app";
    private SavedListViewModel viewModel;
    private SavedListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        LinearLayout backButton = findViewById(R.id.backButton);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SavedListAdapter(this,savedArticleList,this);
        recyclerView.setAdapter(adapter);

        viewModel = new SavedListViewModel(this);
        viewModel.getSavedListObserver().observe(this, new Observer<List<ArticleModel.Article>>() {
            @Override
            public void onChanged(List<ArticleModel.Article> savedList) {
                Log.d(TAG, String.valueOf(savedList.size()));
                adapter.filterList(savedList);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "===========SavedListActivity.java===========");
        super.onResume();
        viewModel.setSavedArticles();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onArticleClick(String articleTitle) {
        startActivity(new Intent(this, ReadActivity.class).putExtra("articleTitle",articleTitle).putExtra("fromSavedListActivity",true));
    }
}