package com.example.crud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class lista extends AppCompatActivity {

    Button upload;
    RecyclerView rv;
    List<Persona> list;
    Button volver, Eliminar;
    SearchView buscar;
    Persona selecionada;
    FirebaseDatabase FirebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);



        volver = (Button) findViewById(R.id.btnVolver2);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        rv = findViewById(R.id.lstContactos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        FirebaseDatabase= FirebaseDatabase.getInstance();

        Adaptador rad= new Adaptador(getApplicationContext(), list,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(),DividerItemDecoration.VERTICAL));
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(rad);

        FirebaseDatabase.getReference().child("Persona").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Persona per = dataSnapshot.getValue(Persona.class);
                    list.add(per);
                }
                rad.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }


}
