package com.example.foodxyz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Daftar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
        Koneksi koneksi=new Koneksi();
        EditText user=findViewById(R.id.username);
        EditText nama=findViewById(R.id.nama);
        EditText alamat=findViewById(R.id.alamat);
        EditText telp=findViewById(R.id.telp);
        EditText email=findViewById(R.id.email);
        EditText pass=findViewById(R.id.pass);
        AppCompatButton daftar=findViewById(R.id.daftar);
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest=new StringRequest(Request.Method.POST, koneksi.getUrl() + "api/register", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            if (jsonObject.getString("status").equals("success")){
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    Toast.makeText(Daftar.this, "register berhasil", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(Daftar.this, "Regiser gagal", Toast.LENGTH_SHORT).show();
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
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded;charset=UTF-8";
                    }

                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param=new HashMap<>();
                        param.put("username",user.getText().toString());
                        param.put("name",nama.getText().toString());
                        param.put("telepon",telp.getText().toString());
                        param.put("alamat",alamat.getText().toString());
                        param.put("password",pass.getText().toString());
                        param.put("email",email.getText().toString());
                        param.put("tipe_user","Member");
                        return param;
                    }
                };
                requestQueue.getCache().clear();
                requestQueue.add(stringRequest);
            }
        });
    }
}