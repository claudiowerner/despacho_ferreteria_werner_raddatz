package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.despachoferreteriaswernerraddatz.baseDatosSQLite.ConnectionSQLiteHelper;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;

import java.util.ArrayList;


public class ActivityNomina extends AppCompatActivity {

    //creación de objetos
    private TextView txtNomina;
    private ListView lstNomina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomina);
        //inicialización de objetos
        txtNomina = findViewById(R.id.txtBusqueda);
        lstNomina = findViewById (R.id.lstNomina);

        //llamada a clase Funciones, donde se almacenan todos los métodos o funciones útiles para el programa
        Funciones fun = new Funciones();

        Intent intent = new Intent(this, activityFacturaNomina.class);

        llenarListView ("");

        lstNomina.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String codigo_barra = lstNomina.getItemAtPosition (position).toString ();

                //división de cadena de caracteres codigo_barra, que obtiene FACXXXXXX-0XX o GUIXXXXXX-0XX
                String token = fun.tokenizer (codigo_barra, " ");

                //envío de valores a la activity Nómina
                intent.putExtra("codigo_barra",token);

                startActivity (intent);
            }
        });

    }


    private void llenarListView(String consulta)
    {
        //llamada a clase ConnectionSQLiteHelper
        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);

        SQLiteDatabase db = conn.getReadableDatabase ();
        if(consulta.equals ("")||consulta.equals (null))
        {
            Cursor cursor = db.rawQuery("select * from caja_estatus_reporte where estatus=3 and estatus!=4 group by num_doc",null);

            ArrayList array = new ArrayList ();

            int id = 0;


            while (cursor.moveToNext ())
            {
                id++;
                array.add("("+id+") "+cursor.getString (1));
            }
            ArrayAdapter<String> arrayOpciones = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,array);
            lstNomina.setAdapter (arrayOpciones);
        }
    }
}