package com.example.despachoferreteriaswernerraddatz;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.StringTokenizer;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button btnRevision, btnDespacho, btnCarga, btnEntrega, btnNomina, btnCrearViaje, btnModificarViaje;
    Funciones fun = new Funciones();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);


        //llamada a la activity ActivityPrimerUso
        Intent intent = new Intent (this, ActivityPrimerUso.class);

        //inicialización de objetos
        btnRevision = findViewById (R.id.btnRevision);
        btnNomina = findViewById (R.id.btnNomina);
        btnDespacho = findViewById (R.id.btnDespacho);
        btnCarga = findViewById (R.id.btnCarga);
        btnEntrega = findViewById (R.id.btnEntrega);

        Toast.makeText (this, "IMEI: " + obtenerAndroidID(this), Toast.LENGTH_LONG).show ();

        //acciones botón revisión
        btnRevision.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrador = new IntentIntegrator (MainActivity.this);
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

    //lectura de código de barra de revisión
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult (requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents () == null)
            {
                Toast.makeText (this, "Lectura cancelada", Toast.LENGTH_LONG).show ();
            }
            else
            {
                Toast.makeText (this, "Cod. barra: "+fun.tokenizer (result.getContents ()),Toast.LENGTH_LONG).show ();
            }
        }
        else
        {
            super.onActivityResult (requestCode, resultCode, data);
        }
    }


    private String obtenerAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            onNetworkChange(ni);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter (android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
    }

    private void onNetworkChange(NetworkInfo networkInfo) {
        if (networkInfo != null) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
            {
                Toast.makeText (this, "CONECTADO: El almacenamiento remoto está activado.", Toast.LENGTH_SHORT).show ();
            }
        }
        else
        {
            disp_desconectado ();
        }
    }

    private void disp_desconectado()
    {
        Toast.makeText (this, "DESCONECTADO: El almacenamiento local está activado.", Toast.LENGTH_SHORT).show ();
        //llamada a la clase ConnectionSQLiteHelper.java

        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr",null,1);
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

        SQLiteDatabase sqlBD = conn.getWritableDatabase (); //sentencia que crea la base de datos
    }
}