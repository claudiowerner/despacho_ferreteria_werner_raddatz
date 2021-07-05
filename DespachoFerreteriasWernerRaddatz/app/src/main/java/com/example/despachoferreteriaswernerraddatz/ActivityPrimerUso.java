package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;

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

        //Strings
        String codigo, nombre, apellido, id_dispositivo, marca_modelo_dispositivo;
        codigo = edtCodigo.getText ().toString ();
        nombre = edtNombre.getText ().toString ();
        apellido = edtApellido.getText ().toString ();
        id_dispositivo = fun.obtenerAndroidID (this);
        marca_modelo_dispositivo = fun.obtenerMarcaDispositivo ()+" "+fun.obtenerModeloDispositivo ();


        //llamada a creación de base de datos. En este caso la BD no se creará, ya que existe.
        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();

        btnRegistrar.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(codigo.equals("")||nombre.equals("")||apellido.equals(""))
                {
                    ContentValues reg = new ContentValues();

                    //insertar datos en tabla id_dispositivo
                    reg.put("id_empleado",codigo);
                    reg.put("nombre", nombre);
                    reg.put("apellido", apellido);
                    db.insert("empleado",null,reg);

                    //insertar datos en tabla id_dispositivo
                    reg.put("id_dispositivo",id_dispositivo);
                    reg.put("marca_modelo", marca_modelo_dispositivo);
                    reg.put("empleado_id_empleado", codigo);
                    db.insert("dispositivo",null,reg);
                    db.close();
                }
            }
        });
    }
}