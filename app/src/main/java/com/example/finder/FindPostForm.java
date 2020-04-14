package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.CategoryListData;
import data.Finds;
import others.BottomAppBarEvent;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindPostForm extends AppCompatActivity {
    EditText description, price, title;
    String categoryText;
    HashMap<Integer, String> checkedList;
    List<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_post_form);
        TextView pageName = findViewById(R.id.page_name);
        pageName.setText("Tell us what you need done");

        ImageView bottomBarImg = findViewById(R.id.img_find_bottom_bar);
        TextView bottomBarTitle = findViewById(R.id.title_find_bottom_bar);
        bottomBarImg.setColorFilter(getResources().getColor(R.color.colorPrimary));
        bottomBarTitle.setTextColor(getResources().getColor(R.color.colorPrimary));

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomAppBarEvent.goToSearchActivity(FindPostForm.this);
            }
        });

        categoryList = new ArrayList<>();
        categoryList.add("Select Category");

        showCategories();

        Spinner categorySpinner = findViewById(R.id.category_list);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Select Category")) {
                } else {
                    categoryText = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        ArrayList<String> promotionData = new ArrayList<>();
//        promotionData.add("Free Delivery");
//        promotionData.add("Get Two, One free");
//        promotionData.add("50% discount");
//        promotionData.add("10% discount");
//        final ListView promotionalList = findViewById(R.id.promotional_list);
//        promotionalList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        ArrayAdapter<String> promotionalAdapter = new ArrayAdapter<>(this, R.layout.item_promotional, R.id.promotion, promotionData);
//
//        checkedList = new HashMap<>();
//        promotionalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (checkedList.containsKey(position)) {
//                    checkedList.remove(position);
//                } else {
//                    checkedList.put(position, promotionalList.getItemAtPosition(position).toString());
//                }
//            }
//        });
//
//        promotionalList.setAdapter(promotionalAdapter);
    }

    private void showCategories() {
        Call<List<CategoryListData>> call = ApiClient.connect().getCategories();
        call.enqueue(new Callback<List<CategoryListData>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryListData>> call, @NonNull Response<List<CategoryListData>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(FindPostForm.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<CategoryListData> categories = response.body();

                assert categories != null;
                for(CategoryListData category : categories) {
                    categoryList.add(category.getName());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryListData>> call, @NonNull Throwable t) {
                //Toast.makeText(FindPostForm.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initForm() {
        description = findViewById(R.id.description);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
    }

    public void addFind(View view) {
        initForm();

        String descriptionText = description.getText().toString();
        String titleText = title.getText().toString();
        String priceText = price.getText().toString();
        String benefit = "";

        if (descriptionText.isEmpty() || titleText.isEmpty() || priceText.isEmpty() || categoryText.isEmpty()) {
            Toast.makeText(this, "All fields are compulsory", Toast.LENGTH_LONG).show();
            return;
        }
//        for (String checked : checkedList.values()) {
//            benefit = benefit + checked + ",";
//        }

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();

        Call<Finds> call = ApiClient.connect().addFind(
                descriptionText, titleText, priceText, "" /*benefit.substring(0, benefit.length() - 1)*/, categoryText,
                a.getAuth()
        );
        call.enqueue(new Callback<Finds>() {
            @Override
            public void onResponse(@NonNull Call<Finds> call, @NonNull Response<Finds> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(FindPostForm.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Finds find = response.body();
                assert find != null;
                if (Boolean.parseBoolean(find.getStatus())) {
                    Intent intent = new Intent(FindPostForm.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Finds> call, @NonNull Throwable t) {
                Toast.makeText(FindPostForm.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindPostForm.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindPostForm.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindPostForm.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindPostForm.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindPostForm.this);
        bottomAppBarEvent.goToMessageListActivity();
    }
}