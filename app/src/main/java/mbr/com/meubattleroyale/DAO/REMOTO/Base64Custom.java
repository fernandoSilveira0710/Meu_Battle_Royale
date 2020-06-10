package mbr.com.meubattleroyale.DAO.REMOTO;

import android.util.Base64;

public class Base64Custom
{
    public static String codificarBase64(String texto)
    {
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","");
    }
    public String decodificarBase64(String textocodificado)
    {
        return new String( Base64.decode(textocodificado, Base64.DEFAULT));
    }
}