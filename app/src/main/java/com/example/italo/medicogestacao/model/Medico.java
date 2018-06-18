package com.example.italo.medicogestacao.model;

import com.example.italo.medicogestacao.config.ConfiguracaoFirebase;
import com.example.italo.medicogestacao.help.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Medico {

    private String id;

    private String nome,  dataNasc, idade, especialidade, crm, email, senha, perfilM,sexo,Foto;

    public Medico() {
    }

    public  void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDataBase();
        DatabaseReference usuario = firebaseRef.child("Medico").child("usuarios").child(getId());
        usuario.setValue(this);

    }
    public void atualizarDados(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDataBase();
        DatabaseReference usuarioRef = database.child("Medico").child("usuarios").child(identificadorUsuario);

        Map<String, Object> valoresUsuario = convertParaMap();

        usuarioRef.updateChildren(valoresUsuario);
    }
    @Exclude
    public Map<String, Object>convertParaMap(){
        HashMap<String,Object> usuarioMap = new HashMap<>();

        usuarioMap.put("crm",getCrm());
        usuarioMap.put("dataNasc",getDataNasc());
        usuarioMap.put("email",getEmail());
        usuarioMap.put("especialidade",getEspecialidade());
        usuarioMap.put("idade",getIdade());
        usuarioMap.put("nome",getNome());
        usuarioMap.put("sexo",getSexo());
        usuarioMap.put("foto",getFoto());

        return usuarioMap;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(String dataNasc) {
        this.dataNasc = dataNasc;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfilM() {
        return perfilM;
    }

    public void setPerfilM(String perfilM) {
        this.perfilM = perfilM;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }
}
