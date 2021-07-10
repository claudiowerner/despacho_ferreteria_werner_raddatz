package com.example.despachoferreteriaswernerraddatz.funciones;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class Funciones
{
    public String fecha()//metodo que permite obtener la fecha local del dispositivo
    {
        //obtención de fecha actual según sistema
        long ahora = System.currentTimeMillis();//representa un instante en el tiempo con una precisión de milisegundos
        Date fecha = new Date(ahora);
        DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");//formateo de fecha con salida tipo dia/mes/año
        return formatoFecha.format(fecha);
    }

    //obtener hora en formato 24rhs
    public String hora()//metodo que permite obtener la fecha local del dispositivo
    {
        Calendar calendario = Calendar.getInstance ();
        int hora, minutos;

        hora =calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE);
        String min = "";
        if(minutos<10)
        {
            min = "0"+minutos;
        }
        else
        {
            min=minutos+"";
        }

        return hora+":"+min;
    }
    public String tokenizer(String cadena, String delimitador)
    {
        //separación de cadena de caracteres
        StringTokenizer st = new StringTokenizer (cadena, delimitador);
        String destino = "";
        String numDocumento = "";
        while (st.hasMoreTokens ()) {
            if (destino.equals ("")) {
                destino = st.nextToken ();
            } else {
                return numDocumento = st.nextToken ();
            }
        }
        return numDocumento;
    }

    public String obtenerAndroidID(Context context) {
        return Settings.Secure.getString (context.getContentResolver (), Settings.Secure.ANDROID_ID);
    }
    public String obtenerMarcaDispositivo() {
        return Build.MANUFACTURER;
    }
    public String obtenerModeloDispositivo() {
        return Build.MODEL;
    }

    public void dialogoAlerta(Context context, String titulo, String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
