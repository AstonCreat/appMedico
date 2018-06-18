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
import com.example.italo.medicogestacao.adapter.ConversasAdapter;
import com.example.italo.medicogestacao.config.ConfiguracaoFirebase;
import com.example.italo.medicogestacao.help.RecyclerItemClickListener;
import com.example.italo.medicogestacao.help.UsuarioFirebase;
import com.example.italo.medicogestacao.model.Conversa;
import com.example.italo.medicogestacao.model.Gestante;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversaFragment extends Fragment {

    private RecyclerView recyclerViewConversas;
    private List<Conversa> listaConversa = new ArrayList<>();
    private ConversasAdapter adapter;
    private DatabaseReference database;
    private  DatabaseReference conversaREF;
    private  ChildEventListener childEventListenerConversas;

    public ConversaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversa, container, false);

        recyclerViewConversas =(RecyclerView) view.findViewById(R.id.recycleListaConversas);

        //Configurar adapter
        adapter = new ConversasAdapter(listaConversa, getActivity());
        //Config recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager(layoutManager);
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter(adapter);
        //configurar evento de click
        recyclerViewConversas.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewConversas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Conversa conversaSelect = listaConversa.get(position);
                Intent i = new Intent(getActivity(), SalaChatActivity.class);
                i.putExtra("chatContato",conversaSelect.getUsuarioExibicao());
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        //configura conversas ref
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        database = ConfiguracaoFirebase.getFirebaseDataBase();
        conversaREF = database.child("conversas").child(identificadorUsuario);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversa();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversaREF.removeEventListener(childEventListenerConversas);
    }

    public void recuperarConversa(){
        listaConversa.clear();
        childEventListenerConversas = conversaREF.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //recuperar conversa
                Conversa conversa = dataSnapshot.getValue(Conversa.class);
                listaConversa.add(conversa);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
