<?php 

    echo "";
    
    require "../conexion/conexion.php";


    $cod_barra = $_GET['cod_barra']; //obtiene el código de barra enviado desde la APP android
    $estatus = $_GET['estatus']; //obtiene el estatus de la caja enviado desde la APP android
    $num_doc = $_GET['num_doc']; //obtiene el numero de documento
    $fecha = $_GET['fecha']; //captura la fecha
    $hora = $_GET['hora']; //captura la hora
    $comentario = $_GET['comentario']; //captura la comentario
    $id_dispositivo = $_GET['id_dispositivo']; //captura el id del dispositivo



    echo "Datos recibidos<br>   
            $cod_barra<br>   
            $estatus<br>   
            $num_doc<br>   
            $fecha<br>   
            $hora<br>   
            $comentario<br>   
            $id_dispositivo";

    $conexion_sql = conexion();

    
     /* $contador: variable usada para detectar si existe algun registro asociado a la consulta realizada. 
     * En caso de no tener resultado, retornará 0*/
    $contador = 0;

    //si la conexión a la BD es exitosa
    if($conexion_sql)
    {
        $sql_select = mysqli_query($conexion_sql,"select * from caja_estatus_reporte where cod_barra_caja = '$cod_barra' and estatus = '$estatus'");
        //contador
        $contador = 0;
        
        while($mostrar = mysqli_fetch_array($sql_select))
        {
            $contador++;
        }
        if($contador==0)
        {
            $sql_cod_barra = mysqli_query($conexion_sql,"insert into caja_estatus_reporte values ('$cod_barra','$num_doc','$fecha','$hora','$estatus','$comentario','$id_dispositivo')");
        }
        echo "comentario: $comentario";
        $sql_comentario = mysqli_query($conexion_sql, "update caja_estatus_reporte set comentario = '$comentario' where cod_barra_caja like '%$numDoc%' and comentario ='s/c'");
    }

/*Esta clase está pensada para crear y modificar registros relacionados con las cajas en la base de datos*/
?>