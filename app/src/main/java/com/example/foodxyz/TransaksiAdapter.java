package com.example.foodxyz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransaksiAdapter extends ArrayAdapter<TransaksiModel> implements Filterable {
    private Context context;
    private  int resource;
    private List<TransaksiModel> transaksiModelList,TransaksiList;
    private ArrayList<String> listid,listnm,listhr,listjmlh;
    TextView total;
    private  String koneksi;
    private Integer totalharga=0;
    filterFilter ff;

    public TransaksiAdapter(@NonNull Context context, int resource, List<TransaksiModel> transaksiModelList, ArrayList<String> listid, ArrayList<String> listnm, ArrayList<String> listhr, ArrayList<String> listjmlh, TextView total, String koneksi) {
        super(context, resource,transaksiModelList);
        this.context = context;
        this.resource = resource;
        this.transaksiModelList = transaksiModelList;
        this.TransaksiList = transaksiModelList;
        this.listid = listid;
        this.listnm = listnm;
        this.listhr = listhr;
        this.listjmlh = listjmlh;
        this.total = total;
        this.koneksi = koneksi;
    }

    class filterFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults=new FilterResults();
            if (constraint!=null && constraint.length()>0){
                constraint=constraint.toString().toUpperCase();
                ArrayList<TransaksiModel> arrayList=new ArrayList<>();
                for (int i = 0; i < TransaksiList.size(); i++) {
                    if (TransaksiList.get(i).getNama().toUpperCase().contains(constraint)){
                        arrayList.add(new TransaksiModel(TransaksiList.get(i).getId(),TransaksiList.get(i).getNama(),TransaksiList.get(i).getHarga(),TransaksiList.get(i).getGambar()));
                    }
                }
                filterResults.values=arrayList;
            }else {
                filterResults.values=transaksiModelList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            TransaksiList=(ArrayList<TransaksiModel>) results.values;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (ff==null){
            ff=new filterFilter();
        }
        return ff;
    }

    public ArrayList<String> getListid() {
        return listid;
    }

    public ArrayList<String> getListnm() {
        return listnm;
    }

    public ArrayList<String> getListhr() {
        return listhr;
    }

    public ArrayList<String> getListjmlh() {
        return listjmlh;
    }

    public Integer getTotalharga() {
        return totalharga;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(resource,null,false);
        }
        TextView harga=convertView.findViewById(R.id.harga);
        TextView nama=convertView.findViewById(R.id.nama);
        TextView number=convertView.findViewById(R.id.number);
        ImageView gambar=convertView.findViewById(R.id.gambar);
        ImageView add=convertView.findViewById(R.id.plus);
        ImageView remove=convertView.findViewById(R.id.min);

        TransaksiModel transaksiModel=TransaksiList.get(position);
        nama.setText(transaksiModel.getNama());
        harga.setText(transaksiModel.getHarga());
        Picasso.get().load(koneksi+transaksiModel.getGambar()).into(gambar);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number.setText(String.valueOf(Integer.parseInt(number.getText().toString())+1));
                if (listid.contains(transaksiModel.getId())){
                    listjmlh.set(listid.indexOf(transaksiModel.getId()),number.getText().toString());
                    listhr.set(listid.indexOf(transaksiModel.getId()),transaksiModel.getHarga());
                }else {
                    listid.add(transaksiModel.getId());
                    listhr.add(transaksiModel.getHarga());
                    listnm.add(transaksiModel.getNama());
                    listjmlh.add(number.getText().toString());
                }
                totalharga=totalharga+Integer.parseInt(transaksiModel.getHarga());
                total.setText(formatRupiah(Double.parseDouble(String.valueOf(totalharga))));
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(number.getText().toString())>0){
                    number.setText(String.valueOf(Integer.parseInt(number.getText().toString())-1));
                    if (listid.contains(transaksiModel.getId())){
                        listjmlh.set(listid.indexOf(transaksiModel.getId()),number.getText().toString());
                        listhr.set(listid.indexOf(transaksiModel.getId()),transaksiModel.getHarga());
                    }else {
                        listid.add(transaksiModel.getId());
                        listhr.add(transaksiModel.getHarga());
                        listnm.add(transaksiModel.getNama());
                        listjmlh.add(number.getText().toString());
                    }
                    totalharga=totalharga-Integer.parseInt(transaksiModel.getHarga());
                    total.setText(formatRupiah(Double.parseDouble(String.valueOf(totalharga))));
                }
            }
        });
        return convertView;
    }
    private String formatRupiah (Double number){
        Locale locale=new Locale("in","ID");
        NumberFormat numberFormat=NumberFormat.getNumberInstance(locale);
        return numberFormat.format(number);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return TransaksiList.size();
    }
}
