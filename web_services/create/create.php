<?php

    /**
    * Esta clase está pensada para crear y modificar registros en la base de datos
    */

    //
    
    require "../conexion/conexion.php";


    $cod_barra = $_POST['cod_barra']; //obtiene el código de barra enviado desde la APP android
    $estatus = $_POST['estatus']; //obtiene el estatus de la caja enviado desde la APP android
    $num_doc = $_POST['num_doc']; //obtiene el numero de documento
    $fecha = $_POST['fecha']; //captura la fecha
    $hora = $_POST['hora']; //captura la hora
    $comentario = $_POST['comentario']; //captura la comentario
    $id_dispositivo = $_POST['id_dispositivo']; //captura el id del dispositivo
    $marca_modelo = $_POST['marca_modelo']; //captura la marca y modelo del dispositivo
    $id_empleado = $_POST['id_empleado']; //captura el id del empleado
    $nombre_empleado = $_POST['nombre_empleado']; //captura el nombre del empleado
    $apellido_empleado = $_POST['apellido_empleado']; //captura el apellido del empleado

    
?>