package com.example.despachoferreteriaswernerraddatz.baseDatosSQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConnectionSQLiteHelper extends SQLiteOpenHelper
{
    //Strings de creación de tablas
    String S_1_CREATE_TABLE_CAJA_ESTADO = "CREATE TABLE caja_estado (cod_barra_caja VARCHAR(30),estatus NUMERIC(1)); ";
    String S_2_CREATE_TABLE_CAJA_REPORTE = "CREATE TABLE caja_estatus_reporte (cod_barra_caja varchar(30) , num_doc varchar(30), fecha varchar(10) , hora varchar(5) , estatus numeric(1) , comentario varchar(1000) ,  id_dispositivo varchar(30) );";
    String S_3_CREATE_TABLE_DISPOSITIVO = "CREATE TABLE dispositivo (id_dispositivo varchar(30) , marca_modelo varchar(30) , empleado_id_empleado varchar(30) );";
    String S_4_CREATE_TABLE_EMPLEADO = "CREATE TABLE empleado ( id_empleado varchar(30) , nombre varchar(30) , apellido varchar(30) );";

    public ConnectionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super (context, name, factory, version);

    }

    //función que crea la BD en el dispositivo
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL (S_1_CREATE_TABLE_CAJA_ESTADO);
        db.execSQL (S_2_CREATE_TABLE_CAJA_REPORTE);
        db.execSQL (S_3_CREATE_TABLE_DISPOSITIVO);
        db.execSQL (S_4_CREATE_TABLE_EMPLEADO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }
}
