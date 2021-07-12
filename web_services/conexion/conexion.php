<?php

/**Esta clase está diseñada para obtener todos los parámetros de conexión a la base de datos
 * Para cambiar los datos de conexión sólo debe modificar los parámetros indicados en las funciones 
 * "function", en el return
 */


    function host()
    {
        //return 'www.campingplayawerner.cl';
        return 'localhost';
    }
    function user()
    {
        //return 'cca10445';
        return 'root';
    }
    function pass()
    {
        //return 'MxIKhLJ4';
        return '';
    }
    function database()
    {
        //return 'cca10445_camping_tesis';
        return 'camping01';
    }
    function port()
    {
        //return '3306';
        return '3306';
    }

    //conexion
    function conexion()
    {
        $conexion = mysqli_connect(host(), user(), pass(), database(), port());
        return $conexion;
    }

?>