<?php

    require "../conexion/conexion.php";

    $cod_barra = $_GET['cod_barra']; //obtiene el código de barra enviado desde la APP android
    $estatus = $_GET['estatus']; //obtiene el estatus de la caja enviado desde la APP android

    $conexion_sql = conexion();
    
    $sql = mysqli_query($conexion_sql, "select * from caja_estado where cod_barra_caja = '$cod_barra'");    

    /* $contador: variable usada para detectar si existe algun registro asociado a la consulta realizada. 
     * En caso de no tener resultado, retornará 0*/
    $contador = 0;
    while($mostrar = mysqli_fetch_array($sql))
    {
        $contador++;
    }
    if($contador!=0)
    {
        $sql = mysqli_query($conexion_sql, "UPDATE caja_estado SET estatus = '$estatus' WHERE cod_barra_caja = '$cod_barra'");
    }
    else
    {
        $sql = mysqli_query($conexion_sql, "insert into caja_estado values ('$cod_barra','$estatus')");
    }

    //actualizar comentario en paso 3 o 4
    
    //$sql_comentario = mysqli_query($conexion_sql, "update caja_estatus_reporte set comentario = '$comentario' where num_doc = '$num_doc' and estatus = 3 or estatus = 4");
    
?>