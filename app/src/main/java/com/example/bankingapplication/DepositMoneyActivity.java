package com.example.bankingapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DepositMoneyActivity extends AppCompatActivity {

    private EditText usernameEditText, depositAmountEditText;
    private Button depositButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_money);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");

        // Find views
        usernameEditText = findViewById(R.id.usernameEditText);
        depositAmountEditText = findViewById(R.id.depositAmountEditText);
        depositButton = findViewById(R.id.depositButton);

        // Set onClickListener for deposit button
        depositButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String depositAmountStr = depositAmountEditText.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(depositAmountStr)) {
                    Toast.makeText(DepositMoneyActivity.this, "Please enter username and deposit amount", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        double depositAmount = Double.parseDouble(depositAmountStr);
                        if (depositAmount > 0) {
                            depositMoneyToAccount(username, depositAmount);
                        } else {
                            Toast.makeText(DepositMoneyActivity.this, "Deposit amount must be positive", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(DepositMoneyActivity.this, "Invalid deposit amount", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void depositMoneyToAccount(String username, double depositAmount) {
        // Search for user by username
        databaseReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Assume usernames are unique, get the first matching user
                            DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();

                            // Retrieve the current balance
                            Double currentBalance = userSnapshot.child("balance").getValue(Double.class);

                            // If balance doesn't exist, initialize to 0
                            if (currentBalance == null) {
                                currentBalance = 0.0;
                            }

                            // Update the balance
                            double newBalance = currentBalance + depositAmount;

                            // Save the new balance to Firebase
                            userSnapshot.getRef().child("balance").setValue(newBalance)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(DepositMoneyActivity.this, "Deposit successful! New Balance: " + newBalance, Toast.LENGTH_SHORT).show();
                                            depositAmountEditText.setText("");
                                            usernameEditText.setText("");
                                        } else {
                                            Toast.makeText(DepositMoneyActivity.this, "Failed to update balance", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(DepositMoneyActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(DepositMoneyActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
