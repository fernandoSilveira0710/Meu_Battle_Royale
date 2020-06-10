package mbr.com.meubattleroyale.VIEW.ADAPTER;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;
import mbr.com.meubattleroyale.MODEL.GERAL.Avatar;
import mbr.com.meubattleroyale.MODEL.GERAL.Mensagem;
import mbr.com.meubattleroyale.MODEL.INTERFACE.CustomConversa;
import mbr.com.meubattleroyale.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorConversa extends RecyclerView.Adapter<AdaptadorConversa.ViewHolder>
{

    private final String seuId;
    private ArrayList<Mensagem> listConversa;
    private Context mContext;
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    // Define listener member variable
    private CustomConversa customConversa;
    private Amigo meuUsuario;
    private long DURATION = 500;
    private boolean on_atach = true;

    public AdaptadorConversa(Context context, ArrayList<Mensagem> list,String seuId, CustomConversa customConversa)
    {
        this.seuId = seuId;
        this.listConversa = list;
        this.mContext = context;
        this.customConversa= customConversa;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adaptador_conversa, viewGroup, false);
        final AdaptadorConversa.ViewHolder mViewHolder = new AdaptadorConversa.ViewHolder(itemView);
        return mViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {

        // Get the data model based on position
        final Mensagem conversa = listConversa.get(position);
        //formatar horas
        Log.d("ADPTC","DADOS CONVERSA: "+ conversa.getRecebido());
        String horas[] = conversa.getData().split(" ");
        Log.d("ADPTC","DADOS CONVERSA:HORA  "+ horas[1]);
        String hora[] = horas[1].split(":");
        String[] icone_tipo = conversa.getRecebido().split(":");

        viewHolder.nick.setText(conversa.getUsername());
        viewHolder.ultmsg.setText(conversa.getMessagem());
        viewHolder.hora.setText(hora[0]+":"+hora[1]);
        viewHolder.imageView.setImageResource(Avatar.identificarAvatar(Integer.parseInt(icone_tipo[0])));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("ADPTC","Enviando pelo onBindViewHolder");
                customConversa.onItemClick(viewHolder.itemView, viewHolder.getAdapterPosition(),conversa);
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
        return listConversa.size();
    }

    // Easy access to the context object in the recyclerview
    private Context getContext()
    {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imageView;
        View parentLayout;
        TextView nick,hora,ultmsg;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.avatar_adpt_amigo);
            nick= (TextView)itemView.findViewById(R.id.txtNick);
            hora = (TextView)itemView.findViewById(R.id.txtHora);
            ultmsg = (TextView) itemView.findViewById(R.id.txtUltMsg);

            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }
}
