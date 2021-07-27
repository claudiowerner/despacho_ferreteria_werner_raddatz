<?php

    /** Esta clase se encarga de descargar los datos registrados en la BD para poder operar offline
    */
    require "../conexion/conexion.php";

    $conexion = conexion();
    
    if($conexion)
    {
        $sql=   "select * from caja_estatus_reporte where estatus = '3'";
        $resultado = mysqli_query(conexion(), $sql);
        while($mostrar = mysqli_fetch_array($resultado))
        {
            $mostrar = array_map('utf8_encode',$mostrar);
            $cliente[] = $mostrar;
        }
        if(isset($cliente))
        {
            print json_encode($cliente, JSON_HEX_TAG | JSON_HEX_APOS | JSON_HEX_QUOT | JSON_HEX_AMP | JSON_UNESCAPED_UNICODE);
        }
        else
        {
            print json_encode("No existen cajas", JSON_HEX_TAG | JSON_HEX_APOS | JSON_HEX_QUOT | JSON_HEX_AMP | JSON_UNESCAPED_UNICODE);
        }
    }
    else
    {
        print json_encode( "La conexión a la base de datos está presentando problemas", JSON_HEX_TAG | JSON_HEX_APOS | JSON_HEX_QUOT | JSON_HEX_AMP | JSON_UNESCAPED_UNICODE);
        echo mysqli_errno($conexion);
    }

?>