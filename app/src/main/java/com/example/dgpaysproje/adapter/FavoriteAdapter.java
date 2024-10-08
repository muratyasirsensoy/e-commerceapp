package com.example.dgpaysproje.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dgpaysproje.R;
import com.example.dgpaysproje.model.ProductModel;
import com.example.dgpaysproje.view.FavoriteActivity;
import com.example.dgpaysproje.view.HomeActivity;
import com.google.gson.Gson;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private ArrayList<ProductModel> favItems;
    private FavoriteActivity favoriteActivity;

    public FavoriteAdapter(ArrayList<ProductModel> favItems, FavoriteActivity favoriteActivity) {
        this.favItems = favItems;
        this.favoriteActivity = favoriteActivity;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_favourite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        ProductModel currentProduct = favItems.get(position);

        holder.titleText.setText(currentProduct.title);

        double price = Double.valueOf(String.valueOf(currentProduct.price));
        holder.priceText.setText(String.format("%.2f TL", price));

        if (currentProduct.images != null && !currentProduct.images.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentProduct.images.get(0))
                    .placeholder(R.drawable.placeholde_image)
                    .error(R.drawable.errorimage_holder)
                    .into(holder.productImage);
        }

        holder.deleteToFav.setOnClickListener(v -> {
            favItems.remove(position);
            notifyDataSetChanged();
            Toast.makeText(holder.itemView.getContext(), "Ürün Favorilerden Silindi", Toast.LENGTH_SHORT).show();

            favoriteActivity.deleteItemsFromSharedPreferences(HomeActivity.FAV_ITEMS_KEY, favItems);

        });
    }

    @Override
    public int getItemCount() {
        return favItems.size();
    }

    // ViewHolder sınıfı
    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText, priceText;
        public ImageView productImage, deleteToFav;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            priceText = itemView.findViewById(R.id.priceText);
            productImage = itemView.findViewById(R.id.productImage);
            deleteToFav = itemView.findViewById(R.id.deleteToFav);
        }
    }

}
