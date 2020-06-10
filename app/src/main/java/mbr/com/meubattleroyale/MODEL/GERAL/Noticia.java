package mbr.com.meubattleroyale.MODEL.GERAL;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Noticia
{
    private String image;
    private String titulo;
    private String data;
    private String ementa;
    private double likes;
    private String id;

    public Noticia(String image, String titulo,String data,double likes,String ementa,String id)
    {
        this.image = image;
        this.data = data;
        this.titulo = titulo;
        this.likes = likes;
        this.ementa = ementa;
        this.id = id;
    }

    public Noticia() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getLikes() {
        return likes;
    }

    public void setLikes(double likes) {
        this.likes = likes;
    }

    public String getEmenta() {
        return ementa;
    }

    public void setEmenta(String ementa) {
        this.ementa = ementa;
    }

    public static String transformNum(Double number)
    {
        String numberString = "";
        if (number >= 1 && number <= 9)
        {
            NumberFormat format = new DecimalFormat("0.#");
            numberString = format.format(number);
            Log.d("NOTICIA>MODEL","if (number > 1 && number < 999) "+ numberString);
        }
        else if (number >= 9 && number <= 99)
        {
            NumberFormat format = new DecimalFormat("00.#");
            numberString = format.format(number);
            Log.d("NOTICIA>MODEL","else if (number > 9 && number < 99) "+ numberString);
        }
        else if (number >= 99 && number <= 999)
        {
            NumberFormat format = new DecimalFormat("000.#");
            numberString = format.format(number);
            Log.d("NOTICIA>MODEL","else if (number > 99 && number < 999) "+ numberString);
        }
        else if (number == 1000)
        {
            numberString = "1K";
        }
        else if (Math.abs(number / 1000000) > 1)
        {
            number = (number / 1000000) ;
            NumberFormat format = new DecimalFormat("0.#");
            numberString = format.format(number)+ "M";
            Log.d("NOTICIA>MODEL","else if (Math.abs(number / 1000000) > 1) "+ numberString);

        } else if (Math.abs(number / 1000) > 1)
        {
            number = (number / 1000) ;
            NumberFormat format = new DecimalFormat("0.#");
            numberString = format.format(number)+ "K";
            Log.d("NOTICIA>MODEL","else if (Math.abs(number / 1000) > 1) "+ numberString);

        }

        return numberString+" likes";
    }
}
