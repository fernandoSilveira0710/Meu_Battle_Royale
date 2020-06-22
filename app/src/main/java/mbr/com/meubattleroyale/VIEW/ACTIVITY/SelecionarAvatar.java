package mbr.com.meubattleroyale.VIEW.ACTIVITY;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;
import mbr.com.meubattleroyale.MODEL.GERAL.Avatar;
import mbr.com.meubattleroyale.R;

import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.widget.Toast.LENGTH_LONG;

public class SelecionarAvatar extends AppCompatActivity
{
    private Button btnLendario,btnEpico,btnMitico;
    private ArrayList<ImageButton> avatares = new ArrayList<>();
    private ImageButton imgAV1T,imgAV2T,imgAV3T,imgAV4T,imgAV5T,imgAV6T,imgAV7T,imgAV8T;
    private ImageButton imgAV9T,imgAV10T,imgAV11T,imgAV12T,imgAV13T,imgAV14T,imgAV15T,imgAV16T;
    private ImageButton imgAV17T,imgAV18T,imgAV19T,imgAV20T,imgAV21T,imgAV22T,imgAV23T,imgAV24T;
    private ImageButton imgAV25T,imgAV26T,imgAV27T,imgAV28T,imgAV29T,imgAV30T,imgAV31T,imgAV32T;
    private int cont = 0;
    private DatabaseHelper db;
    private int id = 1;
    private String TAG = "SELECIONARAVATAR";

    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private String idUser = "";
    private ArrayList<Amigo> meuUser = new ArrayList<>();
    private String[] tipo;
    private Button btnPro;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_avatar);
        db = new DatabaseHelper(getApplicationContext());
        recuperarDb();
        Bundle bundle = getIntent().getExtras();
        idUser = bundle.getString("id_user");
        if (db.getQTDAvatares() > 0)
        {
            id = bundle.getInt("id_avt");
            tipo = bundle.getString("tipo").split("@@");
            Log.d(TAG,"id: "+id+"\n e idUser: "+idUser);
        }


        fazerCast();
        /******* SOBRE O ITEM TIPO QUE É RECUPERADO DO BANCO *********
         TIPO[0] = SALDO  --> 0.55 CENTS
         TIPO[1] = VERSAO --> PRÓ(TODOS OS AVATARES DESBLOQUEADOS E NENHUM ANUNCIO) OU
         FREE(ANUNCIOS E AVATARES BLOQUEADOS,COM EXCESSÃO DOS CONJUNTOS COMPRADOS)
         TIPO[2] = DIA EFETUOU PRÓ(dias que faltam para versão pró expirar)
         TIPO[3] = CONJUNTO DE AVATARES --> 010 (DIG 1 = PCTE EPICO , DIG 2 = PCTE LENDARIO , DIG 3 = PCTE MITICO)
         TIPO[4] = CONTROLE USUARIO --> 531 (DIG 1 = LIMITE AMIGOS , DIG 2 = LIMITE BUSCAS)
         */
        Log.d(TAG, "CONJUNTO: "+tipo[3]);
        String[] pacotes = tipo[3].split("&");
        if (pacotes[0].equals("1"))
        {
            btnEpico.setVisibility(View.GONE);
        }
        if (pacotes[1].equals("1"))
        {
            btnLendario.setVisibility(View.GONE);
        }
        if (pacotes[2].equals("1"))
        {
            btnMitico.setVisibility(View.GONE);
        }
        if (!tipo[1].equals("Free"))
        {
            btnPro.setVisibility(View.GONE);
            btnMitico.setVisibility(View.GONE);
            btnLendario.setVisibility(View.GONE);
            btnEpico.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void fazerCast()
    {
        btnPro = findViewById(R.id.btnPro);
        btnEpico = findViewById(R.id.btnEpicPro);
        btnMitico = findViewById(R.id.btnMiticoPro);
        btnLendario = findViewById(R.id.btnLendarioPro);
        imgAV1T = findViewById(R.id.imgAV1T);
        imgAV2T = findViewById(R.id.imgAV2T);
        imgAV3T = findViewById(R.id.imgAV3T);
        imgAV4T = findViewById(R.id.imgAV4T);
        imgAV5T = findViewById(R.id.imgAV5T);
        imgAV6T = findViewById(R.id.imgAV6T);
        imgAV7T = findViewById(R.id.imgAV7T);
        imgAV8T = findViewById(R.id.imgAV8T);
        imgAV9T = findViewById(R.id.imgAV9T);
        imgAV10T = findViewById(R.id.imgAV10T);
        imgAV11T = findViewById(R.id.imgAV11T);
        imgAV12T = findViewById(R.id.imgAV12T);
        imgAV13T = findViewById(R.id.imgAV13T);
        imgAV14T = findViewById(R.id.imgAV14T);
        imgAV15T = findViewById(R.id.imgAV15T);
        imgAV16T = findViewById(R.id.imgAV16T);
        imgAV17T = findViewById(R.id.imgAV17T);
        imgAV18T = findViewById(R.id.imgAV18T);
        imgAV19T = findViewById(R.id.imgAV19T);
        imgAV20T = findViewById(R.id.imgAV20T);
        imgAV21T = findViewById(R.id.imgAV21T);
        imgAV22T = findViewById(R.id.imgAV22T);
        imgAV23T = findViewById(R.id.imgAV23T);
        imgAV24T = findViewById(R.id.imgAV24T);
        imgAV25T = findViewById(R.id.imgAV25T);
        imgAV26T = findViewById(R.id.imgAV26T);
        imgAV27T = findViewById(R.id.imgAV27T);
        imgAV28T = findViewById(R.id.imgAV28T);
        imgAV29T = findViewById(R.id.imgAV29T);
        imgAV30T = findViewById(R.id.imgAV30T);
        imgAV31T = findViewById(R.id.imgAV31T);
        imgAV32T = findViewById(R.id.imgAV32T);
        // dismiss(); para encerrar dialog
        //
        avatares.add(imgAV1T);
        avatares.add(imgAV2T);
        avatares.add(imgAV3T);
        avatares.add(imgAV4T);
        avatares.add(imgAV5T);
        avatares.add(imgAV6T);
        avatares.add(imgAV7T);
        avatares.add(imgAV8T);

        avatares.add(imgAV9T);
        avatares.add(imgAV10T);
        avatares.add(imgAV11T);
        avatares.add(imgAV12T);
        avatares.add(imgAV13T);
        avatares.add(imgAV14T);
        avatares.add(imgAV15T);
        avatares.add(imgAV16T);

        avatares.add(imgAV17T);
        avatares.add(imgAV18T);
        avatares.add(imgAV19T);
        avatares.add(imgAV20T);
        avatares.add(imgAV21T);
        avatares.add(imgAV22T);
        avatares.add(imgAV23T);
        avatares.add(imgAV24T);

        avatares.add(imgAV25T);
        avatares.add(imgAV26T);
        avatares.add(imgAV27T);
        avatares.add(imgAV28T);
        avatares.add(imgAV29T);
        avatares.add(imgAV30T);
        avatares.add(imgAV31T);
        avatares.add(imgAV32T);

       // btnpto
        btnPro.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
               configurarPagamento(3);
            }
        });


        btnMitico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(SelecionarAvatar.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Pacote Mitico")
                        .setCustomImage(R.drawable.ic_premium)
                        .setContentText("Deseja comprar este pacote?")
                        .setConfirmText("Sim")
                        .setCancelText("Não")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                //ADICIONAR FUNÇÃO DE PAGAMENTO
                                sDialog.dismissWithAnimation();
                                configurarPagamento(0);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
        btnEpico.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new SweetAlertDialog(SelecionarAvatar.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Pacote Epico")
                        .setCustomImage(R.drawable.ic_premium)
                        .setContentText("Deseja comprar este pacote?")
                        .setConfirmText("Sim")
                        .setCancelText("Não")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                //ADICIONAR FUNÇÃO DE PAGAMENTO
                                sDialog.dismissWithAnimation();
                                configurarPagamento(1);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
        btnLendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(SelecionarAvatar.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Pacote Lendário")
                        .setCustomImage(R.drawable.ic_premium)
                        .setContentText("Deseja comprar este pacote?")
                        .setConfirmText("Sim")
                        .setCancelText("Não")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                //ADICIONAR FUNÇÃO DE PAGAMENTO
                                sDialog.dismissWithAnimation();
                                configurarPagamento(2);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });

        for (int i = 0; i < avatares.size(); i++)
        {
            final int x = i;
            avatares.get(i).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // transformar string em drawable
                    final int tag = Integer.parseInt(avatares.get(x).getTag().toString());
                    int drawable = Avatar.identificarAvatar(tag);

                    new SweetAlertDialog(SelecionarAvatar.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("Avatar")
                            .setCustomImage(drawable)
                            .setContentText("Deseja obter este avatar?")
                            .setConfirmText("Sim")
                            .setCancelText("Não")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                            {
                                @Override
                                public void onClick(SweetAlertDialog sDialog)
                                {
                                    if (db.getQTDAvatares() > 0)
                                    {
                                        Log.d(TAG, "Banco atualizado Entrou no if: REPeTICAO: "+x+" \ne \n avatar: "+tag);
                                        recuperarDb();
                                        db.atualizarAvatar(new Avatar(id,String.valueOf(tag),DatabaseHelper.getDateTime()));
                                        db.atualizarAmigo(new Amigo(tag,meuUser.get(0).getNick(),meuUser.get(0).getTipo(),meuUser.get(0).getId(),meuUser.get(0).getAmigos()));
                                        ref.child("usuarios").child(idUser).child("icone").setValue(tag);
                                    }
                                    else
                                    {
                                        recuperarDb();
                                        Log.d(TAG, "Banco atualizado Entrou no else: REPeTICAO: "+x+" \ne \n avatar: "+tag);
                                        db.inserirAvatar(new Avatar(id,String.valueOf(tag),DatabaseHelper.getDateTime()));
                                        ref.child("usuarios").child(idUser).child("icone").setValue(tag);
                                    }

                                    Log.d(TAG, "Banco atualizado: " + db.getQTDAvatares()+" avatares salvos");
                                    finish();
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
            });
        }
    }

    private void configurarPagamento(int n)
    {
        int custoPontos = 0;
        String[] pacotes = tipo[3].split("&");
        String pcte = "";
        Button button = null;
        switch (n)
        {
            case 0:
                pcte = pacotes[0]+"&"+pacotes[1]+"&"+"1";
                custoPontos = 1500;
                button = btnMitico;
                enviarNotificacao(pcte,button,custoPontos,1);
                break;
            case 1:
                custoPontos = 500;
                pcte = "1"+"&"+pacotes[1]+"&"+pacotes[2];
                button = btnEpico;
                enviarNotificacao(pcte,button,custoPontos,1);
                break;
            case 2:
                custoPontos = 1000;
                pcte = pacotes[0]+"&"+"1"+"&"+pacotes[2];
                button = btnLendario;
                enviarNotificacao(pcte,button,custoPontos,1);
                break;
            case 3:
                custoPontos = 2000;
                pcte = pacotes[0]+"&"+pacotes[1]+"&"+pacotes[2];
                button = btnPro;
                enviarNotificacao(pcte,button,custoPontos,0);
                break;
        }

    }
    private  void enviarNotificacao(final String pcte, Button button, final int custoPontos, final int tipoNotificacao)
    {
            final int pontos = Integer.parseInt(tipo[0]);
            final Button finalButton = button;
            Log.d(TAG, "configurarPagamento: pct "+pcte+"\n saldo pontos: "+ pontos);
            new SweetAlertDialog(SelecionarAvatar.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Pagamento")
                    .setCustomImage(R.drawable.ic_pig)
                    .setContentText("Saldo: "+pontos+" pontos disponiveis\n Valor do pacote: ("+custoPontos+" pontos)")
                    .setConfirmText("Saldo")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                    {
                        @Override
                        public void onClick(final SweetAlertDialog sDialog)
                        {
                            if (pontos < custoPontos)
                            {
                                Snackbar.make(getCurrentFocus(),"Seu saldo é inferior ao valor do pacote", Snackbar.LENGTH_LONG).show();
                            }
                            else
                            {
                                String versao = tipo[1];
                                String dataPro = tipo[2];
                                if (tipoNotificacao == 0)
                                {
                                    versao = "Pro";
                                    dataPro = DatabaseHelper.dataFinal(DatabaseHelper.transformarData());
                                }
                                int pontosFinais = pontos - custoPontos;
                                Log.d(TAG, "SALDO FINAL: "+pontosFinais);
                                final String tipoFinal =  pontosFinais+"@@"+
                                        versao+"@@"+
                                        dataPro+"@@"+
                                        pcte;
                                ref.child("usuarios").child(meuUser.get(0).getId()).child("tipo").setValue(tipoFinal).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (tipoNotificacao == 0)
                                        {
                                            Toast.makeText(SelecionarAvatar.this,"Versão Pro comprada com sucesso",LENGTH_LONG).show();
                                            finalButton.setVisibility(View.GONE);
                                        }
                                        else
                                        {
                                            Toast.makeText(SelecionarAvatar.this,"Pacote comprado com sucesso", LENGTH_LONG).show();
                                            finalButton.setVisibility(View.GONE);
                                        }
                                        Log.d(TAG, "onComplete: DB.ATUALIZADO");
                                        db.atualizarAmigo(new Amigo(meuUser.get(0).getIcone(),meuUser.get(0).getNick(),tipoFinal,meuUser.get(0).getId(),meuUser.get(0).getAmigos()));
                                        sDialog.dismissWithAnimation();
                                    }
                                });
                            }
                        }
                    })
                    .show();
    }

    private void recuperarDb()
    {
        meuUser.clear();
        meuUser.addAll(db.recuperaAmigos());
    }
}
