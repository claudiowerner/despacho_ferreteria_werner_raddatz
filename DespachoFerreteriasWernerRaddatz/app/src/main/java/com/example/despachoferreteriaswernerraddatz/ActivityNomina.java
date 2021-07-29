package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private Button btnBuscar;
    private EditText edtNumDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomina);
        //inicialización de objetos
        txtNomina = findViewById(R.id.txtBusqueda);
        lstNomina = findViewById (R.id.lstNomina);
        btnBuscar = findViewById (R.id.btnBuscarDocumento);
        edtNumDoc = findViewById (R.id.edtNumDoc);

        //llamada a clase Funciones, donde se almacenan todos los métodos o funciones útiles para el programa
        Funciones fun = new Funciones();

        Intent intent = new Intent(this, activityFacturaNomina.class);

        llenarListView ();

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

        btnBuscar.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //buscar numero de documento
                buscarDatos (edtNumDoc.getText ().toString ());
            }
        });
    }

    //

    private void llenarListView()
    {
        //llamada a clase ConnectionSQLiteHelper
        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);

        SQLiteDatabase db = conn.getReadableDatabase ();
        ArrayList array = new ArrayList ();
        Cursor cursor = db.rawQuery("select * from caja_estatus_reporte where estatus=3 and estatus=4 group by num_doc",null);

        int id = 0;
        while (cursor.moveToNext ())
        {
            id++;
            array.add("("+id+") "+cursor.getString (1));
        }
        ArrayAdapter<String> arrayOpciones = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,array);
        lstNomina.setAdapter (arrayOpciones);
    }

    private void buscarDatos(String consulta) {
        //llamada a clase ConnectionSQLiteHelper
        ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper (this, "bd_interna_despacho_wyr", null, 1);

        SQLiteDatabase db = conn.getReadableDatabase ();
        ArrayList array = new ArrayList ();
        Cursor cursor = db.rawQuery ("select * from caja_estatus_reporte where num_doc like '%" + consulta + "%' group by num_doc", null);

        int id = 0;
        while (cursor.moveToNext ()) {
            id++;
            array.add ("(" + id + ") " + cursor.getString (1));
        }
        //verifica si el array está vacío o no
        if (array.isEmpty ())
        {
            ArrayAdapter<String> arrayOpciones = new ArrayAdapter<String> (this, android.R.layout.simple_expandable_list_item_1, array);
            lstNomina.setAdapter (arrayOpciones);
            Toast.makeText (this, "La búsqueda no generó resultados", Toast.LENGTH_SHORT).show ();
            ocultarTeclado ();
        }
        else
        {
            ArrayAdapter<String> arrayOpciones = new ArrayAdapter<String> (this, android.R.layout.simple_expandable_list_item_1, array);
            lstNomina.setAdapter (arrayOpciones);
            ocultarTeclado ();
        }
    }

    //ocultará el teclado al momento de obtener o no obtener resultados de la búsqueda de datos
    private void ocultarTeclado()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtNumDoc.getWindowToken(), 0);
    }
}