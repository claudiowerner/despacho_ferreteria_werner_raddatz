package com.example.despachoferreteriaswernerraddatz.funciones;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
}
