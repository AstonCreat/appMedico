package com.example.italo.medicogestacao.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.italo.medicogestacao.R;
import com.example.italo.medicogestacao.adapter.MensagemAdapter;
import com.example.italo.medicogestacao.config.ConfiguracaoFirebase;
import com.example.italo.medicogestacao.help.Base64Custom;
import com.example.italo.medicogestacao.help.Permissao;
import com.example.italo.medicogestacao.help.UsuarioFirebase;
import com.example.italo.medicogestacao.model.Conversa;
import com.example.italo.medicogestacao.model.Gestante;
import com.example.italo.medicogestacao.model.Mensagem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SalaChatActivity extends AppCompatActivity {
    private TextView nome;
    private CircleImageView fotoP;
    private Gestante gestanteDestinatario;
    private EditText digitaMsg;
    private ImageView camera;
    private static final int SELECAO_CAMERA = 100;

    //identificador usuarios remetente e destinatario
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;
    private StorageReference storage;
    private DatabaseReference database;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;

    private RecyclerView recyclerMEnsagem;
    private MensagemAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.CAMERA
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_chat2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //solicitação permissao
        Permissao.validarPermissao(permissoesNecessarias,this,SELECAO_CAMERA);

        //configurações iniciais
        fotoP = (CircleImageView)findViewById(R.id.CircleImageFoto);
        nome = (TextView)findViewById(R.id.textViewChat);
        digitaMsg = (EditText)findViewById(R.id.edtMensagem);
        camera = (ImageView)findViewById(R.id.imageViewCam);

        recyclerMEnsagem = (RecyclerView)findViewById(R.id.recycleMensagem);

        //recupera dados do usuario remetente;
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();


        //recuperar dados da gestante destinatario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            gestanteDestinatario = (Gestante)bundle.getSerializable("chatContato");
            nome.setText(gestanteDestinatario.getNomeC());

            String foto = gestanteDestinatario.getFoto();
            if (foto != null){
                Uri uri = Uri.parse(gestanteDestinatario.getFoto());
                Glide.with(SalaChatActivity.this).load(uri).into(fotoP);
            }else{
                fotoP.setImageResource(R.drawable.pregnancy);
            }
            //recupera dados  usuário destinatario;
            idUsuarioDestinatario = Base64Custom.codificarBase64(gestanteDestinatario.getEmail());
        }

        //configurando adapter
        adapter = new MensagemAdapter(mensagens,getApplicationContext());

        //Configurando recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMEnsagem.setLayoutManager( layoutManager );
        recyclerMEnsagem.setHasFixedSize(true);
        recyclerMEnsagem.setAdapter(adapter);


        database = ConfiguracaoFirebase.getFirebaseDataBase();
        storage = ConfiguracaoFirebase.getFirebaseStorege();
        mensagensRef = database.child("mensagens").child(idUsuarioRemetente).child(idUsuarioDestinatario);

        //evento de clique camera
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_CAMERA);
                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for ( int permissaoResult :grantResults){
            if(permissaoResult == PackageManager.PERMISSION_DENIED){
                alertvalidadePermission();
            }
        }
    }

    private void alertvalidadePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyCustomDialog);
        builder.setTitle("Permissão negada");
        builder.setCancelable(false);
        builder.setMessage("Para utilizar o chat é necessario da permissao");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;
            try {
                switch (requestCode) {
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;

                }


                if (imagem != null) {

                    //recuperar img FireBase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Criar nome imagem
                    String nomeImagem = UUID.randomUUID().toString();

                    //Configurando referencias do firebase
                    StorageReference imgRef = storage.child("imagens")
                            .child("fotoschat")
                            .child(idUsuarioRemetente)
                            .child(nomeImagem);
                    UploadTask uploadTask = imgRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ERRO", "Erro ao fazer upload");
                            Toast.makeText(SalaChatActivity.this, "Erro ao fazer upload", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String donwloadUrl = taskSnapshot.getDownloadUrl().toString();
                            Mensagem mensagem = new Mensagem();
                            mensagem.setIdUsuario(idUsuarioRemetente);
                            mensagem.setMensagem("imagem.jpeg");
                            mensagem.setImagem(donwloadUrl);

                            //salvando imagem para remetente
                            salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario, mensagem);
                            //Salvar mensagem destinatario
                            salvarMensagem(idUsuarioDestinatario,idUsuarioRemetente,mensagem);
                            recyclerMEnsagem.smoothScrollToPosition(mensagens.size()+1);
                            Toast.makeText(SalaChatActivity.this, "Sucesso ao enviar", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void enviarMensagem(View view){
        String txtmensagem = digitaMsg.getText().toString();

        if (!txtmensagem.isEmpty()){

            Mensagem mensagem = new Mensagem();
            mensagem.setIdUsuario(idUsuarioRemetente);
            mensagem.setMensagem(txtmensagem);

            //salvar mensagem para o remetente
            salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario,mensagem);
            //salvar mensagem para o destinatario
            salvarMensagem(idUsuarioDestinatario,idUsuarioRemetente,mensagem);
            //salvar conversa
            salvarConversa(mensagem);

        }else{
            Toast.makeText(this, "Digite uma menasgem para enviar!", Toast.LENGTH_SHORT).show();
        }

    }

    private void salvarConversa(Mensagem msg){
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idUsuarioRemetente);
        conversaRemetente.setIdDestinatario(idUsuarioDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem());
        conversaRemetente.setUsuarioExibicao(gestanteDestinatario);
        conversaRemetente.salvar();
    }


    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDataBase();
        DatabaseReference mensagemREf = database.child("mensagens");
        mensagemREf.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);
        //limpar text
        digitaMsg.setText("");

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }

    private void recuperarMensagens(){
        mensagens.clear();
        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
                recyclerMEnsagem.smoothScrollToPosition(mensagens.size()+1);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

}
