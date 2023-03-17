package com.example.flowershop.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.activities.BillingActivity;
import com.example.flowershop.helpers.ConnectionHelper;
import com.example.flowershop.models.Flower;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private LayoutInflater inflater;
    private List<Flower> cartList;
    StorageReference firebaseStorage;
    RecyclerView recyclerView;
    TextView parentLayout;
    Button checkoutBtn;
    double total;
    Context context;

    public CartAdapter(Context context, List<Flower> list, double total){
        this.inflater = LayoutInflater.from(context);
        this.cartList = list;
        this.total = total;
        this.context = context;
        parentLayout = ((Activity) context).findViewById(R.id.total);
        checkoutBtn = ((Activity) context).findViewById(R.id.checkout);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.cart_list_item, parent, false);
        return new CartAdapter.CartViewHolder(itemView, this);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        Flower current = cartList.get(position);
        holder.name.setText(current.getName());
        holder.price.setText("$" + current.getPrice() + "/ 10fl");
        firebaseStorage = FirebaseStorage.getInstance().getReference("product/" + current.getImage() + ".png");
        try {
            File localFile = File.createTempFile("tempfile", ".png");
            firebaseStorage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    holder.imageView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.quantity.setText(String.valueOf(current.getQuantity()));
        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(holder.quantity.getText().toString()) == 1) return;
                holder.quantity.setText(String.valueOf(Integer.parseInt(holder.quantity.getText().toString()) - 1));
                current.setQuantity(Integer.parseInt(holder.quantity.getText().toString()) - 1);
                total -= current.getPrice();
                parentLayout.setText("Total $" + total);

            }
        });
        holder.plusBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.quantity.setText(String.valueOf(Integer.parseInt(holder.quantity.getText().toString()) + 1));
                current.setQuantity(Integer.parseInt(holder.quantity.getText().toString()) + 1);
                total += current.getPrice();
                parentLayout.setText("Total $" + total);
            }
        }));
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean created = false;
                String createdDate = "";
                Random rand = new Random();
                String orderCode = "USER" + rand.nextInt(5000000);
                int status = 0;
                List<Flower> list = new ArrayList<>();
                ConnectionHelper con = new ConnectionHelper();
                Connection connection = con.getConnection();
                if(connection != null) {
                    try {
                        String sql = "SELECT * FROM Flower";
                        Statement stm = connection.createStatement();
                        ResultSet rs = stm.executeQuery(sql);
                        while (rs.next()) {
                            if(rs.getInt("quantity") > 0) {
                                list.add(new Flower(rs.getInt("flowerID"), rs.getString("fowerName"),
                                        rs.getDouble("price"), rs.getString("flowerImage"),
                                        rs.getInt("quantity"), rs.getString("description")));
                            }
                        }
                        connection.close();
                    }
                    catch(Exception e){
                        Log.e("Error is ", e.getMessage());
                    }
                }
                boolean check = false;
                String flowerName = "";
                for(int i = 0; i < cartList.size(); i++){
                    for(Flower f : list){
                        int id = f.getId();
                        if (cartList.get(i).getId() == id){
                            if(f.getQuantity() < cartList.get(i).getQuantity()){
                                check = true;
                                flowerName = cartList.get(i).getName();
                            }
                        }
                    }
                }
                if(check) {
                    Toast.makeText(holder.plusBtn.getContext(), "Not enough quantity for " + flowerName, Toast.LENGTH_SHORT).show();
                }else{
                    boolean updated = false;
                    for(int i = 0; i < cartList.size(); i++){
                        for(Flower f : list){
                            int id = f.getId();
                            if (cartList.get(i).getId() == id){
                                connection = con.getConnection();
                                PreparedStatement psm = null;
                                if(connection != null) {
                                    try{
                                        String sql = "UPDATE Flower SET quantity = ? WHERE flowerID = ?";
                                        psm = connection.prepareStatement(sql);
                                        psm.setInt(1, f.getQuantity() - cartList.get(i).getQuantity());
                                        psm.setInt(2, f.getId());
                                        updated = psm.executeUpdate() > 0 ? true : false;
                                        connection.close();
                                    }catch(Exception e){
                                        Log.e("Error is ", e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                    if(updated) {
                        created = false;
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        SharedPreferences pref = holder.plusBtn.getContext().getSharedPreferences("CurrentUser", MODE_PRIVATE);
                        connection = con.getConnection();
                        PreparedStatement psm = null;
                        if (connection != null) {
                            try {
                                String sql = "INSERT INTO [Order](orderCode, customerID, createdDate, total, status) VALUES(?,?,?,?,?)";
                                psm = connection.prepareStatement(sql);
                                psm.setString(1, orderCode);
                                psm.setInt(2, pref.getInt("id", 0));
                                psm.setString(3, dateFormat.format(date));
                                psm.setDouble(4, total);
                                psm.setInt(5, 1);
                                created = psm.executeUpdate() > 0 ? true : false;
                                connection.close();
                            } catch (Exception e) {
                                Log.e("Error is ", e.getMessage());
                            }
                        }
                        if (created) {
                            created = false;
                            int orderID = 0;
                            connection = con.getConnection();
                            psm = null;
                            if(connection != null) {
                                try {
                                    String sql = "SELECT * FROM [Order] WHERE orderCode = ?";
                                    psm = connection.prepareStatement(sql);
                                    psm.setString(1, orderCode);
                                    ResultSet rs = psm.executeQuery();
                                    if (rs.next()) {
                                        orderID = rs.getInt("orderID");
                                        createdDate = rs.getString("createdDate");
                                        status = rs.getInt("status");
                                    }
                                    connection.close();
                                } catch (Exception e) {
                                    Log.e("Error is ", e.getMessage());
                                }
                            }
                            if(orderID > 0) {
                                for(int i = 0; i < cartList.size(); i++){
                                    for(Flower f : list){
                                        int id = f.getId();
                                        if (cartList.get(i).getId() == id){
                                            connection = con.getConnection();
                                            psm = null;
                                            if(connection != null) {
                                                try{
                                                    String sql = "INSERT INTO OrderDetail(orderID, flowerID, quantity) VALUES(?,?,?)";
                                                    psm = connection.prepareStatement(sql);
                                                    psm.setInt(1, orderID);
                                                    psm.setInt(2, f.getId());
                                                    psm.setInt(3, cartList.get(i).getQuantity());
                                                    created = psm.executeUpdate() > 0 ? true : false;
                                                    connection.close();
                                                }catch(Exception e){
                                                    Log.e("Error is ", e.getMessage());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(created) {
                        Uri uri = Uri.parse("content://com.example.flowershop/cart");
                        ContentResolver contentResolver = context.getContentResolver();
                        int row = contentResolver.delete(uri, null, null);
                        Intent intent = new Intent(holder.plusBtn.getContext(), BillingActivity.class);
                        intent.putExtra("total", total);
                        intent.putExtra("orderCode", orderCode);
                        intent.putExtra("createdDate", createdDate);
                        intent.putExtra("status", status);
                        holder.plusBtn.getContext().startActivity(intent);
                        ((Activity) context).finish();
                    }
                }
            }
        });
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView imageView;
        TextView price;
        TextView quantity;
        ImageView minusBtn;
        ImageView plusBtn;
        RecyclerView.Adapter adapter;

        public CartViewHolder(@NonNull View itemView, CartAdapter adapter) {
            super(itemView);
            name = itemView.findViewById(R.id.name3);
            imageView = itemView.findViewById(R.id.itemImage3);
            price = itemView.findViewById(R.id.price3);
            quantity = itemView.findViewById(R.id.quantityTxt3);
            minusBtn = itemView.findViewById(R.id.minusBtn3);
            plusBtn = itemView.findViewById(R.id.plusBtn3);
            this.adapter = adapter;
        }
    }
}
