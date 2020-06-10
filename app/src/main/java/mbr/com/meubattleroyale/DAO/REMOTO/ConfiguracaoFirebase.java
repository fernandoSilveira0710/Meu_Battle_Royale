package mbr.com.meubattleroyale.DAO.REMOTO;

/**
 * Created by Fernando Silveira on 08/03/2017.
 */

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public final class ConfiguracaoFirebase
{
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth autenticacao;
    private static FirebaseStorage storage;
    private static StorageReference storageRef;

    public static DatabaseReference getFirebase()
    {
        if( referenciaFirebase == null )
        {
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }
    public static FirebaseAuth getFirebaseAutenticacao()
    {
        if( autenticacao == null )
        {
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }
    public static StorageReference getStorageRef()
    {
        if (storage == null)
        {
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReferenceFromUrl("gs://geekfolks-5695d.appspot.com/");
        }
        return storageRef;
    }
    public static String getUID()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }

}

