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
                if(codigo.equals("")||nombre.equals("")||apellido.equals(""))
                {
                    fun.dialogoAlerta (ActivityPrimerUso.this,"¡Aviso!","Debe rellenar todos los campos.\nCodigo: "+codigo+"\nNombre: "+nombre+"\nApellido: "+apellido);
                }
                else
                {
                    ContentValues regEmpleado = new ContentValues();
                    ContentValues regDispositivo = new ContentValues();

                    //insertar datos en tabla empleado
                    regEmpleado.put("id_empleado",codigo);
                    regEmpleado.put("nombre", nombre);
                    regEmpleado.put("apellido", apellido);
                    db.insert("empleado",null,regEmpleado);

                    //insertar datos en tabla id_dispositivo
                    regDispositivo.put("id_dispositivo",id_dispositivo);
                    regDispositivo.put("marca_modelo", marca_modelo_dispositivo);
                    regDispositivo.put("empleado_id_empleado", codigo);
                    db.insert("dispositivo",null,regDispositivo);
                    db.close();
                }
            }
        });
    }
}