package com.example.italo.medicogestacao.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.italo.medicogestacao.R;
import com.example.italo.medicogestacao.config.ConfiguracaoFirebase;
import com.example.italo.medicogestacao.help.Base64Custom;
import com.example.italo.medicogestacao.model.Medico;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dados_Pessoais_medicosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText nomeMedico, dtnascM, idadeM, espM, crmM, emailM, senhaM,sexoM;
    private Button btnsalvar;
    private FirebaseAuth auttenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados__pessoais_medicos);

        //CRIANDO TOOLBAR;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dados Médico");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Fim TOOLBAR;
        nomeMedico = (EditText) findViewById(R.id.nome_Medico);
        dtnascM = (EditText) findViewById(R.id.dt_nascimento);
        idadeM = (EditText) findViewById(R.id.idadeM);
        espM = (EditText) findViewById(R.id.especialidadeM);
        crmM = (EditText) findViewById(R.id.crm);
        emailM = (EditText) findViewById(R.id.emailM);
        senhaM = (EditText) findViewById(R.id.cadsenhaM);
        sexoM = (EditText) findViewById(R.id.sexoM);

        btnsalvar = (Button) findViewById(R.id.cadMedico);

        //Mascara
        //data
        SimpleMaskFormatter fmDataNasc = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher maskData = new MaskTextWatcher(dtnascM, fmDataNasc);
        dtnascM.addTextChangedListener(maskData);
        //idade
        SimpleMaskFormatter fmIdade = new SimpleMaskFormatter("NNN");
        MaskTextWatcher maskIdade = new MaskTextWatcher(idadeM, fmIdade);
        idadeM.addTextChangedListener(maskIdade);
        //CRM
        SimpleMaskFormatter fmCRM = new SimpleMaskFormatter("LL-NNNNNNN");
        MaskTextWatcher maskCRM = new MaskTextWatcher(crmM, fmCRM);
        crmM.addTextChangedListener(maskCRM);

        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarCadMedico(view);
            }
        });

    }

    public void validarCadMedico(View view){
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        df.setLenient(false);

        String nome = nomeMedico.getText().toString();
        String nascimento = dtnascM.getText().toString();
        String idade = idadeM.getText().toString();
        String especialidade = espM.getText().toString();
        String crm = crmM.getText().toString();
        String email = emailM.getText().toString();
        String senha = senhaM.getText().toString();
        String sexo = sexoM.getText().toString();

        if (!nome.isEmpty()){
            if (!nascimento.isEmpty()){
                try {
                    Date date = df.parse(nascimento);
                    if (!idade.isEmpty()){
                        if(!sexo.isEmpty()){
                            if (!especialidade.isEmpty()){
                                if (!crm.isEmpty()){
                                    if (!email.isEmpty()){
                                        if (!senha.isEmpty()){

                                            Medico medico = new Medico();

                                            medico.setNome(nome);
                                            medico.setDataNasc(nascimento);
                                            medico.setIdade(idade);
                                            medico.setEspecialidade(especialidade);
                                            medico.setCrm(crm);
                                            medico.setEmail(email);
                                            medico.setSenha(senha);
                                            medico.setSexo(sexo);
                                            medico.setPerfilM("2");

                                            cadMedicoFirebase(medico);

                                        }else{
                                            Toast.makeText(Dados_Pessoais_medicosActivity.this, "Preencha campo senha", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(Dados_Pessoais_medicosActivity.this, "Preencha campo email", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(Dados_Pessoais_medicosActivity.this, "Preencha campo crm", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(Dados_Pessoais_medicosActivity.this, "Preencha campo especialidade", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(Dados_Pessoais_medicosActivity.this, "Preencha campo sexo", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(Dados_Pessoais_medicosActivity.this, "Preencha campo idade", Toast.LENGTH_SHORT).show();
                    }
                }catch (ParseException e){
                    Toast.makeText(Dados_Pessoais_medicosActivity.this, "Data de nascimento incorreta!!", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(Dados_Pessoais_medicosActivity.this, "Preencha campo data nascimento", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(Dados_Pessoais_medicosActivity.this, "Preencha campo nome", Toast.LENGTH_SHORT).show();
        }
    }

    public void cadMedicoFirebase(final Medico medico){

        auttenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        auttenticacao.createUserWithEmailAndPassword(
                medico.getEmail(),medico.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    try {
                        String identificadorUser = Base64Custom.codificarBase64(medico.getEmail());
                        medico.setId(identificadorUser);
                        medico.salvar();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Toast.makeText(Dados_Pessoais_medicosActivity.this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                    Intent intent  = new Intent(Dados_Pessoais_medicosActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }else {
                    String erroExcecao =" ";
                    try{
                        throw  task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = "Digite um email válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = "E-mail já se encontra cadastrado, ´em uso no app";
                    }catch (Exception e){
                        erroExcecao = "Erro ao cadastra dados";
                        e.printStackTrace();
                    }
                    Toast.makeText(Dados_Pessoais_medicosActivity.this, "Erro: "+ erroExcecao, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
