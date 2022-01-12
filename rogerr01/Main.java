package com.rogerr01;

import static java.util.Arrays.*;
import static com.rogerr01.Comptador.Metode.*;
import static com.rogerr01.MainView.*;

public final class Main
{

    // Comptadors
    public static Comptador comptadorNoMetode;
    public static Comptador comptadorVolatil;
    public static Comptador comptadorSincronitzat;
    public static Comptador comptadorBloqueix;

    // Array de comptadors
    public static Comptador[] comptadors;

    // FLAGS (VOLATILS)
    public static volatile boolean acabat = true;
    public static volatile boolean actiu = false;

    // Interficie grafica principal que engloba tots els demés elements
    private static final MainView mainView = new MainView();

    public static void main (String[] args)
    {
        // Mostrar la interficie grafica
        mainView.init();
    }

    public static void iniciar()
    {
        // Activar / Desactivar botons
        canviarEstatBotons(false);

        // Indicar que s'han d'executar els fils
        actiu = true;

        // Si ja s'havien finalitzat tots els comptadors (o és el primer cop que s'inicien)
        if (acabat)
        {
            // Crea comptadors nous
            comptadorNoMetode = new Comptador(NO_METODE);
            comptadorVolatil = new Comptador(VOLATILE);
            comptadorSincronitzat = new Comptador(SYNCHRONIZED);
            comptadorBloqueix = new Comptador(READWRITELOCK);

            // Els afegeix a l'array de comptadors
            comptadors = new Comptador[] {
                    comptadorNoMetode, comptadorVolatil,
                    comptadorSincronitzat, comptadorBloqueix
            };

            // Inidica que ja no está acabat, perque ha tornat a començar de nou
            acabat = false;

            // Iniciar tots els comptadors
            stream(comptadors).forEach(Comptador :: init);
        }

        else
        {
            // Si estaba pasuat, continua la execució dels comptadors que estaven a mitjes
            stream(comptadors).forEach(Comptador :: continuar);
        }

        // Inicia els fils que actualitzen els valors de cada columna en la interficie grafica
        new Actualitzador().init();

        // Inicia un fil que comprovará continuament si han finalitzat tots els comptadors
        // i si ja están tots acabats (han arribat al valor maxim) executa la funció 'acabar()'
        // Aixó permet que el programa detecti automaticament quan ha d'aturar els fils
        new Thread(() -> {
            while (!acabat)
                if (Funcions.comptadorsAcabats(comptadors))
                    acabar();
        }).start();

    }

    // Pausar l'execució
    public static void aturar()
    {
        // Marcar els fils com a no actius
        actiu = false;

        // Bloquejar el botó de pausa i activar el d'inici
        canviarEstatBotons(true);

        // Pausar tots els comptadors
        asList(comptadorNoMetode, comptadorVolatil, comptadorSincronitzat, comptadorBloqueix).forEach(Comptador :: pausar);
    }


    // Finalitzar l'execució
    public static void acabar()
    {
        // Pausar l'execució de tots els fils
        aturar();

        // Finalitzar els comptadors
        asList(comptadorNoMetode, comptadorVolatil, comptadorSincronitzat, comptadorBloqueix).forEach(Comptador :: acabar);

        // Marcar els fils com a acabats
        acabat = true;
    }

    // Tancar l'aplicació
    public static void sortir()
    {
        // Aturar tots els fils
        aturar();

        // Deixar de mostrar l'interficie grafica
        mainView.dispose();
        mainView.setVisible(false);

        // Si encara no s'ha tancat l'aplicació la finalitza defintivament
        System.exit(0);
    }
}
