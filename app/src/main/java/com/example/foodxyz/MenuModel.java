package com.example.foodxyz;

public class MenuModel {
    private String id,nama,harga,gambar;

    public MenuModel(String id, String nama, String harga, String gambar) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.gambar = gambar;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getHarga() {
        return harga;
    }

    public String getGambar() {
        return gambar;
    }
}
