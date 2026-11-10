package com.example.trabalho4;

import android.content.ContentValues;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;

public class CameraActivity extends AppCompatActivity {

    private LifecycleCameraController cameraController;
    private PreviewView previewView;
    private int pontoIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        pontoIndex = getIntent().getIntExtra("PONTO_INDEX", -1);
        previewView = findViewById(R.id.preview_view);
        Button btnCapturar = findViewById(R.id.btnCapturar);

        abrirEMostrarCamera();
        btnCapturar.setOnClickListener(v -> tirarFoto());
    }

    // Slide 17 de Permissões: LifecycleCameraController
    private void abrirEMostrarCamera() {
        previewView = findViewById(R.id.preview_view);
        cameraController = new LifecycleCameraController(getBaseContext());
        cameraController.bindToLifecycle(this);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        previewView.setController(cameraController);
    }

    // Slide 20 de Permissões: ImageCapture e MediaStore
    private void tirarFoto() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,
                "foto-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(System.currentTimeMillis()));
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/GuiaTuristico");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        ).build();

        cameraController.takePicture(outputFileOptions,
                ContextCompat.getMainExecutor(this), onImageSavedCallback);
    }

    // Slide 19 de Permissões: Callback de salvamento
    private ImageCapture.OnImageSavedCallback onImageSavedCallback =
            new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    android.net.Uri savedUri = outputFileResults.getSavedUri();
                    if (savedUri != null) {
                        String uriString = savedUri.toString();

                        PontoTuristico ponto = DadosApp.getInstance().getPontoPorIndice(pontoIndex);
                        if (ponto != null) {
                            ponto.adicionarFoto(uriString);
                        }
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(CameraActivity.this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    runOnUiThread(() -> {
                        Toast.makeText(CameraActivity.this, "Erro ao tentar salvar", Toast.LENGTH_SHORT).show();
                    });
                    exception.printStackTrace();
                }
            };
}