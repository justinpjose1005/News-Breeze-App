package com.justinpjose.newsbreezeapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.justinpjose.newsbreezeapp.database.RoomDB;
import com.justinpjose.newsbreezeapp.model.ArticleModel;
import com.justinpjose.newsbreezeapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<ArticleModel.Article> mArticleList;
    private final Context mContext;
    private final ArticleOnClickInterface mArticleOnClickInterface;

    public NewsAdapter(Context context, List<ArticleModel.Article> articleList, ArticleOnClickInterface articleOnClickInterface) {
        this.mContext = context;
        this.mArticleList = articleList;
        this.mArticleOnClickInterface = articleOnClickInterface;
    }

    public void filterList(List<ArticleModel.Article> filterList) {
        mArticleList = filterList;
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
    public void onBindViewHolder(@NonNull @NotNull NewsAdapter.ViewHolder holder, int position) {
        ArticleModel.Article article = mArticleList.get(position);
        String url = article.getImage_url();
        String title = article.getTitle();
        String description = article.getShort_description();
        String date = article.getDate().split("T")[0];
        if (url != null) {
            Glide.with(mContext).load(url).into(holder.imageView);
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
        return mArticleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView titleText, descriptionText, dateText;
        ArticleOnClickInterface articleOnClickInterface;
        Button readButton, saveButton;
        ImageButton saveInActiveImageButton;
        private RoomDB mDatabase;
        ArticleModel.Article article;

        public ViewHolder(@NonNull @NotNull View itemView, ArticleOnClickInterface articleOnClickInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            dateText = itemView.findViewById(R.id.dateText);
            readButton = itemView.findViewById(R.id.readButton);
            saveButton = itemView.findViewById(R.id.saveButton);
            saveInActiveImageButton = itemView.findViewById(R.id.saveInActiveImageButton);
            mDatabase = RoomDB.getInstance(mContext);

            this.articleOnClickInterface = articleOnClickInterface;

            imageView.setOnClickListener(this);
            titleText.setOnClickListener(this);
            descriptionText.setOnClickListener(this);
            readButton.setOnClickListener(this);
            saveButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.saveButton) {
                List<ArticleModel.Article> articleList = mDatabase.articleDao().getAllArticles();
                article = articleList.get(getAdapterPosition());
                if (article.isSaved()) {
                    saveInActiveImageButton.setBackgroundResource(R.drawable.save_icon_not_selected);
                    saveButton.setText("Save");
                } else {
                    saveInActiveImageButton.setBackgroundResource(R.drawable.save_icon_selected);
                    saveButton.setText("Remove");
                }
                articleOnClickInterface.onArticleSaved(getAdapterPosition());
            } else {
                articleOnClickInterface.onArticleClick(getAdapterPosition());
            }
        }
    }

    public interface ArticleOnClickInterface {
        void onArticleSaved(int adapterPosition);

        void onArticleClick(int adapterPosition);
    }
}
