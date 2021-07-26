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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CrudBDExterna
{
    /**
     * Clase que contiene la URL o IP del host al que se tendrá que conectar el dispositivo para consumir los web
     * services*/
    ConnectionDB dbConn = new ConnectionDB ();

    /**llamada a la clase Funciones*/
    Funciones fun = new Funciones ();

    public void registrarDatosEscaneados(Context context, String cod_barra, String estatus, String num_doc, String fecha, String hora, String comentario, String id_dispositivo)//método que sirve para registrar los enviados por el dispositivo Android
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
    //este método detecta si la caja ya está registrada en la BD
    private boolean detectarCajaRepetidaMYSQL(String codBarra1, String estatus1)
    {
        HttpURLConnection conn1;// conecta con el servicio que registra los datos en la tabla caja_estado

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();

        /*Connection db obtendrá parte del host de los Web Services. Por ejemplo si el host sería http://www.wyr.cl/web_services,
         y la dirección de los servicios web se agrega a parte, como se ve más abajo */
        ConnectionDB db = new ConnectionDB ();
        try {
            URL url1 = new URL(db.host () + "update/update_caja.php?" +
                    "cod_barra=" + codBarra1 +
                    "&estatus=" + estatus1);
            conn1 = (HttpURLConnection) url1.openConnection ();
            conn1.setRequestMethod ("GET");
            conn1.setConnectTimeout (10000);
            conn1.setReadTimeout (10000);

            int status1 = conn1.getResponseCode();
            if(status1>500)
            {
                reader = new BufferedReader (new InputStreamReader (conn1.getErrorStream ()));
                while((line = reader.readLine ())!=null)
                {
                    responseContent.append (line);
                }
                reader.close ();
            }
            else
            {
                reader = new BufferedReader (new InputStreamReader (conn1.getInputStream ()));
                while((line = reader.readLine ())!=null)
                {
                    responseContent.append(line);
                }
            }
            conn1.disconnect ();
            System.out.println ("Response content: "+responseContent.toString ());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace ();
        }
        catch (IOException e)
        {
            e.printStackTrace ();
        }
        return false;
    }
}
