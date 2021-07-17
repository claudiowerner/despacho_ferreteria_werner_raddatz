<?php/** 
    * Esta clase está pensada para crear y modificar registros relacionados con las cajas en la base de datos
    */
    
    require "../conexion/conexion.php";


    $cod_barra = $_GET['cod_barra']; //obtiene el código de barra enviado desde la APP android
    $estatus = $_GET['estatus']; //obtiene el estatus de la caja enviado desde la APP android
    $num_doc = $_GET['num_doc']; //obtiene el numero de documento
    $fecha = $_GET['fecha']; //captura la fecha
    $hora = $_GET['hora']; //captura la hora
    $comentario = $_GET['comentario']; //captura la comentario
    $id_dispositivo = $_GET['id_dispositivo']; //captura el id del dispositivo

    $conexion_sql = conexion();

    /**
     * $contador: variable usada para detectar si existe algun registro asociado a la consulta realizada. 
     * En caso de no tener resultado, retornará 0*/
    $contador = 0;

    //si la conexión a la BD es exitosa
    if($conexion_sql)
    {
        $sql_cod_barra = mysqli_query($conexion_sql,"select * from caja_estado where cod_barra_caja = '$cod_barra'");
        while($mostrar = mysqli_fetch_array($sql_cod_barra))
        {
            $contador++;
        }
        /**Si contador!=0, actualizará el estado de la caja en la tabla caja_estado*/
        if($contador != 0)
        {
            $upd_cod_barra = mysqli_query($conexion_sql,"update caja_estado set estatus = $estatus where cod_barra_caja = '$cod_barra'");
            //se verifica si se realizó la modificación.
            $sql_cod_barra = mysqli_query($conexion_sql,"select * from caja_estado where estatus = '$estatus' and cod_barra_caja = '$cod_barra'");
            if($mostrar = mysqli_fetch_array($sql_cod_barra))
            {
                echo "Estado de caja actualizado exitosamente";
            }
            else
            {
                echo "Error al actualizar";
            }

        }
        /**Si contador == 0, creará el registro completo del estado de la caja*/
        else
        {
            $ins_cod_barra = mysqli_query($conexion_sql,"insert into caja_estado values ('$cod_barra', '$estatus')");
            //se verifica si se realizó la inserción en la tabla caja_estado.
            $sql_cod_barra = mysqli_query($conexion_sql,"select * from caja_estado where estatus = '$estatus' and cod_barra_caja = '$cod_barra'");
            if($mostrar = mysqli_fetch_array($sql_cod_barra))
            {
                //insercion de datos en la tabla caja_estatus_reporte
                $ins_caja_reporte = mysqli_query($conexion_sql,"insert into caja_estatus_reporte values( '$cod_barra', '$num_doc', '$fecha', '$hora', $estatus, '$comentario', '$id_dispositivo');");
                $sql_caja_reporte = mysqli_query($conexion_sql,"SELECT * FROM `caja_estatus_reporte` c WHERE c.cod_barra_caja = '$cod_barra' and c.estatus = $estatus;");
                
                //contador que verifica si hay registros en la tabla caja_estatus_reporte
                $contador = 0;
                while($mostrar = mysqli_fetch_array($sql_caja_reporte))
                {
                    $contador++;
                }
                
                //se verifica si el contador es distinto a 0
                if($contador!=0)
                {
                    echo"Caja registrada exitosamente";
                }
                else
                {
                    echo"Error al registrar la caja";
                }
            }
            else
            {
                echo "Error al registrar";
            }
        }
    }
    else
    {

    }
    
?>