package com.example.dgpaysproje.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dgpaysproje.R;
import com.example.dgpaysproje.adapter.RecyclerViewAdapter;
import com.example.dgpaysproje.model.ProductModel;
import com.example.dgpaysproje.service.ProductAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.escuelajs.co/api/v1/";
    private Retrofit retrofit;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<ProductModel> productModels;
    private TextView categoryTitle;

    private static final String TAG = "CategoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // RecyclerView ve LayoutManager ayarı
        recyclerView = findViewById(R.id.recyclerViewCategories);
        categoryTitle = findViewById(R.id.categoryTitle);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 sütunlu grid
        recyclerView.setLayoutManager(gridLayoutManager);

        String categoryName = getIntent().getStringExtra("categoryName");
        if (categoryName != null) {
            categoryTitle.setText(categoryName);
        }

        // Retrofit ve JSON ayarları
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        int categoryId = getIntent().getIntExtra("categoryId", -1);

        if (categoryId != -1) {
            getProductsByCategory(categoryId);
        }
    }

    // API'den kategoriye göre verileri çek
    private void getProductsByCategory(int categoryId) {
        ProductAPI productAPI = retrofit.create(ProductAPI.class);
        Call<List<ProductModel>> call = productAPI.getProductsByCategory(categoryId);

        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productModels = new ArrayList<>(response.body());
                    recyclerViewAdapter = new RecyclerViewAdapter(productModels, CategoryActivity.this);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    Log.d(TAG, "Kategoriye göre API'den veri alındı.");
                } else {
                    Log.e(TAG, "API'den alınan yanıt geçersiz.");
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Log.e(TAG, "API çağrısı başarısız: " + t.getMessage());
            }
        });
    }

    public void increasingPriceClicked(View view) {
        if (productModels != null) {
            Collections.sort(productModels, new Comparator<ProductModel>() {
                @Override
                public int compare(ProductModel p1, ProductModel p2) {
                    return Double.compare(Double.parseDouble(p1.price.toString()), Double.parseDouble(p2.price.toString()));
                }
            });
            recyclerViewAdapter.notifyDataSetChanged();
            Log.d(TAG, "Fiyat artan şekilde sıralandı.");
        }
    }

    public void decreasingPriceClicked(View view) {
        if (productModels != null) {
            Collections.sort(productModels, new Comparator<ProductModel>() {
                @Override
                public int compare(ProductModel p1, ProductModel p2) {
                    return Double.compare(Double.parseDouble(p2.price.toString()), Double.parseDouble(p1.price.toString()));
                }
            });
            recyclerViewAdapter.notifyDataSetChanged();
            Log.d(TAG, "Fiyat azalan şekilde sıralandı.");
        }
    }

    /*public void addToCart(ProductModel product) {
        HomeActivity.cartItems.add(product);
        Log.d(TAG, product.title + " sepete eklendi.");
    }*/
}

