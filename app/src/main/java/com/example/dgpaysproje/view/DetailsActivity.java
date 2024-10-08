package com.example.dgpaysproje.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.dgpaysproje.R;
import com.example.dgpaysproje.model.ProductModel;
import com.google.gson.Gson;

public class DetailsActivity extends AppCompatActivity {

    private ImageView detailImageView;
    private TextView titleTextView, priceTextView, descTextView;
    private Button addToCartButton;
    private ProductModel currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detailImageView = findViewById(R.id.productImage);
        titleTextView = findViewById(R.id.titleText);
        priceTextView = findViewById(R.id.priceText);
        descTextView = findViewById(R.id.descText);
        addToCartButton = findViewById(R.id.addToCartButton);

        String imageUrl = getIntent().getStringExtra("image_url");
        String productTitle = getIntent().getStringExtra("product_title");
        String productPrice = getIntent().getStringExtra("product_price");
        String productDescription = getIntent().getStringExtra("product_description");

        titleTextView.setText(productTitle);
        priceTextView.setText(productPrice);
        descTextView.setText(productDescription);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholde_image)
                    .error(R.drawable.errorimage_holder)
                    .into(detailImageView);
        }

        currentProduct = new ProductModel(productTitle, Double.parseDouble(productPrice.replace(" TL", "")), productDescription, imageUrl);
    }

    public void buyNowClicked(View view) {
        addToCart(currentProduct);

        Intent intent = new Intent(DetailsActivity.this, CartActivity.class);
        startActivity(intent);
    }

    private void addToCart(ProductModel product) {
        HomeActivity.cartItems.add(product);  // Sepete ekliyoruz
        saveCartItemsToSharedPreferences();
    }

    private void saveCartItemsToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCartItems = gson.toJson(HomeActivity.cartItems);
        editor.putString("cartItems", jsonCartItems);
        editor.apply();
    }
}

