package com.example.flowershop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flowershop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(getIntent() != null) {
            ImageView backButton = findViewById(R.id.back);
            TextView quantityTxt = findViewById(R.id.quantityTxt);
            TextView nameTxt = findViewById(R.id.name2);
            TextView priceTxt = findViewById(R.id.price2);
            TextView descriptionTxt = findViewById(R.id.description);
            ImageView minusBtn = findViewById(R.id.minusBtn);
            ImageView plusBtn = findViewById(R.id.plusBtn);
            ImageView imageView = findViewById(R.id.itemImage);
            Button addToCartBtn = findViewById(R.id.addToCartBtn);
            int id = getIntent().getIntExtra("id", 0);
            String name = getIntent().getStringExtra("name");
            String image = getIntent().getStringExtra("image");
            Double price = getIntent().getDoubleExtra("price", 0);
            String description = getIntent().getStringExtra("description");
            int quantity = getIntent().getIntExtra("quantity", 0);
            nameTxt.setText(name);
            priceTxt.setText("$" + String.valueOf(price) + "/ 10FL");
            descriptionTxt.setText(description);
            StorageReference firebaseStorage = FirebaseStorage.getInstance()
                    .getReference("product/" + image + ".png");
            try {
                File localFile = File.createTempFile("tempfile", ".png");
                firebaseStorage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        imageView.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(quantityTxt.getText().toString()) == 1) return;
                    quantityTxt.setText(String.valueOf(Integer.parseInt(quantityTxt.getText().toString()) - 1));
                }
            });
            plusBtn.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    quantityTxt.setText(String.valueOf(Integer.parseInt(quantityTxt.getText().toString()) + 1));
                }
            }));
            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int newQuantity = Integer.parseInt(quantityTxt.getText().toString());
                    boolean existed = false;
                    int oldQuantity = 0;
                    ContentResolver contentResolver = getContentResolver();
                    Uri uri = Uri.parse("content://com.example.flowershop/cart");
                    Cursor cursor = contentResolver.query(uri, null, null, null, null);
                    if(cursor != null && cursor.moveToFirst()){
                        do{
                            if(id == cursor.getInt(cursor.getColumnIndexOrThrow("id"))) {
                                oldQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                                existed = true;
                            }
                        }while(cursor.moveToNext());
                    }
                    if(existed){
                        Uri updateUri = Uri.parse("content://com.example.flowershop/cart/" + id);
                        ContentValues values = new ContentValues();
                        values.put("quantity", oldQuantity + newQuantity);
                        int rowUpdate = contentResolver.update(uri, values, "Id=?", new String[]{String.valueOf(id)});
                        if(rowUpdate>0){
                            Toast.makeText(getApplicationContext(), "Add successful!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Add failed!", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Uri insertUri = Uri.parse("content://com.example.flowershop/cart");
                        ContentValues values = new ContentValues();
                        values.put("id", id);
                        values.put("name", name);
                        values.put("price", price);
                        values.put("image", image);
                        values.put("quantity", Integer.parseInt(quantityTxt.getText().toString()));
                        Uri newRowUri = contentResolver.insert(uri, values);
                        if (newRowUri != null) {
                            Toast.makeText(getApplicationContext(), "Add successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Add failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}