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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;

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
        SQLiteDatabase db = conn.getWritableDatabase ();
        //Detectar primer uso de la aplicación
        boolean primer_uso = detectar_id_dispositivo (conn);
        if(primer_uso==true)
        {
            activar_botones ();
        }
        else
        {
            Toast.makeText (this, "Comprobando primer uso...", Toast.LENGTH_SHORT).show ();
            desactivar_botones ();
            //llamada a activity primer uso
            startActivity(intent);
        }

        final String[] modo = {""};

        //acciones botón revisión
        btnRevision.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                modo[0] = "1";
                intent.putExtra("modo",modo[0]);//La app entrará en modo 1, que sería modo Revisión
                startActivity (intent);
            }
        });



        Intent finalIntent1 = intent;
        btnDespacho.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                modo[0] = "2";
                intent.putExtra("modo",modo[0]);//La app entrará en modo 2, que sería modo Despacho
                startActivity (intent);
            }
        });

        btnCarga.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                modo[0] = "3";
                intent.putExtra("modo",modo[0]);//La app entrará en modo 3, que sería modo Carga
                startActivity (intent);
            }
        });
        btnEntrega.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext (), ActivityPaqueteEscaneado.class);
                modo[0] = "4";
                intent.putExtra("modo",modo[0]);//La app entrará en modo 4, que sería modo Entrega
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
        Toast.makeText (this, "Conectado pero sin internet. El almacenamiento interno está activo.", Toast.LENGTH_SHORT).show ();
        }

    private void onNetworkChange(NetworkInfo networkInfo) {
        if (networkInfo != null)
        {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
            {
                Toast.makeText (this, "CONECTADO: El almacenamiento remoto está activado.", Toast.LENGTH_SHORT).show ();
            }
            if(networkInfo.getState ()==NetworkInfo.State.SUSPENDED)
            {
                Toast.makeText (this, "Conexión suspendida", Toast.LENGTH_SHORT).show ();
            }
            if(networkInfo.getState ()==NetworkInfo.State.CONNECTING)
            {
                Toast.makeText (this, "Conectando", Toast.LENGTH_SHORT).show ();
            }
            if(networkInfo.getState ()==NetworkInfo.State.DISCONNECTING)
            {
                Toast.makeText (this, "Desconectando", Toast.LENGTH_SHORT).show ();
            }
            if(networkInfo.getState ()==NetworkInfo.State.UNKNOWN)
            {
                Toast.makeText (this, "Estado de conexión desconocido", Toast.LENGTH_SHORT).show ();
            }
            if(networkInfo.getState ()==NetworkInfo.State.DISCONNECTING)
            {
                Toast.makeText (this, "Desconectando", Toast.LENGTH_SHORT).show ();
            }
        }

    }
}