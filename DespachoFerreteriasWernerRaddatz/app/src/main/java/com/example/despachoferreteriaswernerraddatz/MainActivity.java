package com.example.despachoferreteriaswernerraddatz;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionDB;
import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;
import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.Sincronizar;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button btnRevision, btnDespacho, btnCarga, btnEntrega, btnNomina;
    Funciones fun = new Funciones ();

    Sincronizar sin = new Sincronizar ();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        //inicialización de objetos
        btnRevision = findViewById (R.id.btnRevision);
        btnNomina = findViewById (R.id.btnNomina);
        btnDespacho = findViewById (R.id.btnDespacho);
        btnCarga = findViewById (R.id.btnCarga);
        btnEntrega = findViewById (R.id.btnEntrega);

        //
        tareaAsincrona ();
        //llamada a la activity ActivityPrimerUso
        Intent intent = new Intent (this, ActivityPrimerUso.class);

        //llamada a la clase ConnectionSQLiteHelper.java
        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase ();
        //Detectar primer uso de la aplicación
        boolean primer_uso = detectar_id_dispositivo (conn);

        /*Si se encuentra o detecta que la aplicación ya fue utilizada por primera vez, los botones
        * de la pantalla principal se activarán*/
        if (primer_uso == true)
        {
            activar_botones ();
        }
        /*Si no se detecta un primer uso, la app arrojará la pantalla de primer uso, donde se tendrá que
        * registrar el empleado, otorgando un ID (preferentemente el del GESCOM), nombre y apellido*/
        else
        {
            Toast.makeText (this, "Comprobando primer uso...", Toast.LENGTH_SHORT).show ();
            desactivar_botones ();
            //llamada a activity primer uso
            startActivity (intent);
        }

        /*String modo obtiene el modo en el que se encuentra la aplicación. Por ejemplo, si presiona el
        * botón REVISIÓN, String modo capturará el número 1, y la app realizará las acciones relacionadas
        * al modo 1 o modo de REVISIÓN.
        *
        * Listado de modos y sus significados
        *
        * 1: REVISIÓN
        * 2: DESPACHO
        * 3: CARGA
        * 4: ENTREGA
        *
        * */
        final String[] modo = {""};

        //acciones botón revisión
        btnRevision.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                modo[0] = "1";
                /*La app entrará en modo 1, que sería modo Revisión*/
                intent.putExtra ("modo", modo[0]);
                startActivity (intent);
            }
        });


        btnDespacho.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                /*La app entrará en modo 2, que sería modo Despacho*/
                modo[0] = "2";
                intent.putExtra ("modo", modo[0]);
                startActivity (intent);
            }
        });

        btnCarga.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                modo[0] = "3";
                /*La app entrará en modo 3, que sería modo Carga*/
                intent.putExtra ("modo", modo[0]);//
                startActivity (intent);
            }
        });
        btnEntrega.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                modo[0] = "4";
                /*La app entrará en modo 4, que sería modo Entrega*/
                intent.putExtra ("modo", modo[0]);
                startActivity (intent);
            }
        });


        //llamada a activity Nómina
        intent = new Intent (this, ActivityNomina.class);

        Intent finalIntent = intent;
        //acciones botón nómina
        btnNomina.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //llamada a ventana ActivityNomina
                startActivity (finalIntent);
            }
        });


    }

    private boolean detectar_id_dispositivo(ConnectionSQLiteHelper conn) {
        SQLiteDatabase db = conn.getWritableDatabase ();
        Cursor cursor = db.rawQuery ("select * from dispositivo where id_dispositivo ='" + fun.obtenerAndroidID (this) + "'", null);
        if (cursor.moveToFirst ())
        {
            return true;
        }
        return false;
    }

    private void desactivar_botones() {
        btnNomina.setEnabled (false);
        btnEntrega.setEnabled (false);
        btnCarga.setEnabled (false);
        btnRevision.setEnabled (false);
        btnDespacho.setEnabled (false);
    }

    private void activar_botones() {
        btnNomina.setEnabled (true);
        btnEntrega.setEnabled (true);
        btnCarga.setEnabled (true);
        btnRevision.setEnabled (true);
        btnDespacho.setEnabled (true);
    }


    //Este método permite ejecutar en segundo plano
    private void tareaAsincrona() {
        Timer timer = new Timer ("SincronizarBD");


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
            //
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