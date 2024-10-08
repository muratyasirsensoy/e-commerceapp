package com.example.dgpaysproje.view;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dgpaysproje.R;
import com.example.dgpaysproje.adapter.FavoriteAdapter;
import com.example.dgpaysproje.model.ProductModel;
import com.google.gson.Gson;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FavoriteAdapter favoriteAdapter;
    ArrayList<ProductModel> favoriteItems;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.recyclerViewFav);
        favoriteItems = HomeActivity.favoriteItems;
        scrollView = findViewById(R.id.scrollViewFav);

        // RecyclerView ayarları
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteAdapter = new FavoriteAdapter(favoriteItems, this);
        recyclerView.setAdapter(favoriteAdapter);

        favoriteAdapter.notifyDataSetChanged();
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
