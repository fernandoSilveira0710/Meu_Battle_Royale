package mbr.com.meubattleroyale.MODEL.GERAL;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Amigo
{
    public int icone;
    public String nick;
    public String tipo;
    public String id;
    public ArrayList<String> amigos;


    public Amigo()
    {

    }

    public Amigo(int icone, String nick, String tipo, String id, ArrayList<String> amigos)
    {
        this.icone = icone;
        this.nick = nick;
        this.tipo = tipo;
        this.id = id;
        this.amigos = amigos;
    }
    public int getIcone() {
        return icone;
    }

    public void setIcone(int icone) {
        this.icone = icone;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Exclude
    public Map<String, Object> mapearUsuario()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nick",nick);
        result.put("icone",icone);
        result.put("tipo",tipo);
        result.put("amigos",amigos);
        result.put("id",id);

        return result;
    }

    public ArrayList<String> getAmigos() {
        return amigos;
    }

    public void setAmigos(ArrayList<String> amigos) {
        this.amigos = amigos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
