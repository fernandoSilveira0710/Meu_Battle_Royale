package mbr.com.meubattleroyale.MODEL.GERAL;

import mbr.com.meubattleroyale.HELPER.RandomColor;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Mensagem
{

  private String id;
  private String messagem;
  private String data;
  private String recebido;
  private String username;

  public Mensagem() {

  }

  public Mensagem(String id,String messagem, String data, String recebido, String username)
  {
    this.id = id;
    this.messagem = messagem;
    this.data = data;
    this.recebido = recebido;
    this.username = username;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRecebido() {
    return recebido;
  }

  public void setRecebido(String recebido) {
    this.recebido = recebido;
  }

  public String getMessagem() {
    return messagem;
  }

  public void setMessagem(String messagem) {
    this.messagem = messagem;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  private static int corRandom()
  {
    RandomColor randomColor = new RandomColor();
    int cor = randomColor.randomColor();
    return cor;
  }

}
