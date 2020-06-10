package mbr.com.meubattleroyale.DAO.API;

import mbr.com.meubattleroyale.MODEL.API.Store;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface FornightService
{
    @Headers("TRN-Api-Key: 4c503120-d709-4743-b15e-4d6c00a167dd")
    @GET("store")
    public Call<List<Store>> getStore();
}

