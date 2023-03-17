package com.example.flowershop.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flowershop.R;
import com.example.flowershop.activities.HomeActivity;
import com.example.flowershop.helpers.ConnectionHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginTabFragment extends Fragment {

    float v = 0;
    EditText email, password;
    Button login;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);
        email = root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);
        login = root.findViewById(R.id.login);
        email.setTranslationX(800);
        password.setTranslationX(800);
        login.setTranslationX(800);
        email.setAlpha(v);
        password.setAlpha(v);
        login.setAlpha(v);
        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        Button login = root.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String emailS = email.getText().toString();
                String passwordS = password.getText().toString();
                boolean check = false;
                int id = 0;
                if(emailS.length() > 0 && passwordS.length() > 0) {
                    ConnectionHelper con = new ConnectionHelper();
                    Connection connection = con.getConnection();
                    if (connection != null) {
                        try {
                            String sql = "SELECT * FROM [User] WHERE username = ? and userPassword = ?";
                            PreparedStatement psm = connection.prepareStatement(sql);
                            psm.setString(1, emailS);
                            psm.setString(2, passwordS);
                            ResultSet rs = psm.executeQuery();
                            if (rs.next()) {
                                id = rs.getInt("userID");
                                check = true;
                            }
                            connection.close();
                        } catch (Exception e) {
                            Log.e("Error is ", e.getMessage());
                        }
                    }
                }
                if(check) {
                    SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("CurrentUser", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("id", id);
                    editor.putString("username", emailS);
                    editor.commit();
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    getActivity().startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "Wrong email or password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }
}
