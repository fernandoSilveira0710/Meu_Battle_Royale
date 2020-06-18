package mbr.com.meubattleroyale.VIEW.ACTIVITY;
import mbr.com.meubattleroyale.R;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

public class TelaBoasVindas extends WelcomeActivity
{
    @Override
    protected WelcomeConfiguration configuration()
    {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.colorPrimaryDark)
                .page(new TitlePage(R.drawable.logo_transp, "Olá seja bem vindo ao Meu Battle Royale,uma plataforma onde poderá interagir com amigos,noticias e muito mais"))
                .page(new BasicPage(R.drawable.ic_friend, "Interaja com amigos", "Aqui poderá encontrar seus amigos,conversar,encontrar novos jogadores e enviar alertas"))
                .page(new BasicPage(R.drawable.ic_news, "Receba noticias", "Noticias sobre o seu Battle Royale favorito!"))
                .page(new BasicPage(R.drawable.ic_loja, "Consulte a loja", "Acompanhe a loja do seu battle royale diariamente!"))
                .build();
    }


}

