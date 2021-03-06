package com.example.italo.medicogestacao.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.italo.medicogestacao.R;
import com.example.italo.medicogestacao.model.Gestante;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.MyViewHolder> {

    private List<Gestante> contatos;
    private Context context;

    public ContatosAdapter(List<Gestante> listaContatos, Context c) {
        this.contatos = listaContatos;
        this.context = c;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos,parent,false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Gestante gestante = contatos.get(position);

        holder.nome.setText(gestante.getNomeC());

     if (  gestante.getFoto() != null){
         Uri uri = Uri.parse(gestante.getFoto());
         Glide.with(context).load(uri).into(holder.foto);
     }else{
         holder.foto.setImageResource(R.drawable.pregnancy);
     }


    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome;


        public MyViewHolder(View itemView) {
            super(itemView);
            foto = (CircleImageView) itemView.findViewById(R.id.imgView);
            nome = (TextView) itemView.findViewById(R.id.textViewNome);


        }
    }
}
