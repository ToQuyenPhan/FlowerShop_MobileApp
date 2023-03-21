package com.example.flowershop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.flowershop.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BillingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        TextView totalPayment = findViewById(R.id.total2);
        TextView orderCode = findViewById(R.id.order_id);
        TextView date = findViewById(R.id.date);
        TextView time = findViewById(R.id.time);
        TextView status = findViewById(R.id.status);
        FloatingActionButton closeBtn = findViewById(R.id.fab_close);
        if(getIntent() != null){
            double total = getIntent().getDoubleExtra("total", 0);
            String orderCodeS = getIntent().getStringExtra("orderCode");
            String createdDate = getIntent().getStringExtra("createdDate");
            int statusIntent = getIntent().getIntExtra("status", 0);
            totalPayment.setText("$" + ((double) Math.round(total * 100) / 100));
            orderCode.setText(orderCodeS);
            if(statusIntent == 1){
                status.setText("CHECKED OUT");
            }
            String[] createdDateArr = createdDate.split(" ");
            String dateS = createdDateArr[0];
            String timeS = createdDateArr[1];
            date.setText(dateS);
            time.setText(timeS);
        }
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
