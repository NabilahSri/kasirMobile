package com.example.foodxyz;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu extends AppCompatActivity {
MenuAdapter menuadapter;
ArrayList<String> listid,listnm,listhr,listjmlh;
SharedPreferences sesion;
String tgltrans;
String idtrans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Koneksi koneksi=new Koneksi();
        listhr=new ArrayList<String>();
        listjmlh=new ArrayList<String>();
        listid=new ArrayList<String>();
        listnm=new ArrayList<String>();
        TextView total=findViewById(R.id.total);
        SearchView cari=findViewById(R.id.cari);
        tgltrans=new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        LinearLayout bayar=findViewById(R.id.bayar);
        sesion=getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sesion.edit();
        List<MenuModel> menuModelList=new ArrayList<>();
        ListView listView=findViewById(R.id.list_menu);

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest= new StringRequest(Request.Method.GET, koneksi.getUrl() + "api/products", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray=null;
                try {
                    jsonArray=new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        menuModelList.add(new MenuModel(jsonObject.getString("id"),jsonObject.getString("nama_barang"), jsonObject.getString("harga"),jsonObject.getString("image") ));
                    }
                    menuadapter=new MenuAdapter(getApplicationContext(),R.layout.menu,menuModelList,listid,listnm,listhr,listjmlh,koneksi.getUrl(),total);
                    listView.setAdapter(menuadapter);

                    cari.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            menuadapter.getFilter().filter(newText);
                            return false;
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> param=new HashMap<>();
                param.put("Content-Type","application/x-www-form-urlencoded");
                param.put("Authorization","Bearer "+sesion.getString("token",""));
                return param;
            }
        };
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

        bayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue1=Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest1=new StringRequest(Request.Method.POST, koneksi.getUrl() + "api/transaksi", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: "+response);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if (jsonObject.getString("status").equals("success")){
                                try{
                                    JSONObject jsonObject1=new JSONObject(jsonObject.getString("transaksi"));
                                    Log.d(TAG, "onResponse: "+jsonObject1);
                                    idtrans=jsonObject1.getString("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            listid=menuadapter.getListid();
                            for (int i = 0; i < listid.size(); i++) {
                                String idbrg=listid.get(i);
                                String nmbrg=listnm.get(i);
                                String jmlhbrg=listjmlh.get(i);
                                Integer posisi = i;
                                RequestQueue requestQueue2=Volley.newRequestQueue(getApplicationContext());
                                StringRequest stringRequest2=new StringRequest(Request.Method.POST, koneksi.getUrl() + "api/detail", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Integer pos = posisi +1;
                                        if(pos==listid.size()){
                                           Intent intent=new Intent(getApplicationContext(),Invoice.class);
                                           intent.putExtra("id_transaksi",idtrans);
                                           startActivity(intent);
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                }){
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> param=new HashMap<>();
                                        param.put("Content-Type","application/x-www-form-urlencoded");
                                        param.put("Authorization","Bearer "+sesion.getString("token",""));
                                        return param;
                                    }

                                    @Override
                                    public String getBodyContentType() {
                                        return "application/x-www-form-urlencoded;charset=UTF-8";
                                    }

                                    @Nullable
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> param=new HashMap<>();
                                        param.put("nama_barang",nmbrg);
                                        param.put("jumlah_barang",jmlhbrg);
                                        param.put("id_barang",idbrg);
                                        param.put("id_transaksi",idtrans.toString());
                                        return param;
                                    }
                                };
                                requestQueue2.getCache().clear();
                                requestQueue2.add(stringRequest2);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> header=new HashMap<>();
                        header.put("Content-Type","application/x-www-form-urlencoded");
                        header.put("Authorization","Bearer "+sesion.getString("token",""));
                        return header;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded;charset=UTF-8";
                    }

                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String>param=new HashMap<>();
                        param.put("tgl_transaksi",tgltrans);
                        param.put("nama_kasir","Inah");
                        param.put("total_bayar",menuadapter.getTotalharga().toString());
                        param.put("id_user",sesion.getString("id",""));
                        return param;
                    }
                };
                requestQueue1.getCache().clear();
                requestQueue1.add(stringRequest1);
            }
        });
    }
}