package com.rogerr01;
import static java.util.Arrays.*;
import static java.util.stream.IntStream.*;
import static com.rogerr01.Funcions.*;
import static com.rogerr01.Taula.*;

public final class Actualitzador
{
    private static final class Columna implements Runnable
    {
        // Elements que formen la columna
        private final  Comptador comptador;
        private final  Marcador[] temps;
        private final  Marcador mitjana;
        private final  Marcador marcadorComptador;

        private Columna (Comptador comptador, Marcador[] temps, Marcador mitjana, Marcador marcadorComptador)
        {
            this.comptador = comptador;
            this.temps = temps;
            this.mitjana = mitjana;
            this.marcadorComptador = marcadorComptador;
        }

        // Iniciar el fil que actualitza els valors de la columna
        private void start()
        {
            new Thread(this).start();
        }

        @Override
        public void run ()
        {
           // Mentres hagi d'estar actiu, actualitza el text dels marcadors
           while (Main.actiu)
           {
               // Només actualitza els temps si el comptador está actiu
                if (!comptador.estaAcabat())
                {
                    // Temps
                    range(0, temps.length).forEach(i -> temps[i].setText(formatarNumero(comptador.temporitzadors[i].getTemps()) + " ms"));

                    // Mitjana temps
                    mitjana.setText(formatarNumero(comptador.getMitjanaTemps()) + " ms");
                }

                // Valor del comptador
                marcadorComptador.setText(formatarNumero(comptador.getValorComptador()) + " / 100.000.000");

                // Barra de progres
                marcadorComptador.mostrarProgres(comptador.getValorComptador());
            }
        }
    }

    // Inicitalitzar el actualitzador de dades de cada columna
    public void init ()
    {
        // Les 4 columnes de la taula
        Columna[] columnes = new Columna[]
                {
                        // Columna no mètode
                        new Columna(Main.comptadorNoMetode, tempsMetode1, mitjanaMetode1, comptador1),
                        // Columna volatil
                        new Columna(Main.comptadorVolatil, tempsMetode2, mitjanaMetode2, comptador2),
                        // Columna sincronitzat
                        new Columna(Main.comptadorSincronitzat, tempsMetode3, mitjanaMetode3, comptador3),
                        // Columna bloqueix
                        new Columna(Main.comptadorBloqueix, tempsMetode4, mitjanaMetode4, comptador4)
                };

        // Iniciar els fils
        stream(columnes).forEach(Columna :: start);
    }

}
