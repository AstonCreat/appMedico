package com.example.italo.medicogestacao.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.italo.medicogestacao.R;
import com.example.italo.medicogestacao.model.Medico;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class Reset_password_MedicoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText email;
    private Button enviar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password__medico);

        firebaseAuth =FirebaseAuth.getInstance();

        //CRIANDO TOOLBAR;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Recuperar senha");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = (EditText)findViewById(R.id.email_reset);
        enviar = (Button)findViewById(R.id.btnconfTrocaSenhaMd);


        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailDado = email.getText().toString();

                if (!emailDado.isEmpty()){
                    Medico md = new Medico();
                    md.setEmail(emailDado);
                    Log.i("valor de email",md.getEmail());
                    firebaseAuth.sendPasswordResetEmail(md.getEmail())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        email.setText("");
                                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Reset_password_MedicoActivity.this);
                                        final View mView = getLayoutInflater().inflate(R.layout.dialog_psw,null);
                                        mBuilder.setView(mView);
                                        final AlertDialog dialog = mBuilder.create();
                                        //dialog aceintando drawable com fundo personalizado
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        //Fim dialog aceintando drawable com fundo personalizado
                                        dialog.setCancelable(false);
                                        dialog.show();

                                        //tempo de fechamendo do dialog
                                        final Timer temp = new Timer();
                                        temp.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                temp.cancel();
                                            }
                                        },3000);

                                    }else{
                                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Reset_password_MedicoActivity.this);
                                        final View mView = getLayoutInflater().inflate(R.layout.dialog_psw_negado,null);
                                        mBuilder.setView(mView);
                                        final AlertDialog dialog = mBuilder.create();
                                        //dialog aceintando drawable com fundo personalizado
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        //Fim dialog aceintando drawable com fundo personalizado
                                        dialog.setCancelable(false);
                                        dialog.show();

                                        //tempo de fechamendo do dialog
                                        final Timer temp = new Timer();
                                        temp.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                temp.cancel();
                                            }
                                        },3000);

                                    }
                                }
                            });
                }else{
                    Toast.makeText(Reset_password_MedicoActivity.this, "Preencha compo email", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
