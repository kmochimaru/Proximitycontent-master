package com.estimote.proximitycontent;

import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.estimote.proximitycontent.adapter.ActivityAdapter;
import com.estimote.proximitycontent.dialog.DateDialog;
import com.estimote.proximitycontent.dialog.TimeDialog;
import com.estimote.proximitycontent.log.MyLog;
import com.estimote.proximitycontent.model.ActivityModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DetailActivity extends AppCompatActivity {

    private RecyclerView rv;
    private List<ActivityModel> items;
    private FloatingActionButton add_activity;
    private ActivityAdapter adapter;
    private AlertDialog dialog;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private String TAG = "DetailActivity";

    private Uri mImageUri;
    final private int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage storage;
    StorageReference storageReference;
    UploadTask mUploadTask;
    View viewSnackBar;
    String uploadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mAuth = FirebaseAuth.getInstance();

        final View viewSnackBar = findViewById(R.id.myCoordinator);

        add_activity = findViewById(R.id.add_activity);
        add_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                View mView = inflater.inflate(R.layout.activity_add, null);

                final EditText input_activity_name = mView.findViewById(R.id.activity_name);
                final EditText input_description = mView.findViewById(R.id.description);
                final EditText input_date_start = mView.findViewById(R.id.date_start);
                final EditText input_time_start = mView.findViewById(R.id.time_start);
                final EditText input_date_end = mView.findViewById(R.id.date_end);
                final EditText input_time_end = mView.findViewById(R.id.time_end);
                final Button button_choose = mView.findViewById(R.id.btnChoose);
                final ImageView image_view = mView.findViewById(R.id.imgView);

                //Event Date & Time Dialog
                input_date_start.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            DateDialog dateDialog = new DateDialog(v);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            dateDialog.show(ft, "DatePicker");
                        }
                    }
                });

                input_date_end.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            DateDialog dateDialog = new DateDialog(v);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            dateDialog.show(ft, "DatePicker");
                        }
                    }
                });

                input_time_start.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            TimeDialog timeDialog = new TimeDialog(v);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            timeDialog.show(ft, "TimePicker");
                        }
                    }
                });

                input_time_end.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            TimeDialog timeDialog = new TimeDialog(v);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            timeDialog.show(ft, "TimePicker");
                        }
                    }
                });

                button_choose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFileChooser();
                    }
                });

                final ActivityModel activity = new ActivityModel();

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setView(mView).setTitle("เพิ่มกิจกรรม")
                        .setPositiveButton("เพิ่ม", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadImage();
                                if (mImageUri != null) {
                                    StorageReference fileReference = storageReference.child("images/" + UUID.randomUUID().toString());
                                    fileReference.putFile(mImageUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    uploadUrl = taskSnapshot.getDownloadUrl().toString();
                                                    Log.d("ImageURL", uploadUrl);
                                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                    DatabaseReference myRef = database.getReference("activity");
                                                    String strActivityName = input_activity_name.getText().toString() == null ? "" : input_activity_name.getText().toString();
                                                    String strDateStart = input_date_start.getText().toString() == null ? "" : input_date_start.getText().toString();
                                                    String strDateEnd = input_date_end.getText().toString() == null ? "" : input_date_end.getText().toString();
                                                    String strTimeStart = input_time_start.getText().toString() == null ? "" : input_time_start.getText().toString();
                                                    String strTimeEnd = input_time_end.getText().toString() == null ? "" : input_time_end.getText().toString();
                                                    String strDescription = input_description.getText().toString() == null ? "" : input_description.getText().toString();

                                                    activity.setActivity_name(strActivityName);
                                                    activity.setDate_start(strDateStart);
                                                    activity.setDate_end(strDateEnd);
                                                    activity.setTime_start(strTimeStart);
                                                    activity.setTime_end(strTimeEnd);
                                                    activity.setDescription(strDescription);
                                                    activity.setImage_url(uploadUrl);

//                                                    if (strActivityName.equals("") ||
//                                                            strDateStart.equals("") ||
//                                                            strDateEnd.equals("") ||
//                                                            strTimeStart.equals("") ||
//                                                            strTimeEnd.equals("") ||
//                                                            strDescription.equals("")) {
//                                                        Snackbar.make(viewSnackBar, "กรุณากรอกข้อมูลให้ครบ", Snackbar.LENGTH_SHORT).show();
//                                                    } else {
                                                        MyLog myLog = new MyLog("Add Activity", mAuth.getCurrentUser().getEmail());
                                                        myLog.addLog();
                                                        myRef.child(myRef.push().getKey()).setValue(activity);
                                                        Snackbar.make(viewSnackBar, "เพิ่มกิจกรรมสำเร็จ", Snackbar.LENGTH_SHORT).show();
//                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("อัพโหลดรูปภาพล้มเหลว", "" + e);
                                                }
                                            });
                                } else {
                                    Snackbar.make(viewSnackBar, "No file selected", Snackbar.LENGTH_SHORT).show();
                                }

                                dialog.dismiss();
                            }
                        })

                        .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                dialog = builder.create();
                dialog.show();
            }
        });

        rv = findViewById(R.id.recyclerView);

        rv.setLayoutManager(new LinearLayoutManager(this));

        items = new ArrayList<>();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("activity");

        try {
            if (!getIntent().getExtras().getString("email").equals("")) {
                add_activity.setVisibility(View.VISIBLE);
                adapter = new ActivityAdapter(true, items, this);
            }
        } catch (NullPointerException ex) {
            add_activity.setVisibility(View.INVISIBLE);
            adapter = new ActivityAdapter(false, items, this);
        }

        rv.setAdapter(adapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.removeAll(items);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        ActivityModel model = new ActivityModel(
                                snapshot.getKey().toString(),
                                snapshot.child("activity_name").getValue().toString(),
                                snapshot.child("date_start").getValue().toString(),
                                snapshot.child("date_end").getValue().toString(),
                                snapshot.child("time_start").getValue().toString(),
                                snapshot.child("time_end").getValue().toString(),
                                snapshot.child("description").getValue().toString(),
                                snapshot.child("image_url").getValue().toString()
                        );
                        items.add(model);
                    } catch (Exception ex) {
                        ActivityModel model = new ActivityModel();
                        model.setId(snapshot.getKey().toString());
                        model.setActivity_name(snapshot.child("activity_name").getValue().toString());
                        model.setDate_start(snapshot.child("date_start").getValue().toString());
                        model.setDate_end(snapshot.child("date_end").getValue().toString());
                        model.setTime_start(snapshot.child("time_start").getValue().toString());
                        model.setTime_end(snapshot.child("time_end").getValue().toString());
                        model.setDescription(snapshot.child("description").getValue().toString());
                        items.add(model);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null && getIntent().getExtras().get("user") == null) {
                    finish();
                }
            }
        };

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String uploadImage() {
        uploadUrl = "";
        if (mImageUri != null) {
            StorageReference fileReference = storageReference.child("images/" + UUID.randomUUID().toString());
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploadUrl = taskSnapshot.getDownloadUrl().toString();
                            Log.d("อัพโหลดรูปภาพสำเร็จ", uploadUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("อัพโหลดรูปภาพล้มเหลว", "" + e);
                        }
                    });
        } else {
            Snackbar.make(viewSnackBar, "No file selected", Snackbar.LENGTH_SHORT).show();
        }
        return uploadUrl;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyLog myLog = new MyLog("Logged Out", mAuth.getCurrentUser().getEmail());
        myLog.addLog();
        mAuth.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}