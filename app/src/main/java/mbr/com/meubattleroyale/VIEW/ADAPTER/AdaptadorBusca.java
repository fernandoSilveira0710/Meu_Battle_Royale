package mbr.com.meubattleroyale.VIEW.ADAPTER;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;
import mbr.com.meubattleroyale.MODEL.GERAL.Avatar;
import mbr.com.meubattleroyale.MODEL.INTERFACE.CustomBusca;
import mbr.com.meubattleroyale.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorBusca extends RecyclerView.Adapter<AdaptadorBusca.ViewHolder>
{

    private final String seuId;
    private ArrayList<Amigo> listUsuarios;
    private Context mContext;
    private long DURATION = 500;
    private boolean on_atach = true;
    private CustomBusca customBusca;


    public AdaptadorBusca(Context context, ArrayList<Amigo> usuarios, String seuId, CustomBusca customBusca)
    {
        this.seuId = seuId;
        this.listUsuarios = usuarios;
        this.mContext = context;
        this.customBusca = customBusca;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adaptador_busca, viewGroup, false);
        final AdaptadorBusca.ViewHolder mViewHolder = new AdaptadorBusca.ViewHolder(itemView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {

        // Get the data model based on position
        final Amigo usuario = listUsuarios.get(position);
        if (usuario.getId().equals(seuId))
        {
            viewHolder.nick.setText("Voce");
            Log.d("ADPTBUSCA","Voce passou por aqui");
            viewHolder.parentLayout.setBackgroundResource(R.drawable.bordas_3logo);
            viewHolder.imageView.setImageResource(Avatar.identificarAvatar(usuario.getIcone()));
            viewHolder.btnMensagem.setVisibility(View.INVISIBLE);
            viewHolder.btnCopiar.setVisibility(View.INVISIBLE);
            viewHolder.txt.setVisibility(View.INVISIBLE);
        }
        else
        {
            viewHolder.btnMensagem.setVisibility(View.VISIBLE);
            viewHolder.btnCopiar.setVisibility(View.VISIBLE);
            viewHolder.txt.setVisibility(View.VISIBLE);
            viewHolder.nick.setText(usuario.getNick());
            viewHolder.parentLayout.setBackgroundResource(R.drawable.bordas_1logo);
            viewHolder.imageView.setImageResource(Avatar.identificarAvatar(usuario.getIcone()));
            viewHolder.btnCopiar.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    customBusca.onCopiar(viewHolder.btnCopiar,viewHolder.getAdapterPosition(),usuario);
                }
            });
            viewHolder.btnMensagem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    customBusca.onMessagemClick(viewHolder.btnMensagem,viewHolder.getAdapterPosition(),usuario);
                }
            });
        }
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
        return listUsuarios.size();
    }

    // Easy access to the context object in the recyclerview
    private Context getContext()
    {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imageView;
        View parentLayout;
        TextView nick,txt;
        ImageButton btnCopiar,btnMensagem;
        SparseBooleanArray array=new SparseBooleanArray();


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            btnMensagem = (ImageButton) itemView.findViewById(R.id.btnMensagem);
            imageView = itemView.findViewById(R.id.avatar_adpt_amigo);
            nick= (TextView)itemView.findViewById(R.id.txtNick_adpt_Amigos);
            txt= (TextView)itemView.findViewById(R.id.txt);
            btnCopiar = (ImageButton)itemView.findViewById(R.id.btnCopiar);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }
}
