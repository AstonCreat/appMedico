package com.example.italo.medicogestacao.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.italo.medicogestacao.R;
import com.example.italo.medicogestacao.config.ConfiguracaoFirebase;
import com.example.italo.medicogestacao.help.Base64Custom;
import com.example.italo.medicogestacao.help.Permissao;
import com.example.italo.medicogestacao.help.UsuarioFirebase;
import com.example.italo.medicogestacao.model.Medico;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    int progressState =0;

    private Toolbar toolbar;
    private EditText nomeMedico, dtnascM, idadeM, espM, crmM,sexoM;
    private Button btnsalvar;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private ImageView btnCamera, btnGaleria;


    private CircleImageView perfil;

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private String identificadorUser;
    private Medico usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);


        storageReference = ConfiguracaoFirebase.getFirebaseStorege();
        identificadorUser = UsuarioFirebase.getIdentificadorUsuario();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //validar Permissoes
        Permissao.validarPermissao(permissoesNecessarias,this,1);

        //CRIANDO TOOLBAR;
        toolbar = (Toolbar) findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //FIM TOOLBAR;

        nomeMedico = (EditText) findViewById(R.id.nome_M);
        dtnascM = (EditText) findViewById(R.id.data_nasci);
        idadeM = (EditText) findViewById(R.id.idadeMedico);
        espM = (EditText) findViewById(R.id.esp_M);
        crmM = (EditText) findViewById(R.id.crm_m);
        sexoM = (EditText) findViewById(R.id.sexoM);

        perfil = (CircleImageView)findViewById(R.id.circleImagePerfil);

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

        FirebaseUser usuario =  UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();
        if (url != null){
            Glide.with(ConfiguracoesActivity.this).load(url).into(perfil);
        }else{
            perfil.setImageResource(R.drawable.medicoft);
        }
        pegandoDadosFireBase();

        btnCamera = (ImageView)findViewById(R.id.imgButtonCamera);
        btnGaleria = (ImageView)findViewById(R.id.imgButtonGaleria);

        btnsalvar = (Button) findViewById(R.id.btncadMedicoConfig);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_CAMERA);
                }

            }
        });
        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_GALERIA);
                }
            }
        });
    btnsalvar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            validarCadMedico(v);
        }
    });
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
                dtnascM.setText(mdUser.getDataNasc());
                sexoM.setText(mdUser.getSexo());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem =(Bitmap) data.getExtras().get("data");
                        break;

                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }
                if(imagem != null){
                    perfil.setImageBitmap(imagem);

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem Firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("Perfil_medico")
                            .child(identificadorUser +".jpeg");

                    showProgressDialog();

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(ConfiguracoesActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();

                            Uri url = taskSnapshot.getDownloadUrl();
                            atualizarFotoUsuario(url);
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public  void atualizarFotoUsuario(Uri url){
        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);
        if (retorno){
            usuarioLogado.setFoto(url.toString());
            //Toast.makeText(this, "Sua Foto foi alterada", Toast.LENGTH_SHORT).show();
        }
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

        String sexo = sexoM.getText().toString();

        if (!nome.isEmpty()){
            if (!nascimento.isEmpty()){
                try {
                    Date date = df.parse(nascimento);
                    if (!idade.isEmpty()){
                        if(!sexo.isEmpty()){
                            if (!especialidade.isEmpty()){
                                if (!crm.isEmpty()){
                                    usuarioLogado.setNome(nome);
                                    usuarioLogado.setDataNasc(nascimento);
                                    usuarioLogado.setIdade(idade);
                                    usuarioLogado.setEspecialidade(especialidade);
                                    usuarioLogado.setCrm(crm);
                                    usuarioLogado.setSexo(sexo);
                                    usuarioLogado.atualizarDados();
                                    Toast.makeText(this, "Dados alterados com sucesso", Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(ConfiguracoesActivity.this, "Preencha campo crm", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(ConfiguracoesActivity.this, "Preencha campo especialidade", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(ConfiguracoesActivity.this, "Preencha campo sexo", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(ConfiguracoesActivity.this, "Preencha campo idade", Toast.LENGTH_SHORT).show();
                    }
                }catch (ParseException e){
                    Toast.makeText(ConfiguracoesActivity.this, "Data de nascimento incorreta!!", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(ConfiguracoesActivity.this, "Preencha campo data nascimento", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(ConfiguracoesActivity.this, "Preencha campo nome", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResult : grantResults) {
            if (permissaoResult == PackageManager.PERMISSION_DENIED) {
                alertvalidadePermission();
            }
        }

    }

    private void alertvalidadePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyCustomDialog);
        builder.setTitle("Permissões Negadas");
        builder.setCancelable(false);
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showProgressDialog(){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Atualizando foto");
        dialog.setMessage("Carregando....");
        dialog.setCancelable(false);

        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(100);

        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressState <100){
                    progressState +=20;

                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    dialog.setProgress(progressState);
                }
                if (progressState>=100){
                    dialog.dismiss();
                }
            }
        }).start();
    }
}
