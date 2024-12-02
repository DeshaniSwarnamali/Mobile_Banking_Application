package com.example.bankingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        Button updateAccountDetailsBtn = findViewById(R.id.updateAccountDetailsBtn);
        Button manageTransactionsBtn = findViewById(R.id.manageTransactionsBtn);

        updateAccountDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Update Account Details activity
                Intent intent = new Intent(AdminLoginActivity.this, ViewAccountDetailsActivity.class);
                startActivity(intent);
            }
        });

        manageTransactionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Manage Transactions activity
                Intent intent = new Intent(AdminLoginActivity.this, ManageTransactionsActivity.class);
                startActivity(intent);
            }
        });
    }
}
