package mbr.com.meubattleroyale.VIEW.FRAGMENT;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;
import mbr.com.meubattleroyale.MODEL.GERAL.Avatar;
import mbr.com.meubattleroyale.MODEL.GERAL.Usuario;
import mbr.com.meubattleroyale.R;
import mbr.com.meubattleroyale.VIEW.ACTIVITY.Login;
import mbr.com.meubattleroyale.VIEW.ACTIVITY.SelecionarAvatar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.tomer.fadingtextview.FadingTextView;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Settings extends Fragment implements RewardedVideoAdListener
{
    private Button btnPro,btnGanhar;
    private ImageButton imgTrocarImg,btnLogout;
    private DatabaseHelper db;
    private FrameLayout frml;
    private ArrayList<Avatar> avatars = new ArrayList<>();
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private TextView txtNick,txtTermos,txtEmail,txtAmigos,txtVersao,txtSaldo;
    private AdView mAdView;
    private FirebaseAuth mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private ArrayList<Amigo> amigos = new ArrayList<>();
    private String[] tipo;
    private String TAG = "SETTINGS_";
    private FadingTextView txtAjuda;
    private RewardedVideoAd mAd;

    /******* SOBRE O ITEM TIPO QUE É RECUPERADO DO BANCO *********
     TIPO[0] = SALDO  --> 0.55 CENTS
     TIPO[1] = VERSAO --> PRÓ(TODOS OS AVATARES DESBLOQUEADOS E NENHUM ANUNCIO) OU
     FREE(ANUNCIOS E AVATARES BLOQUEADOS,COM EXCESSÃO DOS CONJUNTOS COMPRADOS)
     TIPO[2] = DIA EFETUOU PRÓ(dias que faltam para versão pró expirar)
     TIPO[3] = CONJUNTO DE AVATARES --> 010 (DIG 1 = PCTE EPICO , DIG 2 = PCTE LENDARIO , DIG 3 = PCTE MITICO)
     */


    public Settings()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus)
            {

            }
        });
        mAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mAd.setRewardedVideoAdListener(this);
        mAd.loadAd("ca-app-pub-3940256099942544/5224354917",new AdRequest.Builder().build());

        fazerCast(view);
        db = new DatabaseHelper(getContext());
        Log.d(TAG,"TAMANHO DB: "+db.getQTDAvatares());
        usuarios.clear();
        amigos.clear();
        usuarios.addAll(db.recuperarUsuarios());
        amigos.addAll(db.recuperaAmigos());
        Log.d(TAG, "onCreateView: TIPO:"+amigos.get(0).getTipo());
        tipo = amigos.get(0).getTipo().split("@@");
        if (db.getQTDAvatares() > 0)
        {
            avatars.clear();
            avatars.addAll(db.recuperarAvatar());
            if (avatars.get(0).getAvatar().equals("0"))
            {
                imgTrocarImg.setImageResource(R.drawable.ic_add_avatar);
            }
            else imgTrocarImg.setImageResource(Avatar.identificarAvatar(Integer.parseInt(avatars.get(0).getAvatar())));
            Log.d(TAG,"AVATAR: "+avatars.get(0).getAvatar());
            Log.d(TAG,"HORA DA ATUALIZAÇÃO: "+avatars.get(0).getCriado());
        }
        txtAmigos.setText(" Possui "+(amigos.size() - 1) +" amigos");
        txtNick.setText(" "+usuarios.get(0).getNickname());
        txtEmail.setText(" "+mAuth.getCurrentUser().getEmail());
        txtVersao.setText("Versão: "+tipo[1]);
        txtSaldo.setText("Saldo: R$"+tipo[0]);
        if (!tipo[1].equals("Free"))
        {
            btnPro.setVisibility(View.GONE);
            frml.setVisibility(View.GONE);
            txtAjuda.setVisibility(View.GONE);
        }
        return view;
    }
    private void fazerCast(View view)
    {
        txtAjuda = view.findViewById(R.id.txtAjuda);
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtAmigos = view.findViewById(R.id.txtAmigos);
        btnPro = view.findViewById(R.id.btnPro);
        txtVersao = view.findViewById(R.id.txtVersao);
        txtSaldo = view.findViewById(R.id.txtSaldo);
        btnGanhar = view.findViewById(R.id.btnGanhar);
        btnLogout = view.findViewById(R.id.btnLogout);
        imgTrocarImg = view.findViewById(R.id.imgAvatar_setting);
        txtNick = view.findViewById(R.id.txtNick);
        txtTermos = view.findViewById(R.id.txtTermos);
        frml = view.findViewById(R.id.frml);

        txtTermos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Snackbar.make(v,"Clicou em termos", BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });


        btnPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Ganhou 10 pontos")
                        .setCustomImage(R.drawable.ic_fireworks)
                        .setContentText("Parabéns,acaba de ganhar 10 pontos que serão adicionados a sua conta :)")
                        .setConfirmText("Valeu!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(final SweetAlertDialog sDialog)
                            {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getContext(),"ID desconectado :)",Toast.LENGTH_LONG).show();
                //ref.child("usuarios").child(usuarios.get(0).getId()).child("tipo").setValue("deslogado");
                FirebaseAuth.getInstance().signOut();

// DELETANDO BANCO LOCAL
                getContext().deleteDatabase("meuForti_db");

                startActivity(new Intent(getContext(), Login.class));
            }
        });

        imgTrocarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SelecionarAvatar.class);
                Bundle bundle = new Bundle();
                Log.d(TAG,"IDUSER: "+usuarios.get(0).getId());
                bundle.putString("id_user",usuarios.get(0).getId());
                if (db.getQTDAvatares() > 0)
                {
                    bundle.putInt("id_avt",avatars.get(0).getId());
                    bundle.putString("tipo",amigos.get(0).getTipo());
                }
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        btnGanhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnGanhar.setEnabled(false);
                if (mAd.isLoaded())
                {
                    mAd.show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        amigos.clear();
        amigos.addAll(db.recuperaAmigos());
        Log.d(TAG, "onResume: TIPO:"+amigos.get(0).getTipo());
        tipo = amigos.get(0).getTipo().split("@@");
        txtSaldo.setText("Saldo: "+tipo[0]+" pontos");
        if (db.getQTDAvatares() > 0)
        {
            avatars.clear();
            avatars.addAll(db.recuperarAvatar());
            if (avatars.get(0).getAvatar().equals("0"))
            {
                imgTrocarImg.setImageResource(R.drawable.ic_add_avatar);
            }
            else imgTrocarImg.setImageResource(Avatar.identificarAvatar(Integer.parseInt(avatars.get(0).getAvatar())));
            Log.d(TAG,"AVATAR: "+avatars.get(0).getAvatar());
            Log.d(TAG,"HORA DA ATUALIZAÇÃO: "+avatars.get(0).getCriado());
        }
    }


// listeners do reward para interagir
    @Override
    public void onRewardedVideoAdLoaded()
    {
        Log.d(TAG, "onRewardedVideoAdLoaded: ");
        btnGanhar.setEnabled(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdOpened: ");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d(TAG, "onRewardedVideoStarted: ");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.d(TAG, "onRewardedVideoAdClosed: ");
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.d(TAG, "onRewarded: getType()"+rewardItem.getType()+" e amount:"+rewardItem.getAmount());
        salvarReward();
        new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Ganhou 10 pontos")
                .setCustomImage(R.drawable.ic_fireworks)
                .setContentText("Parabéns,acaba de ganhar 10 pontos que serão adicionados a sua conta :)")
                .setConfirmText("Valeu!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();

    }

    private void salvarReward()
    {
            int pontos = Integer.parseInt(tipo[0]);
            pontos = pontos + 10;

            Log.d(TAG, "SALDO FINAL: "+pontos);

            final String tipoFinal =  pontos+"@@"+
                    tipo[1]+"@@"+
                    tipo[2]+"@@"+
                    tipo[3];
            Log.d(TAG, "salvarReward: tipoFinal: "+ tipoFinal);
            ref.child("usuarios").child(amigos.get(0).getId()).child("tipo").setValue(tipoFinal).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "SALVARREWARD: DB.ATUALIZADO");
                    db.atualizarAmigo(new Amigo(amigos.get(0).getIcone(),amigos.get(0).getNick(),tipoFinal,amigos.get(0).getId(),amigos.get(0).getAmigos()));
                }
            });
    }

    @Override
    public void onRewardedVideoAdLeftApplication()
    {
        Log.d(TAG, "onRewardedVideoAdLeftApplication: ");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.d(TAG, "onRewardedVideoAdFailedToLoad: "+i);
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.d(TAG, "onRewardedVideoCompleted: ");
    }
}
