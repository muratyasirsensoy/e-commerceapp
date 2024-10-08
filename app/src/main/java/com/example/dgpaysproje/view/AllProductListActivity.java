package com.example.dgpaysproje.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dgpaysproje.R;
import com.example.dgpaysproje.adapter.RecyclerViewAdapter;
import com.example.dgpaysproje.model.ProductModel;

import java.util.ArrayList;

public class AllProductListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<ProductModel> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product_list);

        recyclerView = findViewById(R.id.recyclerViewProducts);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        allProducts = (ArrayList<ProductModel>) getIntent().getSerializableExtra("allProducts");

        recyclerViewAdapter = new RecyclerViewAdapter(allProducts, this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}

