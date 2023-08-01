package com.example.crud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import com.google.android.gms.tasks.Continuation;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText nombre, apellido, genero,fecha;

    private MaterialCardView cardView;
    private Uri ImageUri;
    private Bitmap bitmap;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private String PhotUrl;
    private  String currentUserId;
    private Button guardar,lista;
    ImageView img;
    private int currentId = 0;
    static final int PETICION_ACCESO_PERMISOS = 100;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.imageView);
        nombre = findViewById(R.id.txtnom);
        apellido = findViewById(R.id.txtapellido);
        genero = findViewById(R.id.txtgenero);
        fecha = findViewById(R.id.txtfecha);
        guardar = (Button) findViewById(R.id.btnActualizar);
        lista = (Button) findViewById(R.id.btnlista);

        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        mStorage=storage.getReference();
        mAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), lista.class);
                startActivity(intent);
            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
                inicializarFirebase();
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });
    }
    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }
    private void permisos() {
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PETICION_ACCESO_PERMISOS);
            } else {
                Obtener();
            }
        }else {
            Obtener();
        }
    }
    private void Obtener() {

        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }
    ActivityResultLauncher<Intent> launcher
            =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    ImageUri=data.getData();
                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver(),
                                ImageUri
                        );
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
                if ( ImageUri!=null){
                    img.setImageBitmap(bitmap);

                }
            }
    );

    private void uploadImage() {
        if (ImageUri != null) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Cargando imagen...");
            progressDialog.show();
            String imageName = UUID.randomUUID().toString();
            StorageReference filePath = mStorage.child("images").child(imageName);

            UploadTask uploadTask = filePath.putFile(ImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        currentId++;
                        PhotUrl = task.getResult().toString();
                        Toast.makeText(getApplicationContext(), "URL de imagen: " + PhotUrl, Toast.LENGTH_LONG).show();
                        uploadInfo();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Selecciona una imagen primero", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadInfo() {
        String nom = nombre.getText().toString().trim();
        String apell = apellido.getText().toString().trim();
        String gen = genero.getText().toString().trim();
        String fech = fecha.getText().toString().trim();

        if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(apell) || TextUtils.isEmpty(gen) || TextUtils.isEmpty(fech)) {
            Toast.makeText(getApplicationContext(), "Por Favor rellene todos los datos", Toast.LENGTH_LONG).show();
        } else {
            Persona p = new Persona();
            p.setId(String.valueOf(currentId));
            p.setNombre(nom);
            p.setApellido(apell);
            p.setGenero(gen);
            p.setFecha(fech);
            p.setImg(PhotUrl);
            databaseReference.child("Persona").child(p.getId()).setValue(p);
            Toast.makeText(getApplicationContext(), "Agregado", Toast.LENGTH_LONG).show();
        }

    }
}