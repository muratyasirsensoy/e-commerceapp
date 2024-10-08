package com.example.dgpaysproje.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dgpaysproje.R;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentDetailsActivity extends AppCompatActivity {

    Button payButton;
    String PublishableKey = "pk_test_51Q2YnIRveENa3Uam9dU8xA9pJbYbbrcaq0CnSDnc3MbkKTDumjQ3LBwM7zt6f7dr4v5xmKSUOucnwQy1gribImYX00p0HbaCW8";
    String SecretKey = "sk_test_51Q2YnIRveENa3UamlGfZOzoWc8h3BQfxb7ZVyUFavKVNaUQF1mTUKGZQ9gRohoflYZy6Yx31SArbETwYA0bhFArk0087mic2i7";
    String CustomerId;
    String EphericalKey;
    String ClientSecret;
    PaymentSheet paymentSheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        Spinner countrySpinner = findViewById(R.id.countrySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        payButton = findViewById(R.id.payButton);
        // Stripe Payment Configuration
        PaymentConfiguration.init(this, PublishableKey);

        // Initialize PaymentSheet
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        payButton.setOnClickListener(view -> paymentFlow());

        // Stripe Müşteri Oluşturma (Volley İsteği)
        createCustomer();
    }

    private void createCustomer() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            CustomerId = object.getString("id");
                            Toast.makeText(PaymentDetailsActivity.this, "Customer ID: " + CustomerId, Toast.LENGTH_SHORT).show();
                            Log.d("Stripe", "Customer ID obtained: " + CustomerId);
                            // Müşteri oluşturuldu, ephemeral key almak için devam et
                            getEphemeralKey();
                        } catch (JSONException e) {
                            Log.e("Stripe", "Error parsing customer ID: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Stripe", "Error creating customer: " + error.getLocalizedMessage());
                Toast.makeText(PaymentDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SecretKey);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getEphemeralKey() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
                            Toast.makeText(PaymentDetailsActivity.this, "Ephemeral Key: " + EphericalKey, Toast.LENGTH_SHORT).show();
                            Log.d("Stripe", "Ephemeral Key obtained: " + EphericalKey);
                            // Ephemeral key alındı, ödeme istemini oluştur
                            getClientSecret();
                        } catch (JSONException e) {
                            Log.e("Stripe", "Error parsing ephemeral key: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Stripe", "Error fetching ephemeral key: " + error.getLocalizedMessage());
                Toast.makeText(PaymentDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SecretKey);
                headers.put("Stripe-Version", "2020-08-27"); // Doğru API versiyonunu kullan
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", CustomerId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getClientSecret() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            Toast.makeText(PaymentDetailsActivity.this, "Client Secret: " + ClientSecret, Toast.LENGTH_SHORT).show();
                            Log.d("Stripe", "Client Secret obtained: " + ClientSecret);
                        } catch (JSONException e) {
                            Log.e("Stripe", "Error parsing client secret: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Stripe", "Error fetching client secret: " + error.getLocalizedMessage());
                Toast.makeText(PaymentDetailsActivity.this, "" + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SecretKey);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void paymentFlow() {
        if (ClientSecret == null || EphericalKey == null || CustomerId == null) {
            Toast.makeText(this, "Payment details are not ready yet. Please wait.", Toast.LENGTH_SHORT).show();
            return; // Eğer değerler boşsa, ödeme işlemini başlatma
        }

        Log.d("Stripe", "Payment flow started with ClientSecret: " + ClientSecret);

        // Eğer tüm değerler doluysa ödeme işlemini başlat
        paymentSheet.presentWithPaymentIntent(ClientSecret, new PaymentSheet.Configuration("Murat Yasir Sensoy",
                new PaymentSheet.CustomerConfiguration(CustomerId, EphericalKey)));
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
            Log.d("Stripe", "Payment completed successfully");
        } else {
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
            Log.d("Stripe", "Payment failed or cancelled");
        }
    }
}

