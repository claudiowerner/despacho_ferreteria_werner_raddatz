package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private Button btnRevision, btnDespacho, btnCarga, btnEntrega, btnNomina, btnCrearViaje, btnModificarViaje;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ActivityPrimerUso.class);

        //inicialización de objetos
        btnRevision = findViewById(R.id.btnRevision);
        btnNomina = findViewById(R.id.btnNomina);
        btnDespacho = findViewById(R.id.btnDespacho);
        btnCarga = findViewById (R.id.btnCarga);
        btnEntrega = findViewById (R.id.btnEntrega);


        //acciones botón revisión
        btnRevision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrador = new IntentIntegrator(MainActivity.this);
                integrador.setDesiredBarcodeFormats(IntentIntegrator.CODE_128);
                integrador.setOrientationLocked (true);
                integrador.setPrompt("LECTOR CÓDIGO DE BARRA");
                integrador.setCameraId(0);
                integrador.setBeepEnabled(true);
                integrador.setBarcodeImageEnabled(true);
                integrador.getCaptureActivity ();
                integrador.initiateScan();
            }
        });

        Intent finalIntent1 = intent;
        btnDespacho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(finalIntent1);
            }
        });


        //llamada a activity Nómina
        intent = new Intent(this, ActivityNomina.class);

        Intent finalIntent = intent;
        //acciones botón nómina
        btnNomina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //llamada a ventana ActivityNomina
                startActivity(finalIntent);
            }
        });




    }

    //lectura de código de barra de revisión
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result!=null)
        {
            if(result.getContents()==null)
            {
                Toast.makeText(this, "Lectura cancelada", Toast.LENGTH_LONG).show();
            }
            else
            {
                //separación de cadena de caracteres
                StringTokenizer st = new StringTokenizer(result.getContents(),"-");
                String destino = "";
                String numDocumento = "";
                while (st.hasMoreTokens()) {
                    if(destino.equals(""))
                    {
                        destino = st.nextToken();
                    }
                    else
                    {
                        numDocumento = st.nextToken();
                    }
                }

                //llamada a activity ActivityRegistroPaquete
                Intent intent = new Intent(this, ActivityRegistroPaquete.class);

                //traspaso a ActivityRegistroPaquete de cadena string obtenida de la lectura del código de barra
                intent.putExtra("codigo_barra",result.getContents());
                intent.putExtra("destino",destino);
                intent.putExtra("numDocumento",numDocumento);


                startActivity(intent);
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}