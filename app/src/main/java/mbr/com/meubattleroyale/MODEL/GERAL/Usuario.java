package mbr.com.meubattleroyale.MODEL.GERAL;

public class Usuario
{
    private String id;
    public String criado;
    private String nickname;

    public Usuario(String id, String criado,String nickname)
    {
        this.id = id;
        this.criado = criado;
        this.nickname = nickname;
    }

    public Usuario() {
    }


    public String getCriado() {
        return criado;
    }

    public void setCriado(String criado) {
        this.criado = criado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
