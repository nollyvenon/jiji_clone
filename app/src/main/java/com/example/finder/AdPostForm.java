package com.example.finder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FileUtils;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.Ads;
import data.Benefit;
import data.CategoryListData;
import de.hdodenhof.circleimageview.CircleImageView;
import others.BottomAppBarEvent;
import others.Constants;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdPostForm extends AppCompatActivity {

    private static final int REQUEST_CODE_GALLERY = 999;
    CircleImageView image;
    Bitmap bitmap;

    public ProgressBar progressBar;
    public static final int FILE_PICKER_REQUEST_CODE = 1;
    AdPoster adPoster;

    EditText description, price, title;
    String categoryText, fileString, adsVisibilityText;
    HashMap<Integer, String> checkedList;
    List<String> categoryList;
    List<String> adVisibility;
    ArrayList<String> promotionData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_post_form);

        ImageView bottomBarImg = findViewById(R.id.img_ad_bottom_bar);
        TextView bottomBarTitle = findViewById(R.id.title_ad_bottom_bar);
        bottomBarImg.setColorFilter(getResources().getColor(R.color.colorPrimary));
        bottomBarTitle.setTextColor(getResources().getColor(R.color.colorPrimary));

        TextView pageName = findViewById(R.id.page_name);
        pageName.setText(R.string.post_service);

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
            }
        });

        adVisibility = new ArrayList<>();
        adVisibility.add("Selected Category");
        adVisibility.add("All Categories (Premium)");
        adVisibility.add("During Search");

        Spinner adsVisibility = findViewById(R.id.ads_visibility);
        ArrayAdapter<String> adsVisibilityAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, adVisibility);
        adsVisibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adsVisibility.setAdapter(adsVisibilityAdapter);

        adsVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().contains("All Categories")) {
                    getUser(parent, position);
                } else {
                    adsVisibilityText = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        getBenefits();

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomAppBarEvent.goToSearchActivity(AdPostForm.this);
            }
        });

    }

    private void getBenefits() {
        Call<List<Benefit>> call = ApiClient.connect().getBenefits();
        call.enqueue(new Callback<List<Benefit>>() {
            @Override
            public void onResponse(@NonNull Call<List<Benefit>> call, @NonNull Response<List<Benefit>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AdPostForm.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Benefit> benefits = response.body();

                assert benefits != null;
                for (Benefit benefit : benefits) {
                    promotionData.add(benefit.getBenefit());
                }

                final ListView promotionalList = findViewById(R.id.promotional_list);
                promotionalList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                ArrayAdapter<String> promotionalAdapter = new ArrayAdapter<>(AdPostForm.this, R.layout.item_promotional, R.id.promotion, promotionData);

                checkedList = new HashMap<>();
                promotionalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (checkedList.containsKey(position)) {
                            checkedList.remove(position);
                        } else {
                            checkedList.put(position, promotionalList.getItemAtPosition(position).toString());
                        }
                    }
                });

                promotionalList.setAdapter(promotionalAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Benefit>> call, @NonNull Throwable t) {
            }
        });
    }

    private void showCategories() {
        Call<List<CategoryListData>> call = ApiClient.connect().getCategories();
        call.enqueue(new Callback<List<CategoryListData>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryListData>> call, @NonNull Response<List<CategoryListData>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AdPostForm.this, "" + response.code(), Toast.LENGTH_LONG).show();
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
            }
        });
    }

    public void initForm() {
        description = findViewById(R.id.ad_description);
        title = findViewById(R.id.ad_title);
        price = findViewById(R.id.ad_price);
    }

    public void addAd(final View view) {
        initForm();

        String descriptionText = description.getText().toString();
        String titleText = title.getText().toString();
        String priceText = price.getText().toString();
        StringBuilder benefit = new StringBuilder();

        final Button btn = findViewById(R.id.post_ad);

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

        for (String checked : checkedList.values()) {
            benefit.append(checked).append(",");
        }

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();

        Call<Ads> call = ApiClient.connect().addAd(
                descriptionText, titleText, priceText, benefit.toString(), imageToString(), categoryText, fileString,
                a.getAuth()
        );
        call.enqueue(new Callback<Ads>() {
            @Override
            public void onResponse(@NonNull Call<Ads> call, @NonNull Response<Ads> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AdPostForm.this, "" + response.code(), Toast.LENGTH_LONG).show();

                    view.setClickable(true);
                    btn.setText(R.string.post_ad);
                    return;
                }

                Ads ad = response.body();
                assert ad != null;
                if (ad.getStatus().equals("exceeded")) {
                    Toast.makeText(AdPostForm.this, "You have exceeded you Ad limit, Upgrade to premium", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AdPostForm.this, Paystack.class);
                    intent.putExtra("auth", a.getAuth());
                    startActivity(intent);
                    return;
                }

                if (ad.getStatus().equals("banned")) {
                    Toast.makeText(AdPostForm.this, "Your account has been banned", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AdPostForm.this, MainActivity.class);
                    startActivity(intent);
                    return;
                }

                if (ad.getStatus().equals("true")) {
                    Toast.makeText(AdPostForm.this, "Ad submitted successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AdPostForm.this, MainActivity.class);
                    startActivity(intent);
                    return;
                }

                view.setClickable(true);
                btn.setText(R.string.post_ad);
            }

            @Override
            public void onFailure(@NonNull Call<Ads> call, @NonNull Throwable t) {

                view.setClickable(true);
                btn.setText(R.string.post_ad);
            }
        });
    }

    public void launchPicker(View view) {
        ActivityCompat.requestPermissions(AdPostForm.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                FILE_PICKER_REQUEST_CODE);
    }

    public void uploadImage(View view) {
        image = findViewById(R.id.image);
        ActivityCompat.requestPermissions(AdPostForm.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(this, "Permission to access file storage not granted", Toast.LENGTH_LONG).show();
            }
            return;
        }

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
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                assert uri != null;
                InputStream inputStream = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            Uri uri = data.getData();
            String uriString = data.toString();
            File file = new File(uriString);
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
                assert is != null;
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
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }

        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    private String imageToString() {
        if (bitmap == null) return "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void getUser(AdapterView<?> parent, int position) {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(AdPostForm.this);
        AdPoster a = dbo.getAdPoster();

        Call<AdPoster> call = ApiClient.connect().getUserByAuth(a.getAuth());
        call.enqueue(new Callback<AdPoster>() {
            @Override
            public void onResponse(@NonNull Call<AdPoster> call, @NonNull Response<AdPoster> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AdPostForm.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                adPoster = response.body();
                assert adPoster != null;
                if (adPoster.getIsPremium().equals("0")) {
                    Intent intent = new Intent(AdPostForm.this, Paystack.class);
                    intent.putExtra("auth", a.getAuth());
                    startActivity(intent);
                } else {
                    adsVisibilityText = parent.getItemAtPosition(0).toString();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdPoster> call, @NonNull Throwable t) {
                Toast.makeText(AdPostForm.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void goBack(View view) {
        finish();
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
