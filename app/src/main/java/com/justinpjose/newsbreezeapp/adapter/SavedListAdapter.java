package com.justinpjose.newsbreezeapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.justinpjose.newsbreezeapp.R;
import com.justinpjose.newsbreezeapp.model.ArticleModel;
import com.justinpjose.newsbreezeapp.repository.database.RoomDB;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SavedListAdapter extends RecyclerView.Adapter<SavedListAdapter.ViewHolder> {
    private final Context context;
    private List<ArticleModel.Article> savedArticleList;
    private final ArticleInterface articleInterface;
    private RoomDB database;

    public SavedListAdapter(Context context, List<ArticleModel.Article> savedArticleList, ArticleInterface articleInterface) {
        this.context = context;
        this.savedArticleList = savedArticleList;
        this.articleInterface = articleInterface;
        database = RoomDB.getInstance(context);
    }

    public void filterList(List<ArticleModel.Article> filterList) {
        savedArticleList = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_article_items, parent, false);
        return new ViewHolder(view, articleInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SavedListAdapter.ViewHolder holder, int position) {
        ArticleModel.Article savedArticle = savedArticleList.get(position);
        String url = savedArticle.getImage_url();
        String title = savedArticle.getTitle();
        String date = savedArticle.getDate().split("T")[0];
        String author = savedArticle.getAuthor();
        if (url != null) {
            Glide.with(context).load(url).into(holder.mImageView);
        } else {
            holder.mImageView.setImageResource(R.drawable.unavailable_image);
        }
        holder.mTitleText.setText(title);
        holder.mDateText.setText(date);
        if (author != null) {
            holder.mAuthorText.setText(author);
        } else {
            holder.mAuthorText.setText("Unknown Author");
        }
    }

    @Override
    public int getItemCount() {
        return savedArticleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mImageView;
        private final ArticleInterface articleInterface;
        private final TextView mTitleText;
        private final TextView mDateText;
        private final TextView mAuthorText;

        public ViewHolder(@NonNull @NotNull View itemView, ArticleInterface articleInterface) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTitleText = itemView.findViewById(R.id.titleText);
            mDateText = itemView.findViewById(R.id.dateText);
            mAuthorText = itemView.findViewById(R.id.authorText);

            this.articleInterface = articleInterface;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ArticleModel.Article article = database.articleDao().getTheArticle(savedArticleList.get(getAdapterPosition()).getTitle());
            articleInterface.onArticleClick(article.getTitle());
        }
    }

    public interface ArticleInterface {
        void onArticleClick(String adapterPosition);
    }
}
