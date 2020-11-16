package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditUserActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText firstNameEditView, lastNameEditView, ageEditView, phoneNumberEditView;
    private Button editUserButton;
    private ActionBar actionBar;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;

    private String[] cameraPermission;
    private String[] storagePermission;

    private Uri imageUri;

    private DatabaseAdapter adapter;
    private String id, firstName, lastName, age, phoneNumber, userImage, createdDate, updatedDate;
    private boolean editMode;
    long userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Edit User");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) findViewById(R.id.userImage);
        firstNameEditView = (EditText) findViewById(R.id.first_name);
        lastNameEditView = (EditText) findViewById(R.id.last_name);
        ageEditView = (EditText) findViewById(R.id.age);
        phoneNumberEditView = (EditText) findViewById(R.id.phone_number);
        editUserButton = (Button) findViewById(R.id.update);

        Intent intent = getIntent();
        editMode = intent.getBooleanExtra("editMode", false);

        cameraPermission = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        adapter = new DatabaseAdapter(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("ID");
        }

        initializeData(intent);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                imagePickDialog();
            }
        });

        editUserButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                firstName = firstNameEditView.getText().toString().trim();
                lastName = lastNameEditView.getText().toString().trim();
                age = ageEditView.getText().toString().trim();
                phoneNumber = phoneNumberEditView.getText().toString().trim();
                updatedDate = ""+System.currentTimeMillis();

                User user = new User(userId, firstName, lastName, age, phoneNumber, imageUri.toString(), createdDate, updatedDate);
                adapter.open();
                adapter.update(user);
                adapter.close();

                startActivity(new Intent(EditUserActivity.this, MainActivity.class));

                Toast.makeText(EditUserActivity.this, "User updated", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void initializeData(Intent intent) {
        adapter.open();
        User user = adapter.getUser(userId);
        firstNameEditView.setText(user.getFirstName());
        lastNameEditView.setText(user.getLastName());
        ageEditView.setText(user.getAge());
        phoneNumberEditView.setText(user.getPhoneNumber());
        userImage = user.getUserImage();

        if (userImage.equals("null")) {
            imageView.setImageResource(R.drawable.ic_action_name);
        }
        else {
            imageUri = Uri.parse(user.getUserImage());
//            imageView.setImageURI(Uri.parse(user.getUserImage()));
            imageView.setImageURI(imageUri);
        }

        adapter.close();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);

            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    imageUri = resultUri;
                    imageView.setImageURI(resultUri);
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void imagePickDialog() {
        String [] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select for image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                else if (i == 1){
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    }
                    else {
                        pickFromStorage();
                    }
                }
            }
        });

        builder.create().show();
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED) &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    pickFromCamera();
                }
                else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }

            }
            break;

            case STORAGE_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromStorage();
                }
                else {
                    Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private void pickFromStorage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }
}