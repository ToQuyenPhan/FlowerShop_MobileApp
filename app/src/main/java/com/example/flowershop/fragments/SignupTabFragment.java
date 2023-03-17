package com.example.flowershop.fragments;

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
import com.example.flowershop.helpers.ConnectionHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignupTabFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);
        EditText email = root.findViewById(R.id.email2);
        EditText phoneNumber = root.findViewById(R.id.phone_number);
        EditText password = root.findViewById(R.id.password2);
        EditText confirmPassword = root.findViewById(R.id.confirm_password);
        Button signup = root.findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordS = password.getText().toString();
                String confirmPasswordS = confirmPassword.getText().toString();
                String username = email.getText().toString();
                String phoneNumberS = phoneNumber.getText().toString();
                if(username.length() == 0 || username == null){
                    Toast.makeText(getActivity(), "Please enter email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(phoneNumberS.length() == 0 || phoneNumberS == null){
                    Toast.makeText(getActivity(), "Please enter phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(passwordS.length() == 0 || passwordS == null){
                    Toast.makeText(getActivity(), "Please enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(confirmPasswordS.length() == 0 || confirmPasswordS == null) {
                    Toast.makeText(getActivity(), "Please enter confirm password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(passwordS.equals(confirmPasswordS)){
                    boolean check = false;
                    ConnectionHelper con = new ConnectionHelper();
                    Connection connection = con.getConnection();
                    if(connection != null) {
                        try {
                            String sql = "SELECT * FROM [User]";
                            Statement stm = connection.createStatement();
                            ResultSet rs = stm.executeQuery(sql);
                            while (rs.next()) {
                                if(rs.getString("username").equals(username)) {
                                    check = true;
                                    break;
                                }
                            }
                            connection.close();
                        }
                        catch(Exception e){
                            Log.e("Error is ", e.getMessage());
                        }
                    }
                    if(check){
                        Toast.makeText(getActivity(), "Email is existed!", Toast.LENGTH_SHORT).show();
                    }else{
                        boolean created = false;
                        String pattern = "MM/dd/yyyy HH:mm:ss";
                        DateFormat df = new SimpleDateFormat(pattern);
                        Date today = Calendar.getInstance().getTime();
                        String todayAsString = df.format(today);
                        connection = con.getConnection();
                        PreparedStatement psm = null;
                        if(connection != null) {
                            try {
                                String sql = "INSERT INTO [User](username, userPassword, userAddress, roleID, birthdate, phoneNumber) "
                                        + "VALUES(?,?,?,?,?,?)";
                                psm = connection.prepareStatement(sql);
                                psm.setString(1, username);
                                psm.setString(2, passwordS);
                                psm.setString(3, "");
                                psm.setInt(4, 2);
                                psm.setString(5, todayAsString);
                                psm.setString(6, phoneNumberS);
                                created = psm.executeUpdate() > 0 ? true : false;
                                connection.close();
                            }
                            catch(Exception e){
                                Log.e("Error is ", e.getMessage());
                            }
                            if(created) {
                                Toast.makeText(getActivity(), "Signup successfully!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), "Signup failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }else{
                    Toast.makeText(getActivity(), "Confirm password is wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }
}
