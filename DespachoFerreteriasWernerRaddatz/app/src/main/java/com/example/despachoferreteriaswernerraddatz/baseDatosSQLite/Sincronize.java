package com.example.despachoferreteriaswernerraddatz.baseDatosSQLite;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.example.despachoferreteriaswernerraddatz.funciones.Funciones;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class Sincronize
{

    Funciones fun = new Funciones();
    ConnectionDB dbConn = new ConnectionDB ();

    public void listarDatosEscaneados(Context context, Drawable drawable, ListView lst)//método que sirve para buscar e imprimir los datos de los clientes
    {
        //progressDialog
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setIcon(drawable);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        final String[][] matriz = {new String[0]};

        String url = dbConn.host ()+"";
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    progressDialog.show();
                    ArrayAdapter<String> arrayCliente;
                    if(response.length()==1)
                    {
                        Thread.sleep(1000);
                        progressDialog.dismiss();
                        fun.dialogoAlerta (context,"Búsqueda", "No existen datos");
                        arrayCliente = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, matriz[0]);
                        lst.setAdapter(arrayCliente);
                    }
                    else
                    {
                        Thread.sleep(1000);
                        progressDialog.dismiss();
                        try
                        {
                            JSONArray arr = new JSONArray(response);
                            matriz[0] = new String[arr.length()];
                            for(int i = 0; i < arr.length(); i++)
                            {
                                matriz[0][i] = arr.getString(0);
                            }
                            arrayCliente = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, matriz[0]);
                            lst.setAdapter(arrayCliente);
                        }
                        catch(JSONException e)
                        {
                            //int err = e.getCause().hashCode();
                            fun.dialogoAlerta (context,"Error", "No se pueden mostrar los datos requeridos. \nCausa: "+e.getCause());
                            e.printStackTrace();
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fun.dialogoAlerta (context,"Error de conexión","Asegúrese de que su dispositivo esté conectado a Internet.");
                progressDialog.dismiss();
            }
        });
        queue.add(stringRequest);
    }
    public void registrarDatosEscaneados(Context context, String cod_barra, String estatus, String num_doc, String fecha, String hora, String comentario, String id_dispositivo, String marca_modelo, String id_empleado, String nombre_empleado, String apellido_empleado)//método que sirve para registrar los enviados por el dispositivo Android
    {
        String url = dbConn.host ()+"create/create.php";
        RequestQueue queue = Volley.newRequestQueue(context);

        //Los datos se enviarán a través del método GET para evitar la visibilidad de estos
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if(response.length()==1)
                {
                    fun.dialogoAlerta (context,"¡Aviso!", response.toString ());
                }
                else
                {
                    //
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText (context, "Tiempo de respuesta agotado. El registro se hizo de manera local.", Toast.LENGTH_SHORT).show ();  
            }
        });
        queue.add(stringRequest);
    }
}