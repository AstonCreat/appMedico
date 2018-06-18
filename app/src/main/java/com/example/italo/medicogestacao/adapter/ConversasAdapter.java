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
import com.example.italo.medicogestacao.model.Conversa;
import com.example.italo.medicogestacao.model.Gestante;
import com.example.italo.medicogestacao.model.Medico;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {
    private List<Conversa> conversas;
    private Context context;

    public ConversasAdapter(List<Conversa> lista, Context c) {
        this.conversas = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversa,parent,false);
        return  new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Conversa conversa = conversas.get(position);
        holder.ultimaMSG.setText(conversa.getUltimaMensagem());

        Gestante gestanteUser = conversa.getUsuarioExibicao();
        holder.nome.setText(gestanteUser.getNomeC());

        if(gestanteUser.getFoto() != null){
            Uri uri = Uri.parse(gestanteUser.getFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }else{
            holder.foto.setImageResource(R.drawable.pregnancy);
        }
    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome,ultimaMSG;

        public MyViewHolder(View itemView) {
            super(itemView);
            foto =(CircleImageView) itemView.findViewById(R.id.imgView);
            nome = (TextView) itemView.findViewById(R.id.textViewNome);
            ultimaMSG = (TextView) itemView.findViewById(R.id.textViewSubtitulo);
        }
    }

}
