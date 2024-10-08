package com.example.dgpaysproje.view;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dgpaysproje.R;

public class AddressInputActivity extends AppCompatActivity {

    private Spinner provinceSpinner;
    private Spinner districtSpinner;
    private Spinner neighborhoodSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_input);

        // Spinner'ları buluyoruz
        provinceSpinner = findViewById(R.id.provinceSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        neighborhoodSpinner = findViewById(R.id.neighborhoodSpinner);

        ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter.createFromResource(this,
                R.array.province_list, android.R.layout.simple_spinner_item);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedProvince = provinceSpinner.getSelectedItem().toString();
                updateDistrictSpinner(selectedProvince);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }
    private void updateDistrictSpinner(String selectedProvince) {
        int districtArray = 0;

        switch (selectedProvince) {
            case "İstanbul":
                districtArray = R.array.district_list_istanbul;
                break;
            case "Ankara":
                districtArray = R.array.district_list_ankara;
                break;
            case "İzmir":
                districtArray = R.array.district_list_izmir;
                break;
            case "Bursa":
                districtArray = R.array.district_list_bursa;
                break;
            default:
                Toast.makeText(this, "Bu il için ilçe bulunamadı.", Toast.LENGTH_SHORT).show();
                break;
        }

        if (districtArray != 0) {
            ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(this,
                    districtArray, android.R.layout.simple_spinner_item);
            districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            districtSpinner.setAdapter(districtAdapter);

            districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    updateNeighborhoodSpinner();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
        }
    }

    private void updateNeighborhoodSpinner() {
        ArrayAdapter<CharSequence> neighborhoodAdapter = ArrayAdapter.createFromResource(this,
                R.array.neighborhood_list, android.R.layout.simple_spinner_item);
        neighborhoodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        neighborhoodSpinner.setAdapter(neighborhoodAdapter);
    }
}

