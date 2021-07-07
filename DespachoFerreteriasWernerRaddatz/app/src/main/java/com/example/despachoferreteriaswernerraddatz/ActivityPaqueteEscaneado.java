package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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


    String modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_paquete_escaneado);

        //recepción de valor enviado desde el MainActivity.java
        Bundle bundle = getIntent().getExtras();
        modo = getIntent().getStringExtra("modo");
        //Creación de objeto ArrayList;
        ArrayList telefonos=new ArrayList();
        ArrayAdapter adaptador1=new ArrayAdapter(this,android.R.layout.simple_list_item_1,telefonos);

        imgButtonEscanear = findViewById (R.id.imgButtonEscanear);
        lstElementosEscaneados=findViewById(R.id.lstElementosEscaneados);
        lstElementosEscaneados.setAdapter(adaptador1);
        lblModoApp = findViewById (R.id.lblModoApp);

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
            public void onClick(View v)
            {
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

        if (result != null)
        {
            if (result.getContents () == null)
            {
                Toast.makeText (this, "Lectura cancelada", Toast.LENGTH_LONG).show ();
            }
            else
            {
                //llamada a clase ConnectionSQLiteHelper
                ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);



                //comparar si la caja está pickeada
                boolean picking = detectar_caja (conn,result.getContents ());
                if(picking)
                {
                    fun.dialogoAlerta (this,"¡Aviso!","Esta caja ya está revisada");
                }
                else
                {
                    registro_bd_interna (result.getContents (),conn);
                }
            }
        }
        else
        {
            super.onActivityResult (requestCode, resultCode, data);
        }
    }

    //registro interno en la base de datos
    private void registro_bd_interna(String cod_barra, ConnectionSQLiteHelper conn)
    {
        int modo_numero = 0;
        SQLiteDatabase db = conn.getWritableDatabase();

        //acciones registro tabla caja_estado
        ContentValues insert_caja_estado = new ContentValues();

        //acciones registro tabla caja_estatus_reporte
        ContentValues insert_caja_estatus_reporte = new ContentValues ();
        if(modo.equals ("1"))
        {
            modo_numero = 1;

            //acciones de registro en modo REVISIÓN
            insert_caja_estado.put ("cod_barra_caja",cod_barra);
            insert_caja_estado.put ("estatus",1);
            //inserción
            db.insert("caja_estado",null,insert_caja_estado);

            insert_caja_estatus_reporte.put ("cod_barra_caja",cod_barra);
            insert_caja_estatus_reporte.put ("fecha",fun.fecha());
            insert_caja_estatus_reporte.put ("hora",fun.hora ());
            insert_caja_estatus_reporte.put ("estatus","1");
            insert_caja_estatus_reporte.put ("comentario","s/c");
            insert_caja_estatus_reporte.put ("id_dispositivo",fun.obtenerAndroidID (this));
            db.insert ("caja_estatus_reporte",null,insert_caja_estatus_reporte);

            

            db.close ();

        }
        else
        {
            if(modo.equals ("2"))
            {
                //
            }
            else
            {
                if(modo.equals ("3"))
                {
                    //
                }
                else
                {
                    //
                }
            }
        }
    }

    private boolean detectar_caja(ConnectionSQLiteHelper conn, String cod_barra)
    {
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery ("select * from caja_estado where cod_barra_caja ='"+cod_barra+"'",null);
        if(cursor.moveToFirst ())
        {
            return true;
        }
        return false;
    }
/*select * from caja_estado ce join caja_estatus_reporte cer on ce.cod_barra_caja = cer.cod_barra_caja join dispositivo dis on cer.id_dispositivo = dis.id_dispositivo join empleado empl on empl.id_empleado =dis.empleado_id_empleado where dis.id_dispositivo = "imei12345" and cer.fecha="10/12/2020"*/

}