package com.example.italo.medicogestacao.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.italo.medicogestacao.R;
import com.example.italo.medicogestacao.config.ConfiguracaoFirebase;
import com.example.italo.medicogestacao.model.Medico;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginMedicoActivity extends AppCompatActivity {

    private EditText login, senha;
    private Button btnentrar, btncadastro;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();//Tirando barra navegação
        setContentView(R.layout.activity_login_medico);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        login = (EditText) findViewById(R.id.userMedico);
        senha = (EditText) findViewById(R.id.senhaMedico);
        btnentrar = (Button)findViewById(R.id.btnLogarM);
        btncadastro = (Button)findViewById(R.id.btnCadastrar);

        TextView reset = (TextView) findViewById(R.id.recSenha);

        btncadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCadastroMedcio();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirRecSenhaMedcio();

            }
        });

        btnentrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarAutenticaorMedico(v);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null){
            abrirPrincipalhMedcio();
        }
    }

    public void validarAutenticaorMedico(View view){
        //recuperar dados;
        String textoEmail = login.getText().toString();
        String textoSenha = senha.getText().toString();

        //verificando se está vazio;
        if(!textoEmail.isEmpty()){
            if (!textoSenha.isEmpty()){

                //login
                Medico authMedico = new Medico();
                authMedico.setEmail(textoEmail);
                authMedico.setSenha(textoSenha);

                logarUser(authMedico);



            }else {

                Toast.makeText(LoginMedicoActivity.this, "Preencha o campo senha", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginMedicoActivity.this, "Preencha o campo Email", Toast.LENGTH_SHORT).show();
        }
    }
    public void logarUser(Medico medico){
        autenticacao.signInWithEmailAndPassword(
                medico.getEmail() , medico.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    abrirPrincipalhMedcio();
                }else{

                    String excecao="";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuário não está cadstrado";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao ="E-mai e senha não correnponde ao cadastrados";
                    }catch (Exception e){
                        excecao = "Erro ao autenticar usuario: " +e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginMedicoActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void abrirCadastroMedcio(){
        Intent intent = new Intent(this, Dados_Pessoais_medicosActivity.class);
        startActivity(intent);
    }
    public void abrirRecSenhaMedcio(){
        Intent intent = new Intent(this, Reset_password_MedicoActivity.class);
        startActivity(intent);
    }
    public void abrirPrincipalhMedcio(){
        Intent intent = new Intent(this, Princiapal_MedicoActivity.class);
        startActivity(intent);
    }

}
