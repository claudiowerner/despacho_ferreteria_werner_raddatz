<?php

    /** Esta clase se encarga de descargar los datos registrados en la BD para poder operar offline
    */
    require "../conexion/conexion.php";

    $conexion = conexion();

    $estatus = $_GET['estatus'];
    $cod_barra_caja = $_GET['cod_barra_caja'];
    
    if($conexion)
    {
        $sql=   "select * from caja_estatus_reporte where estatus = '$estatus' and cod_barra_caja = '$cod_barra_caja'";
        $resultado = mysqli_query(conexion(), $sql);
        while($mostrar = mysqli_fetch_array($resultado))
        {
            $mostrar = array_map('utf8_encode',$mostrar);
            $caja[] = $mostrar;
        }
        if(isset($caja))
        {
            if($estatus=="1")
            {
                echo "Esta caja ya pasó por el proceso de REVISIÓN";
            }
            if($estatus=="2")
            {
                echo "Esta caja ya pasó por el proceso de DESPACHO";
            }
            if($estatus=="3")
            {
                echo "Esta caja ya pasó por el proceso de CARGA";
            }
            if($estatus=="4")
            {
                echo "Esta caja ya pasó por el proceso de ENTREGA";
            }
            
        }
        else
        {
           echo "";
        }
    }
    else
    {
        print json_encode( "La conexión a la base de datos está presentando problemas", JSON_HEX_TAG | JSON_HEX_APOS | JSON_HEX_QUOT | JSON_HEX_AMP | JSON_UNESCAPED_UNICODE);
        echo mysqli_errno($conexion);
    }

?>