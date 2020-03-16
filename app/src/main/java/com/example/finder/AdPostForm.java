package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import others.BottomAppBarEvent;

public class AdPostForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_post_form);

        TextView pageName = findViewById(R.id.page_name);
        pageName.setText("Post Service You Render");

        List<String> categoryList = new ArrayList<>();
        categoryList.add("Select Category");
        categoryList.add("Furniture");
        categoryList.add("Doctor");
        categoryList.add("Private Teacher");
        categoryList.add("Software Developer");
        categoryList.add("Car Technician");

        Spinner categorySpinner = findViewById(R.id.category_list);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Select Category")) {
                    Toast.makeText(AdPostForm.this, "Please select a category", Toast.LENGTH_LONG).show();
                } else {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(AdPostForm.this, item, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> promotionData = new ArrayList<>();
        promotionData.add("Free Delivery");
        promotionData.add("Get Two, One free");
        promotionData.add("50% discount");
        promotionData.add("10% discount");
        ListView promotionalList = findViewById(R.id.promotional_list);
        promotionalList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<String> promotionalAdapter = new ArrayAdapter<>(this, R.layout.promotional_item, R.id.promotion, promotionData);
        promotionalList.setAdapter(promotionalAdapter);
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPostForm.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPostForm.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPostForm.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPostForm.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPostForm.this);
        bottomAppBarEvent.goToMessageListActivity();
    }
}
