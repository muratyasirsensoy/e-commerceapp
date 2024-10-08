package com.example.dgpaysproje.adapter;

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
import com.example.dgpaysproje.view.CartActivity;
import com.example.dgpaysproje.view.HomeActivity;

import java.util.ArrayList;

public class CartViewAdapter extends RecyclerView.Adapter<CartViewAdapter.CartViewHolder> {

    private ArrayList<ProductModel> cartItems;
    private CartActivity cartActivity;

    public CartViewAdapter(ArrayList<ProductModel> cartItems, CartActivity cartActivity) {
        this.cartItems = cartItems;
        this.cartActivity = cartActivity;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductModel currentProduct = cartItems.get(position);

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

        holder.deleteToCart.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyDataSetChanged();
            Toast.makeText(holder.itemView.getContext(), "Ürün Sepetten Silindi", Toast.LENGTH_SHORT).show();

            cartActivity.checkCartStatus();
            cartActivity.deleteItemsFromSharedPreferences(HomeActivity.CART_ITEMS_KEY, cartItems);

        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, priceText, numberCartText;
        ImageView productImage, deleteToCart;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            priceText = itemView.findViewById(R.id.priceText);
            productImage = itemView.findViewById(R.id.productImage);
            deleteToCart = itemView.findViewById(R.id.deleteToCart);
        }
    }
}

