package com.example.despachoferreteriaswernerraddatz.baseDatosSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Callback;

public class CrudBDInterna {

    Funciones fun = new Funciones ();
    ConnectionDB c = new ConnectionDB ();


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

        //acciones de registro en modo REVISIÓN
        insert_caja_estado.put ("cod_barra_caja",cod_barra);
        insert_caja_estado.put ("estatus",estatus);
        //inserción
        db.insert("caja_estado",null,insert_caja_estado);
    }

    public void OkHttpBDInternaCajaEstadoReporteDescarga(ConnectionSQLiteHelper conn, Context context)
    {
        OkHttpClient client = new OkHttpClient ();

        System.out.println ("entra al OkHttp");
        String url = c.host ()+"read/read_caja_estatus_reporte_descarga.php";
        //se indica la URL a la que tendrá que acceder el dispositivo para descargar los datos

        okhttp3.Request request = new okhttp3.Request.Builder ()
                .url (url)
                .build ();
        client.newCall (request).enqueue (new Callback () {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace ();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if(response.isSuccessful ())
                {
                    try
                    {
                        System.out.println (response);

                        boolean caja_registrada;

                        JSONArray array = new JSONArray (response.body().string ());
                        for (int i = 0; i < array.length(); i++)
                        {

                            System.out.println ("Entra al for para descargar datos MainActivity");

                            String cod_barra = array.getJSONObject(i).getString("cod_barra_caja");
                            String st = array.getJSONObject(i).getString("estatus");
                            caja_registrada = verificarRegistroCajaEstadoReporte (conn,cod_barra,st);

                            /*si no se encuentra la caja, caja_registrada retornará false, debido a que la caja
                            seleccionada no se encuentra*/
                            if(caja_registrada==false)
                            {
                                System.out.println ("la caja "+cod_barra+" no existe");
                                registrarCajaEstadoReporte (conn,
                                        array.getJSONObject(i).getString("cod_barra_caja"),
                                        array.getJSONObject(i).getString("num_doc"),
                                        array.getJSONObject(i).getString("fecha"),
                                        array.getJSONObject(i).getString("hora"),
                                        array.getJSONObject(i).getString("estatus"),
                                        array.getJSONObject(i).getString("comentario"),
                                        array.getJSONObject(i).getString("id_dispositivo"));
                            }
                            else
                            {
                                System.out.println ("la caja "+cod_barra+" existe");
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace ();
                        System.out.println ("Error JSON: "+e.getMessage ());
                    }
                }
            }
        });
        try
        {
            okhttp3.Response okResponse = client.newCall (request).execute ();

        }
        catch (IOException e)
        {
            e.printStackTrace ();
            System.out.println ("IOException: "+e.getMessage ());
        }
    }

    public boolean verificarRegistroCajaEstadoReporte(ConnectionSQLiteHelper conn, String cod_barra, String status)
    {
        SQLiteDatabase db = conn.getWritableDatabase();

        //acciones registro tabla caja_estatus_reporte
        ContentValues insert_caja_estatus_reporte = new ContentValues ();

        Cursor cursor = db.rawQuery ("select * from caja_estatus_reporte where cod_barra_caja ='"+cod_barra+"' and estatus ='"+status+"'", null);

        while(cursor.moveToNext ())
        {
            return true;
        }
        return false;
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
