package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapters.CategoryListAdapter;
import data.CategoryListData;
import others.BottomAppBarEvent;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        TextView pageName = findViewById(R.id.page_name);
        pageName.setText("Categories");

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomAppBarEvent.goToSearchActivity(CategoryList.this);
            }
        });

        this.showCategories();
    }

    private void showCategories() {
        final ProgressBar progressBar = findViewById(R.id.progress_circular);

        Call<List<CategoryListData>> call = ApiClient.connect().getAllCategories();
        call.enqueue(new Callback<List<CategoryListData>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryListData>> call, @NonNull Response<List<CategoryListData>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(CategoryList.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                RecyclerView categoryItem = findViewById(R.id.category);
                List<CategoryListData> categories = response.body();
                assert categories != null;
                if(categories.size() == 0) progressBar.setVisibility(View.INVISIBLE);

                List<CategoryListData> categoryListData = new ArrayList<>();
                for(CategoryListData category : categories) {
                    categoryListData.add(new CategoryListData(category.getId(), category.getImage(), category.getName(), category.getCount()));
                }
                CategoryListAdapter categoryListAdapter = new CategoryListAdapter(CategoryList.this, categoryListData, "");
                categoryItem.setLayoutManager(new GridLayoutManager(CategoryList.this, 1));
                categoryItem.setAdapter(categoryListAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryListData>> call, @NonNull Throwable t) {
                //Toast.makeText(CategoryList.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(CategoryList.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(CategoryList.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(CategoryList.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(CategoryList.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(CategoryList.this);
        bottomAppBarEvent.goToMessageListActivity();
    }
}
