package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;

import java.util.ArrayList;

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
        int paq_entr = num_paquetes_entregados (numDoc);//captura el numero de paquetes entregados
        int paq_carga = num_paquetes_totales (numDoc);//obtiene el numero de paquetes existentes en el camión
        int porc_avance = (paq_entr/paq_carga)*100;
        txtNumPaquetesEntregados.setText ("Entregados "+paq_entr+"/"+paq_carga+" ("+porc_avance+"%)");
    }

    //Obtiene el número de paquetes entregados
    private int num_paquetes_entregados(String numDoc)
    {
        int i = 0;//captura el número de paquetes entregados
        //llamada a clase ConnectionSQLiteHelper
        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);

        SQLiteDatabase db = conn.getReadableDatabase ();

        Cursor cursor = db.rawQuery("select * from caja_estatus_reporte where estatus=4 and num_doc = '"+numDoc+"'",null);

        ArrayList array = new ArrayList ();
        while (cursor.moveToNext ())
        {
            i++;
        }
        return i;
    }

    //Obtiene el número de paquetes totales
    private int num_paquetes_totales(String numDoc)
    {
        int i = 0;//captura el número de paquetes entregados
        //llamada a clase ConnectionSQLiteHelper
        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);

        SQLiteDatabase db = conn.getReadableDatabase ();

        Cursor cursor = db.rawQuery("select * from caja_estatus_reporte where estatus=3 and num_doc = '"+numDoc+"'",null);

        ArrayList array = new ArrayList ();
        while (cursor.moveToNext ())
        {
            i++;
        }
        return i;
    }
}