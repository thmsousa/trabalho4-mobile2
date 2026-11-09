package com.example.trabalho4;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class PontoTuristico implements Serializable {
    private String nome;
    private double latitude;
    private double longitude;
    private float raioMetros;
    private ArrayList<String> fotosUris = new ArrayList<>();

    public PontoTuristico(String nome, double latitude, double longitude, float raioMetros) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.raioMetros = raioMetros;
    }

    public String getNome() {
        return nome;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getRaioMetros() {
        return raioMetros;
    }

    public GeoPoint getGeoPoint() {
        return new GeoPoint(latitude, longitude);
    }

    public ArrayList<String> getFotosUris() {
        return fotosUris;
    }

    public void adicionarFoto(String uri) {
        this.fotosUris.add(uri);
    }
}