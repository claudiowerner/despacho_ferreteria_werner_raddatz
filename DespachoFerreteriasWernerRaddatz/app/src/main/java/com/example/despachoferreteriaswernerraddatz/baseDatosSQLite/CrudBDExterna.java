package com.example.despachoferreteriaswernerraddatz.baseDatosSQLite;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;

import org.json.JSONArray;
import org.json.JSONException;

public class CrudBDExterna
{
    /**
     * Clase que contiene la URL o IP del host al que se tendrá que conectar el dispositivo para consumir los web
     * services*/
    ConnectionDB dbConn = new ConnectionDB ();

    /**llamada a la clase Funciones*/
    Funciones fun = new Funciones ();

    public void registrarDatosEscaneados(Context context,
                                         String cod_barra,
                                         String estatus,
                                         String num_doc,
                                         String fecha,
                                         String hora,
                                         String comentario,
                                         String id_dispositivo)//método que sirve para registrar los enviados por el dispositivo Android
    {
        String url = dbConn.host () + "create/create_caja.php?cod_barra="+cod_barra+
                "&estatus="+estatus+
                "&num_doc="+num_doc+
                "&fecha="+fecha+
                "&hora="+hora+
                "&comentario="+comentario+
                "&id_dispositivo="+id_dispositivo;
        RequestQueue queue = Volley.newRequestQueue (context);

        //Los datos se enviarán a través del método GET para evitar la visibilidad de estos
        StringRequest stringRequest = new StringRequest (Request.Method.GET, url, new Response.Listener<String> () {
            @Override
            public void onResponse(String response) {
                if (response.length () == 1)
                {
                    fun.dialogoAlerta (context, "¡Aviso!", response);
                }
                else
                {
                    //
                }
            }
        }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText (context, "Tiempo de respuesta agotado. El registro se hizo de manera local.", Toast.LENGTH_SHORT).show ();
            }
        });
        queue.add (stringRequest);
    }
}
