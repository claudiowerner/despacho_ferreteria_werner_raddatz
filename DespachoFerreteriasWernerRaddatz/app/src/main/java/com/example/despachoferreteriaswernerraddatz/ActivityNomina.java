package com.example.despachoferreteriaswernerraddatz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;


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
        Funciones funciones = new Funciones();

        Intent intent = new Intent(this, activityFacturaNomina.class);

        lstNomina.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity (intent);
            }
        });

        /*Para realizar la consulta con respecto a la nómina, se tiene que asociar el ID del empleado donde la fecha
        * de entrega sea la del sistema
        * Se debe crear un viaje asociado al día actual
        * Para que se listen los datos de la entrega en la nómina, tiene que seleccionarse la fecha actual, la tabla
        * que contenga los paquetes que se han cargado al camión y
        * */


    }
}