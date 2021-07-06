package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class ActivityPaqueteEscaneado extends AppCompatActivity {

    ListView lstElementosEscaneados;
    ImageButton imgButtonEscanear;
    TextView lblModoApp;

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
        lblModoApp = findViewById (R.id.lblModoApp);

        //recepción de valor enviado desde el MainActivity.java
        Bundle bundle = getIntent().getExtras();
        String modo = getIntent().getStringExtra("modo");

        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper ();

        //comparación modo app
        if(modo.equals ("1"))
        {
            lblModoApp.setText (lblModoApp.getText ().toString ()+"REVISIÓN");
            //acciones de registro en modo REVISIÓN


        }
        else
        {
            if(modo.equals ("2"))
            {
                lblModoApp.setText (lblModoApp.getText ().toString ()+"DESPACHO");
            }
            else
            {
                if(modo.equals ("3"))
                {
                    lblModoApp.setText (lblModoApp.getText ().toString ()+"CARGA");
                }
                else
                {
                    lblModoApp.setText (lblModoApp.getText ().toString ()+"ENTREGA");
                }
            }
        }

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

                    if(modo == "0")
                    {

                    }

            }
        });

    }//lectura de código de barra de revisión
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult (requestCode, resultCode, data);

        if (result != null)
        {
            if (result.getContents () == null)
            {
                Toast.makeText (this, "Lectura cancelada", Toast.LENGTH_LONG).show ();
            }
            else
            {
                Toast.makeText (this, "Cod. barra: " + fun.tokenizer (result.getContents ()), Toast.LENGTH_LONG).show ();
            }
        }
        else
        {
            super.onActivityResult (requestCode, resultCode, data);
        }
    }
}