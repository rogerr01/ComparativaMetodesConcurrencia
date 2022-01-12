package com.rogerr01;

import java.awt.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.toList;

public abstract class Funcions
{
    // Colors
    public static Color verd = new Color(0, 133, 49);
    public static Color vermell = new Color(105,0,0);
    public static Color blau =new Color(0, 56, 160);

    // Formatar un numero enter amb separadors, per exemple 100000000 -> 100.000.000
    public static String formatarNumero(int numero)
    {
        return String.format("%,d", numero);
    }

    // Formatar un numero decimal per mostrar 10 xifres, per exemple 10,283178173172312 -> 10,2831781731
    public static String formatarNumero(double numero)
    {
        return String.format("%.10f", numero);
    }

    // Comprova si tots els contadors d'una array estan finalitzats
    public static boolean comptadorsAcabats(Comptador[] comptadors)
    {
        // Calcula el numero de comptadors que han finalitzat, i retorna 'false' si son menys de 4
        return !(stream(comptadors).mapToInt(comptador -> comptador.estaAcabat() ? 1 : 0).sum() < 4);
    }

    // Generar color per al marcador
    public static Color generarColor(double percentatge)
    {
        return new Color(0, 255, 65, (int) (percentatge * 255));
    }

    // Calcular la mitjana de temps d'una serie de temporitzadors
    public static double getMitjanaTemps (Temporitzador[] temporitzadors)
    {
        // Agafa tots els temps dels temporitzadors i calcula la mitjana
        return stream(temporitzadors).map(Temporitzador :: getTemps).collect(toList()).stream().mapToDouble(d -> d).average().orElse(0.0);
    }
}
