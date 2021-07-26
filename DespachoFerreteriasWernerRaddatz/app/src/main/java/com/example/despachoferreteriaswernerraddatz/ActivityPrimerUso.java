package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TaskInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionDB;
import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;
import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.CrudBDExterna;
import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.CrudBDInterna;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityPrimerUso extends AppCompatActivity {


    //creación de objetos
    EditText edtCodigo, edtNombre, edtApellido;
    Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primer_uso);

        //inicialización de objetos
        edtCodigo = findViewById (R.id.edtCodigo);
        edtNombre = findViewById (R.id.edtNombre);
        edtApellido = findViewById (R.id.edtApellido);
        btnRegistrar = findViewById (R.id.btnRegistrarDispositivo);


        //llamada a clase Funciones
        Funciones fun = new Funciones();

        //llamada a creación de base de datos. En este caso la BD no se creará, ya que existe.
        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();

        btnRegistrar.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //Strings
                String codigo, nombre, apellido, id_dispositivo, marca_modelo_dispositivo;
                codigo = edtCodigo.getText ().toString ();
                nombre = edtNombre.getText ().toString ();
                apellido = edtApellido.getText ().toString ();
                id_dispositivo = fun.obtenerAndroidID (getApplicationContext ());
                marca_modelo_dispositivo = fun.obtenerMarcaDispositivo ()+" "+fun.obtenerModeloDispositivo ();

                if(codigo.equals ("")||nombre.equals ("")|| apellido.equals (""))
                {
                    fun.dialogoAlerta (ActivityPrimerUso.this, "¡Aviso!","Debe rellenar todos los campos");
                }
                else
                {
                    //llamada a la clase de registros en BD Interna del dispositivo
                    CrudBDInterna crudBDInterna = new CrudBDInterna ();
                    //Registro de datos en la base de datos interna
                    crudBDInterna.registrarEmpleado (db,getApplicationContext (),codigo,nombre,apellido,id_dispositivo,marca_modelo_dispositivo);

                    //llamada a la clase de registros en la BD remota de la empresa
                    registrarEmpleado(id_dispositivo,marca_modelo_dispositivo,codigo,nombre,apellido);

                    //llamada a la activity
                    Intent intent = new Intent(getApplicationContext (), MainActivity.class);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
    public void registrarEmpleado(String id_dispositivo, String marca_modelo, String id_empleado, String nombre, String apellido)//método que sirve para buscar e imprimir los datos de los clientes
    {
        ConnectionDB db = new ConnectionDB ();

        //Esta URL enviará los datos al servicio web create_empleado.php
        String url = db.host () + "create/create_empleado.php?id_dispositivo="+id_dispositivo+
                "&marca_modelo="+marca_modelo+
                "&id_empleado="+id_empleado+
                "&nombre="+nombre+
                "&apellido="+apellido;
        RequestQueue queue = Volley.newRequestQueue (this);
        StringRequest stringRequest = new StringRequest (Request.Method.GET, url, new Response.Listener<String> () {
            @Override
            public void onResponse(String response) {
                if (response.length () == 1)
                {
                    System.out.println ("Respuesta reg. empleado"+response);
                }
                else
                {
                    System.out.println ("Respuesta reg. empleado"+response);
                }
            }
        }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText (getApplicationContext (), "Tiempo de espera agotado. De igual manera, el registro se hizo de manera local", Toast.LENGTH_LONG).show ();
                System.out.println ("Error de Volley: "+error);
            }
        });
        queue.add (stringRequest);
    }
    public void dialogoAlerta(String titulo, String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void tareaAsincrona() {

        Timer timer = new Timer ("SincronizarBD");

        final int[] i = {0};

        TimerTask tarea = new TimerTask () {
            @Override
            public void run() {
                leerCajaEstado ();
            }
        };

        timer.schedule (tarea, 1000, 2000);
    }


    /*lectura de datos de la base de datos interna. Tabla caja_estado */
    private void leerCajaEstado() {
        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);
        SQLiteDatabase db = conn.getReadableDatabase ();

        Cursor cursor = db.rawQuery ("select * from caja_estado ce join caja_estatus_reporte cer on ce.cod_barra_caja = cer.cod_barra_caja join dispositivo dis on dis.id_dispositivo = cer.id_dispositivo", null);

        //Se leerá la base de datos interna
        while (cursor.moveToNext ()) {
            // se envían los datos recogidos por la BD interna al método que carga los datos a la BD remota
            actualizarCajaEstadoMySQL (
                    cursor.getString (0),/*cod_barra1*/
                    cursor.getString (1),/*estatus1*/
                    cursor.getString (3),/*num:doc1*/
                    cursor.getString (4),/*fecha1*/
                    cursor.getString (5),/*hora1*/
                    cursor.getString (6),/* estatus2 */
                    cursor.getString (7),/*comentario*/
                    cursor.getString (8)/*id_dispositiv1o*/);

            System.out.println ("c: "+cursor.getString (0)+
                    " e: "+cursor.getString (1) +
                    " f: "+ cursor.getString (4));
        }

        cursor.close ();
        db.close ();
    }

    //actualizar bd remota
    private void actualizarCajaEstadoMySQL(String codBarra1,
                                           String estatus1,
                                           String numDoc,
                                           String fecha,
                                           String hora,
                                           String estatus2,
                                           String comentario,
                                           String id_dispositivo1) {

        HttpURLConnection conn1, conn2;// conecta con el servicio que registra los datos en la tabla caja_estado

        BufferedReader reader, reader2;
        String line, line2;
        StringBuffer responseContent = new StringBuffer();

        ConnectionDB db = new ConnectionDB ();
        try {
            URL url1 = new URL(db.host () + "update/update_caja.php?" +
                    "cod_barra=" + codBarra1 +
                    "&estatus=" + estatus1);


            //URL1
            conn1 = (HttpURLConnection) url1.openConnection ();
            conn1.setRequestMethod ("GET");
            conn1.setConnectTimeout (10000);
            conn1.setReadTimeout (10000);

            //registro tabla caja_registro
            int status1 = conn1.getResponseCode ();
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

            //URL2
            URL url2 = new URL (db.host () + "create/create_caja.php?" +
                    "cod_barra=" + codBarra1 +
                    "&estatus=" + estatus2 +
                    "&num_doc=" + numDoc +
                    "&fecha=" + fecha +
                    "&hora=" + hora +
                    "&comentario=" + comentario +
                    "&id_dispositivo=" + id_dispositivo1);
            conn2 = (HttpURLConnection) url2.openConnection ();
            conn2.setRequestMethod ("GET");
            conn2.setConnectTimeout (10000);
            conn2.setReadTimeout (10000);

            int status2 = conn1.getResponseCode ();
            if(status2>500)
            {
                reader2 = new BufferedReader (new InputStreamReader (conn2.getErrorStream ()));
                while((line = reader2.readLine ())!=null)
                {
                    responseContent.append (line);
                }
                reader2.close ();
            }
            else
            {
                reader2 = new BufferedReader (new InputStreamReader (conn2.getInputStream ()));
                while((line2 = reader2.readLine ())!=null)
                {
                    responseContent.append(line2);
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
    }
}