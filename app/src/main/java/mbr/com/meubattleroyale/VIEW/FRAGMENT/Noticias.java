package mbr.com.meubattleroyale.VIEW.FRAGMENT;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Noticia;
import mbr.com.meubattleroyale.MODEL.INTERFACE.CustomNoticia;
import mbr.com.meubattleroyale.R;
import mbr.com.meubattleroyale.VIEW.ACTIVITY.ConteudoNoticia;
import mbr.com.meubattleroyale.VIEW.ADAPTER.AdaptadorNoticias;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Noticias extends Fragment
{
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private ProgressBar progress;
    private RecyclerView recNoticias;
    private ArrayList<Noticia> listNoticias = new ArrayList<>();
    private AdaptadorNoticias adapter;
    private String TAG = "NOTICIAS_";
    private ValueEventListener noticiasValueListener;
    private boolean resultado = false;
    private TextView txt;
    int cont = 0;
    private DatabaseHelper db;
    private ArrayList<Noticia> listNoticiasLocal;
    private boolean shareclick = false;

    public Noticias()
    {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_noticias, container, false);
        fazerCast(view);
        return view;
    }

    private void fazerCast(View view)
    {
        txt = view.findViewById(R.id.txtAviso);
        recNoticias = view.findViewById(R.id.recNoticias);
        progress = view.findViewById(R.id.progress);
        db = new DatabaseHelper(getContext());

    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG,"onStart");
        listNoticias.clear();
        recuperarDadosLocais();
    }

    private void recuperarDadosLocais()
    {
        recNoticias.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdaptadorNoticias(getContext(), listNoticias, new CustomNoticia()
        {
            @Override
            public void onShareClick(ImageButton button, int position, Noticia noticia)
            {

            }

            @Override
            public void onLikeClick(ImageButton button, TextView textView,int position, Noticia noticia,boolean share)
            {
                if (cont == 0)
                {
                    shareclick = share;
                }
                if (shareclick == false)
                {
                    button.setImageResource(R.drawable.ic_like_checked);
                    ref.child("noticias").child(noticia.getId()).child("likes").setValue(noticia.getLikes() +1);
                    textView.setText(Noticia.transformNum(noticia.getLikes() +1));
                    Log.d("Noticias_","Inserindo noticia antes" + db.getQTDNoticias());
                    db.inserirNoticia(noticia);
                    Log.d("Noticias_","Inserindo noticia depois" + db.getQTDNoticias());
                    shareclick = true;
                }
                else
                {
                    button.setImageResource(R.drawable.ic_like);
                    ref.child("noticias").child(noticia.getId()).child("likes").setValue(noticia.getLikes());
                    textView.setText(Noticia.transformNum(noticia.getLikes()));
                    Log.d("Noticias_","Deletando noticia antes" + db.getQTDNoticias());
                    db.deletarNoticia(noticia,"");
                    Log.d("Noticias_","Deletando noticia depois" + db.getQTDNoticias());
                    shareclick = false;
                }
                cont ++;
            }

            @Override
            public void onClick(View button, int position, Noticia noticia)
            {
                //t.makeText(getContext(),"Clicou em View",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), ConteudoNoticia.class);
                Bundle bundle = new Bundle();
                bundle.putString("data",noticia.getData());
                bundle.putString("ementa",noticia.getEmenta());
                bundle.putString("image",noticia.getImage());
                bundle.putString("titulo",noticia.getTitulo());
                bundle.putDouble("likes",noticia.getLikes());
                bundle.putString("id",noticia.getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        recNoticias.setAdapter(adapter);
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
        noticiasValueListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                listNoticias.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    progress.setVisibility(View.VISIBLE);
                    txt.setVisibility(View.GONE);
                    Noticia noticia = snapshot.getValue(Noticia.class);
                    if (!listNoticias.contains(noticia))
                    {
                        resultado = true;
                        listNoticias.add(noticia);
                    }
                }
                if (resultado == false)
                {
                    txt.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                progress.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        };
        ref.child("noticias").addListenerForSingleValueEvent(noticiasValueListener);

    }


}
