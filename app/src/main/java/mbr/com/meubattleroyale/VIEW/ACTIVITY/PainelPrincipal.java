package mbr.com.meubattleroyale.VIEW.ACTIVITY;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import mbr.com.meubattleroyale.DAO.LOCAL.DatabaseHelper;
import mbr.com.meubattleroyale.DAO.REMOTO.ConfiguracaoFirebase;
import mbr.com.meubattleroyale.MODEL.GERAL.Usuario;
import mbr.com.meubattleroyale.R;
import mbr.com.meubattleroyale.VIEW.ADAPTER.PPPagerAdapter;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PainelPrincipal extends AppCompatActivity
{

    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private TabLayout tabLayout;
    private String TAG = "PainelPrincipal_";
    private int[] imageResId = 
            {
            R.drawable.ic_amigos,
            R.drawable.ic_chat,
            R.drawable.ic_search,
            R.drawable.ic_noticia,
            R.drawable.ic_market,
            R.drawable.ic_settings
    };
    private int[] imageResIdChecked = {
            R.drawable.ic_amigos_check,
            R.drawable.ic_chat_click,
            R.drawable.ic_search_clicked,
            R.drawable.ic_noticia_click,
            R.drawable.ic_market_click,
            R.drawable.ic_settings_check
    };
    private int tabsInBack = 0;
    private ViewPager viewPager;
    private Usuario meuUser ;
    private FirebaseAuth user = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseHelper db;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painel_principal);
        fazerCast();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        db = new DatabaseHelper(getApplicationContext());
        usuarios.clear();
        usuarios.addAll(db.recuperarUsuarios());

        PPPagerAdapter painelPrincipalFragmentAdapter = new PPPagerAdapter(getApplicationContext(),getSupportFragmentManager());
        viewPager.setAdapter(painelPrincipalFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        inserirTab(1,tabLayout,0);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager)
                {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab)
                    {
                        super.onTabSelected(tab);
                        inserirTab(2, tabLayout, tab.getPosition());
                        tabsInBack = 1;
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab)
                    {
                        super.onTabUnselected(tab);
                        inserirTab(3,tabLayout,tab.getPosition());
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                    }
                }
        );
        viewPager.setCurrentItem(2);
    }

    private void fazerCast()
    {
        viewPager = findViewById(R.id.vp_painel);
        tabLayout = findViewById(R.id.tab_principal);
    }

    private void inserirTab(int tipo,TabLayout tabLayout,int position)
    {
        switch (tipo)
        {
            case 1:
                for (int i = 0; i < tabLayout.getTabCount(); i++)
                {
                    tabLayout.getTabAt(i).setIcon(imageResId[i]);
                }
                break;
            case 2:
                tabLayout.getTabAt(position).setIcon(imageResIdChecked[position]);
                break;
            case 3:
                tabLayout.getTabAt(position).setIcon(imageResId[position]);
                break;

        }

    }
    @Override
    public void onBackPressed()
    {
        if (tabsInBack == 1 )
        {
            viewPager.setCurrentItem(2);
            tabsInBack = 0;
        }
        else
        {
            new SweetAlertDialog(PainelPrincipal.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso")
                    .setContentText("Deseja realmente sair?")
                    .setConfirmText("Sim")
                    .setCancelText("Cancelar")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            finish();
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }

    }

    @Override
    protected void onStop() 
    {
        super.onStop();
    }
}
