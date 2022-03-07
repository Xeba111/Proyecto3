package com.codigo;

import java.awt.BorderLayout;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Formatter;
import java.util.Random;
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

    private JTextArea outputArea;
    private JTextArea cartasArea;
    private Player[] players ;
    private ServerSocket server;
    private ExecutorService runGame;
    private Lock gameLock;
    private final static int[] numerosJugadores = {0,1,2,3,4,5,6,7,8,9};
    private final static String[] numeroJugadoreString = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private String[] usedCards = new String[20];
    private int numberUsed = 0;

    public BlackjackServer()
    {
        super("Blackjack server"); //Nombre ventana

        runGame = Executors.newFixedThreadPool(10);
        gameLock = new ReentrantLock();
        players = new Player[10];

        try
        {
            server = new ServerSocket(8008);

        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
            System.exit(1);
        }

        //En la parte de abajo
        outputArea = new JTextArea();
        add(outputArea, BorderLayout.SOUTH);
        outputArea.setSize(400, 200);
        outputArea.setText("Server esperando las conexiones. \n");

        //En la parte de arriba
        cartasArea = new JTextArea();
        add(cartasArea, BorderLayout.NORTH);
        cartasArea.setSize(400, 200);
        cartasArea.setText("Aquí se mostrará el estado de los jugadores: \n");

        this.setSize(400, 400);
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

    private static String getRandom(String[] array, String[] used, int numberUsed)
    {
        int random = new Random().nextInt(array.length);
        used[numberUsed] = array[random];
        numberUsed += 1;
        return array[random];

    }

    private class Player implements Runnable
    {
        private int accionBoton = 0;
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



        public void run()
        {
            try
            {
                displayMessage("Player " + playerNumber + " conectado SERVER\n");
                output.format("%s\n", playerNumber);
                output.flush();

                Deck d= new Deck();
                d.createCards();
                Hand handHuman = new Hand();

                handHuman.addCard(d.getCard());
                handHuman.addCard(d.getCard());

                Hand handServer = new Hand();
                handServer.addCard(d.getCard());
                handServer.addCard(d.getCard());
                while (handServer.getValue()<=16){
                    handServer.addCard(d.getCard());
                }
                int validar= 0;
                if (handHuman.getValue()> handServer.getValue() && handHuman.getValue() <= 21){
                  validar=1;
                }
                if (handHuman.getValue()< handServer.getValue() && handServer.getValue() <= 21){
                    validar=1;
                }

                int casa= handServer.ganar() + validar;
                int humano= handHuman.ganar()+ validar;

                String cartas= handHuman.toString();
                output.format("Recibes las cartas: " + cartas + "\n");
                output.flush();
                cartasArea.append("El jugador " + playerNumber + " recibe las cartas: " + handHuman.toString()+".\n");

                while (true)
                    {
                        accionBoton = input.nextInt();

                        if (accionBoton == 1)
                        {
                            handHuman.addCard(d.getCard());

                            String cartasActualizacion = handHuman.toString();
                            output.format("Ahora tienes las cartas: " + cartasActualizacion + "\n");
                            output.flush();
                            cartasArea.append("El jugador " + playerNumber + " tiene las cartas: " + handHuman.toString()+".\n");
                        }
                    }

            }
            finally
            {
                try
                {
                    connection.close();
                }
                catch(IOException ioException)
                {
                    ioException.printStackTrace();
                    System.exit(1);
                }
            }

        }


    }


}
