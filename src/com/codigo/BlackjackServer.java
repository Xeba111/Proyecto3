package com.codigo;

import java.awt.BorderLayout;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class BlackjackServer extends JFrame
{
    private String[] deck = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
    private JTextArea outputArea;
    private Player[] players;
    private ServerSocket server;
    private int currentPlayer;
    private ExecutorService runGame;
    private Lock gameLock;
    private Condition otherPlayerConnected;
    private Condition otherPlayerTurn;
    private final static int[] numerosJugadores = {1,2,3,4,5,6,7,8,9,10};
    private final static String[] numeroJugadoreString = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    public BlackjackServer()
    {
        super("Blackjack server"); //Nombre ventana

        runGame = Executors.newFixedThreadPool(10);
        gameLock = new ReentrantLock();

        otherPlayerConnected = gameLock.newCondition();

        otherPlayerTurn = gameLock.newCondition();

        players = new Player[];

        currentPlayer = 0;

        try
        {
            server = new ServerSocket(12345);

        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
            System.exit(1);
        }

        outputArea = new JTextArea();
        add(outputArea, BorderLayout.CENTER);
        outputArea.setText("Server esperando las conexiones");

        this.setSize(300, 300);
        setVisible(true);

    }

    public void execute()
    {
        //esperar a que todos se conecten
        for (int i = 0; i < players.length; i++)
        {
            try //Esperar conexión, crear jugador, inicializar Runnable
            {
                players[i] = new Player(server.accept(), i);
                runGame.execute(players[i]);
            }
            catch (IOException ioException)
            {
                ioException.printStackTrace();
                System.exit(1);
            }
        }

        gameLock.lock();

        try
        {
            players[numerosJugadores[0]].setSuspended(false);
            otherPlayerConnected.signal();_
        }
        finally
        {
            gameLock.unlock();
        }
    }
    
    private void displayMessage(final String messageToDisplay)
    {
        //mostrar mensaje de un evento de interaccion en el thread de ejecucion?
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        outputArea.append(messageToDisplay);
                    }
                }
        );
    }

    private class Player implements Runnable
    {
        private Socket connection;
        private Scanner input;
        private Formatter output;
        private int playerNumber; //Trackea qué jugador es
        private boolean suspended = true; //Si el thread está suspendido o no

        public Player(Socket socket, int number)
        {
            playerNumber = number;
            connection = socket;

            try
            {
                input = new Scanner(connection.getInputStream());
                output = new Formatter(connection.getOutputStream());
            }
            catch (IOException ioException)
            {
                ioException.printStackTrace();
                System.exit(1);
            }
        }

        //Muestra mensaje de que otro jugador hizo su jugada
        public void otherPlayerMoved(int carta)
        {
            output.format("El oponente se movió \n");
            output.flush();
        }

    }


}
