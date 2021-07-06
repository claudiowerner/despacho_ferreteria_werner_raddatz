package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class ActivityPaqueteEscaneado extends AppCompatActivity {

    ListView lstElementosEscaneados;
    ImageButton imgButtonEscanear;

    Funciones fun = new Funciones ();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_paquete_escaneado);
        //Creación de objeto ArrayList;
        ArrayList telefonos=new ArrayList();
        ArrayAdapter adaptador1=new ArrayAdapter(this,android.R.layout.simple_list_item_1,telefonos);

        telefonos.add("marcos : 43734843");
        telefonos.add("luis : 6554343");
        telefonos.add("ana : 7445434");

        imgButtonEscanear = findViewById (R.id.imgButtonEscanear);
        lstElementosEscaneados=findViewById(R.id.lstElementosEscaneados);
        lstElementosEscaneados.setAdapter(adaptador1);


        //definicion acciones btnEscanear
        imgButtonEscanear.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                    IntentIntegrator integrador = new IntentIntegrator (ActivityPaqueteEscaneado.this);
                    integrador.setDesiredBarcodeFormats (IntentIntegrator.CODE_128);
                    integrador.setOrientationLocked (true);
                    integrador.setPrompt ("LECTOR CÓDIGO DE BARRA");
                    integrador.setCameraId (0);
                    integrador.setBeepEnabled (true);
                    integrador.setBarcodeImageEnabled (true);
                    integrador.getCaptureActivity ();
                    integrador.initiateScan ();

            }
        });

    }//lectura de código de barra de revisión
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult (requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents () == null) {
                Toast.makeText (this, "Lectura cancelada", Toast.LENGTH_LONG).show ();
            } else {
                Toast.makeText (this, "Cod. barra: " + fun.tokenizer (result.getContents ()), Toast.LENGTH_LONG).show ();
            }
        } else {
            super.onActivityResult (requestCode, resultCode, data);
        }
    }
}