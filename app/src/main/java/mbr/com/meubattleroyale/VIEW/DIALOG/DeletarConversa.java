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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DatabaseReference;

import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Mensagem;
import mbr.com.meubattleroyale.R;
import mbr.com.meubattleroyale.VIEW.ACTIVITY.Chat;


public class DeletarConversa extends DialogFragment
{
    private Button btnDeletar,btnCancelar;
    private TextView txtTitulo;
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private DatabaseHelper db;
    private String TAG = "Deletar_";

    public DeletarConversa()
    {

    }

    public static DeletarConversa deletar( String id, String data, String recebido, String username,String mensagem,String meuId)
    {
        DeletarConversa frag = new DeletarConversa();
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("data",data);
        bundle.putString("recebido",recebido);
        bundle.putString("username",username);
        bundle.putString("mensagem",mensagem);
        bundle.putString("meuid",meuId);
        frag.setArguments(bundle);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.dialog_deletar_mensagem, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        btnCancelar = (Button)view.findViewById(R.id.btnCancelar);
        btnDeletar = (Button)view.findViewById(R.id.btnOk);
        txtTitulo = (TextView)view.findViewById(R.id.txtTitulo_NM);
        db = new DatabaseHelper(getContext());
        final String meuid = getArguments().getString("meuid");
        final String id = getArguments().getString("id");
        final String data = getArguments().getString("data");
        final String recebido = getArguments().getString("recebido");
        final String username = getArguments().getString("username");
        final String msg = getArguments().getString("mensagem");
        final Mensagem mensagem = new Mensagem(id,msg,data,recebido,username);
        txtTitulo.setText("Deseja deletar a conversa com "+username);

        getDialog().setTitle("Nova Mensagem");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        btnDeletar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: Banco msg antes "+db.getQTDConversas());
                db.deletarConversa(mensagem,"");
                ref.child("usuarios").child(meuid).child("conversas").child(id).removeValue();
                Log.d(TAG, "onClick: Banco msg depois "+db.getQTDConversas());
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
