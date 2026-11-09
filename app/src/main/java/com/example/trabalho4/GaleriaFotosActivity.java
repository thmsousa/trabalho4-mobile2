package com.example.trabalho4;

import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GaleriaFotosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        int pontoIndex = getIntent().getIntExtra("PONTO_INDEX", -1);
        PontoTuristico ponto = DadosApp.getInstance().getPontoPorIndice(pontoIndex);

        TextView tvTitulo = findViewById(R.id.tvTitulo);
        TextView tvSubtitulo = findViewById(R.id.tvSubtitulo);
        RecyclerView rvFotos = findViewById(R.id.rvFotos);

        if (ponto != null) {
            tvTitulo.setText(ponto.getNome());
            ArrayList<String> fotos = ponto.getFotosUris();
            tvSubtitulo.setText("Raio: " + ponto.getRaioMetros() + "m | Fotos registradas: " + fotos.size());

            rvFotos.setLayoutManager(new GridLayoutManager(this, 3));
            rvFotos.setAdapter(new RecyclerView.Adapter<FotoVH>() {
                @NonNull
                @Override
                public FotoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    ImageView iv = new ImageView(parent.getContext());
                    iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 320));
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setPadding(4, 4, 4, 4);
                    return new FotoVH(iv);
                }

                @Override
                public void onBindViewHolder(@NonNull FotoVH holder, int position) {
                    Glide.with(holder.imageView.getContext())
                            .load(Uri.parse(fotos.get(position)))
                            .into(holder.imageView);
                }

                @Override
                public int getItemCount() {
                    return fotos.size();
                }
            });
        }
    }

    static class FotoVH extends RecyclerView.ViewHolder {
        ImageView imageView;
        FotoVH(@NonNull ImageView itemView) {
            super(itemView);
            this.imageView = itemView;
        }
    }
}