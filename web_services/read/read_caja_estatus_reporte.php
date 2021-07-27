<?php

    /** esta clase se encarga de detectar los registros en la tabla caja_estatus_reporte según un estatus indicado*/
    require "../conexion/conexion.php";

    $estatus = $_GET['estatus'];

    $conexion = conexion();
    
    if($conexion)
    {
        $sql=   "select * from caja_estatus_reporte where estatus = '$estatus'";
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