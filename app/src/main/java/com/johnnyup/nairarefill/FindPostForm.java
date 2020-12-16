package com.johnnyup.nairarefill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    private static final int FILE_PICKER_REQUEST_CODE = 1;
    EditText description, priceOne, priceTwo, title;
    String categoryText, fileString, chatChoiceText;
    HashMap<Integer, String> checkedList;
    List<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_post_form);
        TextView pageName = findViewById(R.id.page_name);
        pageName.setText("Tell us what you need done");

//        ImageView bottomBarImg = findViewById(R.id.img_find_bottom_bar);
//        TextView bottomBarTitle = findViewById(R.id.title_find_bottom_bar);
//        bottomBarImg.setColorFilter(getResources().getColor(R.color.colorPrimary));
//        bottomBarTitle.setTextColor(getResources().getColor(R.color.colorPrimary));

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
                if (parent.getItemAtPosition(position).equals("Select Category")) {
                } else {
                    categoryText = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categoryText = "";
            }
        });

        List<String> chatChoice = new ArrayList<>();
        chatChoice.add("No");
        chatChoice.add("Yes");

        Spinner chatChoiceSpinner = findViewById(R.id.chat_choice);
        ArrayAdapter<String> chatChoiceAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, chatChoice);
        chatChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chatChoiceSpinner.setAdapter(chatChoiceAdapter);

        chatChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chatChoiceText = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chatChoiceText = parent.getItemAtPosition(0).toString();
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
                for (CategoryListData category : categories) {
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
        priceOne = findViewById(R.id.find_price_1);
        priceTwo = findViewById(R.id.find_price_2);
    }

    public void launchPicker(View view) {
        ActivityCompat.requestPermissions(FindPostForm.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                FILE_PICKER_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == FILE_PICKER_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("application/pdf/*");
                startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Permission to access file storage not granted", Toast.LENGTH_LONG).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            Uri uri = data.getData();
            String uriString = data.toString();
            File file = new File(uriString);
            String path = file.getAbsolutePath();
            String fileName = null;

            if (uriString.contains("content://")) {
                assert uri != null;
                try (Cursor cursor = this.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            } else if (uriString.startsWith("file://")) {
                fileName = file.getName();
            }

            TextView file_name = findViewById(R.id.file_name);
            file_name.setText(fileName);

            try {
                assert uri != null;
                InputStream is = getContentResolver().openInputStream(uri);
                fileString = pdfToString(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String pdfToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }

        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    public void addFind(final View view) {
        initForm();

        String descriptionText = description.getText().toString();
        String titleText = title.getText().toString();
        String priceTextOne = priceOne.getText().toString();
        String priceTextTwo = priceTwo.getText().toString();
        final Button btn = findViewById(R.id.post_find);
        String benefit = "";

        String priceText = priceTextOne;
        if(!priceTextTwo.equalsIgnoreCase("")) {
            priceText = priceTextOne + "-" +priceTextTwo;
        }

        if (descriptionText.isEmpty()) {
            Toast.makeText(this, "Description field is compulsory", Toast.LENGTH_LONG).show();
            return;
        }

        if (titleText.isEmpty()) {
            Toast.makeText(this, "Title field is compulsory", Toast.LENGTH_LONG).show();
            return;
        }

        if (categoryText.isEmpty()) {
            Toast.makeText(this, "Category field is compulsory", Toast.LENGTH_LONG).show();
            return;
        }

        view.setClickable(false);
        btn.setText(R.string.submitting);

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();

        Call<Finds> call = ApiClient.connect().addFind(
                descriptionText, titleText, priceText, "" /*benefit.substring(0, benefit.length() - 1)*/, categoryText,
                fileString, chatChoiceText, a.getAuth()
        );
        call.enqueue(new Callback<Finds>() {
            @Override
            public void onResponse(@NonNull Call<Finds> call, @NonNull Response<Finds> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(FindPostForm.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                view.setClickable(true);
                btn.setText("Add Find");

                Finds find = response.body();
                assert find != null;
                if (Boolean.parseBoolean(find.getStatus())) {
                    Intent intent = new Intent(FindPostForm.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(intent);
                    FindPostForm.this.finish();
                    return;
                }

                Toast.makeText(FindPostForm.this, "Its possible you have been banned. Check your profile to check if ban stamp exist", Toast.LENGTH_LONG).show();

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