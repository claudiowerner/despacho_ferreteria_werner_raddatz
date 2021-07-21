<?php

    require "../conexion/conexion.php";

    $cod_barra = $_GET['cod_barra']; //obtiene el código de barra enviado desde la APP android
    $num_doc = $_GET['num_doc']; //obtiene el estatus de la caja enviado desde la APP android
    $comentario = $_GET['comentario'];
    $conexion_sql = conexion();
    
    $sql = mysqli_query($conexion_sql, "update caja_estatus_reporte set comentario = '"+$comentario+"' where num_doc = '"+numDoc+"' and estatus = 3 or estatus = 4'");    

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
        echo "lal";
        $sql = mysqli_query($conexion_sql, "insert into caja_estado values ('$cod_barra','$estatus')");
    }
?>