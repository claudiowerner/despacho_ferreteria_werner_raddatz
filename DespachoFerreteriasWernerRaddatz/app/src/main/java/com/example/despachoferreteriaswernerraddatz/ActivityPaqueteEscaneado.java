package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionDB;
import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ActivityPaqueteEscaneado extends AppCompatActivity {

    ListView lstElementosEscaneados;
    ImageButton imgButtonEscanear;
    TextView lblModoApp;

    Funciones fun = new Funciones ();

    //llamada a clase ConnectionSQLiteHelper
    ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);

    String modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_paquete_escaneado);

        //recepción de valor enviado desde el MainActivity.java
        Bundle bundle = getIntent().getExtras();
        modo = getIntent().getStringExtra("modo");

        imgButtonEscanear = findViewById (R.id.imgButtonEscanear);
        lstElementosEscaneados=findViewById(R.id.lstElementosEscaneados);
        lblModoApp = findViewById (R.id.lblModoApp);

        //rellenar ListView con datos escaneados según el modo de la app
        llenarListView (modo);

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
                integrador.setOrientationLocked (false);
                integrador.setPrompt ("LECTOR CÓDIGO DE BARRA");
                integrador.setCameraId (0);
                integrador.setBeepEnabled (true);
                integrador.setBarcodeImageEnabled(false);
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
                registro_bd_interna (result.getContents (),conn);
                llenarListView (modo);
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
        boolean caja_repetida, paso_anterior, formato;
        int status = Integer.parseInt (modo);
        formato = fun.validarFormatoCodigoBarra (cod_barra);
        //Si el booleano caja_repetida = true, es porque la caja ya está procesada y registrada en la BD*
        if(formato==true)
        {
            if(modo.equals ("1"))
            {
                caja_repetida = detectar_caja_repetida (conn,cod_barra,modo);
                if(caja_repetida)
                {
                    fun.dialogoAlerta (this, "¡Aviso!","La caja "+cod_barra+" ya está revisada");
                }
                else
                {
                    insercion (1,cod_barra);
                }
            }
            else
            {
                if(modo.equals ("2"))
                {
                    caja_repetida = detectar_caja_repetida (conn,cod_barra,modo);
                    if(caja_repetida)
                    {
                        fun.dialogoAlerta (this, "¡Aviso!","La caja "+cod_barra+" ya está despachada");
                    }
                    else
                    {
                        paso_anterior = detectar_paso_anterior (conn,cod_barra,1);
                        if(paso_anterior==false)
                        {
                            fun.dialogoAlerta (this,"¡Aviso!", "Esta caja no ha pasado por el proceso de revisión");
                        }
                        else
                        {
                            insercion (2,cod_barra);
                            actualizarEstatusCajaCodBarra (cod_barra,conn,modo);
                        }
                    }
                }
                else
                {
                    if(modo.equals ("3"))
                    {
                        caja_repetida = detectar_caja_repetida (conn,cod_barra,modo);
                        if(caja_repetida)
                        {
                            fun.dialogoAlerta (this, "¡Aviso!","La caja "+cod_barra+" ya está cargada en el camión");
                        }
                        else
                        {
                            paso_anterior = detectar_paso_anterior (conn,cod_barra,2);
                            if(paso_anterior==false)
                            {
                                fun.dialogoAlerta (this,"¡Aviso!", "Esta caja no ha pasado por el proceso de despacho");
                            }
                            else
                            {
                                insercion (3,cod_barra);
                                actualizarEstatusCajaCodBarra (cod_barra,conn,modo);
                            }
                        }
                    }
                    else
                    {
                        caja_repetida = detectar_caja_repetida (conn,cod_barra,modo);
                        if(caja_repetida)
                        {
                            fun.dialogoAlerta (this, "¡Aviso!","La caja "+cod_barra+" ya está entregada");
                        }
                        else
                        {
                            paso_anterior = detectar_paso_anterior (conn,cod_barra,3);
                            if(paso_anterior==false)
                            {
                                fun.dialogoAlerta (this,"¡Aviso!", "Esta caja no ha pasado por el proceso de carga");
                            }
                            else
                            {
                                insercion (4,cod_barra);
                                actualizarEstatusCajaCodBarra (cod_barra,conn,modo);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            //aviso que aparece cuando no se cumple con el formato de codigo de barra
            fun.dialogoAlerta (this, "Error","El formato de código de barra obtenido no coincide con la\n" +
                    "siguiente estructura: \nXXX-DOC000000-000\n" +
                    "Dato obtenido: "+cod_barra);
        }
    }
    //registro interno en la base de datos
    private void actualizarEstatusCajaCodBarra(String cod_barra, ConnectionSQLiteHelper conn, String estatus)
    {
        //llamada al StringTokenizer, que separa la cadena de caracteres obtenida del código de barra. Lo separará por cada "-" que tenga la cadena
        String num_doc = fun.tokenizer (cod_barra, "-");

        //Obtener la base de datos en modo editable
        SQLiteDatabase db = conn.getWritableDatabase();

        //update de la tabla caja_estado
        db.execSQL ("update caja_estado set estatus = '"+estatus+"' where cod_barra_caja = '"+cod_barra+"'");
    }

    private boolean detectar_caja_repetida(ConnectionSQLiteHelper conn, String cod_barra, String estatus)//detecta si la caja ya ha sido despachada/revisada/entregada/cargada, evitando así doble pickeo
    {
        //llamada al método que consulta en la BD remota
        //boolean bd_repetida = detectarCajaRepetidaMYSQL (cod_barra,estatus);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery ("select * from caja_estatus_reporte where cod_barra_caja ='"+cod_barra+"' and estatus = "+estatus,null);
        if(cursor.moveToFirst ())
        {
            return true;
        }
        return false;
    }

    private boolean detectar_paso_anterior(ConnectionSQLiteHelper conn, String cod_barra, int estatus)//detecta si la caja ya pasó por el paso anterior, es decir, si la caja
    {
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery ("select * from caja_estatus_reporte where cod_barra_caja ='"+cod_barra+"' and estatus = "+estatus,null);
        while(cursor.moveToNext ())
        {
            return true;
        }
        return false;
    }

    //llena el listview con la información que arroje la siguiente consulta a la base de datos
    private void llenarListView(String status)
    {
        SQLiteDatabase db = conn.getReadableDatabase ();

        Cursor cursor = db.rawQuery("select * from caja_estatus_reporte where estatus = "+status+" and fecha = '"+fun.fecha ()+"' order by hora desc",null);

        ArrayList array = new ArrayList ();

        int id = 0;

        //String que obtendrá el número de factura
        while (cursor.moveToNext ())
        {
            id++;
            array.add("("+id+") \tCodigo: "+cursor.getString (0)+"\n\t\t\tHora: "+cursor.getString (3));
        }
        ArrayAdapter<String> arrayOpciones = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,array);
        lstElementosEscaneados.setAdapter (arrayOpciones);
    }

    private void insercion(int modo_numero, String cod_barra)
    {
        String num_doc = fun.tokenizer (cod_barra, "-");

        SQLiteDatabase db = conn.getWritableDatabase();

        //acciones registro tabla caja_estado
        ContentValues insert_caja_estado = new ContentValues();

        //acciones registro tabla caja_estatus_reporte
        ContentValues insert_caja_estatus_reporte = new ContentValues ();
        //acciones de registro en modo REVISIÓN
        insert_caja_estado.put ("cod_barra_caja",cod_barra);
        insert_caja_estado.put ("estatus",1);
        //inserción
        db.insert("caja_estado",null,insert_caja_estado);

        insert_caja_estatus_reporte.put ("cod_barra_caja",cod_barra);
        insert_caja_estatus_reporte.put ("num_doc",num_doc);
        insert_caja_estatus_reporte.put ("fecha",fun.fecha());
        insert_caja_estatus_reporte.put ("hora",fun.hora ());
        insert_caja_estatus_reporte.put ("estatus",modo_numero);
        insert_caja_estatus_reporte.put ("comentario","s/c");
        insert_caja_estatus_reporte.put ("id_dispositivo",fun.obtenerAndroidID (this));
        db.insert ("caja_estatus_reporte",null,insert_caja_estatus_reporte);
    }
    //este método detecta si la caja ya está registrada en la BD
    private boolean detectarCajaRepetidaMYSQL(String codBarra1, String estatus1)
    {
        HttpURLConnection conn1;// conecta con el servicio que registra los datos en la tabla caja_estado

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();

        /*Connection db obtendrá parte del host de los Web Services. Por ejemplo si el host sería http://www.wyr.cl/web_services,
         y la dirección de los servicios web se agrega a parte, como se ve más abajo */
        ConnectionDB db = new ConnectionDB ();
        try {
            URL url1 = new URL(db.host () + "update/update_caja.php?" +
                    "cod_barra=" + codBarra1 +
                    "&estatus=" + estatus1);
            conn1 = (HttpURLConnection) url1.openConnection ();
            conn1.setRequestMethod ("GET");
            conn1.setConnectTimeout (10000);
            conn1.setReadTimeout (10000);

            int status1 = conn1.getResponseCode();
            if(status1>500)
            {
                reader = new BufferedReader (new InputStreamReader (conn1.getErrorStream ()));
                while((line = reader.readLine ())!=null)
                {
                    responseContent.append (line);
                }
                reader.close ();
            }
            else
            {
                reader = new BufferedReader (new InputStreamReader (conn1.getInputStream ()));
                while((line = reader.readLine ())!=null)
                {
                    responseContent.append(line);
                }
            }
            conn1.disconnect ();
            System.out.println ("Response content: "+responseContent.toString ());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace ();
        }
        catch (IOException e)
        {
            e.printStackTrace ();
        }
        return false;
    }
}