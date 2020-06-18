package mbr.com.meubattleroyale.MODEL.INTERFACE;

import android.view.View;

import mbr.com.meubattleroyale.MODEL.GERAL.Mensagem;

public interface CustomConversa
{
    void onItemClick(View itemView, int position, Mensagem conversa);
    void onLongItemClick(View itemView, int position, Mensagem conversa);
}
