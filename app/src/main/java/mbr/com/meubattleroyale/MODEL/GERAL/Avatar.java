package mbr.com.meubattleroyale.MODEL.GERAL;

import mbr.com.meubattleroyale.R;

public class Avatar
{
    public String avatar;
    public String criado;
    public int id;
    private static int[] icons = new int[33];

    public Avatar()
    {

    }
    public Avatar(int id,String icon,String criado)
    {
        this.id = id;
        this.criado = criado;
        this.avatar = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCriado() {
        return criado;
    }

    public void setCriado(String criado) {
        this.criado = criado;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private static int[] adicionarDrawables()
    {
        icons[0] = R.drawable.ic_add_avatar;
        icons[1] = R.drawable.ic_img01avt;
        icons[2] = R.drawable.ic_img02avt;
        icons[3] = R.drawable.ic_img03avt;
        icons[4] = R.drawable.ic_img04avt;
        icons[5] = R.drawable.ic_img05avt;
        icons[6] = R.drawable.ic_img06avt;
        icons[7] = R.drawable.ic_img07avt;
        icons[8] = R.drawable.ic_img08avt;

        icons[9] = R.drawable.ic_img09avt;
        icons[10] = R.drawable.ic_img10avt;
        icons[11] = R.drawable.ic_img11avt;
        icons[12] = R.drawable.ic_img12avt;
        icons[13] = R.drawable.ic_img13avt;
        icons[14] = R.drawable.ic_img14avt;
        icons[15] = R.drawable.ic_img15avt;
        icons[16] = R.drawable.ic_img16avt;

        icons[17] = R.drawable.ic_img17avt;
        icons[18] = R.drawable.ic_img18avt;
        icons[19] = R.drawable.ic_img19avt;
        icons[20] = R.drawable.ic_img20avt;
        icons[21] = R.drawable.ic_img21avt;
        icons[22] = R.drawable.ic_img22avt;
        icons[23] = R.drawable.ic_img23avt;
        icons[24] = R.drawable.ic_img24avt;

        icons[25] = R.drawable.ic_img25avt;
        icons[26] = R.drawable.ic_img26avt;
        icons[27] = R.drawable.ic_img27avt;
        icons[28] = R.drawable.ic_img28avt;
        icons[29] = R.drawable.ic_img29avt;
        icons[30] = R.drawable.ic_img30avt;
        icons[31] = R.drawable.ic_img31avt;
        icons[32] = R.drawable.ic_img32avt;

        return icons;
    }

    public static int identificarAvatar(int name)
    {
        adicionarDrawables();
        int drawable = 0;
        for (int i = 1; i <= 32; i++)
        {
            if (i == name)
            {
                drawable = icons[i];
            }
        }
        return drawable;
    }
}
