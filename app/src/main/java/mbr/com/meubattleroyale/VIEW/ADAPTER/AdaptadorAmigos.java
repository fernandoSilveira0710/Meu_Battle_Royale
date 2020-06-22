package mbr.com.meubattleroyale.VIEW.ADAPTER;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;
import mbr.com.meubattleroyale.MODEL.GERAL.Avatar;
import mbr.com.meubattleroyale.MODEL.INTERFACE.CustomClick;
import mbr.com.meubattleroyale.MODEL.INTERFACE.CustomMsgeNtfc;
import mbr.com.meubattleroyale.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorAmigos extends RecyclerView.Adapter<AdaptadorAmigos.ViewHolder>
{

    private final String seuId;
    private ArrayList<Amigo> listUsuarios;
    private Context mContext;
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    // Define listener member variable
    private CustomClick customBuscaClickList;
    private CustomMsgeNtfc customMsgeNtfc;
    private int tipo;
    private Amigo meuUsuario;
    private long DURATION = 500;
    private boolean on_atach = true;
    private String TAG = "ADPTBUSCA";

    public AdaptadorAmigos(Context context, ArrayList<Amigo> usuarios,String seuId, int tipo , CustomClick customBuscaClickList, CustomMsgeNtfc customMsgeNtfc)
    {
        this.tipo = tipo;
        this.seuId = seuId;
        this.listUsuarios = usuarios;
        this.mContext = context;
        this.customBuscaClickList = customBuscaClickList;
        this.customMsgeNtfc = customMsgeNtfc;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adaptador_amigo, viewGroup, false);
        final AdaptadorAmigos.ViewHolder mViewHolder = new AdaptadorAmigos.ViewHolder(itemView);
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
            Log.d(TAG,"Voce passou por aqui");
            viewHolder.btnNotificacao.setVisibility(View.INVISIBLE);
            viewHolder.btnMensagem.setVisibility(View.INVISIBLE);
            viewHolder.btnSeguir.setVisibility(View.INVISIBLE);
            viewHolder.parentLayout.setBackgroundResource(R.drawable.bordas_3logo);
        }
        else
        {
            viewHolder.nick.setText(usuario.getNick());
            viewHolder.parentLayout.setBackgroundResource(R.drawable.bordas_1logo);
            viewHolder.btnSeguir.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.d(TAG,"Enviando pelo onBindViewHolder");
                    customBuscaClickList.onItemClick(viewHolder.itemView, viewHolder.getAdapterPosition(),viewHolder.btnSeguir,meuUsuario,usuario);
                }
            });
            viewHolder.btnMensagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customMsgeNtfc.onMessagemClick(viewHolder.btnMensagem,viewHolder.getAdapterPosition(),usuario);
                }
            });
            viewHolder.btnNotificacao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customMsgeNtfc.onNotificacaoClick(viewHolder.btnNotificacao,viewHolder.getAdapterPosition(),usuario);
                }
            });
        }
        recuperoMeuNick(viewHolder,usuario);
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

    private void recuperoMeuNick(final AdaptadorAmigos.ViewHolder viewHolder, final Amigo usuario)
    {

        ref.child("usuarios").child(seuId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                meuUsuario = dataSnapshot.getValue(Amigo.class);
                Log.d(TAG, "onDataChange: ICONE"+usuario.getIcone());
                viewHolder.imageView.setImageResource(Avatar.identificarAvatar(usuario.getIcone()));
                //viewHolder.imageView.setImageResource(R.drawable.ic_add_avatar);
                viewHolder.imageView.setVisibility(View.VISIBLE);
                if (meuUsuario.getAmigos() != null)
                {
                    if (meuUsuario.getAmigos().contains(usuario.getId()))
                    {
                        Log.d(TAG,"meuUsuario:" +meuUsuario.getAmigos().size());
                        Log.d(TAG,"IF meuUsuario.getAmigos().contains(usuario.getNick())");
                        Drawable imgClicked  = mContext.getResources().getDrawable(R.drawable.ic_amigos_check);
                        Log.d(TAG, "meuUsuario.getAmigos().contains != null IF  | Nick usuario " + usuario.getNick() + " E Nick meuUser: "+ meuUsuario.getNick());
                        viewHolder.btnSeguir.setCompoundDrawablesWithIntrinsicBounds(null,null,null,imgClicked);
                        viewHolder.btnSeguir.setText("Seguindo");
                    }
                    else
                    {
                        Drawable imgClicked  = mContext.getResources().getDrawable(R.drawable.ic_amigos);
                        viewHolder.btnSeguir.setCompoundDrawablesWithIntrinsicBounds(null,null,null,imgClicked);
                        viewHolder.btnSeguir.setText("Seguir");
                    }
                }
                else {
                    Drawable imgClicked  = mContext.getResources().getDrawable(R.drawable.ic_amigos);
                    viewHolder.btnSeguir.setCompoundDrawablesWithIntrinsicBounds(null,null,null,imgClicked);
                    viewHolder.btnSeguir.setText("Seguir");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    // verifica na lista de amigos se contém o proprio nick para que altere o estado do botão seguir
    private String verificarListasAmigos(ArrayList<String> amigos)
    {
        for (int i = 0; i < amigos.size(); i++)
        {
            if (amigos.get(i).contains(meuUsuario.getNick()))
            {
                return "Sim";
            }
        }
        return "Não";
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
        TextView nick;
        Button btnSeguir;
        SparseBooleanArray array=new SparseBooleanArray();
        ImageButton btnMensagem,btnNotificacao;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.avatar_adpt_amigo);
            nick= (TextView)itemView.findViewById(R.id.txtNick_adpt_Amigos);
            btnSeguir = (Button)itemView.findViewById(R.id.btnSeguir_adpt_busca);
            btnMensagem = (ImageButton) itemView.findViewById(R.id.btnMensagem);
            btnNotificacao = (ImageButton)itemView.findViewById(R.id.btnNotificar);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }
}
