package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityPrimerUso extends AppCompatActivity {


    //creación de objetos
    EditText edtCodigo, edtNombre, edtApellido;
    Button btnRegistrar;

    //llamada a clase funciones
    Funciones fun = new Funciones();

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
                    CrudBDExterna crudBDExterna = new CrudBDExterna ();
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
        final String[][] matriz = {new String[0]};

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
                if (response.length () == 1) {
                    fun.dialogoAlerta (getApplicationContext (), "Oops...", "Ocurrió un error al intentar autorizar el uso de esta aplicación en su dispositivo.");
                }
                else
                {
                    dialogoAlerta ("Enhorabuena", "Autorización exitosa para operar esta aplicación.");
                }
            }
        }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText (getApplicationContext (), "Tiempo de espera agotado. De igual manera, el registro se hizo de manera local", Toast.LENGTH_LONG).show ();
                System.out.println ("Error de Volley; "+error);
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
}