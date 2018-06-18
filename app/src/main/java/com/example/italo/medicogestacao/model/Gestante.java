package com.example.italo.medicogestacao.model;

import java.io.Serializable;

public class Gestante implements Serializable {
    private String id;
    private String nomeC;
    private String dataNAsc;
    private String sexo;
    private String idade;
    private String sangue;
    private String celular;
    private String email;
    private String senha;
    private String foto;
    private String dataBB;
    private String perfilG;
    private String key;

    public Gestante() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeC() {
        return nomeC;
    }

    public void setNomeC(String nomeC) {
        this.nomeC = nomeC;
    }

    public String getDataNAsc() {
        return dataNAsc;
    }

    public void setDataNAsc(String dataNAsc) {
        this.dataNAsc = dataNAsc;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getSangue() {
        return sangue;
    }

    public void setSangue(String sangue) {
        this.sangue = sangue;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getDataBB() {
        return dataBB;
    }

    public void setDataBB(String dataBB) {
        this.dataBB = dataBB;
    }

    public String getPerfilG() {
        return perfilG;
    }

    public void setPerfilG(String perfilG) {
        this.perfilG = perfilG;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
