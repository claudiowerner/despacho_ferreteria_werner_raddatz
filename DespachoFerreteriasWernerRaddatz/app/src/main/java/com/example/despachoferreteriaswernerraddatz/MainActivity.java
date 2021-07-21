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
        if (primer_uso == true) {
            activar_botones ();
        } else {
            Toast.makeText (this, "Comprobando primer uso...", Toast.LENGTH_SHORT).show ();
            desactivar_botones ();
            //llamada a activity primer uso
            startActivity (intent);
        }

        final String[] modo = {""};

        //acciones botón revisión
        btnRevision.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                modo[0] = "1";
                intent.putExtra ("modo", modo[0]);//La app entrará en modo 1, que sería modo Revisión
                startActivity (intent);
            }
        });


        Intent finalIntent1 = intent;
        btnDespacho.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                modo[0] = "2";
                intent.putExtra ("modo", modo[0]);//La app entrará en modo 2, que sería modo Despacho
                startActivity (intent);
            }
        });

        btnCarga.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                modo[0] = "3";
                intent.putExtra ("modo", modo[0]);//La app entrará en modo 3, que sería modo Carga
                startActivity (intent);
            }
        });
        btnEntrega.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                modo[0] = "4";
                intent.putExtra ("modo", modo[0]);//La app entrará en modo 4, que sería modo Entrega
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
        if (cursor.moveToFirst ()) {
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

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver () {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo ();
            onNetworkChange (ni);
        }
    };

    @Override
    public void onResume() {
        super.onResume ();
        registerReceiver (networkStateReceiver, new IntentFilter (android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        unregisterReceiver (networkStateReceiver);
        super.onPause ();
    }

    private void onNetworkChange(NetworkInfo networkInfo) {
        if (networkInfo != null) {
            if (networkInfo.getState () == NetworkInfo.State.CONNECTED) {
                //Toast.makeText (this, "CONECTADO: El almacenamiento remoto está activado.", Toast.LENGTH_SHORT).show ();
            }
            if (networkInfo.getState () == NetworkInfo.State.SUSPENDED) {
                //Toast.makeText (this, "Conexión suspendida", Toast.LENGTH_SHORT).show ();
            }
            if (networkInfo.getState () == NetworkInfo.State.CONNECTING) {
                //Toast.makeText (this, "Conectando", Toast.LENGTH_SHORT).show ();
            }
            if (networkInfo.getState () == NetworkInfo.State.DISCONNECTING) {
                //Toast.makeText (this, "Desconectando", Toast.LENGTH_SHORT).show ();
            }
            if (networkInfo.getState () == NetworkInfo.State.UNKNOWN) {
                //Toast.makeText (this, "Estado de conexión desconocido", Toast.LENGTH_SHORT).show ();
            }
            if (networkInfo.getState () == NetworkInfo.State.DISCONNECTING) {
                //Toast.makeText (this, "Desconectando", Toast.LENGTH_SHORT).show ();
            }
        }
    }

    //Este método permite ejecutar en segundo plano
    private void tareaAsincrona() {
        Toast.makeText (this, "Tarea asíncrona iniciada", Toast.LENGTH_SHORT).show ();
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
        int id = 0;

        //Se leerá la base de datos interna
        while (cursor.moveToNext ()) {
            // se envían los datos recogidos por la BD interna al método que carga los datos a la BD remota
            actualizarCajaEstadoMySQL (
                    cursor.getString (0),/*cod_barra1*/
                    cursor.getString (1),/*estatus1*/
                    cursor.getString (2),/*cod_barra2*/
                    cursor.getString (3),/*num:doc1*/
                    cursor.getString (4),/*fecha1*/
                    cursor.getString (5),/*hora1*/
                    cursor.getString (6),/* estatus2 */
                    cursor.getString (7),/*comentario*/
                    cursor.getString (8),/*id_dispositiv1o*/
                    cursor.getString (9),/*id Dispositivo2*/
                    cursor.getString (10)/*marcamodelo2*/);
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
                                           String codBarra2,
                                           String numDoc,
                                           String fecha,
                                           String hora,
                                           String estatus2,
                                           String comentario,
                                           String id_dispositivo1,
                                           String id_dispositivo2,
                                           String marcaModelo) {

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