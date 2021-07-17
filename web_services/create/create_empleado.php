<?php

    /** Esta clase está pensada para crear y modificar registros 
     * relacionados con las cajas en la base de datos */
    
    require "../conexion/conexion.php";

    $id_dispositivo = $_GET['id_dispositivo']; //captura el ID del dispositivo (ID del SO. Android)
    $marca_modelo = $_GET['marca_modelo']; //captura la marca y el modelo del dispositivo
    $id_empleado = $_GET['id_empleado']; //captura el ID del empleado. Se sugiere que el ID sea el mismo que el del GESCOM
    $nombre = $_GET['nombre']; //captura el nombre del empleado
    $apellido = $_GET['apellido']; //captura el apellido del empleado

    //esta variable contará si existen registros relacionados al dispositivo (tablet/smartphone)
    $contador_dis = 0;

    //esta variable contará si existen registros relacionados al empleado
    $contador_empl = 0;

    $conexion_sql = conexion();

    //si la conexión a la BD es exitosa
    if($conexion_sql)
    {
        //verifica si existe el dispositivo relacionado con el ID del dispositivo y el ID del empleado
        $sql_dispositivo = mysqli_query($conexion_sql, "select * from dispositivo d join empleado e on d.id_empleado=e.id_empleado where d.id_dispositivo = '$id_dispositivo'");
        
        //verifica si existe algún empleado relacionado con el ID especificado
        $sql_empleado = mysqli_query($conexion_sql, "select * from empleado where id_empleado = '$id_empleado'");
        
        //el contador cambiará su valor de acuerdo a la cantidad de filas que arroje la consulta SQL
        while($mostrar = mysqli_fetch_array($sql_dispositivo))
        {
            $contador_dis++;
        }
        //el contador cambiará su valor de acuerdo a la cantidad de filas que arroje la consulta SQL
        while($mostrar = mysqli_fetch_array($sql_empleado))
        {
            $contador_empl++;
        }

        //si el $contador == 0, procede a registrar al empleado
        if($contador_empl==0)
        {
            $ins_datos_disp = mysqli_query($conexion_sql, "insert into empleado values ('$id_empleado', '$nombre', '$apellido')");

            //verificar si se registró el empleado
            $sel_empl = mysqli_query($conexion_sql, "select * from empleado where id_empleado = '$id_empleado'");

            //cuenta la cantidad de vueltas que se dan al momento de realizar la consulta por el empleado
            $empleado = 0;
            while($mostrar = mysqli_fetch_array($sel_empl))
            {
                $empleado++;
            }

            if($empleado!=0)
            {
                echo "Empleado registrado exitosamente.";
            }
            else
            {
                echo "Error al registrar el empleado.";
            }
        }


        //si el contador == 0, se registrará el dispositivo nuevo
        if($contador_dis==0)
        {
            /**Verificar si el dispositivo desde el cual se está ejecutando la aplicación 
             * está asociado a un empleado o no. Si el dispositivo es nuevo (o nunca ha ejecutado la app),
             * en la BD se actualizará la información del dispositivo relacionado con el ID del usuario 
             * (se recomienda que el ID sea el mismo del GESCOM)
             */
            $sel_disp_existente = mysqli_query($conexion_sql, "SELECT * FROM empleado e join dispositivo d on d.id_empleado = e.id_empleado where e.id_empleado = '$id_empleado'");
            
            /**$id_dis_bd obtendrá el ID del dispositivo relacionado al empleado según su ID de GESCOM
             * Si $id_dis_bd = "", se insertará el registro del empleado y del dispositivo, en caso contrario
             * se actualizará el registro anterior.
             */
            $id_dis_bd = "";
            
            while($resultado = mysqli_fetch_array($sel_disp_existente))
            {
                $id_dis_bd = $resultado['id_dispositivo'];
            }
            
            if($id_dis_bd == "")
            {
                //inserción de datos del dispositivo
                $ins_datos_disp = mysqli_query($conexion_sql, "insert into dispositivo values ('$id_dispositivo', '$marca_modelo', '$id_empleado')");
            }
            else
            {
                /**actualización de datos del dispositivo. Esto pasará cuando el empleado inicie sesión en un 
                 * nuevo dispositivo  */ 
            }

        }
    }
?>