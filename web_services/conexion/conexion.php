<?php

/**Esta clase está diseñada para obtener todos los parámetros de conexión a la base de datos
 * Para cambiar los datos de conexión sólo debe modificar los parámetros indicados en las funciones 
 * "function", en el return
 */


    function host()
    {
        return 'localhost';
    }
    function user()
    {
        return 'admin_sistemas_wyr';
    }
    function pass()
    {
        return 'adminsistemas123';
    }
    function database()
    {
        return 'bd_despacho_wyr';
    }
    function port()
    {
        return '3306';
    }

    //conexion
    function conexion()
    {
        $conexion = mysqli_connect(host(), user(), pass(), database(), port());
        return $conexion;
    }

?>