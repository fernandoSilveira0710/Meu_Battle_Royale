package mbr.com.meubattleroyale.MODEL.INTERFACE;

import android.widget.ImageButton;

import mbr.com.meubattleroyale.MODEL.GERAL.Amigo;

public interface CustomBusca
{
    void onCopiar(ImageButton button, int position, Amigo usuario);

    void onMessagemClick(ImageButton button, int position, Amigo usuario);
}
