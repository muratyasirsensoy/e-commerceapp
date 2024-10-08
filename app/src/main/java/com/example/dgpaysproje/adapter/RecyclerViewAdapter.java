package com.example.dgpaysproje.adapter;
import android.content.Context;
import android.content.Intent;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dgpaysproje.R;
import com.example.dgpaysproje.model.ProductModel;
import com.example.dgpaysproje.view.DetailsActivity;
import com.example.dgpaysproje.view.HomeActivity;
import java.util.ArrayList;
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RowHolder> {
    private ArrayList<ProductModel> productLists;
    private static final String TAG = "RecyclerViewAdapter";
    private Context context;
    public RecyclerViewAdapter(ArrayList<ProductModel> productLists, Context context) {
        this.productLists = productLists;
        this.context = context;
    }
    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.viewholder_product, parent, false);
        return new RowHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {
        holder.bind(productLists.get(position));
    }
    @Override
    public int getItemCount() {
        return productLists.size();
    }
    public class RowHolder extends RecyclerView.ViewHolder {
        TextView titleText, priceText;
        ImageView productImage, addToCart, addToFav;
        public RowHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            priceText = itemView.findViewById(R.id.priceText);
            productImage = itemView.findViewById(R.id.productImage);
            addToCart = itemView.findViewById(R.id.addToCart);
            addToFav = itemView.findViewById(R.id.addToFav);
        }
        public void bind(ProductModel productModel) {
            titleText.setText(productModel.title);
            priceText.setText(String.valueOf(productModel.price + " TL"));
            // Glide ile ürün görselini yükleme
            if (productModel.images != null && !productModel.images.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(productModel.images.get(0))
                        .placeholder(R.drawable.placeholde_image)
                        .error(R.drawable.errorimage_holder)
                        .override(300, 300)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(productImage);
                if (HomeActivity.favoriteItems.contains(productModel)) {
                    addToFav.setImageResource(R.drawable.fav_fulled);
                } else {
                    addToFav.setImageResource(R.drawable.fav_item);
                }
                addToFav.setOnClickListener(v -> {
                    if (HomeActivity.favoriteItems.contains(productModel)) {
                        HomeActivity.favoriteItems.remove(productModel);
                        addToFav.setImageResource(R.drawable.fav_item);
                        Toast.makeText(context, "Favorilerden çıkarıldı", Toast.LENGTH_SHORT).show();
                        if (context instanceof HomeActivity) {
                            ((HomeActivity) context).deleteItemsFromSharedPreferences(HomeActivity.FAV_ITEMS_KEY, HomeActivity.favoriteItems);
                        }
                    } else {
                        HomeActivity.favoriteItems.add(productModel);
                        addToFav.setImageResource(R.drawable.fav_fulled);
                        Toast.makeText(context, "Favorilere eklendi", Toast.LENGTH_SHORT).show();
                        if (context instanceof HomeActivity) {
                            ((HomeActivity) context).saveItemsToSharedPreferences(HomeActivity.FAV_ITEMS_KEY, HomeActivity.favoriteItems);
                        }
                        notifyDataSetChanged();
                    }
                });
                addToCart.setOnClickListener(v -> {
                    if (!HomeActivity.cartItems.contains(productModel)) {
                        HomeActivity.cartItems.add(productModel);
                        Toast.makeText(context, productModel.title + " sepete eklendi.", Toast.LENGTH_SHORT).show();
                        if (context instanceof HomeActivity) {
                            ((HomeActivity) context).saveItemsToSharedPreferences(HomeActivity.CART_ITEMS_KEY, HomeActivity.cartItems);
                        }
                    } else {
                        HomeActivity.cartItems.remove(productModel);
                        Toast.makeText(context, productModel.title + " sepetten çıkarıldı.", Toast.LENGTH_SHORT).show();
                        if (context instanceof HomeActivity) {
                            ((HomeActivity) context).deleteItemsFromSharedPreferences(HomeActivity.CART_ITEMS_KEY, HomeActivity.cartItems);
                        }
                        notifyDataSetChanged();
                    }
                });
            } else {
                Log.w(TAG, "Bu ürün için görsel bulunamadı");
            }
            productImage.setOnClickListener(v -> {
                if (productModel.images != null && !productModel.images.isEmpty()) {
                    Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
                    intent.putExtra("image_url", productModel.images.get(0));
                    intent.putExtra("product_title", productModel.title);
                    intent.putExtra("product_price", productModel.price + " TL");
                    intent.putExtra("product_description", productModel.description);
                    itemView.getContext().startActivity(intent);
                } else {
                    Log.e(TAG, "Resim URL'si boş, detay sayfası başlatılamadı.");
                }
            });
        }
    }
}