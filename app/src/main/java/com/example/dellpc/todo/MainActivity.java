package com.example.dellpc.todo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    Button addItemBtn;
    EditText addItemText;
    EditText addUpdatedText;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    CheckBox checkBoxOff;
    CheckBox checkBoxHome;
    private AcivityAdapter mActivityAdapter;
    public ListView mListView;
    public static final int DEFAULT_ITEM_LENGTH_LIMIT = 23;
    public String checkBxOff = "Office Activity";
    public String checkBxHome = "Home Activity";
    public String checkBxOff_checkBxHome = "Home&Off Activity";
    public String category;
    private String TAG;
    public String refKey;
    public String getRefKey;
    final List<Activities> ac = new ArrayList<>();
    public String currentDateandTime;
    public int myposition;
    public  boolean status;
    CheckBox taskStatus;

    Activities act = new Activities();
    Activities act1 = new Activities();
    LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("activity");



        addItemBtn = (Button) findViewById(R.id.sendBtn);
        addItemText = (EditText) findViewById(R.id.edit_text);
        mListView = (ListView) findViewById(R.id.activity_list_view);

        //register the list menu for pop up menu for delete.
        registerForContextMenu(mListView);

        //setting current time and date.
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy KK:mm");
        currentDateandTime = sdf.format(new Date());


        //initializing activity textview and its adapter

        mActivityAdapter = new AcivityAdapter(this, R.layout.item_message, ac);
        mListView.setAdapter(mActivityAdapter);

        addItemText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    addItemBtn.setEnabled(true);
                } else {
                    addItemBtn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addItemText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_ITEM_LENGTH_LIMIT)});


        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activities myActivity = new Activities(addItemText.getText().toString(), CategSelection(category), currentDateandTime, null);
                mDatabaseReference.push().setValue(myActivity);
                addItemText.setText("");
            }
        });

        //attaching read listener to read data.
        attachDataBaseReadListener();

        mListView.setClickable(true);

//       mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//           @Override
//           public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//               MenuInflater inflater = getMenuInflater();
//               inflater.inflate(R.menu.main_menu,);
//
//               switch (position){
//
//               }
//               return true;
//           }
//       });
    }

    //method for check boxes.
    private String CategSelection(String mCateg){
        checkBoxOff = (CheckBox) findViewById(R.id.checkboxOff);
        checkBoxHome = (CheckBox) findViewById(R.id.checkboxHome);

        if(checkBoxHome.isChecked() == true && checkBoxOff.isChecked() == false){
            category = checkBxHome;
        }else if(checkBoxOff.isChecked() == true && checkBoxHome.isChecked() == false){
            category = checkBxOff;
        } else if(checkBoxHome.isChecked()== true && checkBoxOff.isChecked()==true){
            category = checkBxOff_checkBxHome;
        }

        mCateg = category;
        return mCateg;
    }

//    private boolean taskComp(boolean mStatus){
//        View view = inflater.inflate(R.layout.update_item,null);
//        taskStatus = (CheckBox) view.findViewById(R.id.updateTaskComp);
//        if(taskStatus.isChecked() == true){
//            status = mStatus;
//            status = true;
//
//        }else if(taskStatus.isChecked() == false){
//            status = mStatus;
//            status = false;
//        }
//        return status;
//
//    }

    //method for attaching a read listener to bring data from fire base.
    private void attachDataBaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    act = dataSnapshot.getValue(Activities.class);
                    act.setRefKey(dataSnapshot.getKey());
                    mActivityAdapter.add(act);
                    mListView.setAdapter(mActivityAdapter);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                   String updatedKey = dataSnapshot.getKey();

                    Activities newact = dataSnapshot.getValue(Activities.class);

                    for (Activities act : ac) {
                        if (updatedKey.equals(act.getRefKey())) {
                            act.setValues(newact);

                            break;
                        }
                    }
                    mActivityAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    act.setRefKey(dataSnapshot.getKey());

                    String Key = dataSnapshot.getKey();
                    for (Activities act : ac) {
                        if (Key.equals(act.getRefKey())) {
                            ac.remove(act);
                            mActivityAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    mActivityAdapter.notifyDataSetChanged();
                    ac.notifyAll();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);

        }
    }

    //method to delete items from list.
    public int deleteItem(int position){
        mActivityAdapter.remove(mActivityAdapter.getItem(position));
        return position;
    }

    //method to remove item from firebase database.
    public void remove(Activities myact){
        mDatabaseReference.child(myact.getRefKey()).removeValue();
    }

    //method to update.
    public void update(Activities act_updated, String newItem){

        act_updated.setActivityName(newItem);
        mDatabaseReference.child(act_updated.getRefKey()).setValue(act_updated);
    }


    //overriding a method for pop up menu.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//        myposition = info.position;
        if(v.getId() == R.id.activity_list_view){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu,menu);
        }
    }

    //to perform action on the selected item from context menu.
    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_menu:

//                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//                deleteItem(((int) info.id));
//                Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show();
//                refKey = getRefKey;
//                mDatabaseReference.child(refKey).removeValue();
//                //mActivityAdapter.remove(mActivityAdapter.getItem(info.position));
//                attachDataBaseReadListener();

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
               /// myposition = info.position;

                //getting the particular line from list view.
                myposition  = ((int) mListView.getItemIdAtPosition(info.position));
                //act = mActivityAdapter.getItem(myposition);

                act  = mActivityAdapter.getItem(myposition);
                remove(act);
                //deleteItem(((int) info.id));

                //mActivityAdapter.remove(act);
                return true;
            case R.id.update_menu:
                //calling method to update dialog.
//                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        myposition = position;
//                        onUpdateDialog();
//                    }
//                });
                AdapterView.AdapterContextMenuInfo info1 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                int myposition1 = ((int) mListView.getItemIdAtPosition(info1.position));
                act1 = mActivityAdapter.getItem(myposition1);
                onUpdateDialog(act1);

                Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }

    //creating method for update dialog box.
    public Dialog onUpdateDialog(final Activities acti) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Get the layout inflater
        final LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.update_item, null))
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // add in the activity ...

                        //creating a dialog class to extract data from dialog edittext.
                        Dialog f = (Dialog) dialog;
                        addUpdatedText = (EditText) f.findViewById(R.id.editItemUpdate);


                        //pushing the updated value to firebase.
//                        Activities myActivity = new Activities(addUpdatedText.getText().toString(), CategSelection(category), currentDateandTime, null);
//                        mDatabaseReference.push().setValue(myActivity);
//                        addItemText.setText("");
//
////                        addItemText.setText("");
//                        update(myActivity,"abc");
//                        Activities acting = mActivityAdapter.getItem(myposition);
                        update(acti,addUpdatedText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        //create alert dialog.
        AlertDialog alertdialog = builder.create();
        //show it.
        alertdialog.show();
        return alertdialog;

    }

}
