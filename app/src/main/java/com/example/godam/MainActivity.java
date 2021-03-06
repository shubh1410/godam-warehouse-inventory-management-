package com.example.godam;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
{
    Button login_button;
    EditText user,pass;
    TextView msg;

    private static final String TAG = "MainActivity";
    private final String uname="uname";
    private final String passtext="passtext";

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate: in");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login_button = (Button)findViewById(R.id.login);
        user = (EditText)findViewById(R.id.username);
        pass = (EditText)findViewById(R.id.password);
//        msg = (TextView)findViewById(R.id.errormsg);

        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user_email=firebaseAuth.getCurrentUser();

                if(user_email!=null){
                    Log.d(TAG,"user signed in");
                }
                else{
                    Log.d(TAG,"user signed out");
                }
            }
        };
        
        login_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final String email=user.getText().toString();
                String pwd=pass.getText().toString();

                if(!email.equals("") && !pwd.equals("")) {
                    /*Toast.makeText(getApplicationContext(),
                            "Redirecting...",Toast.LENGTH_SHORT).show();*/
                    mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Signed in",Toast.LENGTH_SHORT).show();
                                //switching to correct screen
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String temp_email=email.replaceAll("[.]","");
                                        if(dataSnapshot.child("Superviser").child(temp_email).exists())
                                        {
                                            Intent intent = new Intent(MainActivity.this, supervisor.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                        else if(dataSnapshot.child("Inventory Manager").child(temp_email).exists())
                                        {
                                            Intent intent = new Intent(MainActivity.this, inventorymanagerview.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            //finish();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                //**********************************

                            }
                            else{
                                Toast.makeText(MainActivity.this, "Wrong Credentials",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(MainActivity.this, "Email or Password cannot be empty",Toast.LENGTH_SHORT).show();
                    //msg.setText("Wrong Credentials");
                    //msg.setVisibility(View.VISIBLE);
                }
            }

        });
        Log.d(TAG, "onCreate: out");
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: in");
        super.onRestart();
        Log.d(TAG, "onRestart: out");
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: in");
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG, "onStart: out");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener !=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: in");
        super.onPause();
        Log.d(TAG, "onPause: out");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: in");
        outState.putString(uname,user.getText().toString());
        outState.putString(passtext,pass.getText().toString());
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: out");
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: in");
        super.onRestoreInstanceState(savedInstanceState);
        user.setText(savedInstanceState.getString(uname));
        pass.setText(savedInstanceState.getString(passtext));
        Log.d(TAG, "onRestoreInstanceState: out");
    }

    /*For Menu Button in Action Bar*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    /*For Menu Button in Action Bar*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.Exit)
        {
//            mAuth.signOut();
           onBackPressed();
//            Toast.makeText(this,"Exit Clicked",Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId() == R.id.aboutUs)
        {
            Toast.makeText(this,"About Us Clicked",Toast.LENGTH_SHORT).show();
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    /*Exit Notification*/
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
