package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;
import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.CrudBDInterna;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;

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

                //llamada a método de registro
                CrudBDInterna crud = new CrudBDInterna ();
                crud.registrarEmpleado (db,getApplicationContext (),codigo,nombre,apellido,id_dispositivo,marca_modelo_dispositivo);



                Intent intent = new Intent(getApplicationContext (), MainActivity.class);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}