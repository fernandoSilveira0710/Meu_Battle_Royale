package mbr.com.meubattleroyale.VIEW.ACTIVITY;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.DAO.REMOTO.PermissionsUtils;
import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;
import mbr.com.meubattleroyale.MODEL.GERAL.Avatar;
import mbr.com.meubattleroyale.MODEL.GERAL.Usuario;
import mbr.com.meubattleroyale.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stephentuso.welcome.WelcomeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Login extends AppCompatActivity
{
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private FirebaseAuth mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();

    //nick
    private FrameLayout frmlPostNICK;
    private EditText edtNick;
    private Button btnCadNick;
    public ProgressBar prgLoginNICK;

    WelcomeHelper welcomeScreen;
    private String TAG = "LOGIN_";

    String[] permissoes = new String[]
            {
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.WAKE_LOCK
            };


    private DatabaseHelper db;
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private Button gg;
    private SweetAlertDialog pDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private String id = "";

    @Override
    protected void onStart()
    {
        super.onStart();
        db = new DatabaseHelper(getApplicationContext());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        recuperarBancoLocal();
        pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        
        welcomeScreen = new WelcomeHelper(this, TelaBoasVindas.class);
        welcomeScreen.show(savedInstanceState);
        PermissionsUtils.ActivePermissions(this,permissoes,1);
        fazerCast();
        btnCadNick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (!edtNick.getText().toString().isEmpty())
                {
                    prgLoginNICK.setVisibility(View.VISIBLE);
                    btnCadNick.setVisibility(View.GONE);
                    Usuario user = new Usuario(id,DatabaseHelper.getDateTime(),edtNick.getText().toString());
                    db.inserirUser(user);
                    salvar(user);

                }
            }
        });


        //Inicio configurações login com google
        final SignInButton signInButton =  findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);


//chamar botão google
        gg = (Button) findViewById(R.id.btnGoogle);
        gg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent sign = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(sign,9001);
            }
        });
    }
    private void logarcomGoogle(GoogleSignInAccount account)
    {
        showProgress(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            new SweetAlertDialog(Login.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Login Confirmado!")
                                    .setContentText("Login com google confirmado com sucesso!")
                                    .setConfirmText("Bora")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog)
                                        {
                                            sDialog.dismissWithAnimation();
                                            id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            Log.d("LOGIN_", "onClick: ID CRIADO "+id);
                                            recuperarBancoRemoto();
                                            //salvando usuário logado pela primeira vez
                                           //iniciarAnimacao();
                                        }
                                    })
                                    .show();
                        }
                        else
                        {
                            showProgress(false);
                            new SweetAlertDialog(Login.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Login Negado!")
                                    .setContentText("Algum erro não esperado aconteceu!")
                                    .setCancelText("Rever login")
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();

                        }
                    }
                });
        showProgress(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9001)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                //caso google autenticado com sucesso eu mando para o firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                logarcomGoogle(account);
            }catch (ApiException e)
            {
                Log.e("LOGIN_","um erro no login do Google: "+e);
            }
        }
    }
    private void showProgress(boolean b)
    {
        if (b == true)
        {
            pDialog.show();
        }
        else
        {
            pDialog.dismissWithAnimation();
        }

    }

    private void recuperarBancoRemoto()
    {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.child("usuarios").child(id).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                try
                {
                        Amigo user = dataSnapshot.getValue(Amigo.class);
                        Log.d(TAG, "recuperarBancoRemoto: "+ user.getNick());
                        if (user.getNick() != null)
                        {
                            db.inserirUser(new Usuario(user.getId(),DatabaseHelper.getDateTime(),user.getNick()));
                            db.inserirAmigo(user);
                            Log.d(TAG, "user.getNick() != null:\n QTDUSER:"+ db.getQTDUsuarios()+"\n QTDAMIGO: "+db.getQTDAmigos());
                            if (db.getQTDAvatares() == 0)
                            {
                                Avatar avatar = new Avatar(1,String.valueOf(user.getIcone()),DatabaseHelper.getDateTime());
                                db.inserirAvatar(avatar);
                                Log.d(TAG, "db.getQTDAvatares() == 0:\n QTDAVTR:"+ db.getQTDAvatares());
                            }
                            else
                            {
                                Avatar avatar = new Avatar(1,String.valueOf(user.getIcone()),DatabaseHelper.getDateTime());
                                db.atualizarAvatar(avatar);
                            }
                            prgLoginNICK.setVisibility(View.GONE);
                            new SweetAlertDialog(Login.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Cadastrado!")
                                    .setContentText("Seja bem vindo a nossa plataforma!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                                    {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog)
                                        {
                                            startActivity(new Intent(getApplicationContext(), PainelPrincipal.class));
                                        }
                                    })
                                    .show();
                        }
                        else
                        {
                            iniciarAnimacao();
                        }
                }catch (NullPointerException e)
                {
                    Log.d(TAG, "NULL POINTER");
                    iniciarAnimacao();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fazerCast()
    {

        //adm
        frmlPostNICK = findViewById(R.id.frml_adm);
        edtNick = findViewById(R.id.edtNickLogin);
        btnCadNick = findViewById(R.id.btnLogin);
        prgLoginNICK = findViewById(R.id.prgbLogin);
    }
    private void iniciarAnimacao()
    {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_fadein);
        anim.reset();
        if (frmlPostNICK != null && frmlPostNICK.getVisibility() == View.GONE)
        {
            frmlPostNICK.clearAnimation();
            frmlPostNICK.startAnimation(anim);
            frmlPostNICK.setVisibility(View.VISIBLE);
        }
        else if (frmlPostNICK != null && frmlPostNICK.getVisibility() == View.VISIBLE)
        {
            frmlPostNICK.clearAnimation();
            anim = AnimationUtils.loadAnimation(this,R.anim.anim_fadeout);
            frmlPostNICK.startAnimation(anim);
            frmlPostNICK.setVisibility(View.GONE);
        }
        if (edtNick != null)
        {
            edtNick.clearAnimation();
            btnCadNick.clearAnimation();
            edtNick.startAnimation(anim);
            btnCadNick.startAnimation(anim);
        }
        int SPLASH_DISPLAY_LENGTH = 1000;
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults)
        {
            if (result == PackageManager.PERMISSION_DENIED)
            {
                ativeasPermissoes();
                return;
            }
        }
    }
    private void ativeasPermissoes()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar este app, é necessário aceitar as permissões");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //VERIFICA SE O BANCO LOCAL NÃO ESTÁ VAZIO
    private void recuperarBancoLocal()
    {
        try
        {
            if (db.getQTDUsuarios() > 0 )
            {
                usuarios.addAll( db.recuperarUsuarios());
                Log.d("LOGIN_ACVTY",usuarios.get(0).getId());
            }
        }
        catch (NullPointerException e)
        {
            usuarios = null;
            Log.d("LOGIN_ACVTY","VAZIO ESSA PORRA");
        }
    }
    /******* SOBRE O ITEM TIPO QUE É RECUPERADO DO BANCO *********
     TIPO[0] = SALDO  --> 0.55 CENTS
     TIPO[1] = VERSAO --> PRÓ(TODOS OS AVATARES DESBLOQUEADOS E NENHUM ANUNCIO) OU
     FREE(ANUNCIOS E AVATARES BLOQUEADOS,COM EXCESSÃO DOS CONJUNTOS COMPRADOS)
     TIPO[2] = DIA EFETUOU PRÓ(dias que faltam para versão pró expirar)
     TIPO[3] = CONJUNTO DE AVATARES --> 010 (DIG 1 = PCTE EPICO , DIG 2 = PCTE LENDARIO , DIG 3 = PCTE MITICO)
     TIPO[4] = CONTROLE USUARIO --> 531 (DIG 1 = LIMITE AMIGOS , DIG 2 = LIMITE BUSCAS)
     */
    private void salvar(Usuario usuario)
    {
        String nickname = usuario.getNickname().replace(" ","");

        ref.child("nick").child(nickname).setValue(usuario.getId());

        Amigo amigo = new Amigo(0,nickname,"0@@Free@@0@@0&&0&&0", usuario.getId(), null);
        // salvar no banco local como amigo para recuperar futuramente; usuario lista0
        db.inserirAmigo(amigo);
        Avatar avatar = new Avatar(1,"0",DatabaseHelper.getDateTime());
        db.inserirAvatar(avatar);

        Map<String, Object> userMap = amigo.mapearUsuario();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/usuarios/"+amigo.getId(), userMap);
        ref.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                prgLoginNICK.setVisibility(View.GONE);
                new SweetAlertDialog(Login.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Cadastrado!")
                        .setContentText("Seja bem vindo a nossa plataforma!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog)
                            {
                                startActivity(new Intent(getApplicationContext(), PainelPrincipal.class));
                            }
                        })
                        .show();
            }
        });

    }
    private void closeKeyboard(View view)
    {
        final InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
