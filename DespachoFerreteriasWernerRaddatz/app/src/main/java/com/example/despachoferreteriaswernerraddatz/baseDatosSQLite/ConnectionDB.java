package com.example.despachoferreteriaswernerraddatz.baseDatosSQLite;

//esta clase almacena los parámetros de conexión a los web services
public class ConnectionDB
{
    public String host()
    {
        //retorna la dirección de los archivos .php que contienen los servicios web o web services

        //Conexión al servidor "donandres"
        //return "http://192.168.1.177/wyr_app_despacho/web_services/";

        //conexion al servidor local
        return "http://192.168.1.102/ferreteria/web_services/";
    }
}
