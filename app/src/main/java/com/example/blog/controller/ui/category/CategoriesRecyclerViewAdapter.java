package com.example.blog.controller.ui.category;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.blog.R;
import com.example.blog.model.Categories;

import java.util.List;

public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.ViewHolder> {

    private List<String> mData;
    private List<Categories> categoriesList;
    private LayoutInflater mInflater;

    private ItemClickListener mClickListener;

    // data is passed into the constructor
//    MyRecyclerViewAdapter(Context context, List<String> data) {
//
//        this.mInflater = LayoutInflater.from(context);
//        this.mData = data;
//    }
    CategoriesRecyclerViewAdapter(Context context, List<Categories> itemData) {

        this.mInflater = LayoutInflater.from(context);
        this.categoriesList = itemData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.category_single_item, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row

//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        String id = mData.get(position);
//
//        holder.itemId.setText(id);
//    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Categories categories=categoriesList.get(position);
       // holder.itemId.setText(item.getId());
        holder.catName.setText(categories.getName());
        holder.catId.setText(String.valueOf(categories.getId()));
        holder.catId.setVisibility(View.GONE);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return categoriesList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       TextView catName,catId;


        ViewHolder(View itemView) {
            super(itemView);

            catName=itemView.findViewById(R.id.catName);
            catId=itemView.findViewById(R.id.catId);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
//    String getItem(int id) {
//        return mData.get(id);
//    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}