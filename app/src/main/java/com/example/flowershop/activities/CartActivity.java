package com.example.flowershop.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flowershop.R;
import com.example.flowershop.adapters.CartAdapter;
import com.example.flowershop.models.Flower;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Button checkoutBtn = findViewById(R.id.checkout);
        TextView totalPrice = findViewById(R.id.total);
        List<Flower> list = new ArrayList<>();
        double total = 0;
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
                total += quantity * price;
                list.add(new Flower(id, name, price, image, quantity, ""));
            }while(cursor.moveToNext());
        }
        totalPrice.setText("Total $" + total);
        RecyclerView recyclerView = findViewById(R.id.recyclerView3);
        CartAdapter adapter = new CartAdapter(this, list, total);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageView backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(CartActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Uri uri = Uri.parse("content://com.example.flowershop/cart/" + list.get(position).getId());
                ContentResolver contentResolver = getContentResolver();
                String id = String.valueOf(list.get(position).getId());
                int row = contentResolver.delete(uri, "Id=?", new String[]{id});
                if(row > 0){
                    Toast.makeText(CartActivity.this, list.get(position).getName() + " removed!", Toast.LENGTH_SHORT).show();
                    list.remove(position);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getApplicationContext(), "Delete failed!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}