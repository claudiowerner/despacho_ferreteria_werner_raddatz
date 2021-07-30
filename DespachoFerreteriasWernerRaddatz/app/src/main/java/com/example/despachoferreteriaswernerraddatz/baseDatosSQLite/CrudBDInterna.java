package com.example.despachoferreteriaswernerraddatz.baseDatosSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;

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
    public void registrarCajaEstado(ConnectionSQLiteHelper conn, String cod_barra, String estatus)
    {

        SQLiteDatabase db = conn.getWritableDatabase();

        //db.execSQL ("delete from caja_estado");

        //acciones registro tabla caja_estado
        ContentValues insert_caja_estado = new ContentValues();

        //acciones registro tabla caja_estatus_reporte
        ContentValues insert_caja_estatus_reporte = new ContentValues ();
        //acciones de registro en modo REVISIÓN
        insert_caja_estado.put ("cod_barra_caja",cod_barra);
        insert_caja_estado.put ("estatus",estatus);
        //inserción
        db.insert("caja_estado",null,insert_caja_estado);
    }
    public void registrarCajaEstadoReporte(ConnectionSQLiteHelper conn, String cod_barra, String num_doc, String fecha, String hora, String estatus, String comentario, String id_dispositivo)
    {
        SQLiteDatabase db = conn.getWritableDatabase();

        //db.execSQL ("delete from caja_estatus_reporte");

        //acciones registro tabla caja_estatus_reporte
        ContentValues insert_caja_estatus_reporte = new ContentValues ();

        //acciones de registro en modo REVISIÓN
        insert_caja_estatus_reporte.put ("cod_barra_caja", cod_barra);
        insert_caja_estatus_reporte.put ("num_doc", num_doc);
        insert_caja_estatus_reporte.put ("fecha", fecha);
        insert_caja_estatus_reporte.put ("hora", hora);
        insert_caja_estatus_reporte.put ("estatus", estatus);
        insert_caja_estatus_reporte.put ("comentario", comentario);
        insert_caja_estatus_reporte.put ("id_dispositivo", id_dispositivo);
        //inserción
        db.insert("caja_estatus_reporte",null,insert_caja_estatus_reporte);
    }
}
