package com.justinpjose.newsbreezeapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.justinpjose.newsbreezeapp.R;
import com.justinpjose.newsbreezeapp.adapter.SavedAdapter;
import com.justinpjose.newsbreezeapp.database.RoomDB;
import com.justinpjose.newsbreezeapp.model.ArticleModel;
import com.justinpjose.newsbreezeapp.ui.ReadActivity;

import java.util.List;

public class SaveActivity extends AppCompatActivity implements SavedAdapter.ArticleInterface {
    private RecyclerView mRecyclerView;
    RoomDB mDatabase;
    private static final String TAG = "Save_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        mDatabase = RoomDB.getInstance(this);
        LinearLayout backButton = findViewById(R.id.backButton);
        mRecyclerView = findViewById(R.id.recyclerView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshArticleList();
    }

    private void refreshArticleList() {
        List<ArticleModel.Article> savedArticleList = mDatabase.articleDao().getAllSavedArticles(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new SavedAdapter(this, savedArticleList,this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onArticleClick(int adapterPosition) {
        startActivity(new Intent(this, ReadActivity.class).putExtra("indexPosition",adapterPosition));
    }
}