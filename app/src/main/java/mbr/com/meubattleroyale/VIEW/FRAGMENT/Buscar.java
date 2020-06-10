package mbr.com.meubattleroyale.VIEW.FRAGMENT;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;
import mbr.com.meubattleroyale.MODEL.INTERFACE.CustomBusca;
import mbr.com.meubattleroyale.R;
import mbr.com.meubattleroyale.VIEW.ADAPTER.AdaptadorBusca;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class Buscar extends Fragment
{
    private LinearLayout lnlTopo,lnlFundo,lnlBusca;
    private Switch swtchMeuHS,swtchSeuHS,swtchDuo,swtchSQD;
    private ImageButton img;
    private LottieAnimationView animationView;
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private RecyclerView recbusca;
    private DatabaseHelper db;

    private String TAG = "BUSCAR_";
    private ArrayList<Amigo> listUsers = new ArrayList<>(),meuUser = new ArrayList<>();
    private AdaptadorBusca adapter;
    private  String mHs = "semhead",hsA = "semhead",tipoSquad = "";
    private ChildEventListener buscaListener;
    private TextView txt;
    private TextView txtTemporizador;
    private int cont = 0;
    private int qtd = 0;
    private CountDownTimer countDownTimer;
    private AdView mAdView;
    private FrameLayout frml;
    private String[] tipo;
    private int limite = 3;

    public Buscar()
    {
    }

    @Override
    public void onPause()
    {
        super.onPause();
        verificarSwitchs(meuUser.get(0).getId(),1);
        pararBusca(mHs,tipoSquad,meuUser.get(0).getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buscar, container, false);
        fazerCast(view);
        db = new DatabaseHelper(getContext());
        meuUser.addAll(db.recuperaAmigos());
        tipo = meuUser.get(0).getTipo().split("@@");
        if (tipo[1].equals("Pro"))
        {
            frml.setVisibility(View.GONE);
            limite = 99999;
        }
        Log.d("BUSCAR_","meuUser.get(0).getId(): "+meuUser.get(0).getId());
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        recbusca.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdaptadorBusca(getContext(), listUsers, meuUser.get(0).getId(), new CustomBusca() {
            @Override
            public void onCopiar(ImageButton button, int position, Amigo usuario) {
                // inserir funcao copiar id
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text",usuario.getNick());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(),"Copiado com sucesso",Toast.LENGTH_LONG).show();
                button.setImageResource(R.drawable.ic_success);
            }

            @Override
            public void onMessagemClick(ImageButton button, int position, Amigo usuario)
            {
                ref.child("novaMensagem").child(meuUser.get(0).getId()).setValue(usuario.getId());
            }
        });
        recbusca.setAdapter(adapter);
        img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (swtchDuo.isChecked() || swtchSQD.isChecked())
                {

                    iniciarTemporizador();
                }
                else
                {
                    Snackbar.make(getView(),"Voce precisa selecionar pelo menos ESQUADRÕES OU DUPLAS",Snackbar.LENGTH_LONG).show();
                }
            }
        });
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                verificarSwitchs(meuUser.get(0).getId(),1);
                pararBusca(mHs,tipoSquad,meuUser.get(0).getId());
            }
        });



     return view;
    }

    private void iniciarTemporizador()
    {
        cont = 0;
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);
        img.setVisibility(View.GONE);
        lnlTopo.setVisibility(View.GONE);
        lnlFundo.setVisibility(View.VISIBLE);
        Log.d("BUSCAR_","RESUMO: \n SWITCH MHS: "+swtchMeuHS.getShowText()+
                "\nSWITCH SHS: "+swtchSeuHS.getShowText()+
                "\nSWITCH DUO: "+swtchDuo.getShowText()+
                "\nSWITCH SQD: "+swtchSQD.getShowText()
        );
        countDownTimer = new CountDownTimer(30000, 1000)
        {
            @Override
            public void onTick(long l)
            {
                txtTemporizador.setText("Buscando "+ cont + " segundos");
                switch (cont)
                {
                    case 5:
                        verificarSwitchs(meuUser.get(0).getId(),0);
                    case 15:
                        if (listUsers.size() == qtd)
                        {
                            this.cancel();
                            pararBusca(mHs,tipoSquad,meuUser.get(0).getId());
                            txtTemporizador.setText("Clique para buscar");
                            txt.setText("Encontramos "+ qtd + " nicks");
                        }
                    case 29:
                        if (listUsers.size() != qtd)
                        {
                            txt.setText("Encontramos "+ (listUsers.size()) + " nicks");
                        }
                }
                cont ++;
            }

            @Override
            public void onFinish()
            {
                pararBusca(mHs,tipoSquad,meuUser.get(0).getId());
                txtTemporizador.setText("Clique para buscar");
                txt.setText("Encontramos "+ listUsers.size() + " nicks");
            }
        }.start();
    }

    private void verificarSwitchs(String meuId,int tip)
    {
        if (swtchSeuHS.isChecked())
        {
            Log.d(TAG, "verificarSwitchs:swtchSeuHS TRUE");
            hsA = "comhead";
        }
        if (swtchMeuHS.isChecked())
        {
            Log.d(TAG, "verificarSwitchs:swtchMeuHS TRUE");
            mHs = "comhead";
        }
        if (swtchSQD.isChecked())
        {
            Log.d(TAG, "verificarSwitchs:swtchSQD TRUE");
            qtd = 4;
            tipoSquad = "squad";
        }
        if (swtchDuo.isChecked())
        {
            qtd = 2;
            Log.d(TAG, "verificarSwitchs:swtchDuo TRUE");
            tipoSquad = "dupla";
        }
        if (tip == 0)
        {
            listUsers.clear();
            iniciarBusca(mHs,hsA,tipoSquad,meuId,qtd);
        }
    }

    private void iniciarBusca(final String mHeadset, final String headsetA, final String tipoSquad, final String meuid, final int qtd)
    {
        ref.child("pareamento").child(mHeadset).child(tipoSquad).child(meuid).setValue(meuUser.get(0));

        final int quantidade = qtd -1;
        txt.setText("Buscando "+quantidade+" amiguinhos");

        //adicionando o proprio usuario
        listUsers.add(meuUser.get(0));
        adapter.notifyDataSetChanged();

        buscaListener = new ChildEventListener()
        {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Amigo amigo = dataSnapshot.getValue(Amigo.class);
                if (listUsers.size() <= quantidade)
                {
                    if (!amigo.getId().equals(meuUser.get(0).getId()))
                    {
                        txt.setText(amigo.getNick() +" adicionado a lista");
                        listUsers.add(amigo);
                        adapter.notifyDataSetChanged();
                        recbusca.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }
                else
                {
                    txt.setText(" Busca concluida \n copie os nicks em seu jogo");
                    pararBusca(mHeadset,tipoSquad,meuid);
                }
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                Log.d(TAG,"Removido "+ dataSnapshot.getValue(Amigo.class).toString());
                listUsers.remove(dataSnapshot.getValue(Amigo.class));
                adapter.notifyDataSetChanged();
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override public void onCancelled(DatabaseError databaseError)
            {

            }
        };
        ref.child("pareamento").child(headsetA).child(tipoSquad).addChildEventListener(buscaListener);
    }

    private void pararBusca(final String mHeadset, final String tipoSquad, final String meuid)
    {
        try
        {
            countDownTimer.cancel();
            txtTemporizador.setText("Clique para buscar");
            animationView.setVisibility(View.GONE);
            animationView.cancelAnimation();
            img.setVisibility(View.VISIBLE);
            txt.setText(" Busca concluida \n copie os nicks em seu jogo");
            lnlTopo.setVisibility(View.VISIBLE);
            ref.child("pareamento").child(mHeadset).child(tipoSquad).child(meuid).removeValue();
            ref.removeEventListener(buscaListener);
        }catch (NullPointerException e)
        {
            animationView.cancelAnimation();
            img.setVisibility(View.VISIBLE);
            animationView.setVisibility(View.GONE);
            lnlTopo.setVisibility(View.VISIBLE);
        }

    }

    public void fazerCast(View view)
    {
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        frml = (FrameLayout) view.findViewById(R.id.frml);
        txt = (TextView) view.findViewById(R.id.txtQTD);
        txtTemporizador = (TextView) view.findViewById(R.id.txtContagem);
        lnlBusca = (LinearLayout) view.findViewById(R.id.lnl);
        lnlTopo = (LinearLayout) view.findViewById(R.id.lnlTopo_buscar);
        lnlFundo = (LinearLayout) view.findViewById(R.id.lnlFundo_buscar);
        swtchDuo = (Switch) view.findViewById(R.id.swtchDuo_buscar);
        swtchSQD = (Switch) view.findViewById(R.id.swtchSquad_buscar);
        swtchMeuHS = (Switch) view.findViewById(R.id.swtchMeuHeadSet_buscar);
        swtchSeuHS = (Switch) view.findViewById(R.id.swtchSeuHeadSet_buscar);
        img = (ImageButton)view.findViewById(R.id.imgbSearch_BUSCAR);
        animationView = (LottieAnimationView) view.findViewById(R.id.animation_view);
        recbusca = (RecyclerView)view.findViewById(R.id.recBusca);
        frml = (FrameLayout) view.findViewById(R.id.frml);

    }

}
