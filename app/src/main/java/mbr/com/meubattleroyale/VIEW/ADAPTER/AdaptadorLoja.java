package mbr.com.meubattleroyale.VIEW.ADAPTER;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import mbr.com.meubattleroyale.MODEL.API.Store;
import mbr.com.meubattleroyale.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptadorLoja extends RecyclerView.Adapter<AdaptadorLoja.CustomViewHolder>{

    private Context context;
    private List<Store> popularList;

    public AdaptadorLoja(Context context, List<Store> popularList)
    {
        this.context = context;
        this.popularList = popularList;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adaptador_loja, null);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position)
    {

        Store currentStore = popularList.get(position);
        Log.d("AdaptLoja_","Raridade de cada skin: "+ currentStore.getRarity());

        holder.vBucks.setText(currentStore.getvBucks() + "");
        holder.name.setText(currentStore.getName());
        //holder.rarity.setText(currentStore.getRarity());
        Picasso.get().load(currentStore.getImageUrl()).into(holder.image);
    }


    @Override
    public int getItemCount() {
        return (null != popularList ? popularList.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        ImageView image;
        TextView vBucks;
        TextView name;
        LinearLayout lnlADPT;

        CustomViewHolder(View itemView)
        {
            super(itemView);
            image = itemView.findViewById(R.id.imgSkinLoja);
            vBucks = itemView.findViewById(R.id.vBucksLoja);
            name = itemView.findViewById(R.id.nameLoja);
            lnlADPT = itemView.findViewById(R.id.lnl);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Toast.makeText(context, popularList.get(clickedPosition).getName(), Toast.LENGTH_LONG).show();
        }
    }
}





