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

public class WithdrawMoneyActivity extends AppCompatActivity {

    private EditText usernameEditText, withdrawAmountEditText;
    private Button withdrawButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_money);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");

        // Find views
        usernameEditText = findViewById(R.id.usernameEditText);
        withdrawAmountEditText = findViewById(R.id.withdrawAmountEditText);
        withdrawButton = findViewById(R.id.withdrawButton);

        // Set onClickListener for withdraw button
        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String withdrawAmountStr = withdrawAmountEditText.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(withdrawAmountStr)) {
                    Toast.makeText(WithdrawMoneyActivity.this, "Please enter username and withdraw amount", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        double withdrawAmount = Double.parseDouble(withdrawAmountStr);
                        if (withdrawAmount > 0) {
                            withdrawMoney(username, withdrawAmount);
                        } else {
                            Toast.makeText(WithdrawMoneyActivity.this, "Withdraw amount must be positive", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(WithdrawMoneyActivity.this, "Invalid withdraw amount", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void withdrawMoney(String username, double withdrawAmount) {
        // Query the database for the customer by username
        databaseReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Get the first matching user (assume usernames are unique)
                            DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();

                            // Retrieve the current balance
                            Double currentBalance = userSnapshot.child("balance").getValue(Double.class);

                            if (currentBalance == null) {
                                currentBalance = 0.0;
                            }

                            // Check if the balance is sufficient
                            if (currentBalance >= withdrawAmount) {
                                double newBalance = currentBalance - withdrawAmount;

                                // Update the balance in the database
                                userSnapshot.getRef().child("balance").setValue(newBalance)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(WithdrawMoneyActivity.this, "Withdrawal successful! New Balance: " + newBalance, Toast.LENGTH_SHORT).show();

                                                // Clear the input fields
                                                withdrawAmountEditText.setText("");
                                                usernameEditText.setText("");

                                                // Record the transaction
                                                recordTransaction(userSnapshot.child("account").getValue(String.class), withdrawAmount);
                                            } else {
                                                Toast.makeText(WithdrawMoneyActivity.this, "Failed to update balance", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(WithdrawMoneyActivity.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WithdrawMoneyActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(WithdrawMoneyActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void recordTransaction(String accountNumber, double amount) {
        if (accountNumber == null) return;

        // Reference to the transactions node
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("transactions");

        // Create a transaction object
        DatabaseReference newTransactionRef = transactionsRef.push();
        newTransactionRef.child("amount").setValue(amount);
        newTransactionRef.child("recipientAccount").setValue(accountNumber)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(WithdrawMoneyActivity.this, "Transaction recorded", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WithdrawMoneyActivity.this, "Failed to record transaction", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
