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

public class MenuAdapter extends ArrayAdapter<MenuModel> implements Filterable {
    private Context context;
    private int resource;
    private List<MenuModel> menuModelList, menuList;
    private ArrayList<String> listid,listnm,listhr,listjmlh;
    private String koneksi;
    TextView total;
    private Integer totalharga=0;

    filterFilter ff;

    public MenuAdapter(@NonNull Context context, int resource, List<MenuModel> menuModelList, ArrayList<String> listid, ArrayList<String> listnm, ArrayList<String> listhr, ArrayList<String> listjmlh, String koneksi, TextView total) {
        super(context, resource,menuModelList);
        this.context = context;
        this.resource = resource;
        this.menuModelList = menuModelList;
        this.menuList = menuModelList;
        this.listid = listid;
        this.listnm = listnm;
        this.listhr = listhr;
        this.listjmlh = listjmlh;
        this.koneksi = koneksi;
        this.total = total;
    }
    class filterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults=new FilterResults();
            if (constraint!=null && constraint.length()>0){
                constraint=constraint.toString().toUpperCase();
                ArrayList<MenuModel> arrayList=new ArrayList<>();
                for (int i = 0; i < menuList.size(); i++) {
                    if (menuList.get(i).getNama().toUpperCase().contains(constraint)){
                        arrayList.add(new MenuModel(menuList.get(i).getId(),menuList.get(i).getNama(),menuList.get(i).getHarga(),menuList.get(i).getGambar()));
                    }
                }
                filterResults.values=arrayList;
            }else {
                filterResults.values=menuModelList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            menuList=(ArrayList<MenuModel>) results.values;
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
        TextView nama=convertView.findViewById(R.id.nama);
        TextView harga=convertView.findViewById(R.id.harga);
        ImageView gambar=convertView.findViewById(R.id.gambar);
        TextView number=convertView.findViewById(R.id.number);
        ImageView remove=convertView.findViewById(R.id.min);
        ImageView add=convertView.findViewById(R.id.plus);
        MenuModel menuModel=menuList.get(position);
        harga.setText(formatRupiah(Double.parseDouble(menuModel.getHarga())));
        nama.setText(menuModel.getNama());
        Picasso.get().load(menuModel.getGambar()).into(gambar);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number.setText(String.valueOf(Integer.parseInt(number.getText().toString())+1));
                if (listid.contains(menuModel.getId())){
                    listjmlh.set(listid.indexOf(menuModel.getId()),number.getText().toString());
                    listhr.set(listid.indexOf(menuModel.getId()),menuModel.getHarga());
                }else {
                    listid.add(menuModel.getId());
                    listnm.add(menuModel.getNama());
                    listhr.add(menuModel.getHarga());
                    listjmlh.add(number.getText().toString());
                }
                totalharga=totalharga+Integer.parseInt(menuModel.getHarga());
                total.setText(formatRupiah(Double.parseDouble(String.valueOf(totalharga))));
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (Integer.parseInt(number.getText().toString())>0){
                   number.setText(String.valueOf(Integer.parseInt(number.getText().toString())-1));
                   if (listid.contains(menuModel.getId())){
                       listjmlh.set(listid.indexOf(menuModel.getId()),number.getText().toString());
                       listhr.set(listid.indexOf(menuModel.getId()),menuModel.getHarga());
                   }else {
                       listid.add(menuModel.getId());
                       listnm.add(menuModel.getNama());
                       listhr.add(menuModel.getHarga());
                       listjmlh.add(number.getText().toString());
                   }
                   totalharga=totalharga-Integer.parseInt(menuModel.getHarga());
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
        return menuList.size();
    }
}
