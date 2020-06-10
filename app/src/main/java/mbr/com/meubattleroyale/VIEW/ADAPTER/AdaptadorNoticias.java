package mbr.com.meubattleroyale.VIEW.ADAPTER;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Noticia;
import mbr.com.meubattleroyale.MODEL.INTERFACE.CustomNoticia;
import mbr.com.meubattleroyale.R;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptadorNoticias extends RecyclerView.Adapter<AdaptadorNoticias.ViewHolder>
{

    private ArrayList<Noticia> listNoticia;
    private Context mContext;
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private CustomNoticia customNoticia;
    private long DURATION = 500;
    private boolean on_atach = true,shareclick = false;

    private DatabaseHelper db;
    private ArrayList<Noticia> listNoticiasLocal = new ArrayList<>();

    public AdaptadorNoticias(Context context, ArrayList<Noticia> list, CustomNoticia customNoticia)
    {
        this.listNoticia = list;
        this.mContext = context;
        this.customNoticia= customNoticia;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adaptador_noticias, viewGroup, false);
        final AdaptadorNoticias.ViewHolder mViewHolder = new AdaptadorNoticias.ViewHolder(itemView);
        return mViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {

        // Get the data model based on position
        final Noticia noticia = listNoticia.get(position);
        //formatar horas
        Log.d("ADPTN","DADOS NOTICIA: "+ noticia.getTitulo());
        db = new DatabaseHelper(mContext);
        if (db.getQTDNoticias() >= 1)
        {
            listNoticiasLocal.addAll(db.recuperaNoticias());
        }
        for (int i = 0; i < listNoticiasLocal.size(); i++)
        {
            try
            {
                if (listNoticiasLocal.get(i).getId().equals(noticia.getId()))
                {
                    viewHolder.like.setImageResource(R.drawable.ic_like_checked);
                    shareclick = true;
                }
            }catch (NullPointerException  e)
            {

            }
        }
        viewHolder.data.setText(noticia.getData());
        viewHolder.numViews.setText(Noticia.transformNum(noticia.getLikes()));
        viewHolder.titulo.setText(noticia.getTitulo());
        Picasso.get().load(noticia.getImage()).into(viewHolder.imageView);
        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customNoticia.onShareClick(viewHolder.share,viewHolder.getAdapterPosition(),noticia);
            }
        });
        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customNoticia.onLikeClick(viewHolder.like,viewHolder.numViews,viewHolder.getAdapterPosition(),noticia,shareclick);
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("ADPTC","Enviando pelo onBindViewHolder");
                customNoticia.onClick(viewHolder.itemView, viewHolder.getAdapterPosition(),noticia);
            }
        });
        iniciarAnimacao(viewHolder.itemView,position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                on_atach = false;
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void iniciarAnimacao(View itemView, int position)
    {
        if (!on_atach)
        {
            position = -1;
        }
        boolean naoEstaNoPrimeiroItem = position == -1;
        position++;
        itemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(itemView,"alpha",0.f,0.5f,1.0f);
        animator.setStartDelay(naoEstaNoPrimeiroItem ? DURATION / 2 : (position * DURATION / 3));
        animator.setDuration(500);
        animatorSet.play(animator);
        animator.start();
    }

    @Override
    public int getItemCount() {
        return listNoticia.size();
    }

    // Easy access to the context object in the recyclerview
    private Context getContext()
    {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageButton like,share;
        View parentLayout;
        TextView titulo,data,numViews;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgCapaNoticias);
            titulo= (TextView)itemView.findViewById(R.id.txtTitulo);
            data = (TextView)itemView.findViewById(R.id.txtData);
            numViews = (TextView) itemView.findViewById(R.id.txtViews);
            like = (ImageButton) itemView.findViewById(R.id.imgLike);
            share = (ImageButton) itemView.findViewById(R.id.imgShare);


            parentLayout = itemView.findViewById(R.id.frml);
        }
    }
}
