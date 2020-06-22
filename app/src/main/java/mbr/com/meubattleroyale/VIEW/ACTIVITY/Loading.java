package mbr.com.meubattleroyale.VIEW.ACTIVITY;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;
import mbr.com.meubattleroyale.MODEL.GERAL.Avatar;
import mbr.com.meubattleroyale.MODEL.GERAL.Usuario;
import mbr.com.meubattleroyale.R;
import mbr.com.meubattleroyale.SERVICE.NotificacaoService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Loading extends AppCompatActivity
{
    private TextView txt;
    private DatabaseHelper dbLocal;
    private  ArrayList<Usuario> usuarios = new ArrayList<>();
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private FirebaseAuth mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        txt = (TextView) findViewById(R.id.txt_descricao);
        startAnimations();

    }
    private void startAnimations()
    {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        FrameLayout l = (FrameLayout) findViewById(R.id.frml);
        if (l != null)
        {
            l.clearAnimation();
            l.startAnimation(anim);
        }
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();

        animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        int SPLASH_DISPLAY_LENGTH = 2500;
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                consultarDados();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        dbLocal = new DatabaseHelper(getApplicationContext());
    }
//recupera banco e verifica login
    private void consultarDados()
    {
        recuperarBancoLocal();
        try
        {
            Log.d("LOADING_", "SIZE USUARIOS"+String.valueOf(usuarios.size()));
            if (mAuth.getCurrentUser() != null)
            {
                atualizarBancoLocal();
            }
            else
            {
                txt.setText("bem vindo a nossa plataforma");
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        } catch (IndexOutOfBoundsException e)
        {
            txt.setText("bem vindo a nossa plataforma");
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
    }

    private void atualizarBancoLocal()
    {
        ref.child("usuarios").child(usuarios.get(0).getId()).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Amigo meuUser = dataSnapshot.getValue(Amigo.class);
                try
                {
                    if (meuUser.getNick() != null)
                    {
                        txt.setText("bem vindo de volta ");
                        dbLocal.atualizarAmigo(meuUser);
                        if (dbLocal.getQTDAvatares() == 0)
                        {
                            Avatar avatar = new Avatar(1,String.valueOf(meuUser.getIcone()),DatabaseHelper.getDateTime());
                            dbLocal.inserirAvatar(avatar);
                            //iniciando service
                            Intent startIntent = new Intent(getApplicationContext(),NotificacaoService.class);
                            startIntent.setAction("NS");
                            startService(startIntent);
                        }
                        else
                        {
                            Avatar avatar = new Avatar(1,String.valueOf(meuUser.getIcone()),DatabaseHelper.getDateTime());
                            dbLocal.atualizarAvatar(avatar);
                            dbLocal.atualizarAmigo(meuUser);
                            dbLocal.atualizarUsuario(new Usuario(meuUser.getId(),DatabaseHelper.getDateTime(),meuUser.getNick()));
                            //iniciando service
                            //iniciando service
                            Intent startIntent = new Intent(getApplicationContext(),NotificacaoService.class);
                            startIntent.setAction("NS");
                            startService(startIntent);
                        }
                        startActivity(new Intent(getApplicationContext(), PainelPrincipal.class));
                    }
                    else
                    {
                        txt.setText("bem vindo a nossa plataforma");
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    }
                }catch (NullPointerException e)
                {
                    txt.setText("bem vindo a nossa plataforma");
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //VERIFICA SE O BANCO LOCAL NÃO ESTÁ VAZIO
    private void recuperarBancoLocal()
    {
        try
        {
            Log.d("LOADING_","SIZE BANCO "+dbLocal.getQTDUsuarios());
            if (dbLocal.getQTDUsuarios() > 0 )
            {
                usuarios.addAll( dbLocal.recuperarUsuarios());
                Log.d("LOADING_",usuarios.get(0).getId());
            }
        }
        catch (NullPointerException e)
        {
            usuarios = null;
        }
    }

}

