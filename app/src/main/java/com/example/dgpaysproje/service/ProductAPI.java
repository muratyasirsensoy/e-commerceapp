package com.example.dgpaysproje.service;

import com.example.dgpaysproje.model.ProductModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductAPI {

    // GET, POST, UPDATE, DELETE

    @GET("products")
    Call<List<ProductModel>> getProducts();

    @GET("products")
    Call<List<ProductModel>> getProductsByCategory(@Query("categoryId") int categoryId);

    // Fiyata göre artan veya azalan sıralama
    @GET("products")
    Call<List<ProductModel>> getProductsSortedByPrice(
            @Query("order") String order,
            @Query("sortBy") String sortBy
    );
}
