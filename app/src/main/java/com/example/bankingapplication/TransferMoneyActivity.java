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

public class TransferMoneyActivity extends AppCompatActivity {

    private EditText senderUsernameEditText, recipientUsernameEditText, transferAmountEditText;
    private Button transferButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_money);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");

        // Find views
        senderUsernameEditText = findViewById(R.id.senderUsernameEditText);
        recipientUsernameEditText = findViewById(R.id.recipientUsernameEditText);
        transferAmountEditText = findViewById(R.id.transferAmountEditText);
        transferButton = findViewById(R.id.transferButton);

        // Set onClickListener for transfer button
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String senderUsername = senderUsernameEditText.getText().toString().trim();
                String recipientUsername = recipientUsernameEditText.getText().toString().trim();
                String transferAmountStr = transferAmountEditText.getText().toString().trim();

                if (TextUtils.isEmpty(senderUsername) || TextUtils.isEmpty(recipientUsername) || TextUtils.isEmpty(transferAmountStr)) {
                    Toast.makeText(TransferMoneyActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        double transferAmount = Double.parseDouble(transferAmountStr);
                        if (transferAmount > 0) {
                            transferMoney(senderUsername, recipientUsername, transferAmount);
                        } else {
                            Toast.makeText(TransferMoneyActivity.this, "Transfer amount must be positive", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(TransferMoneyActivity.this, "Invalid transfer amount", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void transferMoney(String senderUsername, String recipientUsername, double transferAmount) {
        // Query for both sender and recipient data
        databaseReference.orderByChild("username").equalTo(senderUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot senderSnapshot) {
                if (senderSnapshot.exists()) {
                    // Get sender's data
                    DataSnapshot senderDataSnapshot = senderSnapshot.getChildren().iterator().next();
                    Double senderBalance = senderDataSnapshot.child("balance").getValue(Double.class);

                    // Check if sender has enough balance
                    if (senderBalance != null && senderBalance >= transferAmount) {

                        // Query for recipient's data
                        databaseReference.orderByChild("username").equalTo(recipientUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot recipientSnapshot) {
                                if (recipientSnapshot.exists()) {
                                    // Get recipient's data
                                    DataSnapshot recipientDataSnapshot = recipientSnapshot.getChildren().iterator().next();
                                    Double recipientBalance = recipientDataSnapshot.child("balance").getValue(Double.class);

                                    // Update balances
                                    if (recipientBalance != null) {
                                        double newSenderBalance = senderBalance - transferAmount;
                                        double newRecipientBalance = recipientBalance + transferAmount;

                                        // Update both sender and recipient balances in Firebase
                                        senderDataSnapshot.getRef().child("balance").setValue(newSenderBalance);
                                        recipientDataSnapshot.getRef().child("balance").setValue(newRecipientBalance)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(TransferMoneyActivity.this, "Transfer successful", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(TransferMoneyActivity.this, "Failed to transfer funds", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(TransferMoneyActivity.this, "Recipient username not found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(TransferMoneyActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(TransferMoneyActivity.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TransferMoneyActivity.this, "Sender username not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TransferMoneyActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
