package com.example.foodxyz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Invoice extends AppCompatActivity {
    File fille,f;
    String tipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        requestPermission();
        TextView tp=findViewById(R.id.tp);
        LinearLayout linear=findViewById(R.id.linear);
        LinearLayout content=findViewById(R.id.content);
        Button save= findViewById(R.id.save);
        Spinner selectiontipe=findViewById(R.id.selection_type);
        Button share =findViewById(R.id.share);

        Intent intent=getIntent();
        ArrayList<String> Listnama=intent.getExtras().getStringArrayList("nama_barang");
        ArrayList<String> Listharga=intent.getExtras().getStringArrayList("harga_barang");
        ArrayList<String> Listjumlah=intent.getExtras().getStringArrayList("jumlah_beli");

        for (int i = 0; i < Listnama.size(); i++) {
            String nama=Listnama.get(i);
            String harga=Listharga.get(i);
            String jumlah=Listjumlah.get(i);

            TableRow tableRow=new TableRow(this);
            TableRow.LayoutParams params=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,1f);
            TextView textView=new TextView(this);
            textView.setText(nama);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(15);
            textView.setLayoutParams(params);
            tableRow.addView(textView);

            TextView textView1=new TextView(this);
            textView1.setText(harga);
            textView1.setTextColor(Color.BLACK);
            textView1.setTextSize(15);
            textView1.setGravity(Gravity.CENTER);
            textView1.setLayoutParams(params);
            tableRow.addView(textView1);

            TextView textView2=new TextView(this);
            textView2.setText(jumlah);
            textView2.setGravity(Gravity.CENTER);
            textView2.setTextColor(Color.BLACK);
            textView2.setTextSize(15);
            textView2.setLayoutParams(params);
            tableRow.addView(textView2);

            TextView textView3=new TextView(this);
            textView3.setText(String.valueOf(Integer.parseInt(jumlah)*Integer.parseInt(harga)));
            textView3.setGravity(Gravity.CENTER);
            textView3.setTextColor(Color.BLACK);
            textView3.setTextSize(15);
            textView3.setLayoutParams(params);
            tableRow.addView(textView3);
            linear.addView(tableRow);

            ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(getApplicationContext(),R.array.list_type, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectiontipe.setAdapter(adapter);

            selectiontipe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    tipe=adapter.getItem(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    content.setDrawingCacheEnabled(true);
                    Bitmap bitmap=content.getDrawingCache();
                    if (tipe.equals("Document PDF")){
                        PdfDocument pdfDocument=new PdfDocument();
                        PdfDocument.PageInfo pageInfo= new PdfDocument.PageInfo.Builder(bitmap.getWidth(),bitmap.getHeight(),1).create();
                        PdfDocument.Page page=pdfDocument.startPage(pageInfo);

                        Canvas canvas=page.getCanvas();
                        canvas.drawBitmap(bitmap,0,0,null);
                        pdfDocument.finishPage(page);
                        try{
                            if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                fille=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"foodku");
                                if (!fille.exists()){
                                    fille.mkdir();
                                }
                                f=new File(fille.getAbsoluteFile()+"/"+intent.getExtras().getString("id_transaksi")+".pdf");
                                pdfDocument.writeTo(new FileOutputStream(f));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(Invoice.this, "File berhasil disimpan", Toast.LENGTH_SHORT).show();
                        pdfDocument.close();
                    }else {
                        try{
                            if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                fille=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"foodku");
                                if (!fille.exists()){
                                    fille.mkdir();
                                }
                                f=new File(fille.getAbsoluteFile()+"/"+intent.getExtras().getString("id_transaksi")+".png");
                                FileOutputStream fileOutputStream=new FileOutputStream(f);
                                bitmap.compress(Bitmap.CompressFormat.PNG,10,fileOutputStream);
                                Toast.makeText(Invoice.this, "File berhasil disimpan", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tipe.equals("Image")){
                        Intent intent1=new Intent(Intent.ACTION_SEND);
                        intent1.putExtra(Intent.EXTRA_STREAM, Uri.parse(String.valueOf(f)));
                        intent1.setType("image/png");
                        startActivity(Intent.createChooser(intent1,"Share image via....."));
                    }else {
                        Uri PdfUri= FileProvider.getUriForFile(getApplicationContext(),"FoodPdf",f);
                        Intent intent1=new Intent(Intent.ACTION_SEND);
                        intent1.putExtra(Intent.EXTRA_STREAM, Uri.parse(String.valueOf(PdfUri)));
                        intent1.setType("application/pdf");
                        startActivity(Intent.createChooser(intent1,"Share File via....."));
                    }
                }
            });
        }
    }
    private boolean requestPermission(){
        String[] permission;
        boolean request=true;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            permission=new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
            };
        }else {
            permission=new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
            };
        }
        if (permission.length!=0){
            ActivityCompat.requestPermissions(this,permission,102);
            request=true;
        }else {
            request=false;
        }
        return request;
    }
}