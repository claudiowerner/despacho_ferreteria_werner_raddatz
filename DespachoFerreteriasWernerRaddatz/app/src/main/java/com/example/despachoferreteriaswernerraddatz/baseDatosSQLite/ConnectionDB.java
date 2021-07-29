package com.example.despachoferreteriaswernerraddatz.baseDatosSQLite;

//esta clase almacena los parámetros de conexión a los web services
public class ConnectionDB
{
    public String host()
    {
        //retorna la dirección de los archivos .php que contienen los servicios web o web services
        return "http://192.168.43.165/ferreteria/web_services/";
    }
}
