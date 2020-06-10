package mbr.com.meubattleroyale.MODEL.GERAL;

public class Notificacao
{
    private String recebido;
    private String id;
    private String data;

    public Notificacao(String recebido, String id,String data)
    {
        this.recebido = recebido;
        this.id = id;
        this.data = data;
    }

    public Notificacao() {

    }

    public String getRecebido() {
        return recebido;
    }

    public void setRecebido(String recebido) {
        this.recebido = recebido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
