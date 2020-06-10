package mbr.com.meubattleroyale.VIEW.ACTIVITY;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Noticia;
import mbr.com.meubattleroyale.R;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ConteudoNoticia extends AppCompatActivity
{
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();

    private String data,ementa,image,titulo,id;
    private double likes;
    private ImageView imgTbr;
    private ImageButton imgLikes,imgShare;
    private ArrayList<Noticia> listNoticiasLocal = new ArrayList<>();
    private TextView txtEmenta,txtTitulo,txtLikes,txtData;

    private boolean shareclick = false;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteudo_noticia);
        Bundle bundle = getIntent().getExtras();
        data = bundle.getString("data");
        ementa = bundle.getString("ementa");
        image = bundle.getString("image");
        titulo = bundle.getString("titulo");
        likes = bundle.getDouble("likes");
        id = bundle.getString("id");

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgTbr = (ImageView)findViewById(R.id.imgToolbar);
        imgLikes = (ImageButton)findViewById(R.id.imgLike);
        imgShare = (ImageButton)findViewById(R.id.imgShare);
        txtData = (TextView)findViewById(R.id.txtData);
        txtLikes = (TextView)findViewById(R.id.txtLikes);
        txtTitulo = (TextView)findViewById(R.id.txtTitulo);
        txtEmenta = (TextView)findViewById(R.id.txtEmenta);

        db = new DatabaseHelper(getApplicationContext());
        if (db.getQTDNoticias() >= 1)
        {
            listNoticiasLocal.addAll(db.recuperaNoticias());
            for (int i = 0; i < listNoticiasLocal.size(); i++)
            {
                try
                {
                    if (listNoticiasLocal.get(i).getId().equals(id))
                    {
                        imgLikes.setImageResource(R.drawable.ic_like_checked);
                        shareclick = true;
                    }
                }catch (NullPointerException  e)
                {
                    shareclick = false;
                }
            }
        }

        Picasso.get().load(image).into(imgTbr);
        txtEmenta.setText(ementa);
        txtLikes.setText(Noticia.transformNum(likes));
        txtData.setText("Criada em "+data);
        txtTitulo.setText(titulo);

        imgLikes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (shareclick == false)
                {
                    imgLikes.setImageResource(R.drawable.ic_like_checked);
                    ref.child("noticias").child(id).child("likes").setValue(likes +1);
                    txtLikes.setText(Noticia.transformNum(likes +1));
                    Log.d("ConteudoNoticias_","Inserindo noticia antes" + db.getQTDNoticias());
                    db.inserirNoticia(new Noticia(null,null,null,0.0,null,id));
                    Log.d("ConteudoNoticias_","Inserindo noticia depois" + db.getQTDNoticias());
                    shareclick = true;
                }
                else
                    {
                        imgLikes.setImageResource(R.drawable.ic_like);
                        ref.child("noticias").child(id).child("likes").setValue(likes);
                        txtLikes.setText(Noticia.transformNum(likes));
                        Log.d("ConteudoNoticias_","Deletando noticia antes" + db.getQTDNoticias());
                        db.deletarNoticia(new Noticia(null,null,null,0.0,null,id),"");
                        Log.d("ConteudoNoticias_","Deletando noticia depois" + db.getQTDNoticias());
                        shareclick = false;
                    }
            }
        });
        imgShare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });



    }
}
