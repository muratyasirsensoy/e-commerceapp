package com.example.dgpaysproje.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.dgpaysproje.R;
import com.example.dgpaysproje.adapter.RecyclerViewAdapter;
import com.example.dgpaysproje.model.ProductModel;
import com.example.dgpaysproje.service.ProductAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    public static final String FAV_ITEMS_KEY = "favoriteItems";
    public static final String CART_ITEMS_KEY = "cartItems";
    private static final String BASE_URL = "https://api.escuelajs.co/api/v1/";
    public static final String PREFS_NAME = "MyPrefs";
    private ArrayList<ProductModel> productModels;
    private Retrofit retrofit;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    public static ArrayList<ProductModel> cartItems = new ArrayList<>();
    public static ArrayList<ProductModel> favoriteItems = new ArrayList<>();
    private ImageView cartButton, favButton, profileButton;
    private ImageView tableCategory, sneakersCategory, hatCategory, headphoneCategory, tshirtCategory;
    private TextView seeAll;
    private FirebaseAuth auth;
    private TextView nameText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        auth = FirebaseAuth.getInstance();
        initializeViews();
        loadItemsFromSharedPreferences();
        setupRecyclerView();
        setupRetrofit();
        getDataFromAPI();
        displayUserEmail();
    }
    private void initializeViews() {
        seeAll = findViewById(R.id.seeAll);
        seeAll.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AllProductListActivity.class);
            intent.putExtra("allProducts", productModels);
            startActivity(intent);
        });
        nameText = findViewById(R.id.nameText);
        cartButton = findViewById(R.id.cartButton);
        favButton = findViewById(R.id.favButton);
        cartButton.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, CartActivity.class)));
        favButton.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, FavoriteActivity.class)));
        initializeCategoryViews();
    }
    private void initializeCategoryViews() {
        tableCategory = findViewById(R.id.tableCategory);
        sneakersCategory = findViewById(R.id.sneakersCategory);
        hatCategory = findViewById(R.id.hatCategory);
        headphoneCategory = findViewById(R.id.headphoneCategory);
        tshirtCategory = findViewById(R.id.tshirtCategory);
        setClickListenerForCategory(tableCategory, 1, "Belirsizler");
        setClickListenerForCategory(sneakersCategory, 2, "Elektronik Ürünler");
        setClickListenerForCategory(hatCategory, 3, "Masa ve Sandalyeler");
        setClickListenerForCategory(headphoneCategory, 4, "Ayakkabılar");
        setClickListenerForCategory(tshirtCategory, 5, "Diğer Ürünler");
    }
    private void setClickListenerForCategory(ImageView categoryView, int categoryId, String categoryName) {
        categoryView.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("categoryName", categoryName);
            startActivity(intent);
        });
    }
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
    }
    private void setupRetrofit() {
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    private void getDataFromAPI() {
        ProductAPI productAPI = retrofit.create(ProductAPI.class);
        Call<List<ProductModel>> call = productAPI.getProducts();
        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful()) {
                    List<ProductModel> responseList = response.body();
                    if (responseList != null && !responseList.isEmpty()) {
                        productModels = new ArrayList<>(responseList);
                        recyclerViewAdapter = new RecyclerViewAdapter(productModels, HomeActivity.this);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Log.e("HomeActivity", "API Call failed: " + t.getMessage());
            }
        });
    }
    private void displayUserEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                nameText.setText(email);
            }
        }
    }
    public void saveItemsToSharedPreferences(String key, ArrayList<ProductModel> items) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonItems = gson.toJson(items);
        editor.putString(key, jsonItems);
        editor.apply();
        Log.d("SAVE_SHARED_PREFS", "SharedPreferences kaydedildi: " + key + " - Öğe sayısı: " + items.size());
    }
    public void deleteItemsFromSharedPreferences(String key, ArrayList<ProductModel> items) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonItems = gson.toJson(items);
        editor.putString(key, jsonItems);
        editor.apply();
        Log.d("DELETE_SHARED_PREFS", "SharedPreferences güncellendi: " + key + " - Öğe sayısı: " + items.size());
    }
    public void loadItemsFromSharedPreferences() {
        cartItems = loadListFromSharedPreferences(CART_ITEMS_KEY);
        Log.d("LOAD_CART", "Yüklenen sepet öğeleri: " + cartItems.size());
        favoriteItems = loadListFromSharedPreferences(FAV_ITEMS_KEY);
        Log.d("LOAD_FAVORITES", "Yüklenen favori öğeleri: " + favoriteItems.size());
    }
    private ArrayList<ProductModel> loadListFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonItems = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<ProductModel>>() {}.getType();
        ArrayList<ProductModel> items = gson.fromJson(jsonItems, type);
        return items != null ? items : new ArrayList<>();
    }
    public void goToSneakers(View view) {
        int sneakersCategoryId = 4;
        String sneakersCategoryName = "Ayakkabılar";
        Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
        intent.putExtra("categoryId", sneakersCategoryId);
        intent.putExtra("categoryName", sneakersCategoryName);
        startActivity(intent);
    }
}