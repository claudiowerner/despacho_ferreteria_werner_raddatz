package com.example.despachoferreteriaswernerraddatz.baseDatosSQLite;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTableLockedException;
import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.despachoferreteriaswernerraddatz.ActivityPrimerUso;
import com.example.despachoferreteriaswernerraddatz.MainActivity;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;

import org.json.JSONArray;
import org.json.JSONException;

public class CrudBDInterna {

    Funciones fun = new Funciones ();
    ConnectionDB dbConn = new ConnectionDB ();

    public void registrarEmpleado(SQLiteDatabase db, Context context, String codigo, String nombre, String apellido, String id_dispositivo, String marca_modelo_dispositivo)
    {
        if(codigo.equals("")||nombre.equals("")||apellido.equals(""))
        {
            fun.dialogoAlerta (context,"¡Aviso!","Debe rellenar todos los campos.\nCodigo: "+codigo+"\nNombre: "+nombre+"\nApellido: "+apellido);
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
            //cerrar activity

        }
    }
}
