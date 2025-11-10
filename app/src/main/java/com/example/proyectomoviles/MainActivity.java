package com.example.proyectomoviles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ListView listMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listMenu = findViewById(R.id.ListViewMenu);

        String[] titulos = {
                getString(R.string.cliente),
                getString(R.string.tituloPelicula),
                getString(R.string.funcion)
        };


        int[] iconos = {
                R.drawable.client,
                R.drawable.movie,
                R.drawable.function
        };

        MenuAdapter adapter = new MenuAdapter(this, titulos, iconos);
        listMenu.setAdapter(adapter);

        listMenu.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            switch (position) {
                case 0:
                    Intent intent= new Intent(this,Cliente.class);
                    startActivity(intent);
                    break;
                case 1:
                    Intent intent1= new Intent(this,Pelicula.class);
                    startActivity(intent1);
                    break;
                case 2:
                    Intent intent2= new Intent(this,Funcion.class);
                    startActivity(intent2);
                    break;
            }
        });
    }

}
