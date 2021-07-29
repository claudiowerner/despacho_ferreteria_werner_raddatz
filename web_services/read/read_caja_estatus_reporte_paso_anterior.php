<?php

    /** Esta clase se encarga de descargar los datos registrados en la BD para poder operar offline
    */
    require "../conexion/conexion.php";

    $conexion = conexion();

    $estatus = $_GET['estatus'];
    $cod_barra_caja = $_GET['cod_barra_caja'];

    
    if($conexion)
    {
        $sql= "select estatus from caja_estado where cod_barra_caja = '$cod_barra_caja'";
        $resultado = mysqli_query(conexion(), $sql);
        while($mostrar = mysqli_fetch_array($resultado))
        {
            $paso_obtenido=$mostrar['estatus'];
            $dif_estado = $estatus-$paso_obtenido;
            if(isset($dif_estado))
            {
                if($dif_estado==1)
                {
                    echo "ok";
                }
                else
                {
                    if($dif_estado>1)
                    {
                        echo "La caja $cod_barra_caja no ha pasado por el paso anterior a ";
                    }
                }
            }
            else
            {
                echo "dif estado no seteado";
            }
        }

    }
    else
    {
        print json_encode( "La conexión a la base de datos está presentando problemas", JSON_HEX_TAG | JSON_HEX_APOS | JSON_HEX_QUOT | JSON_HEX_AMP | JSON_UNESCAPED_UNICODE);
        echo mysqli_errno($conexion);
    }

?>