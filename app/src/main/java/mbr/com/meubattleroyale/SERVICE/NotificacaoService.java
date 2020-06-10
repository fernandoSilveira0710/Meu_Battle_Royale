package mbr.com.meubattleroyale.SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Avatar;
import mbr.com.meubattleroyale.MODEL.GERAL.Mensagem;
import mbr.com.meubattleroyale.MODEL.GERAL.Noticia;
import mbr.com.meubattleroyale.MODEL.GERAL.Notificacao;
import mbr.com.meubattleroyale.MODEL.GERAL.Usuario;
import mbr.com.meubattleroyale.R;
import mbr.com.meubattleroyale.VIEW.ACTIVITY.Chat;
import mbr.com.meubattleroyale.VIEW.ACTIVITY.ConteudoNoticia;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotificacaoService extends Service

{
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private ChildEventListener mensagemListener,noticiaListener;
    private ValueEventListener notificacaoListener;
    private String TAG = "SERVICO_";
    private DatabaseHelper db;
    private ArrayList<Usuario> meuUsuario = new ArrayList<>();
    private ArrayList<Avatar> meuAvatar = new ArrayList<>();
    private int NOTIFICATION_ID;
    private String CHANEL_NOTICIAS = "1",CHANEL_CHAT = "2",CHANEL_NOTIFICACAO = "3";
    private ArrayList<Notificacao> minhasNotificacoes = new ArrayList<>();
    private static boolean isServiceRunning = false;

    private int cont = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "Iniciando Service");
        if (intent != null)
        {
            escutarBancos();
        }
        else stopMyService();
        return START_STICKY;
    }

    private void stopMyService()
    {
        ref.child("noticias").removeEventListener(noticiaListener);
        ref.child("usuarios").child(meuUsuario.get(0).getId()).child("conversas").removeEventListener(mensagemListener);
        ref.child("alerta").child(meuUsuario.get(0).getId()).removeEventListener(notificacaoListener);
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }

    @Override
    public void onCreate()
    {
        Log.d(TAG, "onCreate");
        escutarBancos();
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved: ");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopMyService();
        Log.d(TAG, "Destruindo service");
    }
    private void escutarBancos()
    {
        if (isServiceRunning) return;
        isServiceRunning = true;

        db = new DatabaseHelper(getApplicationContext());
        minhasNotificacoes.clear();
        meuUsuario.clear();
        meuAvatar.clear();
        minhasNotificacoes.addAll(db.recuperaNotificacao());
        meuUsuario.addAll(db.recuperarUsuarios());
        meuAvatar.addAll(db.recuperarAvatar());
        //escutar alterações em mensagens
        escutarNovasMensagens();
        //escutar e notificar novas noticias
        escutarNovasNoticias();
        //escutar e notificar novos alertas
        escutarNovosAlertas();
    }


    private void escutarNovosAlertas()
    {
        notificacaoListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Notificacao notificacao = dataSnapshot.getValue(Notificacao.class);
                enviarNotificacao(notificacao,null,3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.child("alerta").child(meuUsuario.get(0).getId()).addValueEventListener(notificacaoListener);
    }

    private void escutarNovasNoticias()
    {
       noticiaListener = new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Noticia noticia = dataSnapshot.getValue(Noticia.class);
                Log.d("service_","nova noticia : "+ noticia.getTitulo());
                verificarNotificacao(noticia.getId(),noticia.getData(),noticia.getTitulo(),2,noticia);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.child("noticias").addChildEventListener(noticiaListener);
    }

    private void escutarNovasMensagens()
    {
        mensagemListener= new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Mensagem new_mensagem = dataSnapshot.getValue(Mensagem.class);
                Log.d("service_","nova mensagem endAt : "+ new_mensagem.getMessagem());
                String[] icone_tipo = new_mensagem.getRecebido().split(":");
                if (icone_tipo[1].equals("0"))
                {
                    enviarNotificacaoMsg(new_mensagem,icone_tipo[0]);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Mensagem new_mensagem = dataSnapshot.getValue(Mensagem.class);
                Log.d("service_","nova mensagem alterada endAt : "+ new_mensagem.getMessagem());
                String[] icone_tipo = new_mensagem.getRecebido().split(":");
                if (icone_tipo[1].equals("0"))
                {
                    enviarNotificacaoMsg(new_mensagem,icone_tipo[0]);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        };
        ref.child("usuarios").child(meuUsuario.get(0).getId()).child("conversas").addChildEventListener(mensagemListener);
    }

    private void verificarNotificacao(String id,String data,String recebido,int tipo,Noticia noticia)
    {
        /******************************** tipos:**************
         * tipo == 2 é noticia adicionada
         * tipo == 3 é notificacao adicionada
          */
        boolean res = false;
        for (int i = 0; i < minhasNotificacoes.size(); i++)
        {
            Log.d(TAG, "IDLISTA: "+minhasNotificacoes.get(i).getId()+" e IDMENSAGEM: "+id);
            if (minhasNotificacoes.get(i).getId().equals(id))
            {
                if (minhasNotificacoes.get(i).getData().equals(data))
                {
                    res = true;
                    Log.d(TAG, "Esse id já foi notificado: "+ data);
                    break;
                }
                else
                {
                    switch (tipo)
                    {
                        case 2:
                            try
                            {
                                db.atualizarNotificacao(new Notificacao(noticia.getTitulo(),noticia.getId(),noticia.getData()));
                                Log.d(TAG, "Banco Atualizado Notificacao : "+db.getQTDNotificacao());
                                cont ++;
                                enviarNotificacao(null,noticia,1);
                            }catch (NullPointerException e)
                            {
                                Log.d(TAG, "Entrou no catch,atualizando mensagem... ");
                            }
                            break;
                    }
                }
                Log.d(TAG, "esse id já foi notificado --> "+ data);
            }
        }
        if (res == false)
        {
            switch (tipo)
            {
                case 2:
                    Log.d(TAG, "Banco Notificacao Antes: "+db.getQTDNotificacao());
                    cont ++;
                    enviarNotificacao(null,noticia,1);
                    db.inserirNotificacao(new Notificacao(noticia.getTitulo(),noticia.getId(),noticia.getData()));
                    Log.d(TAG, "Banco Notificacao Depois: "+db.getQTDNotificacao());
                    break;
            }
        }
    }

    // enviar uma notificacao de mensagem
    private void enviarNotificacaoMsg(Mensagem mensagem, String icone)
    {
        try
        {
            int NOTIFICACAO_ID = cont +1;
            Log.i(TAG, "enviarNotificação Mensagem: " + mensagem.getId());
// Intenção de iniciar a atividade principal
            Intent chat = new Intent(getApplicationContext(), Chat.class);
            Bundle dados = new Bundle();
            dados.putString("meu_id", meuUsuario.get(0).getId());
            dados.putString("id_user", mensagem.getId());
            dados.putString("meu_nick",meuUsuario.get(0).getNickname());
            dados.putString("nick_amigo", mensagem.getUsername());
            dados.putString("iconeA",icone);
            dados.putString("mIcone", meuAvatar.get(0).getAvatar());
            chat.putExtras(dados);
            //chat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//CRIANDO INTENÇÃO
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,chat,0);
// CRIANDO NOTIFICAÇÃO
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANEL_CHAT)
                    .setSmallIcon(R.mipmap.ic_logo)
                    .setAutoCancel(true)
                    .setContentTitle(mensagem.getUsername())
                    .setContentText(mensagem.getMessagem())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);
// Criando e enviando notificação
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            int random = (int) System.currentTimeMillis();

//CRIANDO CANAL DE NOTIFICAÇÃO
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel canalChat = new NotificationChannel(CHANEL_CHAT,"Chat",NotificationManager.IMPORTANCE_HIGH);
                canalChat.setDescription("Noticias do seu game");
                canalChat.setLightColor(R.color.logo1);
                notificationManager.createNotificationChannel(canalChat);
                builder.setChannelId(CHANEL_CHAT);
            }
            notificationManager.notify(NOTIFICACAO_ID, builder.build());
            ref.child("usuarios").child(meuUsuario.get(0).getId()).child("conversas").child(mensagem.getId()).child("recebido").setValue(icone+":1");
        }catch (NullPointerException e)
        {

        }
    }

    // Enviar uma notificação
    private void enviarNotificacao(Notificacao notificacao,Noticia noticia,int tipo)
    {
        switch (tipo)
        {
            case 1:
                try
                {
                    int NOTIFICACAO_ID = cont +1;
                    Log.i(TAG, "enviarNotificação Noticia: " + noticia.getId());
// Intenção de iniciar a atividade principal
                    Intent conteudoNoticia = new Intent(getApplicationContext(), ConteudoNoticia.class);
                    Bundle dadosNoticia = new Bundle();
                    dadosNoticia.putString("data", noticia.getData());
                    dadosNoticia.putString("ementa",noticia.getEmenta() );
                    dadosNoticia.putString("image",noticia.getImage());
                    dadosNoticia.putString("titulo",noticia.getTitulo());
                    dadosNoticia.putDouble("likes", noticia.getLikes());
                    dadosNoticia.putString("id", noticia.getId());
                    conteudoNoticia.putExtras(dadosNoticia);
                    //chat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//CRIANDO INTENÇÃO
                    PendingIntent intencaoPendente = PendingIntent.getActivity(getApplicationContext(),0,conteudoNoticia,0);
// CRIANDO NOTIFICAÇÃO
                    NotificationCompat.Builder builderNoticia = new NotificationCompat.Builder(this,CHANEL_CHAT)
                            .setContentTitle(noticia.getTitulo())
                            .setSmallIcon(R.mipmap.ic_logo)
                            .setAutoCancel(true)
                            .setContentText(noticia.getEmenta())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(intencaoPendente);
// Criando e enviando notificação
                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());

//CRIANDO CANAL DE NOTIFICAÇÃO
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        NotificationChannel canalNoticias = new NotificationChannel(CHANEL_NOTICIAS,"Noticias",NotificationManager.IMPORTANCE_DEFAULT);
                        canalNoticias.setDescription("Receba notificações das novas conversas");
                        canalNoticias.setLightColor(R.color.logo2);
                        managerCompat.createNotificationChannel(canalNoticias);
                        builderNoticia.setChannelId(CHANEL_NOTICIAS);
                    }
                    managerCompat.notify(NOTIFICACAO_ID, builderNoticia.build());
                }catch (NullPointerException e)
                {

                }
                break;

            case 3:
                try
                {
                    String NOTIFICACAO_ID = String.valueOf(cont +1);
                    Log.d(TAG, "CASE3: cont "+NOTIFICACAO_ID);
                    Log.d(TAG, "CASE3: notificacao "+notificacao.getRecebido());
                    //personalizar os botões de ok
                    String textos[] = notificacao.getRecebido().split("###");
                    Log.d(TAG, "enviarNotificacao: size texto "+textos.length
                            +"\n TEXTO[0] "+textos[0]
                            +"\n TEXTO[1] "+textos[1]
                            +"\n TEXTO[2] "+textos[2]
                            +"\n TEXTO[3] "+textos[3]
                            +"\n TEXTO[4] "+textos[4]);
                    //tipo+":"+idA+":"+mId+":"+icone+":"+nick+":"id

                    // tipo 0 é o usúario enviando alerta
                    //tipo 1 é o usuario respondendo a alerta
                    if (textos[0].equals("0"))
                    {
                        //criando intencao do botão Não
                        Intent brodcastN = new Intent(getApplicationContext(),NotificacaoReceiver.class);
                        Bundle bundleN = new Bundle();
                        bundleN.putString("idA",textos[1]);
                        bundleN.putString("mId",textos[2]);
                        bundleN.putString("icone",textos[3]);
                        bundleN.putString("nick",textos[4]);
                        bundleN.putString("notificacao_id",NOTIFICACAO_ID);
                        brodcastN.putExtras(bundleN);
                        brodcastN.setAction("NAO");
                        //criando intenção do botão Bora
                        Intent brodcastB = new Intent(getApplicationContext(),NotificacaoReceiver.class);
                        Bundle bundle = new Bundle();
                        //brodcastB.putExtra("texto",notificacao.getRecebido());
                        bundle.putString("idA",textos[1]);
                        bundle.putString("mId",textos[2]);
                        bundle.putString("icone",textos[3]);
                        bundle.putString("nick",textos[4]);
                        bundle.putString("notificacao_id",NOTIFICACAO_ID);
                        brodcastB.putExtras(bundle);
                        brodcastB.setAction("BORA");
                        brodcastB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //sendBroadcast(brodcastB);
                        PendingIntent pendingN = PendingIntent.getBroadcast(this,0,brodcastN,PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent pendingB = PendingIntent.getBroadcast(this,0,brodcastB,PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder compat = new NotificationCompat.Builder(this,CHANEL_NOTIFICACAO)
                                .setSmallIcon(R.mipmap.ic_logo)
                                .setContentTitle("NOVO ALERTA RECEBIDO")
                                .setContentText("SEU AMIGO "+textos[4]+" ESTA TE CHAMANDO...")
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setAutoCancel(true)
                                .addAction(0,"Bora",pendingB)
                                .addAction(0,"agora não",pendingN);

                        // Criando e enviando notificação
                        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

//CRIANDO CANAL DE NOTIFICAÇÃO
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        {
                            NotificationChannel canalNotificacao = new NotificationChannel(CHANEL_NOTIFICACAO,"Notificacao",NotificationManager.IMPORTANCE_HIGH);
                            canalNotificacao.setDescription("Receba notificações dos alertas dos amigos");
                            canalNotificacao.setLightColor(R.color.logo2);
                            manager.createNotificationChannel(canalNotificacao);
                            compat.setChannelId(CHANEL_NOTICIAS);
                        }
                        manager.notify(Integer.parseInt(NOTIFICACAO_ID), compat.build());
                        //startForeground(NOTIFICATION_ID,compat.build());

                    }
                    else
                    {
                        NotificationCompat.Builder compat = new NotificationCompat.Builder(this,CHANEL_NOTIFICACAO)
                                .setSmallIcon(R.mipmap.ic_logo)
                                .setContentTitle("RESPOSTA DE ALERTA RECEBIDA")
                                .setContentText(textos[4]+" NÃO PODE JOGAR NO MOMENTO!")
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setAutoCancel(true);

                        // Criando e enviando notificação
                        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

//CRIANDO CANAL DE NOTIFICAÇÃO
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        {
                            NotificationChannel canalNotificacao = new NotificationChannel(CHANEL_NOTIFICACAO,"Notificacao",NotificationManager.IMPORTANCE_HIGH);
                            canalNotificacao.setDescription("Receba notificações dos alertas dos amigos");
                            canalNotificacao.setLightColor(R.color.logo2);
                            manager.createNotificationChannel(canalNotificacao);
                            compat.setChannelId(CHANEL_NOTICIAS);
                        }
                        manager.notify(Integer.parseInt(NOTIFICACAO_ID), compat.build());
                        ref.child("alerta").child(textos[2]).removeValue();
                    }
                }catch (NullPointerException e)
                {

                }
                break;
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
