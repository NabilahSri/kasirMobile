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

public class Transaksi extends AppCompatActivity {
    TransaksiAdapter transaksiAdapter;
    ArrayList<String> listid,listnm,listhr,listjmlh;
    SharedPreferences sesion;
    String tgltrans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Koneksi koneksi=new Koneksi();
        listid=new ArrayList<String>();
        listhr=new ArrayList<String>();
        listnm=new ArrayList<String>();
        listjmlh=new ArrayList<String>();
        TextView total=findViewById(R.id.total);
        SearchView cari=findViewById(R.id.cari);
        tgltrans=new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        LinearLayout bayar=findViewById(R.id.bayar);
        sesion=getSharedPreferences("sesion", Context.MODE_PRIVATE);

        List<TransaksiModel> transaksiModelList=new ArrayList<>();
        ListView listView=findViewById(R.id.list_menu);


        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, koneksi.getUrl() + "api/products", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray=null;
                try{
                    jsonArray=new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        transaksiModelList.add(new TransaksiModel(jsonObject.getString("id"),jsonObject.getString("nama_barang"),jsonObject.getString("harga"), jsonObject.getString("gambar")));
                    }
                    transaksiAdapter=new TransaksiAdapter(getApplicationContext(),R.layout.menu,transaksiModelList,listid,listnm,listhr,listjmlh,total, koneksi.getUrl());
                    listView.setAdapter(transaksiAdapter);

                    cari.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            transaksiAdapter.getFilter().filter(newText);
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
               listid=transaksiAdapter.getListid();
               for (int i = 0; i < listid.size(); i++) {
                   String nmbrg=listnm.get(i);
                   String hrbrg=listhr.get(i);
                   String jmlhbrg=listjmlh.get(i);
                   RequestQueue requestQueue1=Volley.newRequestQueue(getApplicationContext());
                   StringRequest stringRequest1=new StringRequest(Request.Method.POST, koneksi.getUrl() + "api/transaksi", new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {
                           Intent intent=new Intent(getApplicationContext(),Invoice.class);
                           intent.putExtra("nama_barang",listnm);
                           intent.putExtra("jumlah_beli",listjmlh);
                           intent.putExtra("harga_barang",listhr);
                           startActivity(intent);
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
                           Map<String, String> param=new HashMap<>();
                           param.put("tgl_transaksi",tgltrans);
                            param.put("nama_barang",nmbrg);
                            param.put("harga_barang",hrbrg);
                            param.put("jumlah_beli",jmlhbrg);
                            param.put("subtotal", String.valueOf(Integer.parseInt(jmlhbrg)*Integer.parseInt(hrbrg)));
                           param.put("id_user",sesion.getString("id",""));
                           return param;
                       }
                   };
                   requestQueue1.getCache().clear();
                   requestQueue1.add(stringRequest1);
               }
           }
       });


    }
}