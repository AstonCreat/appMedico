package com.example.italo.medicogestacao.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.italo.medicogestacao.R;
import com.example.italo.medicogestacao.activity.SalaChatActivity;
import com.example.italo.medicogestacao.adapter.ContatosAdapter;
import com.example.italo.medicogestacao.config.ConfiguracaoFirebase;
import com.example.italo.medicogestacao.help.RecyclerItemClickListener;
import com.example.italo.medicogestacao.model.Gestante;
import com.example.italo.medicogestacao.model.Medico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatoFragment extends Fragment {

    private RecyclerView recyclerViewContatos;
    private ContatosAdapter adapter;
    private ArrayList<Gestante> listaContato = new ArrayList<>();
    private DatabaseReference userRef;
    private  ValueEventListener valueEventListenerContatos;



    public ContatoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_contato, container, false);

        userRef = ConfiguracaoFirebase.getFirebaseDataBase().child("Gestante").child("usuarios");

        //recycle view inicialização
        recyclerViewContatos = (RecyclerView)view.findViewById(R.id.recicleViewGestante);

        //configuração Adapter
        adapter = new ContatosAdapter(listaContato, getActivity());

        //COnfigurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerViewContatos.setLayoutManager(layoutManager);
        recyclerViewContatos.setHasFixedSize(true);
        recyclerViewContatos.setAdapter(adapter);

        //configurar  evento de clique no recyclerView
        recyclerViewContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(), recyclerViewContatos, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Gestante userSelecionado = listaContato.get(position);
                        Intent i = new Intent(getActivity(), SalaChatActivity.class);
                        i.putExtra("chatContato",userSelecionado);
                        startActivity(i);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                )
        );



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContato();
    }

    @Override
    public void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerContatos);
    }

    public void recuperarContato(){
        valueEventListenerContatos = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaContato.clear();
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Gestante gestante = dados.getValue(Gestante.class);
                    gestante.setKey(dados.getKey());
                    listaContato.add(gestante);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
