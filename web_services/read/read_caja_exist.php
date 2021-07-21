<?php

    require "../conexion/conexion.php";

    $cod_barra = $_GET['cod_barra']; //obtiene el código de barra enviado desde la APP android
    $estatus = $_GET['estatus']; //obtiene el estatus de la caja enviado desde la APP android

    $conexion_sql = conexion();
    
    $sql = mysqli_query($conexion_sql, "select * from caja_estatus_reporte where cod_barra_caja = '$cod_barra' and estatus = '$estatus'");    

    $contador = 0;
    while($mostrar = mysqli_fetch_array($sql))
    {
        $contador++;
    }
    if($contador!=0)
    {
        echo "Existente";
    }
?>