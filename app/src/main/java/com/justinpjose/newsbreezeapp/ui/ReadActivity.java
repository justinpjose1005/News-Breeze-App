package com.justinpjose.newsbreezeapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.justinpjose.newsbreezeapp.R;
import com.justinpjose.newsbreezeapp.database.RoomDB;
import com.justinpjose.newsbreezeapp.model.ArticleModel;

import java.util.List;

public class ReadActivity extends AppCompatActivity {
    private Button mSaveButton;
    private RoomDB mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        mDatabase = RoomDB.getInstance(this);
        ImageView imageView = findViewById(R.id.imageView);
        TextView titleText = findViewById(R.id.titleText);
        TextView dateText = findViewById(R.id.dateText);
        TextView authorNameText = findViewById(R.id.authorNameText);
        TextView contentText = findViewById(R.id.contentText);
        LinearLayout backButton = findViewById(R.id.backButton);
        mSaveButton = findViewById(R.id.saveButton);

        List<ArticleModel.Article> articleList = mDatabase.articleDao().getAllArticles();

        int position = getIntent().getIntExtra("indexPosition", 0);
        ArticleModel.Article article = articleList.get(position);
        String url = article.getImage_url();
        String date = article.getDate().split("T")[0];
        String title = article.getTitle();
        String author = article.getAuthor();
        String content = article.getContent();
        boolean isSaved = article.isSaved();
        if (url != null) {
            Glide.with(this).load(url).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.unavailable_image);
        }
        dateText.setText(date);
        titleText.setText(title);
        if (author != null) {
            authorNameText.setText(author);
        } else {
            authorNameText.setText("Unkown Author");
        }
        if (content != null) {
            contentText.setText(content);
        } else {
            contentText.setText("Content unavailable");
        }
        checkIfSaved(isSaved);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeIfSaved(position);
            }
        });

    }

    public void checkIfSaved(boolean isSaved) {
        // check on activity created
        String text;
        if (isSaved) {
            text = "Remove";
        } else {
            text = "Save";
        }
        mSaveButton.setText(text);
    }

    private void changeIfSaved(int position) {
        // check on save button click
        List<ArticleModel.Article> articleList = mDatabase.articleDao().getAllArticles();
        ArticleModel.Article article = articleList.get(position);
        boolean isSaved = article.isSaved();
        String text;
        boolean status;
        if (isSaved) {
            status = false;
            text = "Save";
        } else {
            status = true;
            text = "Remove";
        }
        String articleTitle = article.getTitle();
        mDatabase.articleDao().update(status, articleTitle);
        mSaveButton.setText(text);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}