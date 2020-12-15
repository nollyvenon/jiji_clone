package com.jonnyup.nairarefill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapters.SearchRecyclerViewAdapter;
import data.SearchData;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity {

    List<SearchData> searchList = new ArrayList<>();
    SearchRecyclerViewAdapter searchRecyclerViewAdapter;
    RecyclerView findView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EditText search = findViewById(R.id.search);
        search.requestFocus();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                getSearch(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSearch(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findView = findViewById(R.id.search_list);
        searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(Search.this, searchList);
        findView.setLayoutManager(new GridLayoutManager(this, 1));
        findView.setAdapter(searchRecyclerViewAdapter);
    }

    private void getSearch(CharSequence searchText) {
        Call<List<SearchData>> call = ApiClient.connect().getSearch(searchText);
        call.enqueue(new Callback<List<SearchData>>() {
            @Override
            public void onResponse(@NonNull Call<List<SearchData>> call, @NonNull Response<List<SearchData>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(Search.this, response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                searchList = response.body();
                searchRecyclerViewAdapter.setData(searchList);
            }

            @Override
            public void onFailure(@NonNull Call<List<SearchData>> call, @NonNull Throwable t) {
                Toast.makeText(Search.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void goBack(View view) {
        finish();
    }
}
