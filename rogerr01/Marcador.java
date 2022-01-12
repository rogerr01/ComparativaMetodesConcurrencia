package com.rogerr01;

import javax.swing.*;
import java.awt.*;

import static java.awt.Color.*;
import static javax.swing.BorderFactory.*;
import static javax.swing.JLabel.*;

public class Marcador extends JPanel
{
    // Valor maxim del marcadador
    private final static double MAX = 100_000_000;

    // TextField on mostra les dadaes
    JTextField tf = new JTextField();

    // Barra de progres
    private double progres;

    // Color de fons del marcador
    private Color colorFons;

    // Capçaleres
    public Marcador(String text)
    {
        this(text,0,0);
        setBackground(BLACK);
        tf.setForeground(WHITE);
    }

    // Marcadors
    public Marcador (String text, int posicioX, int posicioY)
    {
        super();

        // Fer que el TextField ocupi tot el marcador
        setLayout(new GridLayout(1,1));

        // Donar-li color de fons
        setBackground(WHITE);

        // Colocar el text
        tf.setText(text);

        // Crear el borde
        tf.setBorder(createMatteBorder(1, posicioX, posicioY, 1, BLACK));

        // Aliniar el contingut
        tf.setHorizontalAlignment(CENTER);

        // Bloquejar l'edició manual
        tf.setEditable(false);

        // Fer-lo transparent
        tf.setOpaque(false);

        // Afegir el TextField al marcador
        add(tf);
    }


    // Modificar el valor del marcador
    public void setText(String s)
    {
        tf.setText(s);
    }


    // Mostrar la barra de progres (per als marcadors que compten fins a 100.000.000)
    public void mostrarProgres(double valor)
    {
        // El percentatge és la quantitat del marcador que s'ha d'emplenar
        double percentatge = (valor / MAX);

        // El progres és el percentatge aplicat a la mida del marcador
        progres = getWidth() * percentatge;

        // El color de fons sempre és verd, pero puja d'intensitat segons avança el comptador
        colorFons =  Funcions.generarColor(percentatge);

    }

    @Override
    protected void paintComponent (Graphics g)
    {
        super.paintComponent(g);
        g.setColor(colorFons);

        // Cada cop que es fagi automaticament el repaint del marcador, mostrara la quantitat de progress,
        // ja que va dibuixant un rectangle de color verd que cada cop será més gran, i quan s'arribi al maxim
        // ja haurá emplenat tot el marcador
        g.fillRect(0,0, (int) progres, getHeight());
    }
}


