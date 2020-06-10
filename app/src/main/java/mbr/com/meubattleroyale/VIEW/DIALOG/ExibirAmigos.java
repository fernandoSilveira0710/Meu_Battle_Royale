package mbr.com.meubattleroyale.VIEW.DIALOG;


import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;
import mbr.com.meubattleroyale.R;

import java.util.ArrayList;


public class ExibirAmigos extends DialogFragment
{
    private RecyclerView recAmigos;
    private Typeface fortniteFont;

    public ExibirAmigos()
    {

    }

    public static ExibirAmigos exibirAmigos(ArrayList<Amigo> amigos)
    {
        ExibirAmigos frag = new ExibirAmigos();
        Bundle bundle = new Bundle();
       // bundle.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) amigos);


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
        recAmigos = (RecyclerView) view.findViewById(R.id.recAmigos);



        ArrayList<? extends Amigo> amigos = getArguments().getParcelableArrayList("list");

        Log.d("EA_","AMIGOS SIZE: "+amigos.size());


        getDialog().setTitle("EXIBIR AMIGOS");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        /*
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

         */
    }
}
