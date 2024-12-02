package com.example.bankingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, accountNumber, adminPasswordEditText;
    private Button customerLoginButton, adminLoginButton, customerSignupButton; // updated to customerSignupButton
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");

        // Find views
        usernameEditText = findViewById(R.id.E1);
        accountNumber = findViewById(R.id.E2);
        adminPasswordEditText = findViewById(R.id.E3);
        customerLoginButton = findViewById(R.id.B2);  // Customer Login
        adminLoginButton = findViewById(R.id.B3);    // Admin Login
        customerSignupButton = findViewById(R.id.B1);  // Customer Signup

        // Customer Signup Button Listener
        customerSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String account = accountNumber.getText().toString().trim();

                // Check if both fields are filled
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(account)) {
                    Toast.makeText(MainActivity.this, "Please enter both username and account number", Toast.LENGTH_SHORT).show();
                } else {
                    saveCustomerToFirebase(username, account);
                }
            }
        });

        // Admin Login Button Listener
        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adminPassword = adminPasswordEditText.getText().toString().trim();

                if (adminPassword.equals("1234")) {
                    // Navigate to the next activity for admin
                    Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid Admin Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Customer Login Button Listener (redirect to login activity)
        customerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to save customer data under their username in Firebase
    private void saveCustomerToFirebase(String username, String account) {
        databaseReference.child(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().exists()) {
                // Proceed to save new user
                DatabaseReference userReference = databaseReference.child(username);
                Customer customer = new Customer(username, account); // Create Customer object

                // Save the Customer object to Firebase under the username
                userReference.setValue(customer)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Customer details saved successfully", Toast.LENGTH_SHORT).show();
                                usernameEditText.setText("");  // Clear username field
                                accountNumber.setText("");  // Clear account number field
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to save customer details", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(MainActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Define a Customer class
    public static class Customer {
        public String username;
        public String account;

        public Customer() {
            // Default constructor required for Firebase
        }

        public Customer(String username, String account) {
            this.username = username;
            this.account = account;
        }
    }
}
