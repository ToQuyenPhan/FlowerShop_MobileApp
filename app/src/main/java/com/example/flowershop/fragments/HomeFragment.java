package com.example.flowershop.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.adapters.FlowerListAdapter;
import com.example.flowershop.helpers.ConnectionHelper;
import com.example.flowershop.models.Flower;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
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
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        FlowerListAdapter adapter = new FlowerListAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }
}
