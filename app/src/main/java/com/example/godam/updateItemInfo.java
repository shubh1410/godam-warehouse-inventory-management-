package com.example.godam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.godam.Utils.Item_new;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class updateItemInfo extends AppCompatActivity {

    Button update_item;
    EditText upname,upbrand,uquantity,updesc,umno;
    AutoCompleteTextView ucategory;
    public List<String> categories= new ArrayList<String>();
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "updateItemInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database= FirebaseDatabase.getInstance();
        databaseReference=database.getReference();
        databaseReference.keepSynced(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item_info);

        Intent intent = getIntent();
        final Item_new selected_data = (Item_new) getIntent().getParcelableExtra("parcel_data");

        Log.d(TAG, "onCreate: recieved data "+selected_data.toString());

        update_item = (Button)findViewById(R.id.updateItem);
        ucategory = (AutoCompleteTextView) findViewById(R.id.ucategory);
        upname = (EditText)findViewById(R.id.upname);
        upbrand = (EditText)findViewById(R.id.upbrand);
        uquantity = (EditText)findViewById(R.id.uquantity);
        updesc = (EditText)findViewById(R.id.updesc);
        umno = (EditText)findViewById(R.id.umno);

        ucategory.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.e("TAG","Done pressed");
                }
                return false;
            }
        });
        ucategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }

        });

        databaseReference.child("Product_categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categories.clear();
                categories.add("none");
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String temp = areaSnapshot.getKey();
                    categories.add(temp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, categories);
        ucategory.setThreshold(1);
        ucategory.setAdapter(adapter);

        ucategory.setText(selected_data.getProduct_category());
        upname.setText(selected_data.getProduct_name());
        upbrand.setText(selected_data.getProduct_brand());
        uquantity.setText(selected_data.getProduct_quantity());
        updesc.setText(selected_data.getProduct_desc());
        umno.setText(selected_data.getModel_number());

        update_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prod_category = ucategory.getText().toString().toLowerCase();
                String prod_pname    = upname.getText().toString().toLowerCase();
                String prod_pbrand   = upbrand.getText().toString().toLowerCase();
                String prod_quantity = uquantity.getText().toString();
                String prod_desc     = updesc.getText().toString();
                String prod_modelno  = umno.getText().toString();

                if (!prod_category.isEmpty() && !prod_pname.isEmpty()    &&
                        !prod_pbrand.isEmpty()   && !prod_quantity.isEmpty() &&
                        !prod_desc.isEmpty()     && !prod_modelno.isEmpty())
                {
                    Item_new item = new Item_new(prod_category,prod_pname,prod_pbrand,prod_modelno,prod_desc,prod_quantity,selected_data.getFb_key());
                    String key = selected_data.getFb_key();
                    databaseReference.child("Product_info").child(prod_category).child(key).setValue(item);
                    databaseReference.child("Product_categories").child(prod_category).setValue(prod_category);
                    databaseReference.child("Product_brand").child(prod_category).child(prod_pbrand).setValue(prod_pbrand);
                    Toast.makeText(updateItemInfo.this, "Information Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(updateItemInfo.this, viewInventory.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //finish();
                    //startActivity(new Intent(updateItemInfo.this, inventorymanagerview.class));
                }
                else
                {
                    Toast.makeText(updateItemInfo.this,"No Field can be EMPTY!!!", Toast.LENGTH_SHORT).show();
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
//            mAuth.signOut();
            Intent intent = new Intent(updateItemInfo.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
//            Toast.makeText(this,"logOut Clicked",Toast.LENGTH_SHORT).show();
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    /*Exit Notification*/
}
