package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionDB;
import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;
import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.CrudBDInterna;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class ActivityPaqueteEscaneado extends AppCompatActivity {

    ListView lstElementosEscaneados;
    ImageButton imgButtonEscanear;
    Button btnDescargarInfo;
    TextView lblModoApp;

    Funciones fun = new Funciones ();

    // Esta variable permitirá rellenar el listado con los materiales escaneados
    String[] llenarListViewVacia = {"No existen datos a la fecha "+fun.fecha ()};

    /*ConnectionDB obtendrá parte del host de los Web Services. Por ejemplo si el host fuese http://www.wyr.cl/web_services,
         y la dirección de los servicios web se agrega a parte, como se ve más abajo */
    ConnectionDB c = new ConnectionDB ();

    //llamada a clase ConnectionSQLiteHelper
    ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);

    String modo;
    String modoParaMostrarEnResponse;

    int[] datosDescargados = {0};/*booleano que retorna true en caso de que la descarga de datos se haya producido
        y false en caso de que haya ocurrido algún evento*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_paquete_escaneado);

        //recepción de valor enviado desde el MainActivity.java
        modo = getIntent().getStringExtra("modo");

        modoParaMostrarEnResponse = "";

        if(modo.equals ("4"))
        {
            modoParaMostrarEnResponse = "ENTREGA";
        }
        else
        {
            if(modo.equals ("3"))
            {
                modoParaMostrarEnResponse = "CARGA";
            }
            else
            {
                if(modo.equals ("2"))
                {
                    modoParaMostrarEnResponse = "DESPACHO";
                }
                else
                {
                    if(modo.equals ("1"))
                    {
                        modoParaMostrarEnResponse = "REVISIÓN";
                    }
                }
            }
        }

        imgButtonEscanear = findViewById (R.id.imgButtonEscanear);
        lstElementosEscaneados=findViewById(R.id.lstElementosEscaneados);
        lblModoApp = findViewById (R.id.lblModoApp);
        btnDescargarInfo = findViewById (R.id.btnDescargarInfo);
        btnDescargarInfo.setEnabled (false);

        //rellenar ListView con datos escaneados según el modo de la app
        //llenarListView(modo);

        llenarListViewSQL (modo, fun.fecha ());

        //comparación modo app
        if(modo.equals ("1"))
        {
            lblModoApp.setText (lblModoApp.getText ().toString ()+"REVISIÓN");
            llenarListViewSQL(modo, fun.fecha());
        }
        else
        {
            if(modo.equals ("2"))
            {
                lblModoApp.setText (lblModoApp.getText ().toString ()+"DESPACHO");
                llenarListViewSQL(modo, fun.fecha());
            }
            else
            {
                if(modo.equals ("3"))
                {
                    lblModoApp.setText (lblModoApp.getText ().toString ()+"CARGA");
                    llenarListViewSQL(modo, fun.fecha());
                    btnDescargarInfo.setEnabled (true);
                }
                else
                {
                    lblModoApp.setText (lblModoApp.getText ().toString ()+"ENTREGA");
                    llenarListViewSQL(modo, fun.fecha());
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
                integrador.setBarcodeImageEnabled(false);
                integrador.getCaptureActivity ();
                integrador.initiateScan ();
            }
        });

        //botón de descarga de la información
        btnDescargarInfo.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v)
            {
                descargarDatosTablaCajaEstado();
                descargarDatosTablaCajaEstadoReporte();
            }
        });

    }

    /*Este metodo a diferencia de los demás se desarrolló con la librería Volley de Google, que permite
     conectar el dispositivo Android con los servicios web o web services. */
    private void descargarDatosTablaCajaEstado()//descarga los datos almacenados en la tabla Caja Estado
    {
        Toast.makeText (this, "Descargando codigos de barra...", Toast.LENGTH_SHORT).show ();

        //se indica la URL a la que tendrá que acceder el dispositivo para descargar los datos
        String url = c.host ()+"read/read_caja_estatus_reporte_descarga.php";

        RequestQueue queue = Volley.newRequestQueue(ActivityPaqueteEscaneado.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            //String response: es el que lee o captura la respuesta entregada por el webservice.
            public void onResponse(String response) {
                try
                {
                    if (response.length() == 1)
                    {
                        /*no hay registros o el Web Service no es capaz de retornar resultados*/
                    }
                    else
                    {
                        Thread.sleep(1000);
                        try
                        {
                            ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (getApplicationContext (), "bd_interna_despacho_wyr", null, 1);

                            /*JSONArray se encarga de convertir el arreglo JSON obtenido por el webservice a un
                            dato humanamente legible y entendible */

                            CrudBDInterna bdInterna = new CrudBDInterna();

                            JSONArray arr = new JSONArray(response);
                            for (int i = 0; i < arr.length(); i++)
                            {
                                System.out.println ("Entra al for para descargar datos");
                                bdInterna.registrarCajaEstado (conn,
                                        arr.getJSONObject(i).getString("cod_barra_caja"),
                                        arr.getJSONObject(i).getString("estatus"));
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            /*en caso de ocurrir un evento de error, se ejecutará el siguiente void o método
             * mostrando el mensaje NO EXISTE CONEXION...*/
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText (ActivityPaqueteEscaneado.this, "NO EXISTE CONEXIÓN CON EL SERVIDOR", Toast.LENGTH_SHORT).show ();
            }
        });
        queue.add(stringRequest);

    }

    private void descargarDatosTablaCajaEstadoReporte()//descarga los datos almacenados en la tabla Caja Estado
    {
        final int[] datosDescargados = {0}; /*contará la cantidad de vueltas que dará el for al buscar los datos ubicado
        debajo de la llamada al objeto JSONarray*/
        Toast.makeText (this, "Descargando estados de cajas...", Toast.LENGTH_SHORT).show ();

        //se indica la URL a la que tendrá que acceder el dispositivo para descargar los datos
        String url = c.host ()+"read/read_caja_estatus_reporte_descarga.php";

        RequestQueue queue = Volley.newRequestQueue(ActivityPaqueteEscaneado.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            //String response: es el que lee o captura la respuesta entregada por el webservice.
            public void onResponse(String response) {
                try
                {
                    if (response.length() == 1)
                    {
                        /*no hay registros o el Web Service no es capaz de retornar resultados*/
                    }
                    else
                    {
                        Thread.sleep(1000);
                        try
                        {
                            ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (getApplicationContext (), "bd_interna_despacho_wyr", null, 1);

                            /*JSONArray se encarga de convertir el arreglo JSON obtenido por el webservice a un
                            dato humanamente legible y entendible */

                            CrudBDInterna bdInterna = new CrudBDInterna();

                            JSONArray arr = new JSONArray(response);
                            for (int i = 0; i < arr.length(); i++)
                            {
                                datosDescargados[0]++;
                                System.out.println ("Entra al for para descargar datos");
                                bdInterna.registrarCajaEstadoReporte (conn,
                                        arr.getJSONObject(i).getString("cod_barra_caja"),
                                        arr.getJSONObject(i).getString("num_doc"),
                                        arr.getJSONObject(i).getString("fecha"),
                                        arr.getJSONObject(i).getString("hora"),
                                        arr.getJSONObject(i).getString("estatus"),
                                        arr.getJSONObject(i).getString("comentario"),
                                        arr.getJSONObject(i).getString("id_dispositivo"));
                            }
                            if(datosDescargados[0]!=0)
                            {
                                Toast.makeText (ActivityPaqueteEscaneado.this, "MODO OFFLINE ACTIVADO. Ahora puede continuar con el proceso de entrega sin conexión a la red", Toast.LENGTH_SHORT).show ();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            /*en caso de ocurrir un evento de error, se ejecutará el siguiente void o método
             * mostrando el mensaje NO EXISTE CONEXION...*/
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println ("Error volley: "+error);
                Toast.makeText (ActivityPaqueteEscaneado.this, "NO EXISTE CONEXIÓN CON EL SERVIDOR", Toast.LENGTH_SHORT).show ();
            }
        });
        queue.add(stringRequest);

    }

    //lectura de código de barra de revisión
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*Activa la cámara para escanear los códigos de barras. Si se ejecuta la sentencia result.getContents(), se
        * obtiene el código de barra resultante del escaneo*/
        IntentResult result = IntentIntegrator.parseActivityResult (requestCode, resultCode, data);

        //obtención del código de barra resultante del escaneo
        String cod_barra = result.getContents ();

        llenarListViewSQL (modo,fun.fecha ());
        if (result != null)
        {
            if (result.getContents () == null)
            {
                Toast.makeText (this, "Lectura cancelada", Toast.LENGTH_LONG).show ();
            }
            else
            {
                if (modo.equals ("1"))
                {
                    accionRegistroCodigo (modo,cod_barra);
                }
                else
                {
                    if(modo.equals ("2"))
                    {
                        accionRegistroCodigo (modo,cod_barra);
                    }
                    else
                    {
                        if(modo.equals ("3"))
                        {
                            accionRegistroCodigo (modo,cod_barra);
                        }
                        else
                        {
                            if(modo.equals ("4"))
                            {
                                accionRegistroCodigo (modo,cod_barra);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            super.onActivityResult (requestCode, resultCode, data);
            llenarListViewSQL (modo,fun.fecha ());
        }
    }


    private void accionRegistroCodigo(String modo, String cod_barra)
    {
        boolean paso_anterior = detectarPasoAnteriorSQL (cod_barra,modo);
        if(paso_anterior == false)
        {
            if(modo.equals ("1"))
            {
                insercion (modo,cod_barra);
            }
            else
            {
                fun.dialogoAlerta (ActivityPaqueteEscaneado.this,"¡Aviso!", "Esta caja no ha pasado por el proceso anterior al de "+modoParaMostrarEnResponse);
            }
        }
        else
        {
            if(modo.equals ("2"))
            {
                insercion(modo, cod_barra);
                actualizarEstatusCajaCodBarraBDInterna(cod_barra,conn,modo);
                actualizarEstadoCajaEstadoSQL(cod_barra,modo);
            }
            if(modo.equals ("3"))
            {
                insercion(modo, cod_barra);
                actualizarEstatusCajaCodBarraBDInterna(cod_barra,conn,modo);
                actualizarEstadoCajaEstadoSQL(cod_barra,modo);
            }
            if(modo.equals ("4"))
            {
                insercion (modo, cod_barra);
                actualizarEstatusCajaCodBarraBDInterna (cod_barra,conn,modo);
            }
        }
    }

    //Modificación interna del estado de la caja en la tabla caja_estado
    private void actualizarEstatusCajaCodBarraBDInterna(String cod_barra, ConnectionSQLiteHelper conn, String estatus)
    {
        //llamada al StringTokenizer, que separa la cadena de caracteres obtenida del código de barra. Lo separará por cada "-" que tenga la cadena
        String num_doc = fun.tokenizer (cod_barra, "-");

        //Obtener la base de datos en modo editable
        SQLiteDatabase db = conn.getWritableDatabase();

        //update de la tabla caja_estado
        db.execSQL ("update caja_estado set estatus = '"+estatus+"' where cod_barra_caja = '"+cod_barra+"'");
    }

    /*Este metodo detecta si la caja ya pasó por el proceso anterior en la BD remota. Por ejemplo
     * si se escanéa en modo de carga, la aplicación revisará si la caja pasó por el proceso de despacho. En caso de
     * cumplirse la condición, retornará true, y en caso contrario, false.*/
    private boolean detectarPasoAnteriorSQL(String cod_barra, String status)//detecta si la caja ya ha sido despachada/revisada/entregada/cargada, evitando así doble pickeo
    {
        //Obtener la base de datos en modo editable
        SQLiteDatabase db = conn.getWritableDatabase();

        int st = Integer.parseInt (status);//convierte la variable String status en tipo numérico

        Cursor cursor = db.rawQuery("select * from caja_estado where cod_barra_caja ='"+cod_barra+"' and estatus = '"+(st-1)+"'",null);

        //Leerá si la consulta es correcta o entrega resultados
        while (cursor.moveToNext ())
        {
            System.out.println ("entra al cursor.moveToNext()");
            return true;
        }

        return false;
    }

    //llena el listview con la información que arroje la siguiente consulta a la base de datos interna
    private void llenarListViewBDInterna(String status)
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
    //llena el listview con la información que arroje la siguiente consulta a la base de datos interna
    private void llenarListViewSQL(String status, String fecha)
    {
        System.out.println ("llama al metodo SQL");

        //progressDialog
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(ActivityPaqueteEscaneado.this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        String url = c.host()+"read/read_caja_estatus_reporte.php?estatus="+status+"&fecha="+fecha;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    System.out.println ("entra al try");
                    progressDialog.show();
                    ArrayAdapter<String> arrayListaCajasEscaneadas;
                    if(response.length()==1)
                    {
                        System.out.println ("Entra al response.length()==1");
                        Thread.sleep(1000);
                        progressDialog.dismiss();
                        arrayListaCajasEscaneadas = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, llenarListViewVacia);
                        lstElementosEscaneados.setAdapter(arrayListaCajasEscaneadas);
                    }
                    else
                    {
                        System.out.println ("entra al else");
                        Thread.sleep(1000);
                        progressDialog.dismiss();
                        try
                        {
                            System.out.println ("entra al try");
                            JSONArray arr = new JSONArray(response);
                            llenarListViewVacia = new String[arr.length()];
                            for(int i = 0; i < arr.length(); i++)
                            {
                                System.out.println ("rellena el listview con los datos");
                                llenarListViewVacia[i] = "("+(i+1)+") \tCodigo: "+arr.getJSONObject(i).getString("cod_barra_caja")+"\n\t\t\tHora: "+arr.getJSONObject(i).getString("hora");
                            }
                            arrayListaCajasEscaneadas = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, llenarListViewVacia);
                            lstElementosEscaneados.setAdapter(arrayListaCajasEscaneadas);
                            progressDialog.dismiss ();
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
               progressDialog.dismiss();
            }
        });
        queue.add(stringRequest);

        ArrayAdapter<String> arrayOpciones = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,llenarListViewVacia);
        lstElementosEscaneados.setAdapter (arrayOpciones);
    }

    //actualizar estado de la caja en la tabla caja_estado
    private void actualizarEstadoCajaEstadoSQL(String codBarra, String estatus) {
        HttpURLConnection conn1, conn2;// conecta con el servicio que registra los datos en la tabla caja_estado

        BufferedReader reader, reader2;
        String line, line2;
        StringBuffer responseContent = new StringBuffer ();

        ConnectionDB db = new ConnectionDB ();
        try {
            URL url1 = new URL (db.host () + "update/update_caja.php?" +
                    "cod_barra=" + codBarra +
                    "&estatus=" + estatus);


            //URL1
            conn1 = (HttpURLConnection) url1.openConnection ();
            conn1.setRequestMethod ("GET");
            conn1.setConnectTimeout (10000);
            conn1.setReadTimeout (10000);


        } catch (ProtocolException e) {
            e.printStackTrace ();
        } catch (MalformedURLException e) {
            e.printStackTrace ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }


    private void insercion(String modo_numero, String cod_barra)
    {
        String num_doc = fun.tokenizer (cod_barra, "-");
        SQLiteDatabase db = conn.getWritableDatabase ();
        //acciones registro tabla caja_estado
        ContentValues insert_caja_estado = new ContentValues ();

        //acciones registro tabla caja_estatus_reporte
        ContentValues insert_caja_estatus_reporte = new ContentValues ();
        //acciones de registro en modo REVISIÓN
        insert_caja_estado.put ("cod_barra_caja", cod_barra);
        insert_caja_estado.put ("estatus", 1);
        //inserción
        db.insert ("caja_estado", null, insert_caja_estado);

        insert_caja_estatus_reporte.put ("cod_barra_caja", cod_barra);
        insert_caja_estatus_reporte.put ("num_doc", num_doc);
        insert_caja_estatus_reporte.put ("fecha", fun.fecha ());
        insert_caja_estatus_reporte.put ("hora", fun.hora ());
        insert_caja_estatus_reporte.put ("estatus", modo_numero);
        insert_caja_estatus_reporte.put ("comentario", "s/c");
        insert_caja_estatus_reporte.put ("id_dispositivo", fun.obtenerAndroidID (this));
        db.insert ("caja_estatus_reporte", null, insert_caja_estatus_reporte);
    }
}
