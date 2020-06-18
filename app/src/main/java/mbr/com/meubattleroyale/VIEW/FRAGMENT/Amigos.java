package mbr.com.meubattleroyale.VIEW.FRAGMENT;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;
import mbr.com.meubattleroyale.MODEL.GERAL.Avatar;
import mbr.com.meubattleroyale.MODEL.GERAL.Notificacao;
import mbr.com.meubattleroyale.MODEL.GERAL.Usuario;
import mbr.com.meubattleroyale.MODEL.INTERFACE.CustomClick;
import mbr.com.meubattleroyale.MODEL.INTERFACE.CustomMsgeNtfc;
import mbr.com.meubattleroyale.R;
import mbr.com.meubattleroyale.VIEW.ADAPTER.AdaptadorAmigos;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Amigos extends Fragment
{

    private EditText srchBuscar;
    private TextView  txtUsuario,txtBusca;
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private ValueEventListener eventBuscaListener;
    private RecyclerView recBusca,recAmigos;
    private ProgressBar prgUser;

    private AdaptadorAmigos adapterBusca,adaptadorAmigos ;
    private DatabaseHelper db;

    private ArrayList<Amigo> listAmigos = new ArrayList<>();
    private ArrayList<Amigo> listaBusca = new ArrayList<>();

    private ArrayList<Usuario> meuUsuario = new ArrayList<>();
    private ArrayList<Avatar> meuAvatar = new ArrayList<>();
    private ViewPager viewPager;
    private FrameLayout frml;

    private AdView mAdView;
    private String[] tipo;
    private String TAG = "AMIGOS_";
    private int limite = 6;

    /******* SOBRE O ITEM TIPO QUE É RECUPERADO DO BANCO *********
     TIPO[0] = SALDO  --> 0.55 CENTS
     TIPO[1] = VERSAO --> PRÓ(TODOS OS AVATARES DESBLOQUEADOS E NENHUM ANUNCIO) OU
     FREE(ANUNCIOS E AVATARES BLOQUEADOS,COM EXCESSÃO DOS CONJUNTOS COMPRADOS)
     TIPO[2] = DIA EFETUOU PRÓ(dias que faltam para versão pró expirar)
     TIPO[3] = CONJUNTO DE AVATARES --> 010 (DIG 1 = PCTE EPICO , DIG 2 = PCTE LENDARIO , DIG 3 = PCTE MITICO)
     */

    public Amigos()
    {
    }

    @Override
    public void onStart()
    {
        super.onStart();
       // recuperarBancoRemoto();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_amigos, container, false);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        fazerCast(view);
        Log.d(TAG,"CRIANDO VIEW");
        return view;
    }


    private void recuperarMeusDados()
    {
        db = new DatabaseHelper(getContext());
        meuUsuario.addAll(db.recuperarUsuarios());
        meuAvatar.addAll(db.recuperarAvatar());
        listAmigos.addAll(db.recuperaAmigos());
        tipo = listAmigos.get(0).getTipo().split("@@");
        if (listAmigos.size() == 1)
        {
            txtUsuario.setVisibility(View.VISIBLE);
            txtUsuario.setText("Voce não possui nenhum amigo adicionado! \n Busque por novos amigos...");
        }
        if (tipo[1].equals("Pro"))
        {
            frml.setVisibility(View.GONE);
            limite = 99999;
        }
        Log.d("Amigos","LISTA AMIGOS SIZE "+listAmigos.size()+" \n Banco local TAM: "+ db.getQTDAmigos());
    }

    private void recuperarBF(String id)
    {
        ref.child("usuarios").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Amigo meuUser = dataSnapshot.getValue(Amigo.class);
                    // implementar banco remoto que não esta atualizando
                try
                {
                    if (meuUser.getAmigos().size() > 0)
                    {
                        for (int i = 0; i < meuUser.getAmigos().size(); i++)
                        {
                            buscarNicks(meuUser.getAmigos().get(i));
                        }
                    }
                }catch (NullPointerException e)
                {

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void buscarNicks(String id)
    {
        ref.child("usuarios").child(id).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                    Log.d(TAG,"CHAVE: "+dataSnapshot.getValue());
                    Amigo meuAmigo = dataSnapshot.getValue(Amigo.class);
                    Log.d(TAG,"AMIGO: "+meuAmigo.getNick());
                boolean res = false;
                for (int i = 0; i < listAmigos.size(); i++)
                {
                    if (listAmigos.get(i).getNick().contains(meuAmigo.getNick()))
                    {
                        res = true;
                        if (listAmigos.get(i).getIcone() != meuAmigo.getIcone())
                        {
                            db.atualizarAmigo(meuAmigo);
                            listAmigos.get(i).setIcone(meuAmigo.getIcone());
                            listAmigos.get(i).setAmigos(meuAmigo.getAmigos());
                            txtUsuario.setVisibility(View.GONE);
                            adaptadorAmigos.notifyDataSetChanged();
                        }
                    }
                }
                if (res == false)
                {
                    db.inserirAmigo(meuAmigo);
                    listAmigos.add(meuAmigo);
                    txtUsuario.setVisibility(View.GONE);
                    adaptadorAmigos.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"VIEW CRIADA");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG,"PAUSADO");
        limparRecycler(1);
        limparRecycler(0);
        txtBusca.setVisibility(View.GONE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG,"RESUMO");
        try
        {
            recuperarMeusDados();
        }catch (NullPointerException e)
        {
            if (listAmigos.size() == 1)
            {
                txtUsuario.setVisibility(View.VISIBLE);
                txtUsuario.setText("Voce não possui nenhum amigo adicionado! \n Busque por novos amigos...");
            }
        }
        iniciRecAmigos();
        recuperarBF(listAmigos.get(0).getId());
        iniciarRecBusca();
        recAmigos.setAdapter(adaptadorAmigos);
    }

    private void fazerCast(final View view)
    {
        Log.d(TAG,"FAzendo CASt");
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        viewPager = (ViewPager) getActivity().findViewById(R.id.vp_painel);
        srchBuscar = view.findViewById(R.id.edtBuscar_busca);
        txtUsuario = view.findViewById(R.id.txtUsuarios_Busca);
        txtBusca = view.findViewById(R.id.txtResBusca);
        recBusca = view.findViewById(R.id.recBuscaUsuarios_Busca);
        recAmigos = view.findViewById(R.id.recAmigos);
        prgUser = view.findViewById(R.id.prgbUser_Busca);
        frml = (FrameLayout) view.findViewById(R.id.frml);

        //listAmigos.add(new Amigo())

        srchBuscar.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    Log.d(TAG,"Dentro do srch");
                    limparRecycler(0);
                    iniciarRecBusca();
                    prgUser.setVisibility(View.GONE);
                    final String query = srchBuscar.getText().toString();
                            if (query.length() != 1)
                            {
                                if (query.isEmpty())
                                {
                                    srchBuscar.clearFocus();
                                }
                                else
                                {
                                    //ref.child("nick").addListenerForSingleValueEvent(new ValueEventListener()
                                    Query nicksQuery = ref.child("usuarios").orderByChild("nick").equalTo(query);

                                    eventBuscaListener = new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            boolean resultado = false;
                                            for (DataSnapshot dados : dataSnapshot.getChildren())
                                            {
                                                Log.d(TAG, "Resultado: " + query);
                                                Log.d(TAG, "onDataChange: "+dados.getKey());
                                                Amigo amigo = dados.getValue(Amigo.class);
                                                Log.d(TAG, "onDataChange Dados amigo: "+amigo.getNick());
                                                //String nicks = dados.getKey();
                                                if (amigo.getNick().equals(query))
                                                {
                                                    if (!amigo.getId().equals(listAmigos.get(0).getId()))
                                                    {
                                                        resultado = true;
                                                        prgUser.setVisibility(View.VISIBLE);
                                                        srchBuscar.clearFocus();
                                                        txtBusca.setVisibility(View.VISIBLE);
                                                        Log.d(TAG, "Contém: " + dados.getValue().toString());
                                                        prgUser.setVisibility(View.VISIBLE);
                                                        popularLista(amigo);
                                                    }
                                                }
                                            }
                                            if (resultado == false)
                                            {
                                                srchBuscar.clearFocus();
                                                txtBusca.setVisibility(View.VISIBLE);
                                                txtBusca.setText("A busca não obteve sucesso...");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError)
                                        {

                                        }
                                    };
                                    nicksQuery.addListenerForSingleValueEvent(eventBuscaListener);
                                }
                            }
                        }
                return false;
            }
        });

    }
    private void popularLista(Amigo amigo)
    {
        prgUser.setVisibility(View.GONE);
        txtUsuario.setVisibility(View.VISIBLE);
        txtUsuario.setText("usuarios");
        listaBusca.add(amigo);
        adapterBusca.notifyDataSetChanged();
        recAmigos.setVisibility(View.VISIBLE);
        txtBusca.setVisibility(View.VISIBLE);
    }

    private void limparRecycler(int t)
    {
        switch (t)
        {
            case 0:
                listaBusca.clear();
                adapterBusca.notifyDataSetChanged();
                break;

            case 1:
                listAmigos.clear();
                adaptadorAmigos.notifyDataSetChanged();
                break;
        }
    }
    private void iniciRecAmigos()
    {
        // adicionar modificações user

        //recycler Amigo
        LinearLayoutManager lnlMAmigos = new LinearLayoutManager(getContext());
        lnlMAmigos.setOrientation(LinearLayoutManager.VERTICAL);
        recAmigos.setLayoutManager(lnlMAmigos);
        adaptadorAmigos = new AdaptadorAmigos(getContext(), listAmigos,meuUsuario.get(0).getId(),0, new CustomClick()
        {
            @Override
            public void onItemClick(View itemView, int position, Button button, final Amigo meuUsario, Amigo usuario)
            {
                //Toast.makeText(getApplicationContext(), "Amigo adicionado: Posição da Lista"+listaUsuarios.get(position).getNome(), Toast.LENGTH_LONG).show();
                Drawable imgClicked  = getActivity().getResources().getDrawable(R.drawable.ic_amigos_check);
                Drawable imgOutClicked  = getActivity().getResources().getDrawable(R.drawable.ic_amigos);
//Tratando da lista de amigos
                Log.d(TAG, "REC CLICADO : " + usuario.getNick());
                if (meuUsario.getAmigos() != null)
                {
                    if (meuUsario.getAmigos().contains(usuario.getId()))
                    {
                        Log.d("AMIGOs", "meuUsuario.getAmigos().contains != null IF  | Nick usuario " + usuario.getNick() + " E Nick meuAmigo: " + meuUsario.getNick());
                        button.setCompoundDrawablesWithIntrinsicBounds(null, null, null,imgOutClicked);
                        button.setText("Seguir");
                        meuUsario.getAmigos().remove(usuario.getId());
                        //atualizando banco firebase
                        ref.child("usuarios").child(meuUsario.getId()).child("amigos").setValue(meuUsario.getAmigos());
                        //removendo do banco local
                        Log.i("AMIGOs","()deletando amigo,Antes: "+ db.getQTDAmigos()+" amigos");
                        db.deletarAmigo(usuario,"");
                        listAmigos.remove(usuario);
                        // ATUALIZAR LISTA ATUAL
                        listAmigos.clear();
                        listAmigos.addAll(db.recuperaAmigos());
                        adaptadorAmigos.notifyDataSetChanged();
                        Log.i("AMIGOs","atualizando banco,Depois: "+db.getQTDAmigos()+" amigos");
                    } else
                    {
                        //se limite de 5 for atingido eu recomendo ao usuário a virar pró
                        if (listAmigos.size() < limite)
                        {
                            Log.d("AMIGOs", "meuUsuario.getAmigos().contains != null ELSE  | Nick usuario " + usuario.getNick() + " E Nick meuAmigo: " + meuUsario.getNick());
                            button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, imgClicked);
                            button.setText("Seguindo");
                            //inserindo novo amigo no banco local
                            Log.i("AMIGOs","(APOS_SALVAR)atualizando amigo,Antes: "+ db.getQTDAmigos()+" amigos");
                            db.inserirAmigo(usuario);
                            listAmigos.add(usuario);
                            adaptadorAmigos.notifyDataSetChanged();
                            Log.i("AMIGOs","(APOS_SALVAR)atualizando banco,Depois: "+db.getQTDAmigos()+" amigos");
                            meuUsario.getAmigos().add(usuario.getId());
                            ref.child("usuarios").child(meuUsario.getId()).child("amigos").setValue(meuUsario.getAmigos());
                        }
                        else
                        {
                            //chamar pro caso o usúario tenha atingido o limite
                            chamarPro(meuUsario);
                        }
                    }

                } else
                {
                    Log.d("AMIGOs", "meuUsuario.getAmigos() != null ELSE | Nick usuario " + usuario.getNick() + " E Nick meuAmigo: " + meuUsario.getNick());
                    button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, imgClicked);
                    button.setText("Seguindo");
                    ArrayList<String> staticList = new ArrayList<>();
                    staticList.add(usuario.getId());
                    meuUsario.setAmigos(staticList);
                    ref.child("usuarios").child(meuUsario.getId()).child("amigos").setValue(staticList);

                    //adicionando no banco local
                    Log.i("AMIGOs","()deletando amigo,Antes: "+ db.getQTDAmigos()+" amigos");
                    db.inserirAmigo(usuario);
                    listAmigos.add(usuario);
                    adaptadorAmigos.notifyDataSetChanged();
                    Log.i("AMIGOs","atualizando banco,Depois: "+db.getQTDAmigos()+" amigos");

                }

            }

        },new CustomMsgeNtfc()
        {
            @Override
            public void onNotificacaoClick(ImageButton button, int position, Amigo usuario)
            {
                chamarNotifificacao(usuario);
            }

            @SuppressLint("ResourceType")
            @Override
            public void onMessagemClick(ImageButton button, int position, Amigo usuario)
            {
                ref.child("novaMensagem").child(meuUsuario.get(0).getId()).setValue(usuario.getId());
                //viewPager.setCurrentItem(2);
            }
        });

    }
    private void iniciarRecBusca()
    {
        //recycler busca
        recBusca.setHasFixedSize(true);
        LinearLayoutManager lnlMBusca = new LinearLayoutManager(getContext());
        lnlMBusca.setOrientation(LinearLayoutManager.VERTICAL);
        recBusca.setLayoutManager(lnlMBusca);
        adapterBusca = new AdaptadorAmigos(getContext(), listaBusca,meuUsuario.get(0).getId(), 1, new CustomClick() {
            @Override
            public void onItemClick(View itemView, int position, Button button,
                                    Amigo meuUsuario, Amigo usuario) {
                //Toast.makeText(getApplicationContext(), "Amigo adicionado: Posição da Lista"+listaUsuarios.get(position).getNome(), Toast.LENGTH_LONG).show();
                Drawable imgClicked = getActivity().getResources().getDrawable(R.drawable.ic_amigos_check);
                Drawable imgOutClicked = getActivity().getResources().getDrawable(R.drawable.ic_amigos);
//Tratando da lista de amigos
                Log.d("Busca", "REC CLICADO : " + usuario.getNick());
                if (meuUsuario.getAmigos() != null)
                {
                    if (meuUsuario.getAmigos().contains(usuario.getId()))
                    {
                        Log.d("AMIGOs", "meuUsuario.getAmigos().contains != null IF  | Nick usuario " + usuario.getNick() + " E Nick meuAmigo: " + meuUsuario.getNick());
                        button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, imgOutClicked);
                        button.setText("Seguir");
                        meuUsuario.getAmigos().remove(usuario.getId());
                        //atualizando banco firebase
                        ref.child("usuarios").child(meuUsuario.getId()).child("amigos").setValue(meuUsuario.getAmigos());
                        //removendo do banco local
                        Log.d("AMIGOs", "Chegou aqui");
                        Log.i("AMIGOs", "()deletando amigo,Antes: " + db.getQTDAmigos() + " amigos");
                        db.deletarAmigo(usuario, "");
                        listAmigos.clear();
                        listAmigos.addAll(db.recuperaAmigos());
                        adaptadorAmigos.notifyDataSetChanged();
                        Log.i("AMIGOs", "atualizando banco,Depois: " + db.getQTDAmigos() + " amigos");
                    } else
                        {
                            if (listAmigos.size() < limite)
                            {
                                Log.d("AMIGOs", "meuUsuario.getAmigos().contains != null ELSE  | Nick usuario " + usuario.getNick() + " E Nick meuAmigo: " + meuUsuario.getNick());
                                button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, imgClicked);
                                button.setText("Seguindo");
                                //inserindo novo amigo no banco local
                                Log.i("AMIGOs", "(APOS_SALVAR)atualizando amigo,Antes: " + db.getQTDAmigos() + " amigos");
                                db.inserirAmigo(usuario);
                                Log.i("AMIGOs", "(APOS_SALVAR)atualizando banco,Depois: " + db.getQTDAmigos() + " amigos");
                                meuUsuario.getAmigos().add(usuario.getId());
                                ref.child("usuarios").child(meuUsuario.getId()).child("amigos").setValue(meuUsuario.getAmigos());
                                if (listAmigos.contains(usuario))
                                {
                                    listAmigos.remove(usuario);
                                }
                                listAmigos.add(usuario);
                                adaptadorAmigos.notifyDataSetChanged();
                                listaBusca.clear();
                                adapterBusca.notifyDataSetChanged();
                                txtBusca.setVisibility(View.GONE);
                                srchBuscar.clearFocus();
                            }
                            else
                            {
                                //chamar pro caso o usúario tenha atingido o limite
                                chamarPro(meuUsuario);
                            }
                        }

                } else
                    {
                        Log.d("AMIGOs", "meuUsuario.getAmigos() != null ELSE | Nick usuario " + usuario.getNick() + " E Nick meuAmigo: " + meuUsuario.getNick());
                        button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, imgClicked);
                        button.setText("Seguindo");
                        ArrayList<String> staticList = new ArrayList<>();
                        staticList.add(usuario.getId());
                        meuUsuario.setAmigos(staticList);
                        ref.child("usuarios").child(meuUsuario.getId()).child("amigos").setValue(staticList);

                        //adicionando no banco local
                        Log.i("AMIGOs", "(APOS_SALVAR)atualizando amigo,Antes: " + db.getQTDAmigos() + " amigos");
                        db.inserirAmigo(usuario);
                        if (listAmigos.contains(usuario))
                        {
                            listAmigos.remove(usuario);
                        }
                        listAmigos.add(usuario);
                        adaptadorAmigos.notifyDataSetChanged();

                        listaBusca.clear();
                        adapterBusca.notifyDataSetChanged();
                        txtBusca.setVisibility(View.GONE);
                        srchBuscar.clearFocus();
                        Log.i("AMIGOs", "(APOS_SALVAR)atualizando banco,Depois: " + db.getQTDAmigos() + " amigos");
                }
            }

        }, new CustomMsgeNtfc()
        {
            @Override
            public void onNotificacaoClick(ImageButton button, int position, Amigo usuario)
            {
                //ref.child("notificacao").child(usuario.getId()).setValue(new Notificacao(":",usuario.getId()));
                chamarNotifificacao(usuario);
            }

            @SuppressLint("ResourceType")
            @Override
            public void onMessagemClick(ImageButton button, int position, Amigo usuario)
            {
                ref.child("novaMensagem").child(meuUsuario.get(0).getId()).setValue(usuario.getId());
            }
        });
        recBusca.setAdapter(adapterBusca);
    }
        private void chamarNotifificacao(Amigo usuario)
        {
                //tipo+":"+idA+":"+mId+":"+icone+":"+nick+":"id
                ref.child("alerta").child(usuario.getId()).setValue(new Notificacao(
                        "0"+"###"+meuUsuario.get(0).getId()+"###"+usuario.getId()+ "###"+meuAvatar.get(0).getAvatar()+"###"+meuUsuario.get(0).getNickname(),
                        meuUsuario.get(0).getId(),DatabaseHelper.getDateTime())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(getView(),"Notificação encaminhada com sucesso!\n Aguarde resposta...", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                    }
                });
        }

    //Cria um alert dialog de chamada de plano pro
    public void chamarPro(final Amigo meuUsauario)
    {
        final double saldo = Double.parseDouble(tipo[0]);
        new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Limite de amigos atingido")
                .setCustomImage(R.drawable.ic_money)
                .setContentText("Seu limite de amigos é de apenas 5 ,Deseja virar pró e ter quantos amigos quiser?")
                .setConfirmText("Sim")
                .setCancelText("Não")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog
                                .setTitleText("Como deseja Pagar?")
                                .setCustomImage(R.drawable.ic_pig)
                                .setContentText("Voce possui R$ "+saldo+" de saldo")
                                .setConfirmText("Saldo")
                                .setCancelText("Google Play")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                                {
                                    @Override
                                    public void onClick(final SweetAlertDialog sDialog)
                                    {
                                        if (saldo < 0.99)
                                        {
                                            Toast.makeText(getContext(),"Seu saldo é inferior ao valor do pacote",Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            Double saldoFinal = saldo - 0.99;
                                            saldoFinal = Double.valueOf(Math.round(saldoFinal));
                                            Log.d(TAG, "SALDO FINAL: "+saldoFinal);
                                            final String tipoFinal =  saldoFinal+"@@"+
                                                    tipo[1]+"@@"+
                                                    "Pro"+"@@"+
                                                    tipo[3];
                                            ref.child("usuarios").child(meuUsuario.get(0).getId()).child("tipo").setValue(tipoFinal).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getContext(),"Pacote pró comprado com sucesso",Toast.LENGTH_LONG).show();
                                                    Log.d(TAG, "onComplete: DB.ATUALIZADO");
                                                    db.atualizarAmigo(new Amigo(meuUsauario.getIcone(),meuUsuario.get(0).getNickname(),tipoFinal,meuUsuario.get(0).getId(),meuUsauario.getAmigos()));
                                                    sDialog.dismissWithAnimation();
                                                    frml.setVisibility(View.GONE);

                                                }
                                            });
                                        }
                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
                                {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        processarPagamentoGP();
                                    }
                                });
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
    //processa pagamento google play
    private void processarPagamentoGP()
    {

    }



}
