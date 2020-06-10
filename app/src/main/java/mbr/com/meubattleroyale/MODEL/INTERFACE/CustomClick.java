package mbr.com.meubattleroyale.MODEL.INTERFACE;

import android.view.View;
import android.widget.Button;

import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;

public interface CustomClick
{
    void onItemClick(View itemView, int position, Button button,
                     Amigo meuUsario, Amigo usuario);

}
