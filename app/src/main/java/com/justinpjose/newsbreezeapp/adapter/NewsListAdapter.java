package com.justinpjose.newsbreezeapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.justinpjose.newsbreezeapp.repository.database.RoomDB;
import com.justinpjose.newsbreezeapp.model.ArticleModel;
import com.justinpjose.newsbreezeapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {
    private List<ArticleModel.Article> articleList;
    private final Context context;
    private final ArticleOnClickInterface mArticleOnClickInterface;
    private final RoomDB database;
    private static final String TAG = "news_breeze_app";

    public NewsListAdapter(Context context, List<ArticleModel.Article> articleList, ArticleOnClickInterface articleOnClickInterface) {
        this.context = context;
        this.articleList = articleList;
        this.mArticleOnClickInterface = articleOnClickInterface;
        database = RoomDB.getInstance(context);
    }

    public void filterList(List<ArticleModel.Article> filterList) {
        articleList = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_items, parent, false);
        return new ViewHolder(view, mArticleOnClickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NewsListAdapter.ViewHolder holder, int position) {
        ArticleModel.Article article = articleList.get(position);
        String url = article.getImage_url();
        String title = article.getTitle();
        String description = article.getShort_description();
        String date = article.getDate().split("T")[0];
        if (url != null) {
            Glide.with(context).load(url).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.unavailable_image);
        }
        holder.titleText.setText(title);
        if (description != null) {
            holder.descriptionText.setText(description);
        } else {
            holder.descriptionText.setText("Description not available");
            holder.descriptionText.setTypeface(holder.descriptionText.getTypeface(), Typeface.ITALIC);
        }
        holder.dateText.setText(date);
        if (article.isSaved()) {
            holder.saveInActiveImageButton.setBackgroundResource(R.drawable.save_icon_selected);
            holder.saveButton.setText("Remove");
        } else {
            holder.saveInActiveImageButton.setBackgroundResource(R.drawable.save_icon_not_selected);
            holder.saveButton.setText("Save");
        }
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView titleText, descriptionText, dateText;
        ArticleOnClickInterface articleOnClickInterface;
        Button readButton, saveButton;
        ImageButton saveInActiveImageButton;

        public ViewHolder(@NonNull @NotNull View itemView, ArticleOnClickInterface articleOnClickInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            dateText = itemView.findViewById(R.id.dateText);
            readButton = itemView.findViewById(R.id.readButton);
            saveButton = itemView.findViewById(R.id.saveButton);
            saveInActiveImageButton = itemView.findViewById(R.id.saveInActiveImageButton);

            this.articleOnClickInterface = articleOnClickInterface;

            imageView.setOnClickListener(this);
            titleText.setOnClickListener(this);
            descriptionText.setOnClickListener(this);
            readButton.setOnClickListener(this);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "save button clicked");
                    ArticleModel.Article article = database.articleDao().getTheArticle(articleList.get(getAdapterPosition()).getTitle());
                    if (article.isSaved()) {
                        saveInActiveImageButton.setBackgroundResource(R.drawable.save_icon_not_selected);
                        saveButton.setText("Save");
                    } else {
                        saveInActiveImageButton.setBackgroundResource(R.drawable.save_icon_selected);
                        saveButton.setText("Remove");
                    }
                    articleOnClickInterface.onArticleSaved(article.getTitle());
                }
            });
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "article clicked");
            ArticleModel.Article article = database.articleDao().getTheArticle(articleList.get(getAdapterPosition()).getTitle());
            articleOnClickInterface.onArticleClick(article.getTitle());
            Log.d(TAG, article.isSaved() + "=" + article.getTitle());
        }
    }

    public interface ArticleOnClickInterface {
        void onArticleSaved(String articleTitle);
        void onArticleClick(String articleTitle);
    }
}
