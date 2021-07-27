<?php

    /** Esta clase se encarga de descargar los datos registrados en la BD para poder operar offline
    */
    require "../conexion/conexion.php";

    $conexion = conexion();

    $estatus = $_GET['estatus'];
    $cod_barra_caja = $_GET['cod_barra_caja'];
    
    if($conexion)
    {

        /**la variable paso_anterior permite detectar si la caja escaneada pasó por el paso anterior.
         * Por ejemplo, si se está escaneando una caja en el modo despacho, y no ha pasado por el paso de revisión,
         * la app avisará que la caja no pasó por el paso anterior. 
         * 
         * El funcionamiento sería el siguiente:
         * 
         *      Suponiendo que, $estatus=4, $paso_anterior sería $paso_anterior=($estatus-1); y el resultado sería 3.
         *      Por ende, se consultará si la caja escaneada ha pasado por el paso 3.
         */

        $paso_anterior = ($estatus-1);
        $sql=   "select * from caja_estatus_reporte where estatus = '$paso_anterior' and cod_barra_caja = '$cod_barra_caja'";
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
                echo "Esta caja no ha pasado por el proceso de REVISIÓN";
            }
            if($estatus=="2")
            {
                echo "Esta caja no ha pasado por el proceso de DESPACHO";
            }
            if($estatus=="3")
            {
                echo "Esta caja no ha pasado por el proceso de CARGA";
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