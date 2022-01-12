package com.rogerr01;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.Color.*;
import static java.awt.Font.*;
import static com.rogerr01.Taula.*;

public class Boto extends JButton
{
    // El color del botó
    private final Color color;

    // Crear un botó a partir d'un text i un color determinats
    public Boto (String text, Color color)
    {
        // Crea un JButton amb el text passat per parametre
        super(text);

        // Color del botó
        this.color = color;

        // El fa opac per donar-li un color de fons
        setOpaque(true);

        // La font per defecte será Sans Serif 16 en negreta
        setFont(new Font(SANS_SERIF, BOLD,16));

        // Un borde de color negre i dos pixels de gruix
        setBorder(new LineBorder(BLACK,2));

        // El color de fons per defecte és blanc
        setBackground(WHITE);

        // Cada botó tindrá el color de lletra que se li ha passat per parametre
        setForeground(color);

        // Les mides dels botons per defecte serán les d'una casella de la taula
        // tot i que una mica més petit en llargada per deixar marge entre ells
        setSize((int) getMidaColumna() - 2, (int) getMidaFila());

        // ActionListener
        addActionListener(new Accions());

        // Mouse Listener per donar un efecte visual al botó en posar-li el cursos a sobre
        addMouseListener(new java.awt.event.MouseAdapter() {

            // Activar l'efecte al colocar el cursor
            public void mouseEntered(java.awt.event.MouseEvent evt) {

                // S'aplica només en cas de que el botó estigui habilitat
                if (isEnabled())
                {
                    activarHover();
                }
            }

            // Treure l'efecte quan el cursor deixa d'estar sobre el botó
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                desactivarHover();
            }
        });
    }

    // Canviar l'estil del botó a l'estil hover
    private void activarHover()
    {
        // S'intercanvien els color de fons i de lletra
        setBackground(color);
        setForeground(WHITE);

        // Es mostra la icona del cursor d'una forma que indica que l'objecte es clicable
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Canviar l'estil del botó a l'estil original
    private void desactivarHover()
    {
        // Es tornen a intrercanviar els colors
        setBackground(WHITE);
        setForeground(color);

        // La icona del cursos torna a ser la que el sistema posa per defecte
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    // Habilitar o deshabilitar el botó
    @Override
    public void setEnabled(boolean b)
    {
        super.setEnabled(b);

        // Si es desactiva el botó s'anula l'efecte de hover inmediatament
        if (!b)
        {
            desactivarHover();
        }
    }

    // ActionListener dels botons
    private static class Accions implements ActionListener
    {

        @Override
        public void actionPerformed (ActionEvent e)
        {
            // Botó que s'ha clicat
            Boto origen = (Boto) e.getSource();

            // Segons el text del botó s'elegeix l'acció a realitzar
            switch (origen.getText())
            {
            case "INICI"   :  Main.iniciar(); break;
            case "ATURA"   :  Main.aturar();  break;
            case "SORTIDA" :  Main.sortir();  break;
            }
        }
    }
}
