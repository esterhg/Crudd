package com.example.crud;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class update extends AppCompatActivity {
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
    private Button guardar,eliminar;
    ImageView img;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update);
        img = findViewById(R.id.imageView);
        nombre = findViewById(R.id.txtnom);
        apellido = findViewById(R.id.txtapellido);
        genero = findViewById(R.id.txtgenero);
        fecha = findViewById(R.id.txtfecha);
        guardar = (Button) findViewById(R.id.btnActualizar);


    }
}
