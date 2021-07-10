package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class activityFacturaNomina extends AppCompatActivity {

    //declaración de objetos

    TextView txtNumDoc, txtNumPaquetesEntregados;
    EditText edtComentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_factura_nomina);

        txtNumDoc = findViewById (R.id.txtNumDoc);
        txtNumPaquetesEntregados = findViewById (R.id.txtNumPaquetesEntregados);
        edtComentario = findViewById (R.id.edtComentario);

        //recepción de valor enviado desde el ActivityNomina.java
        Bundle bundle = getIntent().getExtras();
        String numDoc = getIntent().getStringExtra("codigo_barra");
        txtNumDoc.setText ("Documento no.\n"+numDoc);
    }
}