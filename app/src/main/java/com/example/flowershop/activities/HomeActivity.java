package com.example.flowershop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.flowershop.R;
import com.example.flowershop.fragments.ChatFragment;
import com.example.flowershop.fragments.HomeFragment;
import com.example.flowershop.fragments.LocationFragment;
import com.example.flowershop.models.Flower;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ImageView cartBtn;
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.home:
                    cartBtn.setVisibility(View.VISIBLE);
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.location:
                    cartBtn.setVisibility(View.INVISIBLE);
                    selectedFragment = new LocationFragment();
                    break;
                case R.id.chat:
                    cartBtn.setVisibility(View.VISIBLE);
                    selectedFragment = new ChatFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new HomeFragment()).commit();
        cartBtn = findViewById(R.id.cart);
        BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);
        List<Flower> list = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://com.example.flowershop/cart");
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        list.clear();
        if(cursor != null && cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String image = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                list.add(new Flower(id, name, price, image, quantity, ""));
            }while(cursor.moveToNext());
        }
        if(list.size() > 0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel notificationChannel = new NotificationChannel("123", "Cart Notification",
                            NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(notificationChannel);
                Intent intent = new Intent(this, CartActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "123")
                            .setSmallIcon(R.drawable.ic_baseline_shopping_cart_24)
                            .setContentTitle("Cart Flowers")
                            .setContentText("You can checkout to buy these flowers in your cart!")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                Notification notification = mBuilder.build();
                manager.notify(1, notification);
            }
        }
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        navigationView.setOnNavigationItemSelectedListener(navListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
}