package com.rogerr01;

import info.clearthought.layout.TableLayout;
import javax.swing.*;
import java.util.stream.Stream;
import static java.util.stream.IntStream.*;

public class Taula extends JPanel
{

    // Numero de columnes que formen la taula
    private static final int NUM_COLUMNES = 4;

    // Numero de files que formen la taula
    private static final int NUM_FILES = 14;

    // Mida de cada una de les files
    private static final double MIDA_FILA = 40.0;

    // Mida de cada una de les columnes
    private static final double MIDA_COLUMNA = 200.0;

    // Llargada de la taula (mida de cada columna per numero de columnes)
    private static final int WIDTH_TAULA = (int) MIDA_COLUMNA * NUM_COLUMNES;

    // Altura de la taula (mida de les files per numero de files)
    private static final int HEIGHT_TAULA = (int) MIDA_FILA * NUM_FILES;

    // Capçaleres (marcadors superiors de la taula, amb els titols de les columnes)
    private static final Marcador metode1 = new Marcador("No mètode");
    private static final Marcador metode2 = new Marcador("Mètode volatil");
    private static final Marcador metode3 = new Marcador("Sincronització");
    private static final Marcador metode4 = new Marcador("Bloqueix Lectura/Escriptura");

    // Temps de cada metode (10 marcadors per metode, ja que hi ha 10 fils)
    public static Marcador[] tempsMetode1 = new Marcador[10];
    public static Marcador[] tempsMetode2 = new Marcador[10];
    public static Marcador[] tempsMetode3 = new Marcador[10];
    public static Marcador[] tempsMetode4 = new Marcador[10];

    // Mitjana de temps de cada metode
    public static Marcador mitjanaMetode1 = new Marcador("Mitjana: 0 ms", 1 , 0);
    public static Marcador mitjanaMetode2 = new Marcador("Mitjana: 0 ms", 0 ,0);
    public static Marcador mitjanaMetode3 = new Marcador("Mitjana: 0 ms", 0,0);
    public static Marcador mitjanaMetode4 = new Marcador("Mitjana: 0 ms", 0,0);

    // Comptador de cada metode (del 0 al 100.000.000)
    public static Marcador comptador1 = new Marcador("0 / 100.000.000", 1, 1);
    public static Marcador comptador2 = new Marcador("0 / 100.000.000" ,0,  1);
    public static Marcador comptador3 = new Marcador("0 / 100.000.000", 0 , 1);
    public static Marcador comptador4 = new Marcador("0 / 100.000.000", 0 , 1);

    // Array on després es coloquen els elements
    JComponent[][] elements;

    // Aquesta variable conté un element buit, el qual permetra
    // crear una separació entre files o columnes quan sigui necessari.
    // Li dono el nom espaiEnBlanc per a que quan posi espais en blanc
    // s'entengui millor aquella part del codi
    private static final JComponent espaiEnBlanc = new JLabel();
    
    public Taula ()
    {
        // Array amb la mida de cada columna
        double[] midaColumnes = range(0, NUM_COLUMNES).mapToDouble(i -> MIDA_COLUMNA).toArray();

        // Array amb la mida de cada fila
        double[] midaFiles = range(0, NUM_FILES).mapToDouble(i -> MIDA_FILA).toArray();

        // Per a la distribució de la interficiee grafica he implementat un Table Layout
        setLayout(new TableLayout(midaColumnes, midaFiles));

        // Generar l'array d'elements
        elements = generarArrayElements();

        // Afegir els elements en format de taula
        for (int i = 0; i < NUM_COLUMNES; i++)
            for (int j = 0; j < NUM_FILES; j++)
                // El TableLayout ens demana el element i la seva posició en la taula
                // La posició es una String en aquest format -> "NUMERO-COLUMNA, NUMERO-FILA"
                add(elements[i][j], i + ", " + j);
            
    }

    // Retorna l'array amb tots el components grafics
    private JComponent[][] generarArrayElements ()
    {

        // Colocar el text per defecte (0 ms) en tots els marcadors de temps
        Stream.of(tempsMetode1, tempsMetode2, tempsMetode3, tempsMetode4).forEach(
                    j -> range(0, 10).forEach(
                    i -> j[i] = new Marcador(
              "0 ms", j == tempsMetode1 ? 1 : 0, i != 9 ? 0 : 1)
                ));
            
            
        // Crear l'array d'elements amb tots els marcadors ordenats per files i columnes
        return new JComponent[][] {

            // Primera columna -> No mètode
            {
                    metode1,
                    tempsMetode1[0], tempsMetode1[1],
                    tempsMetode1[2], tempsMetode1[3],
                    tempsMetode1[4], tempsMetode1[5],
                    tempsMetode1[6], tempsMetode1[7],
                    tempsMetode1[8], tempsMetode1[9],
                    espaiEnBlanc,
                    mitjanaMetode1, comptador1
            },

            // Segona columna -> Mètode volatil
            {
                    metode2,
                    tempsMetode2[0], tempsMetode2[1],
                    tempsMetode2[2], tempsMetode2[3],
                    tempsMetode2[4], tempsMetode2[5],
                    tempsMetode2[6], tempsMetode2[7],
                    tempsMetode2[8], tempsMetode2[9],
                    espaiEnBlanc,
                    mitjanaMetode2, comptador2
            },

            // Tercera columna -> Sincronització
            {
                    metode3,
                    tempsMetode3[0], tempsMetode3[1],
                    tempsMetode3[2], tempsMetode3[3],
                    tempsMetode3[4], tempsMetode3[5],
                    tempsMetode3[6], tempsMetode3[7],
                    tempsMetode3[8], tempsMetode3[9],
                    espaiEnBlanc,
                    mitjanaMetode3, comptador3
            },

            // Quarta columna -> Bloqueix Lectura / Escriptura
            {
                    metode4,
                    tempsMetode4[0], tempsMetode4[1],
                    tempsMetode4[2], tempsMetode4[3],
                    tempsMetode4[4], tempsMetode4[5],
                    tempsMetode4[6], tempsMetode4[7],
                    tempsMetode4[8], tempsMetode4[9],
                    espaiEnBlanc,
                    mitjanaMetode4, comptador4
            }
        };
    }

    // Obtenir la mida de les files
    public static double getMidaFila ()
    {
        return MIDA_FILA;
    }

    // Obtenir la mida de les columnes
    public static double getMidaColumna ()
    {
        return MIDA_COLUMNA;
    }

    // Obtenir la llargada de la taula
    public static int getWidthTaula ()
    {
        return WIDTH_TAULA;
    }

    // Obtenir l'altura de la taula
    public static int getHeightTaula ()
    {
        return HEIGHT_TAULA;
    }

}
