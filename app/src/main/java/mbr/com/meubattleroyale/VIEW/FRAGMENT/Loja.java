package mbr.com.meubattleroyale.VIEW.FRAGMENT;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import mbr.com.meubattleroyale.DAO.API.FornightService;
import mbr.com.meubattleroyale.HELPER.NetworkCheckingClass;
import mbr.com.meubattleroyale.MODEL.API.Store;
import mbr.com.meubattleroyale.R;
import mbr.com.meubattleroyale.VIEW.ADAPTER.AdaptadorLoja;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Loja extends Fragment
{
    private RecyclerView recyclerViewHorizontal;
    private AdaptadorLoja adaptadorLoja;
    private ArrayList<Store> stores = new ArrayList<Store>();
    private ProgressBar progressBar;


    public Loja() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_loja, container, false);
        fazerCast(view);
        return view;
    }

    private void fazerCast(View view)
    {
        recyclerViewHorizontal = view.findViewById(R.id.horizontal_recycler_view);
        recyclerViewHorizontal.setLayoutManager(new GridLayoutManager(getContext(),3));
        progressBar = view.findViewById(R.id.progressBar);
        if (NetworkCheckingClass.isNetworkAvailable(getContext()))
        {
            progressBar.setVisibility(View.VISIBLE);
            atualizarLoja();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Sem conexão", Toast.LENGTH_LONG).show();
        }
    }
    private void atualizarLoja()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.fortnitetracker.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(FornightService.class).getStore().enqueue(new Callback<List<Store>>()
        {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response)
            {
                stores = new ArrayList<>();
                stores.addAll(response.body());
                adaptadorLoja = new AdaptadorLoja(getContext(), stores);
                recyclerViewHorizontal.setAdapter(adaptadorLoja);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t)
            {
                Log.d("InfoConta", t.getMessage());
            }
        });
    }

}
