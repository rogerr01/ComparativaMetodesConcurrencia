package com.rogerr01;

public class Temporitzador
{
    // Referencia de temps en nanosegons que ens dona el sistema al iniciar el temporitzador
    private double tempsInici;

    // Diferencia entre el temps inicial i la referencia que ens dona el sistema a l'aturar el temporitzador
    private double tempsTotal;

    // Constructor buit ja que no se li ha de passar cap parametre
    public Temporitzador () {}

    // Guarda la referencia de temps inicial
    public void iniciar()
    {
       tempsInici = System.nanoTime();
    }

    // Calcula quants nanosegons han passat des de que es va agafar la referencia inicial
    public void aturar()
    {
        tempsTotal = System.nanoTime() - tempsInici;
    }

    // Retorna quin es el tempsTotal que hi ha enregistrar en el moment de consultar-ho i el passa a milisegons
    public double getTemps()
    {
        return tempsTotal / 1_000_000;
    }

}
