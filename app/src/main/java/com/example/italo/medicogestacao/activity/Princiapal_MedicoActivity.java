package com.example.italo.medicogestacao.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.italo.medicogestacao.R;
import com.example.italo.medicogestacao.config.ConfiguracaoFirebase;
import com.example.italo.medicogestacao.help.UsuarioFirebase;
import com.example.italo.medicogestacao.model.AlertGestacao;
import com.example.italo.medicogestacao.model.Medico;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Princiapal_MedicoActivity extends AppCompatActivity {

    private TextView nomeMedico,idadeM,espM,crmM;
    private CircleImageView perfil;



    private LinearLayout chat, alert;
    private Toolbar toolbar;
    private FirebaseAuth autenticacao;
    private DatabaseReference referenciaDBAlert;
    private String iduser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_princiapal__medico);

         autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //CRIANDO TOOLBAR;
        toolbar = (Toolbar) findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Medico");
        setSupportActionBar(toolbar);

        //FIM TOOLBAR;

        nomeMedico = (TextView) findViewById(R.id.textNome);
        idadeM = (TextView) findViewById(R.id.txtidade);
        espM = (TextView) findViewById(R.id.espcMedico);
        crmM = (TextView) findViewById(R.id.crm_medico);


        perfil = (CircleImageView)findViewById(R.id.imgPerfilM);

        chat = (LinearLayout) findViewById(R.id.menuChat);
        alert = (LinearLayout) findViewById(R.id.cardAlert);


        referenciaDBAlert = FirebaseDatabase.getInstance().getReference();

        iduser = UsuarioFirebase.getIdentificadorUsuario();



        SimpleDateFormat fd = new SimpleDateFormat("dd-MM-yyyy");
        Date data = new Date();
        final String dataFD = fd.format(data);


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                abrirTelaChat();

            }
        });
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Princiapal_MedicoActivity.this,R.style.MyCustomDialog);
                 View mView = getLayoutInflater().inflate(R.layout.fragment_alert__gestacao,null);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                final EditText assunto= (EditText)mView.findViewById(R.id.assuntoAlert);
                final EditText textArea = (EditText)mView.findViewById(R.id.textareaAlert);

               Button fechar =(Button)mView.findViewById(R.id.cancelarPublicacao);
               Button salvar =(Button)mView.findViewById(R.id.salvarPublicacao);



                dialog.setCancelable(false);

                fechar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });
                salvar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String ass = assunto.getText().toString();
                        final String txtArea = textArea.getText().toString();
                        if (!ass.isEmpty()){
                            if (!txtArea.isEmpty()){

                                AlertGestacao alert = new AlertGestacao();
                                alert.setAssunto(ass);
                                alert.setMensagem(txtArea);
                                alert.setAutor(nomeMedico.getText().toString());
                                alert.setDataAtual(dataFD);
                                /*
                                Log.i("Assunto",""+ass);
                                Log.i("Mensagem",""+txtArea);
                                Log.i("Mensagem",""+nomeMedico.getText().toString());
                                Log.i("Mensagem",""+dataFD);
*/
                                referenciaDBAlert.child("alerta_gestacao").push().setValue(alert);

                                Toast.makeText(Princiapal_MedicoActivity.this, "Alert gestação publicado", Toast.LENGTH_SHORT).show();
                                //limpar campo
                                assunto.setText("");
                                textArea.setText("");

                            }else {
                                Toast.makeText(Princiapal_MedicoActivity.this, "Preencha campo Mensagem", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(Princiapal_MedicoActivity.this, "Preencha campo Assunto", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.show();
            }
        });


        FirebaseUser usuario =  UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        if (url != null){
            Glide.with(Princiapal_MedicoActivity.this).load(url).into(perfil);
        }else{
            perfil.setImageResource(R.drawable.medicoft);
        }
          pegandoDadosFireBase();


    }
    public void pegandoDadosFireBase() {
        DatabaseReference userMedico = ConfiguracaoFirebase.getFirebaseDataBase();
        DatabaseReference medico = userMedico.child("Medico").child("usuarios").child(UsuarioFirebase.getIdentificadorUsuario());

        medico.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Medico mdUser = dataSnapshot.getValue(Medico.class);
                Log.i("Firebase",dataSnapshot.getValue().toString());
                nomeMedico.setText(mdUser.getNome());
                idadeM.setText(mdUser.getIdade());
                crmM.setText(mdUser.getCrm());
                espM.setText(mdUser.getEspecialidade());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.sair:
                deslogarUsuario();
                finish();
                break;

            case R.id.configuracoes:
                abrirTelaConfiguracoes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario(){
        try {
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public  void abrirTelaConfiguracoes(){
        Intent intent = new Intent(this, ConfiguracoesActivity.class);
        startActivity(intent);
    }
    public  void abrirTelaChat(){
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

}
