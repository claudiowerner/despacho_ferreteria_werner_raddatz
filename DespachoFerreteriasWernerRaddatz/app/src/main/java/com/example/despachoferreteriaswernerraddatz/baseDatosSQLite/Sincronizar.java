package com.example.despachoferreteriaswernerraddatz.baseDatosSQLite;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class Sincronizar extends IntentService implements Runnable
{

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public Sincronizar() {
        super ("");
    }

    /**
     * extends IntentService nos sirve en este caso para ejecutar tareas en segundo plano
     *
     * Runnable nos permite ejecutar una tarea cada cierto tiempo (el tiempo que sea estimado)
     *
     * */

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        run ();
    }

    Timer timer;
    @Override
    public void run()
    {
        timer = new Timer ( ) ;
        //la Tarea1 se ejecuta pasado 1 segundo y luego periódicamente cada segundo
        timer.schedule (new Tarea1(),1000,1000) ;
    }
    public class Tarea1 extends TimerTask
    {
        @Override
        public void run() {
            System.out.println ("Ejecutando tarea1 ");
        }
    }
}
