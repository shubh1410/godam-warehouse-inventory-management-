package com.example.godam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.godam.Utils.Item_new;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class createNewItem extends AppCompatActivity
{

    Button create_item;
    EditText pname,pbrand,quantity,pdesc,mno;
    Spinner category;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        databaseReference=database.getReference();
        databaseReference.keepSynced(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_item);

        create_item = (Button)findViewById(R.id.createItem);
        category = (Spinner)findViewById(R.id.category);
        pname = (EditText)findViewById(R.id.pname);
        pbrand = (EditText)findViewById(R.id.pbrand);
        quantity = (EditText)findViewById(R.id.quantity);
        pdesc = (EditText)findViewById(R.id.pdesc);
        mno = (EditText)findViewById(R.id.mno);

        create_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prod_category = category.getSelectedItem().toString();
                String prod_pname    = pname.getText().toString();
                String prod_pbrand   = pbrand.getText().toString();
                String prod_quantity = quantity.getText().toString();
                String prod_desc     = pdesc.getText().toString();
                String prod_modelno  = mno.getText().toString();

                if (!prod_category.isEmpty() && !prod_pname.isEmpty()    &&
                    !prod_pbrand.isEmpty()   && !prod_quantity.isEmpty() &&
                    !prod_desc.isEmpty()     && !prod_modelno.isEmpty())
                {
                    Item_new item = new Item_new(prod_category,prod_pname,prod_pbrand,prod_modelno,prod_desc,prod_quantity);
                    String key = databaseReference.child("Products").child(prod_category).child(prod_pbrand).push().getKey();
                    databaseReference.child("Products").child(prod_category).child(prod_pbrand).child(key).setValue(item);
                    Toast.makeText(createNewItem.this, "Item added", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(createNewItem.this, inventorymanagerview.class));
                }
                else
                {
                    Toast.makeText(createNewItem.this,"No Field can be EMPTY!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*For Menu Button in Action Bar*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.others_menu,menu);
        return true;
    }
    /*For Menu Button in Action Bar*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logOut)
        {
            Intent intent = new Intent(createNewItem.this, MainActivity.class);
            startActivity(intent);
//            Toast.makeText(this,"logOut Clicked",Toast.LENGTH_SHORT).show();
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}

