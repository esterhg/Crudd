package com.example.crud;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adaptador extends RecyclerView.Adapter<Adaptador.PersonaViewHolder>{
    List<Persona>  personaList;

    Context mContext;
   OnItemClickListener mListener;

    private Activity mActivity;

    private int selectedPosition = RecyclerView.NO_POSITION;
    public Adaptador(Context context,List<Persona> personaList,Activity activity) {
        this.mContext =context;
        this. personaList = personaList;
        this.mActivity = activity;
    }

    @Override
    public PersonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.items, parent, false);
        return new PersonaViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull PersonaViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Persona persona1 =  personaList.get(position);
        holder.txt.setText(persona1.getNombre());
        holder.txt2.setText(persona1.getApellido());
        String imageUrl = persona1.getImg(); // Obtén la URL de la imagen desde tu objeto Persona
        Picasso.get()
                .load(persona1.getImg())
                .placeholder(R.drawable.img)
                .fit()
                .centerCrop()
                .into(holder.img);
        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(mActivity)
                        .setContentHolder(new ViewHolder(R.layout.update))
                        .setExpanded(true, 2000)
                        .create();
                View view = dialogPlus.getHolderView();

                EditText nombre = view.findViewById(R.id.txtnom);
                EditText img = view.findViewById(R.id.Url);
                EditText apellido = view.findViewById(R.id.txtapellido);
                EditText genero = view.findViewById(R.id.txtgenero);
                EditText fecha = view.findViewById(R.id.txtfecha);
                Button actualizar = view.findViewById(R.id.btnActualizar);

                nombre.setText(persona1.getNombre());
                img.setText(persona1.getImg());
                apellido.setText(persona1.getApellido());
                genero.setText(persona1.getGenero());
                fecha.setText(persona1.getFecha());

                dialogPlus.show();

                actualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Obtén los datos actualizados desde los EditText
                        String nuevoNombre = nombre.getText().toString();
                        String nuevaUrl = img.getText().toString();
                        String nuevoApellido = apellido.getText().toString();
                        String nuevoGenero = genero.getText().toString();
                        String nuevaFecha = fecha.getText().toString();

                        // Crea un mapa con los datos actualizados
                        Map<String, Object> map = new HashMap<>();
                        map.put("nombre", nuevoNombre);
                        map.put("img", nuevaUrl);
                        map.put("apellido", nuevoApellido);
                        map.put("genero", nuevoGenero);
                        map.put("fecha", nuevaFecha);

                        // Obtiene la referencia a la persona actual en la base de datos
//                        DatabaseReference personaRef = FirebaseDatabase.getInstance().getReference()
//                                .child("Persona")
//                                .child(String.valueOf(getRef(position).getKey()));
//                        personaRef.updateChildren(map)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(mActivity, "Datos actualizados", Toast.LENGTH_SHORT).show();
//                                        dialogPlus.dismiss();
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(mActivity, "Error al actualizar datos", Toast.LENGTH_SHORT).show();
//                                        dialogPlus.dismiss();
//                                    }
//                                });
                        Persona persona = personaList.get(position);
                        DatabaseReference personaRef = FirebaseDatabase.getInstance().getReference()
                                .child("Persona")
                                .child(persona.getId());
                        personaRef.updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(mActivity, "Datos actualizados", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mActivity, "Error al actualizar datos", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                });

                    }
                });
            }
        });
        holder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mActivity.isFinishing() && !mActivity.isDestroyed()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.txt.getContext());
                    builder.setTitle("Estás seguro?");
                    builder.setMessage("Eliminar datos...");

                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Persona persona = personaList.get(position);
                            String personaKey = persona.getId();

                            DatabaseReference personaRef = FirebaseDatabase.getInstance().getReference()
                                    .child("Persona")
                                    .child(personaKey);
                            personaRef.removeValue();
                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mActivity, "Cancelado", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (mActivity.getWindow() != null && mActivity.getWindow().getDecorView().getWindowToken() != null) {
                        builder.show();
                    }
                }
            }
        });


    }
    @Override
    public int getItemCount() {
        return  personaList.size();
    }

    public class PersonaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
    View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
//        ArrayList<Persona> listado = new ArrayList<>();
//        ArrayList<Persona> copyContactos = new ArrayList<>();
        TextView txt,txt2;
        ImageView img;
        Button editar,eliminar,leer;
        public PersonaViewHolder(@NonNull View itemView) {
            super(itemView);

            txt=(TextView) itemView.findViewById(R.id.txt);
            img =(ImageView)itemView.findViewById(R.id.img);
            txt2=(TextView) itemView.findViewById(R.id.txt2);

            editar= (Button) itemView.findViewById(R.id.Editar);
            eliminar= (Button) itemView.findViewById(R.id.btnEliminar);


            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if( mListener!=null){
                if( mListener!=null){
                    int position=getAbsoluteAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION){
                        mListener.onItemClick(position);

                    }
                }
            }
        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem showItem = menu.add(Menu.NONE, 1, Menu.NONE, "Show");
            MenuItem deleteItem = menu.add(Menu.NONE, 2, Menu.NONE, "Delete");

            showItem.setOnMenuItemClickListener(this);
            deleteItem.setOnMenuItemClickListener(this);
        }


        public boolean onMenuItemClick (MenuItem item){
            if ( mListener!=null){
                int position = getAbsoluteAdapterPosition();
                if(position!=RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            mListener.onShowItemClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteItemClick(position);
                            return true;
                    }
                }
            }

            return false;
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onShowItemClick(int position);
        void onDeleteItemClick(int position);
    }


}





