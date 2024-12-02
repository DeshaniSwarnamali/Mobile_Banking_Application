package com.example.bankingapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManageTransactionsActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchUserButton;
    private LinearLayout usersLayout;

    private DatabaseReference customersRef;
    private DatabaseReference transactionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transactions);

        // Firebase Database References
        customersRef = FirebaseDatabase.getInstance().getReference("Customers");
        transactionsRef = FirebaseDatabase.getInstance().getReference("transactions");

        // Initialize UI components
        searchEditText = findViewById(R.id.searchEditText);
        searchUserButton = findViewById(R.id.searchUserButton);
        usersLayout = findViewById(R.id.usersLayout);

        // Set search button listener
        searchUserButton.setOnClickListener(v -> searchUser());
    }

    private void searchUser() {
        String usernameToSearch = searchEditText.getText().toString().trim();

        if (usernameToSearch.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        // Search for the user in Firebase
        customersRef.orderByChild("username").equalTo(usernameToSearch)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        usersLayout.removeAllViews(); // Clear previous results

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                                String account = customerSnapshot.child("account").getValue(String.class);
                                String balance = String.valueOf(customerSnapshot.child("balance").getValue(Integer.class));
                                String username = customerSnapshot.child("username").getValue(String.class);

                                // Display user details
                                displayUserDetails(username, account, balance);

                                // Fetch last transaction for the user
                                fetchLastTransaction(account);
                            }
                        } else {
                            Toast.makeText(ManageTransactionsActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("ManageTransactions", "Database error: " + databaseError.getMessage());
                    }
                });
    }

    private void displayUserDetails(String username, String account, String balance) {
        TextView userDetailsTextView = new TextView(this);
        userDetailsTextView.setText("Username: " + username + "\nAccount: " + account + "\nBalance: " + balance);
        userDetailsTextView.setTextSize(16);
        userDetailsTextView.setPadding(10, 10, 10, 10);
        usersLayout.addView(userDetailsTextView);
    }

    private void fetchLastTransaction(String account) {
        transactionsRef.orderByChild("yourAccount").equalTo(account).limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot transactionSnapshot : dataSnapshot.getChildren()) {
                                String amount = transactionSnapshot.child("amount").getValue(String.class);
                                String recipientAccount = transactionSnapshot.child("recipientAccount").getValue(String.class);

                                // Display transaction details
                                displayTransactionDetails(amount, recipientAccount);
                            }
                        } else {
                            displayNoTransactionMessage();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("ManageTransactions", "Database error: " + databaseError.getMessage());
                    }
                });
    }

    private void displayTransactionDetails(String amount, String recipientAccount) {
        TextView transactionDetailsTextView = new TextView(this);
        transactionDetailsTextView.setText("Last Transaction:\nAmount: " + amount + "\nRecipient Account: " + recipientAccount);
        transactionDetailsTextView.setTextSize(14);
        transactionDetailsTextView.setPadding(10, 10, 10, 10);
        transactionDetailsTextView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        usersLayout.addView(transactionDetailsTextView);
    }

    private void displayNoTransactionMessage() {
        TextView noTransactionTextView = new TextView(this);
        noTransactionTextView.setText("No transactions found for this user.");
        noTransactionTextView.setTextSize(14);
        noTransactionTextView.setPadding(10, 10, 10, 10);
        noTransactionTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        usersLayout.addView(noTransactionTextView);
    }
}
