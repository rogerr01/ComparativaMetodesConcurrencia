package com.rogerr01;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.rogerr01.Comptador.Accio.*;

public final class Comptador
{

    // CONSTANTS
    private static final int LIMIT = 100_000_000;
    private static final int VELOCITAT_COMPTADOR = 1;
    private static final int NUM_FILS = 10;
    private static final Object lock = new Object();
    private static final ReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Metode METODE;
    private final ExecutorService exe = Executors.newFixedThreadPool(10);

    // Temporitzadors (un per cada fil)
    public final Temporitzador[] temporitzadors = new Temporitzador[10];

    // VALOR DEL COMPTADOR
    private int valorComptador;

    // VALOR DEL COMPTADOR SI EL METODE ES VOLATIL
    private volatile int valorComptadorV;

    // FLAGS (volatils)
    private volatile boolean comptadorAcabat = false;
    private volatile boolean comptadorPausat = false;

    // Crear un comptador amb el metode elegit
    public Comptador (Metode METODE)
    {
        valorComptador = 0;
        valorComptadorV = 0;
        this.METODE = METODE;
    }

    // Inicia els fils d'execucció, 5 per escriure i 5 per llegir
    public void init ()
    {
        // Si s'ha quedat bloquejada la lectura o escriptura les desbloqueja
        if (METODE == Metode.READWRITELOCK)
        {
            try
            {
                rwl.readLock().unlock();
                rwl.writeLock().unlock();
            }

            // Si ja estaven desbloquejats no s'ha de fer res
            catch (IllegalMonitorStateException ignored)
            {
            }
        }

        // Crea 10 temporitzadors, 1 per a cada fil
        for (int i = 0; i < temporitzadors.length; i++)
        {
            temporitzadors[i] = new Temporitzador();
        }

        // Iniciar els comptadors
        for (int i = 0; i < NUM_FILS; i++)
        {
            try
            {
                // Crea 10 fils, els d'index parell seran d'escriptura i els altres de lectura
                Accio accio = i % 2 == 0 ? ESCRIPTURA : LECTURA;

                // Executa el fil que acaba de crear
                exe.execute(new FilComptador(accio, temporitzadors[i]));
            }

            // Si no s'arribes a executar es repetiria la iteració fins a conseguir-ho
            catch (RejectedExecutionException e)
            {
                i--;
            }
        }
    }

    // Posa en pausa un comptador
    public void pausar()
    {
      comptadorPausat = true;
    }


    // Reanuda un comptador
    public void continuar()
    {
        // Comprova si encara no está acabat i si estaba pausat
        if (!comptadorAcabat & comptadorPausat)
        {
            // Continua amb l'execució del comptador
            comptadorPausat = false;
            init();
        }
    }

    // Finalitza un comptador (quan ha arribat al limit)
    public void acabar()
    {

        // Marca el comptador com acabat
        comptadorAcabat = true;

        // Indica que s'han d'acabar tots els fils d'execució
        exe.shutdown();

        try
        {
            // Si després de 10 milisegons encara queda algun actiu l'atura de cop
            if (!exe.awaitTermination(10, TimeUnit.MILLISECONDS))
            {
                exe.shutdownNow();
            }

        } catch (InterruptedException e)
        {
            // Si hi ha un error durant el procés, atura també tots els fils de cop
            exe.shutdownNow();
        }
    }

    // Calcula la mitjana de temps
    public double getMitjanaTemps ()
    {
        return Funcions.getMitjanaTemps(temporitzadors);
    }

    // Comprova si un comptador s'ha finalitzat
    public boolean estaAcabat ()
    {
        return comptadorAcabat;
    }

    // Retorna el valor del comptador depenen del metode usat
    public int getValorComptador ()
    {
        return METODE == Metode.VOLATILE ? valorComptadorV : valorComptador;
    }


    // METODES D'ACCÉS A DADES CONCURRENTS
    enum Metode
    {
        NO_METODE,
        VOLATILE,
        SYNCHRONIZED,
        READWRITELOCK
    }

    // ACCIO QUE POT REALITZAR UN FIL
    enum Accio
    {
        LECTURA,
        ESCRIPTURA
    }

    // Classe per a un fil d'execucció
    private class FilComptador extends Thread
    {
        // L'acció que realitza (lectura o escriptura)
        private final Accio ACCIO;

        // El seu temporitzador que permet anar enregistrant els milisegons que tarda
        private final Temporitzador temporitzador;

        private FilComptador (Accio ACCIO, Temporitzador temporitzador)
        {
            this.ACCIO = ACCIO;
            this.temporitzador = temporitzador;
        }

        // Metode run del fil, que s'executa al iniciar-lo
        @Override
        public void run ()
        {
            // Depenen del metode executa una comanda diferent
            switch (METODE)
            {
            case NO_METODE: runNoMetode(); break;
            case VOLATILE: runVolatil(); break;
            case SYNCHRONIZED: runSinc(); break;
            case READWRITELOCK: runLock(); break;
            }
        }

        // Metode 'No Mètode'
        private void runNoMetode()
        {
            // Bucle per als fils d'escriptura
            if (ACCIO == ESCRIPTURA)
            {
                // S'executa mentres no s'hagi acabat ni s'hagi pausat
                while (!comptadorAcabat & !comptadorPausat)
                {
                    // Inicia el temporitzador
                    temporitzador.iniciar();

                    // Realitza l'acció de comptar (incrementa el valor del comptador)
                    comptar();

                    // Atura el temporitzador i calcula el temps transcorregut en milisegons
                    temporitzador.aturar();
                }
            }

            // Bucle per als fils de lectura
            else
            {
                // S'executa mentres no s'hagi acabat ni s'hagi pausat
                while (!comptadorAcabat & !comptadorPausat)
                {
                    // Inicia el temporitzador
                    temporitzador.iniciar();

                    // Realitza l'acció de llegir
                    llegir(valorComptador);

                    // Atura el temporitzador i calcula el temps transcorregut en milisegons
                    temporitzador.aturar();
                }
            }
        }


        // Metode 'Volatil'
        private void runVolatil()
        {

            // Bucle per als fils d'escriptura
            if (ACCIO == ESCRIPTURA)
            {
                // S'executa mentres no s'hagi acabat ni s'hagi pausat
                while (!comptadorAcabat & !comptadorPausat)
                {
                    // Inicia el temporitzador
                    temporitzador.iniciar();

                    // Realitza l'acció de comptar (incrementa el valor del comptador)

                    // En aquest cas li passo 'null' com a parametre, ja que vull que
                    // incrementi el comptador especial que he creat per al metode volatil
                    // i per a diferenciar el metode comptar() del metode comptar(volatil)
                    // li passo qualsevol parametre i aixi executa l'altre metode amb
                    // el mateix nom però amb diferents arguments
                    comptar(null);

                    // Atura el temporitzador i calcula el temps transcorregut en milisegons
                    temporitzador.aturar();
                }
            }

            // Bucle per als fils de lectura
            else
            {
                // S'executa mentres no s'hagi acabat ni s'hagi pausat
                while (!comptadorAcabat & !comptadorPausat)
                {
                    // Inicia el temporitzador
                    temporitzador.iniciar();

                    // Realitza l'acció de llegir (el valor del comptador volatil)
                    llegir(valorComptadorV);

                    // Atura el temporitzador i calcula el temps transcorregut en milisegons
                    temporitzador.aturar();
                }
            }
        }

        // Metode 'sincronitzat'
        private void runSinc()
        {
            // Bucle per als fils d'escriptura
            if (ACCIO == ESCRIPTURA)
            {
                while (!comptadorAcabat & !comptadorPausat)
                {

                    // Comença a comptar abans de realitzar el bloqueix,
                    // per tant tots els fils estará sumant temps mentres
                    // esperen per passar a l'execucció de la seguent comanda,
                    // i el temps mitjá será molt més gran que en els metodes
                    // anteriors on no hi havia bloqueix
                    temporitzador.iniciar();

                    // Crea el bloc de codi amb el bloqueix
                    synchronized (lock)
                    {
                        // Només un fil a la vegada podrá executar l'acció de comptar
                        comptar();
                    } // Finalitza el bloqueix

                    // Atura el temporitzador
                    temporitzador.aturar();
                }
            }

            // Bucle per als fils de lectura
            else
            {
                while (!comptadorAcabat & !comptadorPausat)
                {
                    // Comença a comptar abans de realitzar el bloqueix
                    temporitzador.iniciar();

                    // Crea el bloc de codi amb el bloqueix
                    synchronized (lock)
                    {
                        // Només un fil a la vegada podrá llegir el valor del comptador
                        llegir(valorComptador);
                    } // Finalitza el bloqueix

                    // Atura el temporitzador
                    temporitzador.aturar();
                }
            }
        }

        // Metode 'Bloqueix Lectura / Escriptura'
        private void runLock()
        {
            if (ACCIO == ESCRIPTURA)
            {
                while (!comptadorAcabat & !comptadorPausat)
                {
                    // Inicia el comptador abans del bloqueix
                    temporitzador.iniciar();

                    // Bloqueja l'escriptura
                    rwl.writeLock().lock();

                    // Incrementa el valor del comptador, peró com que
                    // la escriptura está bloquejada nomes ho podra fer
                    // un fil i els demés s'hauran d'esperar
                    comptar();

                    // Desbloquja l'escriptura per permetre que el
                    // seguent fil pugui incrementar el comptador de forma
                    // sincronitzada
                    rwl.writeLock().unlock();

                    // Atura el temporitzador just després de que s'hagi
                    // realitzat l'escriptura
                    temporitzador.aturar();
                }
            }
            else
            {
                while (!comptadorAcabat & !comptadorPausat)
                {
                    // Iniciar el temporitzador
                    temporitzador.iniciar();

                    // Bloquejar la lectura
                    rwl.readLock().lock();

                    // Només un fil podrá llegir ja que
                    // quan els demés arriben aqui la lectura
                    // está bloquejada i per tant s'han d'esperar
                    llegir(valorComptador);

                    // Desbloquejar la lectura per a que pugui
                    // continuar el seguent fil
                    rwl.readLock().unlock();

                    // Aturar el temporitzador
                    temporitzador.aturar();
                }
            }
        }


        // Comptar en el valorComptador normal
        private void comptar ()
        {
            // Si el valor del comptador no ha superat el limit
            if (valorComptador < LIMIT)
            {
                // Incrementa el valor del comptador
                valorComptador += VELOCITAT_COMPTADOR;
            }

            // Si s'ha arribat al valor limit, s'atura l'execucció en lloc de incrementar
            // més el valor, d'aquesta forma s'assegura que casi sempre el valor del comptador
            // es quedará aturat en la posició excta que se li ha indiciat, i per tant será en
            // aquest cas de 100.000.000 (tot i que en el No Metode pot donar valors aproximats)
            else
            {
                // Atura el temporitzador, ja que al executar després la funció 'acabar()'
                // es trencara el bucle en el que estaba, i per tant el temporitzador seguiria
                // actiu per a sempre i el seu valor seria incorrecte
                temporitzador.aturar();

                // Finalitza l'execució dels 10 fils del comptador actual
                acabar();
            }
        }

        // Comptar en el valorComptador volatil (el parametre permet diferenciar els metodes)
        private void comptar (Object o)
        {
            // Si el comptador volatil no ha superat el limit
            if (valorComptadorV < LIMIT)
            {
                // Incrementa el valor del contador volatil
                valorComptadorV += VELOCITAT_COMPTADOR;
            }

            // Si el valor ja ha arribat al maxim, finalitza la execució
            else
            {
                // Atura el temporitzador, ja que al executar després la funció 'acabar()'
                // es trencara el bucle en el que estaba, i per tant el temporitzador seguiria
                // actiu per a sempre i el seu valor seria incorrecte
                temporitzador.aturar();

                // Finalitza l'execució dels 10 fils del comptador actual
                acabar();
            }
        }

        // La funció de llegir només té com a proposit medir
        // el temps que es tarde en obtenir el valor d'una variable,
        // aixi que per a fer-ho comprovo per exemple si és major que 0
        // tot i que no fagi res amb el resultat de la comprovació
        private void llegir(int contador)
        {
            if (contador > 0);
        }
    }
}
