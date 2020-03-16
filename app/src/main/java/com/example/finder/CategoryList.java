package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapters.CategoryListAdapter;
import data.CategoryListData;
import others.BottomAppBarEvent;

public class CategoryList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        TextView pageName = findViewById(R.id.page_name);
        pageName.setText("Categories");

        this.showCategories();
    }

    private void showCategories() {
        RecyclerView categoryItem = findViewById(R.id.category);
        List<CategoryListData> categoryListData = new ArrayList<>();
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Management & Business Community", "3,305,383"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Technology Community", "3,005,720"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Healthcare Community", "6,732,499"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Sales Community", "3,304,539"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Management & Business Community", "3,305,383"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Technology Community", "3,005,720"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Finance Community", "2,792,280"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Healthcare Community", "6,732,499"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Sales Community", "3,304,539"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Sales Community", "3,304,539"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Management & Business Community", "3,305,383"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Technology Community", "3,005,720"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Finance Community", "2,792,280"));
        CategoryListAdapter categoryListAdapter = new CategoryListAdapter(this, categoryListData);
        categoryItem.setLayoutManager(new GridLayoutManager(this, 1));
        categoryItem.setAdapter(categoryListAdapter);
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
