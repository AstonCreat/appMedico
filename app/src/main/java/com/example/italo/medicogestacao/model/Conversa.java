package com.example.italo.medicogestacao.model;

import com.example.italo.medicogestacao.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {
    private String idRemetente, idDestinatario, ultimaMensagem;
    private Gestante  usuarioExibicao;

    public Conversa() {
    }
    public void salvar(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDataBase();
        DatabaseReference conversaRef = databaseReference.child("conversas");
        conversaRef.child(this.getIdRemetente())
                .child(idDestinatario)
                .setValue(this);
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Gestante getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Gestante usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }
}
