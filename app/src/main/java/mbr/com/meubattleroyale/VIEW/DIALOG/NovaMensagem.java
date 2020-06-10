package mbr.com.meubattleroyale.VIEW.DIALOG;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import mbr.com.meubattleroyale.R;
import mbr.com.meubattleroyale.VIEW.ACTIVITY.Chat;


public class NovaMensagem extends DialogFragment
{
    private Button btnEnviar,btnCancelar;
    private TextView txtTitulo;

    public NovaMensagem()
    {

    }

    public static  NovaMensagem novaMensagem(String texto,String idAmigo,String meuId,String meuNick,String nickAmigo,String meuIcone,String iconeAmigo)
    {
        NovaMensagem frag = new NovaMensagem();
        Bundle bundle = new Bundle();
        bundle.putString("titulo",texto);
        bundle.putString("idUser",idAmigo);
        bundle.putString("meuId",meuId);
        bundle.putString("nickAmigo",nickAmigo);
        bundle.putString("meuNick",meuNick);
        bundle.putString("mIcone",meuIcone);
        bundle.putString("iconeA",iconeAmigo);

        frag.setArguments(bundle);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.dialog_nova_mensagem, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        btnCancelar = (Button)view.findViewById(R.id.btnCancelar);
        btnEnviar = (Button)view.findViewById(R.id.btnOk);
        txtTitulo = (TextView)view.findViewById(R.id.txtTitulo_NM);

        final String titulo = getArguments().getString("titulo");
        final String id = getArguments().getString("idUser");
        final String meuId = getArguments().getString("meuId");
        final String meuNick = getArguments().getString("meuNick");
        final String nickAmigo = getArguments().getString("nickAmigo");
        final String mIcone = getArguments().getString("mIcone");
        final String iconeA = getArguments().getString("iconeA");

        Log.d("NM_","TT: "+titulo+" \n ID: "+id+" \n MID: "+meuId);
        txtTitulo.setText(titulo);

        getDialog().setTitle("Nova Mensagem");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        btnEnviar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), Chat.class);
                Bundle bundle = new Bundle();
                Log.d("NM_","IDUSER: "+id);
                Log.d("NM_","MEUID: "+meuId);
                bundle.putString("id_user",id);
                bundle.putString("meu_id",meuId);
                bundle.putString("meu_nick",meuNick);
                bundle.putString("nick_amigo",nickAmigo);
                bundle.putString("mIcone",mIcone);
                bundle.putString("iconeA",iconeA);
                intent.putExtras(bundle);
                startActivity(intent);
                dismiss();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });
    }
}
