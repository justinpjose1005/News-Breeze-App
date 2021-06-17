package com.justinpjose.newsbreezeapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.justinpjose.newsbreezeapp.R;
import com.justinpjose.newsbreezeapp.repository.database.RoomDB;
import com.justinpjose.newsbreezeapp.model.ArticleModel;
import com.justinpjose.newsbreezeapp.viewmodel.NewsListViewModel;
import com.justinpjose.newsbreezeapp.viewmodel.ReadViewModel;

import java.util.List;

public class ReadActivity extends AppCompatActivity {
    private Button saveButton;
    private static final String TAG = "news_breeze_app";
    private ReadViewModel viewModel;
    private static boolean fromSavedListActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        ImageView imageView = findViewById(R.id.imageView);
        TextView titleText = findViewById(R.id.titleText);
        TextView dateText = findViewById(R.id.dateText);
        TextView authorNameText = findViewById(R.id.authorNameText);
        TextView contentText = findViewById(R.id.contentText);
        LinearLayout backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);

        String articleTitle = getIntent().getStringExtra("articleTitle");
        fromSavedListActivity = getIntent().getBooleanExtra("fromSavedListActivity",false);

        viewModel = new ReadViewModel(this, articleTitle, fromSavedListActivity);
        viewModel.getArticleObserver().observe(this, new Observer<ArticleModel.Article>() {
            @Override
            public void onChanged(ArticleModel.Article article) {
                if (article.getImage_url() != null) {
                    Glide.with(getApplicationContext()).load(article.getImage_url()).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.unavailable_image);
                }
                dateText.setText(article.getDate().split("T")[0]);
                titleText.setText(article.getTitle());
                if (article.getAuthor() != null) {
                    authorNameText.setText(article.getAuthor());
                } else {
                    authorNameText.setText("Unkown Author");
                }
                if (article.getContent() != null) {
                    contentText.setText(article.getContent());
                } else {
                    contentText.setText("Content unavailable");
                }
                if (article.isSaved()) {
                    saveButton.setText("Remove");
                } else {
                    saveButton.setText("Save");
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromSavedListActivity) {
                    Log.d(TAG, "from saved activity");
                    viewModel.updateArticlesSaveState(articleTitle);
                    onBackPressed();
                } else {
                    Log.d(TAG, "not from saved activity");
                    viewModel.updateArticlesSaveState(articleTitle);
                    viewModel.getCurrentArticle();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        Log.e(TAG, "===========ReadActivity.java===========");
        super.onResume();
        viewModel.getCurrentArticle();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}