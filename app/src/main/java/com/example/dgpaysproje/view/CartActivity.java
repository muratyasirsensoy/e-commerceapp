package com.example.dgpaysproje.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dgpaysproje.R;
import com.example.dgpaysproje.adapter.CartViewAdapter;
import com.example.dgpaysproje.model.ProductModel;
import com.example.dgpaysproje.view.HomeActivity;
import com.google.gson.Gson;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CartViewAdapter cartViewAdapter;
    ArrayList<ProductModel> cartItems;
    LinearLayout emptyCart;
    ScrollView scrollView;
    ImageView goToPayment;
    ImageView goToAddress;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        emptyCart = findViewById(R.id.emptyCart);
        scrollView = findViewById(R.id.scrollView3);
        recyclerView = findViewById(R.id.recyclerView2);
        cartItems = HomeActivity.cartItems;
        goToPayment = findViewById(R.id.goToPayment);
        goToAddress = findViewById(R.id.goToAddress);

        checkCartStatus();
        updateCartTotal();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartViewAdapter = new CartViewAdapter(cartItems, this);
        recyclerView.setAdapter(cartViewAdapter);

        cartViewAdapter.notifyDataSetChanged();

        goToPayment.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, PaymentDetailsActivity.class);
            startActivity(intent);
        });

        ImageView goToAddress = findViewById(R.id.goToAddress);
        goToAddress.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, AddressInputActivity.class);
            startActivity(intent);
        });
    }

    public void confirmButtonClicked(View view) {
        List<String> productIds = new ArrayList<>();
        double totalPrice = 0.0;

        for (ProductModel product : cartItems) {
            productIds.add(String.valueOf(product.id));
            if (product.price instanceof Double) {
                totalPrice += (Double) product.price;
            } else if (product.price instanceof String) {
                totalPrice += Double.parseDouble((String) product.price);
            }
        }

        saveOrderToFirestore(productIds, totalPrice);

        showOrderSuccessDialog();
    }

    private void saveOrderToFirestore(List<String> productIds, double totalPrice) {
        String userId = auth.getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis();

        List<String> productTitles = new ArrayList<>();
        for (ProductModel product : cartItems) {
            productTitles.add(product.title);
        }

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("productIds", productIds);
        orderData.put("totalPrice", totalPrice);
        orderData.put("productTitles", productTitles);
        orderData.put("timestamp", timestamp);

        db.collection("orders")
                .add(orderData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Sipariş başarıyla kaydedildi: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Sipariş kaydedilemedi", e);
                });
    }

    private void showOrderSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setTitle("Siparişiniz Oluşturuldu");
        builder.setMessage("Siparişiniz başarıyla oluşturuldu.");

        builder.setPositiveButton("Tamam", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void checkCartStatus() {
        if (cartItems == null || cartItems.isEmpty()) {
            emptyCart.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        } else {
            emptyCart.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    public void updateCart() {
        checkCartStatus();
    }

    private void updateCartTotal() {
        double totalPrice = 0.0;

        for (ProductModel product : HomeActivity.cartItems) {
            if (product.price instanceof Double) {
                totalPrice += (Double) product.price;
            } else if (product.price instanceof String) {
                totalPrice += Double.parseDouble((String) product.price);
            }
        }

        TextView totalFeeText = findViewById(R.id.totalFeeText);
        totalFeeText.setText(String.format("%.2f TL", totalPrice));

        double totalWithShipping = totalPrice + 69;
        TextView totalText = findViewById(R.id.totalText);
        totalText.setText(String.format("%.2f TL", totalWithShipping));
    }

    public void deleteItemsFromSharedPreferences(String key, ArrayList<ProductModel> items) {
        SharedPreferences sharedPreferences = getSharedPreferences(HomeActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonItems = gson.toJson(items);
        editor.putString(key, jsonItems);
        editor.apply();
        Log.d("DELETE_SHARED_PREFS", "SharedPreferences güncellendi: " + key + " - Öğe sayısı: " + items.size());
    }
}

