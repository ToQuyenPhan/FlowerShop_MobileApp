package com.example.flowershop.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.activities.DetailActivity;
import com.example.flowershop.listeners.ItemClickListener;
import com.example.flowershop.models.Flower;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FlowerListAdapter extends RecyclerView.Adapter<FlowerListAdapter.FlowerViewHolder> {

    private LayoutInflater inflater;
    private List<Flower> flowerList;
    StorageReference firebaseStorage;

    public FlowerListAdapter(Context context, List<Flower> list){
        this.inflater = LayoutInflater.from(context);
        this.flowerList = list;
    }

    @NonNull
    @Override
    public FlowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item, parent, false);
        return new FlowerViewHolder(itemView, this);
    }

    @Override
    public int getItemCount() {
        return flowerList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull FlowerViewHolder holder, int position) {
        Flower current = flowerList.get(position);
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
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(holder.imageView.getContext(), DetailActivity.class);
                intent.putExtra("id", current.getId());
                intent.putExtra("name", current.getName());
                intent.putExtra("description", current.getDescription());
                intent.putExtra("price", current.getPrice());
                intent.putExtra("quantity", current.getQuantity());
                intent.putExtra("image", current.getImage());
                holder.imageView.getContext().startActivity(intent);
            }
        });
    }

    public class FlowerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        ImageView imageView;
        TextView price;
        RecyclerView.Adapter adapter;
        private ItemClickListener itemClickListener;

        public FlowerViewHolder(@NonNull View itemView, FlowerListAdapter adapter) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.itemImage);
            price = itemView.findViewById(R.id.price);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }
    }
}



