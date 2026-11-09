package com.example.trabalho4;

import java.util.ArrayList;

public class DadosApp {
    private static DadosApp instance;
    private ArrayList<PontoTuristico> pontos = new ArrayList<>();

    private DadosApp() {
        // Ponto inicial pré-cadastrado em Palmas (Praça dos Girassóis)
        PontoTuristico p1 = new PontoTuristico("Praça dos Girassóis", -10.18444, -48.33361, 300f);
        pontos.add(p1);
    }

    public static synchronized DadosApp getInstance() {
        if (instance == null) {
            instance = new DadosApp();
        }
        return instance;
    }

    public ArrayList<PontoTuristico> getPontos() {
        return pontos;
    }

    public void adicionarPonto(PontoTuristico ponto) {
        pontos.add(ponto);
    }

    public PontoTuristico getPontoPorIndice(int index) {
        if (index >= 0 && index < pontos.size()) {
            return pontos.get(index);
        }
        return null;
    }
}