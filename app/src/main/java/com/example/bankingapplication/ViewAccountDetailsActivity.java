package com.example.bankingapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewAccountDetailsActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private Button viewBalanceButton;
    private TextView accountBalanceTextView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account_details);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");

        // Find views
        usernameEditText = findViewById(R.id.usernameEditText);
        viewBalanceButton = findViewById(R.id.viewBalanceButton);
        accountBalanceTextView = findViewById(R.id.accountBalanceTextView);

        // Set up button click listener
        viewBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(ViewAccountDetailsActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                } else {
                    // Fetch account balance for the entered username
                    fetchAccountBalance(username);
                }
            }
        });
    }

    private void fetchAccountBalance(final String username) {
        // Fetch data from Firebase based on the entered username
        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the account balance for the user
                    Double balance = dataSnapshot.child("balance").getValue(Double.class);
                    if (balance != null) {
                        accountBalanceTextView.setText("Account Balance: $" + balance);
                    } else {
                        accountBalanceTextView.setText("Account Balance: $0.0");
                    }
                } else {
                    accountBalanceTextView.setText("");
                    Toast.makeText(ViewAccountDetailsActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewAccountDetailsActivity.this, "Failed to load account details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
