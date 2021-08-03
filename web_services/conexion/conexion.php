<?php

/**Esta clase está diseñada para obtener todos los parámetros de conexión a la base de datos
 * Para cambiar los datos de conexión sólo debe modificar los parámetros indicados en las funciones 
 * "function", en el return
 */


    /*function host()
    {
        $return = 'http://donandres';
    }
    function user()
    {
        return 'root';
    }
    function pass()
    {
        return 'wyr77admin';
        //wyr77admin
    }
    function database()
    {
        //return 'bd_despacho_wyr';
        return 'gescom';
    }/*
    function port()
    {
        return '3306';
    }*/

    //conexion

    function conexion()
    {
        //$conexion = mysqli_connect("donandres", "root", "wyr77admin", "gescom",3306);
        $conexion = mysqli_connect("localhost", "root", "", "gescom");
        return $conexion;
    }

?>