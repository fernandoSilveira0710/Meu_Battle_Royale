package mbr.com.meubattleroyale.VIEW.ADAPTER;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import mbr.com.meubattleroyale.VIEW.FRAGMENT.Amigos;
import mbr.com.meubattleroyale.VIEW.FRAGMENT.Buscar;
import mbr.com.meubattleroyale.VIEW.FRAGMENT.Conversas;
import mbr.com.meubattleroyale.VIEW.FRAGMENT.Loja;
import mbr.com.meubattleroyale.VIEW.FRAGMENT.Noticias;
import mbr.com.meubattleroyale.VIEW.FRAGMENT.Settings;

public class PPPagerAdapter extends FragmentPagerAdapter
{

    private final Context mContext;

    public PPPagerAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        mContext = context;
    }
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return new Amigos();
            case 1:
                return new Conversas();
            case 2:
                return new Buscar();
            case 3:
                return new Noticias();
            case 4:
                return new Loja();
            case 5:
                return new Settings();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {

        return null;
    }
}




