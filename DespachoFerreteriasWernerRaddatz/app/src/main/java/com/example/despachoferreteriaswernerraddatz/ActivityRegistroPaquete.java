package com.example.despachoferreteriaswernerraddatz;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import java.util.StringTokenizer;

public class ActivityRegistroPaquete extends AppCompatActivity
{

    //declaracion de objetos
    private EditText edtCodigoBarra, edtNumeroDocumento, edtCiudadDestino, edtNombreCliente;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_paquete);

        //inicialización de objetos
        bundle = getIntent().getExtras();//llamada a objeto Bundle, que se encargará de traer los datos obtenidos desde la activity anterior
        edtCodigoBarra = findViewById(R.id.edtCodigoBarra);
        edtNumeroDocumento = findViewById(R.id.edtNumDocumento);
        edtNombreCliente = findViewById(R.id.edtNombreCliente);
        edtCiudadDestino = findViewById(R.id.edtCiudadDestino);

        String codigoBarra = getIntent().getExtras().getString("codigo_barra");//obtiene el número de codigo de barra enviado desde la activity MainActivity
        String numDocumento = getIntent().getExtras().getString("numDocumento");//obtiene el número de documento enviado desde la activity MainActivity
        String destino = getIntent().getExtras().getString("destino");//obtiene el destino enviado desde la activity MainActivity

        edtCodigoBarra.setText(codigoBarra);
        edtCiudadDestino.setText(destino);
        edtNumeroDocumento.setText(numDocumento);
    }
}