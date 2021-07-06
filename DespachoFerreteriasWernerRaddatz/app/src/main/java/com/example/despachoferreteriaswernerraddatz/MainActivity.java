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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button btnRevision, btnDespacho, btnCarga, btnEntrega, btnNomina, btnCrearViaje, btnModificarViaje;
    Funciones fun = new Funciones ();

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

        //llamada a la activity ActivityPrimerUso
        Intent intent = new Intent (this, ActivityPrimerUso.class);

        //llamada a la clase ConnectionSQLiteHelper.java
        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);

        //Detectar primer uso de la aplicación
        boolean primer_uso = detectar_id_dispositivo (conn);
        if(primer_uso==true)
        {
            Toast.makeText (this, "Verdadero", Toast.LENGTH_SHORT).show ();
            activar_botones ();
        }
        else
        {
            Toast.makeText (this, "Comprobando primer uso...", Toast.LENGTH_SHORT).show ();
            desactivar_botones ();
            //llamada a activity primer uso
            startActivity(intent);
        }

        Toast.makeText (this, "IMEI: " + fun.obtenerAndroidID (this), Toast.LENGTH_LONG).show ();

        //acciones botón revisión
        btnRevision.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                intent.putExtra("modo",1);//La app entrará en modo 1, que sería modo Revisión
                startActivity (intent);
            }
        });



        Intent finalIntent1 = intent;
        btnDespacho.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity (finalIntent1);
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




    private void disp_conectado() {
        Toast.makeText (this, "CONECTADO: El almacenamiento remoto está activado.", Toast.LENGTH_SHORT).show ();
        //llamada a la clase ConnectionSQLiteHelper.java

        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);
        /* DESCRIPCION LLAMADA A CLASE ConnectionSQLiteHelper.java
         *   (context: this): Es una clase abstracta que implementa Android. Permite acceder a los recursos específicos
         * de la aplicación y a sus clases, así como llamar al padre para realizar operaciones a nivel de la aplicación,
         * como lanzar Activities, difundir mensajes por el sistema, recibir Intents, etc.
         *
         * (name: "bd_interna_despacho_wyr"): le da el nombre a la base de datos que se creará
         *
         * (factory: null):
         *
         * (version: 1): indica la versión de la base de datos
         * */

    }
    private boolean detectar_id_dispositivo(ConnectionSQLiteHelper conn)
    {
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery ("select * from dispositivo where id_dispositivo ='"+fun.obtenerAndroidID (this)+"'",null);
        if(cursor.moveToFirst ())
        {
            return true;
        }
        return false;
    }
    private void desactivar_botones()
    {
        btnNomina.setEnabled (false);
        btnEntrega.setEnabled (false);
        btnCarga.setEnabled (false);
        btnRevision.setEnabled (false);
        btnDespacho.setEnabled (false);
    }
    private void activar_botones()
    {
        btnNomina.setEnabled (true);
        btnEntrega.setEnabled (true);
        btnCarga.setEnabled (true);
        btnRevision.setEnabled (true);
        btnDespacho.setEnabled (true);
    }
}