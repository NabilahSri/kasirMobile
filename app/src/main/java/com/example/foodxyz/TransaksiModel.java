package com.example.foodxyz;

public class TransaksiModel {
    private String id,nama,harga,gambar;

    public TransaksiModel(String id, String nama, String harga, String gambar) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.gambar = gambar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
