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

public class MainActivity extends AppCompatActivity {
    SharedPreferences sesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Koneksi koneksi=new Koneksi();
        sesion=getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sesion.edit();
        EditText user=findViewById(R.id.username);
        EditText pass=findViewById(R.id.pass);
        AppCompatButton daftar=findViewById(R.id.daftar);
        AppCompatButton login=findViewById(R.id.login);
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getApplicationContext(),Daftar.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getText().toString().length()==0){
                    user.setError("Masukan username anda ");
                } else if (pass.getText().toString().length()==0) {
                    pass.setError("Masukan password anda");
                }else {
                    RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest=new StringRequest(Request.Method.POST, koneksi.getUrl() + "api/login", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonObject=new JSONObject(response);
                                if (jsonObject.getString("status").equals("success")){
                                    try {
                                        JSONObject jsonObject1=new JSONObject(jsonObject.getString("user"));
                                        editor.putString("id",jsonObject1.getString("id"));
                                        editor.putString("token",jsonObject.getString("token"));
                                        editor.commit();
                                        startActivity(new Intent(getApplicationContext(),Transaksi.class));
                                        Toast.makeText(MainActivity.this, "login berhasil", Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    Toast.makeText(MainActivity.this, "Login gagal", Toast.LENGTH_SHORT).show();
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
                            param.put("password",pass.getText().toString());
                            return param;
                        }
                    };
                    requestQueue.getCache().clear();
                    requestQueue.add(stringRequest);
                }
            }
        });
    }
}