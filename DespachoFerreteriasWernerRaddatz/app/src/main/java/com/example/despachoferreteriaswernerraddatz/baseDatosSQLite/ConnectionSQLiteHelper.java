package com.example.despachoferreteriaswernerraddatz.baseDatosSQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConnectionSQLiteHelper extends SQLiteOpenHelper
{

    String scriptBD = "CREATE TABLE caja_estado (\n" +
            "    cod_barra_caja   VARCHAR(30) \n" +
            "--  ERROR: VARCHAR2 size not specified \n" +
            "     NOT NULL,\n" +
            "    estatus          NUMERIC(1)\n" +
            ");\n" +
            "\n" +
            "ALTER TABLE caja_estado ADD CONSTRAINT caja_estado_pk PRIMARY KEY ( cod_barra_caja );\n" +
            "\n" +
            "CREATE TABLE caja_estatus_reporte (\n" +
            "    cod_barra_caja   VARCHAR(30) \n" +
            "--  ERROR: VARCHAR2 size not specified \n" +
            "     NOT NULL,\n" +
            "    fecha            VARCHAR(10),\n" +
            "    hora             VARCHAR(5),\n" +
            "    estatus          NUMERIC(1),\n" +
            "    comentario       VARCHAR(1000),\n" +
            "    id_dispositivo   VARCHAR(30) \n" +
            "--  ERROR: VARCHAR2 size not specified \n" +
            "     NOT NULL\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE dispositivo (\n" +
            "    id_dispositivo         VARCHAR(30) \n" +
            "--  ERROR: VARCHAR2 size not specified \n" +
            "     NOT NULL,\n" +
            "    marca_modelo           VARCHAR(30) \n" +
            "--  ERROR: VARCHAR2 size not specified \n" +
            "   ,\n" +
            "    empleado_id_empleado   VARCHAR(30) \n" +
            "--  ERROR: VARCHAR2 size not specified \n" +
            "     NOT NULL\n" +
            ");\n" +
            "\n" +
            "ALTER TABLE dispositivo ADD CONSTRAINT dispositivo_pk PRIMARY KEY ( id_dispositivo );\n" +
            "\n" +
            "CREATE TABLE empleado (\n" +
            "    id_empleado   VARCHAR(30) \n" +
            "--  ERROR: VARCHAR2 size not specified \n" +
            "     NOT NULL,\n" +
            "    nombre        VARCHAR(30)\n" +
            "--  ERROR: VARCHAR2 size not specified \n" +
            "   ,\n" +
            "    apellido      VARCHAR(30)\n" +
            "--  ERROR: VARCHAR2 size not specified \n" +
            ");\n" +
            "\n" +
            "ALTER TABLE empleado ADD CONSTRAINT empleado_pk PRIMARY KEY ( id_empleado );\n" +
            "\n" +
            "ALTER TABLE caja_estatus_reporte\n" +
            "    ADD CONSTRAINT caja_estado_fk FOREIGN KEY ( cod_barra_caja )\n" +
            "        REFERENCES caja_estado ( cod_barra_caja );\n" +
            "\n" +
            "ALTER TABLE dispositivo\n" +
            "    ADD CONSTRAINT dispositivo_empleado_fk FOREIGN KEY ( empleado_id_empleado )\n" +
            "        REFERENCES empleado ( id_empleado );\n" +
            "\n" +
            "ALTER TABLE caja_estatus_reporte\n" +
            "    ADD CONSTRAINT id_dispositivo_fk FOREIGN KEY ( id_dispositivo )\n" +
            "        REFERENCES dispositivo ( id_dispositivo );";

    public ConnectionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super (context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
