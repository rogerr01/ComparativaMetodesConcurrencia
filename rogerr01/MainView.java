package com.rogerr01;

import javax.swing.*;
import java.util.Arrays;

import static com.rogerr01.Taula.*;

public final class MainView extends JFrame
{

    // Elements
    private static final Taula taula = new Taula();
    private static final Boto btnInici = new Boto("INICI", Funcions.verd);
    private static final Boto btnAtura = new Boto("ATURA", Funcions.vermell);
    private static final Boto btnSortida = new Boto("SORTIDA", Funcions.blau);

    public MainView ()
    {
        setTitle("Actualització de dades concurrents");

        int WIDTH_PANTALLA = 900;
        int HEIGTH_PANTALLA = 700;

        setBounds(100,100, WIDTH_PANTALLA, HEIGTH_PANTALLA);

        // Posicionar elements
        setLayout(null);
        taula.setBounds(50,25, getWidthTaula(), getHeightTaula());

        int x = 50;
        int y = getHeightTaula() + 40;

        // Duplicar el tamany del botó d'inici ja que ocupa dos posicions
        btnInici.setSize(btnInici.getWidth() * 2, btnInici.getHeight());

        // Coloca els botons en la mainView
        for (Boto boto : Arrays.asList(btnInici, btnAtura, btnSortida))
        {
            boto.setLocation(x,y);
            add(boto);
            x += boto.getWidth() + 3 ;
        }

        // Bloquejar botó d'aturar
        btnAtura.setEnabled(false);

        // Afegir taula
        add(taula);
    }

    public static void canviarEstatBotons(boolean actiu)
    {
        btnInici.setEnabled(actiu);
        btnAtura.setEnabled(!actiu);
    }

    // Mostrar l'interficie grafica d'aquest panell
    public void init()
    {
        // Al tancar la finestra s'ha d'acabar l'execució del programa
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Mostrar la mainView
        setVisible(true);
    }

}
