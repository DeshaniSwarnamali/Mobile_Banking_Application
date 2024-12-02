package com.example.bankingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login); // Set your XML layout file

        // Find all ImageViews by their IDs
        ImageView viewAccountDetailsImage = findViewById(R.id.viewAccountDetailsImage);
        ImageView transferMoneyImage = findViewById(R.id.transferMoneyImage);
        ImageView depositMoneyImage = findViewById(R.id.depositMoneyImage);
        ImageView withdrawMoneyImage = findViewById(R.id.withdrawMoneyImage);

        // Set OnClickListener for each ImageView
        viewAccountDetailsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the activity for viewing account details
                Intent intent = new Intent(CustomerLoginActivity.this, ViewAccountDetailsActivity.class);
                startActivity(intent);
            }
        });

        transferMoneyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the activity for transferring money
                Intent intent = new Intent(CustomerLoginActivity.this, TransferMoneyActivity.class);
                startActivity(intent);
            }
        });

        depositMoneyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the activity for depositing money
                Intent intent = new Intent(CustomerLoginActivity.this, DepositMoneyActivity.class);
                startActivity(intent);
            }
        });

        withdrawMoneyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the activity for withdrawing money
                Intent intent = new Intent(CustomerLoginActivity.this, WithdrawMoneyActivity.class);
                startActivity(intent);
            }
        });
    }
}
