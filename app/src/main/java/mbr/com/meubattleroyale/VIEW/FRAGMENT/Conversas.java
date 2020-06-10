package mbr.com.meubattleroyale.VIEW.FRAGMENT;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;
import mbr.com.meubattleroyale.MODEL.GERAL.Avatar;
import mbr.com.meubattleroyale.MODEL.GERAL.Mensagem;
import mbr.com.meubattleroyale.MODEL.GERAL.Usuario;
import mbr.com.meubattleroyale.MODEL.INTERFACE.CustomConversa;
import mbr.com.meubattleroyale.R;
import mbr.com.meubattleroyale.VIEW.ACTIVITY.Chat;
import mbr.com.meubattleroyale.VIEW.ADAPTER.AdaptadorConversa;
import mbr.com.meubattleroyale.VIEW.DIALOG.NovaMensagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Conversas extends Fragment
{
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private RecyclerView recChat;
    private ViewPager viewPager;
    private DatabaseHelper db;
    private ArrayList<Usuario> meuUsuario = new ArrayList<>();
    private ArrayList<Amigo> meuUser = new ArrayList<>();
    private ArrayList<Avatar> meuAvatar = new ArrayList<>();
    private ArrayList<Mensagem> listConversa = new ArrayList<>();
    private AdaptadorConversa adapter;
    private FrameLayout frml;
    private String TAG = "CONVERSAS_";
    /******* SOBRE O ITEM TIPO QUE É RECUPERADO DO BANCO *********
     TIPO[0] = SALDO  --> 0.55 CENTS
     TIPO[1] = VERSAO --> PRÓ(TODOS OS AVATARES DESBLOQUEADOS E NENHUM ANUNCIO) OU
     FREE(ANUNCIOS E AVATARES BLOQUEADOS,COM EXCESSÃO DOS CONJUNTOS COMPRADOS)
     TIPO[2] = DIA EFETUOU PRÓ(dias que faltam para versão pró expirar)
     TIPO[3] = CONJUNTO DE AVATARES --> 010 (DIG 1 = PCTE EPICO , DIG 2 = PCTE LENDARIO , DIG 3 = PCTE MITICO)
     TIPO[4] = CONTROLE USUARIO --> 531 (DIG 1 = LIMITE AMIGOS , DIG 2 = LIMITE CONVERSAS , DIG 3 = LIMITE BUSCAS)
     */


    private ValueEventListener novamsgValueListener;
    private TextView txtConversa;
    private String[] tipo;


    public Conversas()
    {
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        recuperarDadosLocais();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        adapter.notifyDataSetChanged();
        Log.d(TAG,"onPause");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        novamsgValueListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                try
                {
                    if (dataSnapshot.getValue().toString() != null)
                    {
                        viewPager.setCurrentItem(1);
                        recuperandoAmigo(dataSnapshot.getValue().toString());
                        //REMOVE VALOR DO BANCO PARA QUE OUTRA NOVA MENSAGEM ENTRE
                        ref.child("novaMensagem").child(meuUsuario.get(0).getId()).removeValue();
                    }

                }catch (NullPointerException e)
                {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.child("novaMensagem").child(meuUsuario.get(0).getId()).addValueEventListener(novamsgValueListener);

    }

    private void recuperandoAmigo(String nick)
    {
        ref.child("usuarios").child(nick).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                try
                {
                    Amigo amigo = dataSnapshot.getValue(Amigo.class);
                    iniciarNovaMensagem(amigo);
                    Log.d(TAG,"DADOS AMIGO RECEBIDO \n Nick: "+amigo.nick);
                }catch (NullPointerException e)
                {
                    Toast.makeText(getContext(),"Usuário não encontrado \n tente novamente!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    // CRIAR CHAMADA DE NOVA MENSAGEM VINDA DA LISTA AMIGOS
    private void iniciarNovaMensagem(Amigo amigo)
    {
        FragmentManager fm = getChildFragmentManager();
        NovaMensagem novaMensagem = NovaMensagem.novaMensagem("Conversar com "+amigo.getNick(),
                amigo.getId(),
                meuUsuario.get(0).getId(),
                meuUsuario.get(0).getNickname(),
                amigo.getNick(),
                meuAvatar.get(0).getAvatar(),
                String.valueOf(amigo.getIcone()));
        novaMensagem.show(fm,"fragment_alert");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_conversa, container, false);
        fazerCast(view);
        recuperarDadosLocais();
        recuperarBanco();

        return view;
    }
    //recupera banco remoto
    private void recuperarBanco()
    {
        //listConversa.clear();
        try
        {
            ref.child("usuarios").child(meuUsuario.get(0).getId()).child("conversas").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        Mensagem new_mensagem = snapshot.getValue(Mensagem.class);
                        Log.d(TAG, "startAt ICONE DATA"+new_mensagem.getRecebido());
                        if (!listConversa.contains(new_mensagem))
                        {
                            Log.d(TAG, "startAt USERNAME: "+new_mensagem.getUsername());
                            //listConversa.add(new_mensagem);
                            txtConversa.setVisibility(View.GONE);
                            db.inserirConversa(new_mensagem);
                            db.atualizarConversa(new_mensagem);
                            Log.d(TAG, "startAt TAM LIST CONVRS: "+listConversa.size());
                            Log.d(TAG, "startAt TAM LIST CONVRS BANCO IF: "+db.getQTDConversas());
                            adapter.notifyDataSetChanged();
                        }
                    }
                    recuperarDadosLocais();
                    if (listConversa.size() == 0)
                    {
                        txtConversa.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (IndexOutOfBoundsException e)
        {
            Toast.makeText(getContext(),"Não entrou na query",Toast.LENGTH_LONG).show();
        }
    }
    private void recuperarDadosLocais()
    {
        db = new DatabaseHelper(getContext());
        meuUsuario.clear();
        meuAvatar.clear();
        meuUser.clear();
        listConversa.clear();
        meuUser.addAll(db.recuperaAmigos());
        tipo = meuUser.get(0).getTipo().split("@@");
        meuUsuario.addAll(db.recuperarUsuarios());
        meuAvatar.addAll(db.recuperarAvatar());
        listConversa.addAll(db.recuperaConversas());
        Log.d("CONVERSAS_","USUARIO: "+meuUsuario.get(0).getNickname());
        try
        {
            Log.d("CONVERSAS_","AVATAR: "+listConversa.get(0).getRecebido());
            Log.d("CONVERSAS_","QTD CONVERSAS: "+listConversa.size());
            Log.d("CONVERSAS_","DADOS CONVERSA 1 : "+listConversa.get(0).getUsername());

        }catch (IndexOutOfBoundsException e)
        {
            Log.d("CONVERSAS_","PRIMEIRO ACESSO");
        }
        recChat.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdaptadorConversa(getContext(), listConversa,meuUsuario.get(0).getId(), new CustomConversa()
        {
            @Override
            public void onItemClick(View itemView, int position, Mensagem conversa)
            {
                String[] icone_tipo = conversa.getRecebido().split(":");
                Intent intent = new Intent(getContext(), Chat.class);
                Bundle bundle = new Bundle();
                Log.d("NM_","IDUSER: "+conversa.getId());
                Log.d("NM_","MEUID: "+meuUsuario.get(0).getId());
                bundle.putString("id_user",conversa.getId());
                bundle.putString("meu_id",meuUsuario.get(0).getId());
                bundle.putString("meu_nick",meuUsuario.get(0).getNickname());
                bundle.putString("nick_amigo",conversa.getUsername());
                bundle.putString("mIcone",meuAvatar.get(0).getAvatar());
                bundle.putString("iconeA",icone_tipo[0]);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        recChat.setAdapter(adapter);

    }

    private void fazerCast(View view) {
        recChat = view.findViewById(R.id.recChat);
        viewPager = (ViewPager) getActivity().findViewById(R.id.vp_painel);
        txtConversa = (TextView) view.findViewById(R.id.txtConversas);

    }

}
