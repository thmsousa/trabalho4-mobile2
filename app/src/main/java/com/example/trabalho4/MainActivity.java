package com.example.trabalho4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISSOES = 1;
    private MapView map;
    private IMapController mapController;
    private FusedLocationProviderClient fusedLocationClient;
    private ExtendedFloatingActionButton btnTirarFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Slide 6 do OSMBonusPack
        Configuration.getInstance().setUserAgentValue("br.edu.ifto.trabalho4.guia/1.0 (aluno@ifto.edu.br)");        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnTirarFoto = findViewById(R.id.btnTirarFoto);

        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(15.0);

        GeoPoint startPoint = new GeoPoint(-10.18444, -48.33361);
        mapController.setCenter(startPoint);

        btnTirarFoto.setOnClickListener(v -> checarLocalizacaoEVerificarPonto());

        solicitarPermissoes();
        atualizarMapa();
    }

    private void solicitarPermissoes() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA
            }, CODIGO_PERMISSOES);
        }
    }

    private void atualizarMapa() {
        map.getOverlays().clear();

        List<PontoTuristico> lista = DadosApp.getInstance().getPontos();

        for (int i = 0; i < lista.size(); i++) {
            PontoTuristico ponto = lista.get(i);
            final int indexPonto = i;

            // Slide 7 do OSMBonusPack
            Marker marcador = new Marker(map);
            marcador.setPosition(ponto.getGeoPoint());
            marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marcador.setTitle(ponto.getNome());
            marcador.setSnippet("Raio: " + ponto.getRaioMetros() + "m (Toque para ver fotos)");

            // Usa o marcador do drawable (ou ícone padrão se não houver)
            try {
                marcador.setIcon(ContextCompat.getDrawable(this, R.drawable.marcador_vetor));
            } catch (Exception ignored) {}

            marcador.setOnMarkerClickListener((marker, mapView) -> {
                Intent intent = new Intent(MainActivity.this, GaleriaFotosActivity.class);
                intent.putExtra("PONTO_INDEX", indexPonto);
                startActivity(intent);
                return true;
            });

            map.getOverlays().add(marcador);

            // Slide 9 do OSMBonusPack: Círculo de raio
            Polygon poligono = new Polygon(map);
            poligono.setFillColor(0x330088FF);
            poligono.setStrokeColor(Color.RED);
            poligono.setStrokeWidth(4);
            List<GeoPoint> pontosDoCirculo = Polygon.pointsAsCircle(ponto.getGeoPoint(), ponto.getRaioMetros());
            poligono.setPoints(pontosDoCirculo);
            map.getOverlays().add(poligono);
        }

        map.invalidate();
    }

    // Slide 14 de Permissões: FusedLocationProviderClient
    private void checarLocalizacaoEVerificarPonto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            solicitarPermissoes();
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken())
                .addOnSuccessListener(this, location -> {
                    if (location == null) {
                        Toast.makeText(this, "Não foi possível obter o GPS. Ative a localização.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int indicePonto = encontrarPontoNoRaio(location);

                    if (indicePonto != -1) {
                        PontoTuristico ponto = DadosApp.getInstance().getPontoPorIndice(indicePonto);
                        Toast.makeText(this, "Você está em: " + ponto.getNome(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        intent.putExtra("PONTO_INDEX", indicePonto);
                        startActivity(intent);
                    } else {
                        abrirDialogoNovoPonto(location);
                    }
                });
    }

    private int encontrarPontoNoRaio(Location userLocation) {
        List<PontoTuristico> pontos = DadosApp.getInstance().getPontos();
        for (int i = 0; i < pontos.size(); i++) {
            PontoTuristico p = pontos.get(i);
            float[] resultado = new float[1];
            Location.distanceBetween(
                    userLocation.getLatitude(), userLocation.getLongitude(),
                    p.getLatitude(), p.getLongitude(),
                    resultado
            );
            if (resultado[0] <= p.getRaioMetros()) {
                return i;
            }
        }
        return -1;
    }

    private void abrirDialogoNovoPonto(Location loc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Novo Ponto Turístico");
        builder.setMessage("Você está fora de qualquer ponto turístico cadastrado. Deseja registrar este local?");

        View v = LayoutInflater.from(this).inflate(R.layout.dialog_novo_ponto, null);
        EditText etNome = v.findViewById(R.id.etNomePonto);
        EditText etRaio = v.findViewById(R.id.etRaioPonto);
        builder.setView(v);

        builder.setPositiveButton("Salvar e Fotografar", (dialog, which) -> {
            String nome = etNome.getText().toString().trim();
            String raioStr = etRaio.getText().toString().trim();

            if (nome.isEmpty() || raioStr.isEmpty()) {
                Toast.makeText(this, "Preencha o nome e o raio!", Toast.LENGTH_SHORT).show();
                return;
            }

            float raio = Float.parseFloat(raioStr);
            PontoTuristico novo = new PontoTuristico(nome, loc.getLatitude(), loc.getLongitude(), raio);
            DadosApp.getInstance().adicionarPonto(novo);

            atualizarMapa();
            mapController.setCenter(novo.getGeoPoint());

            int novoIndex = DadosApp.getInstance().getPontos().size() - 1;
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            intent.putExtra("PONTO_INDEX", novoIndex);
            startActivity(intent);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
        atualizarMapa();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}