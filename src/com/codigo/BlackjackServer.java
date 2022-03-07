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
import javax.swing.*;


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
    Hand handServer = new Hand();
    Deck d= new Deck();

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
        outputArea = new JTextArea(10,10);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);
        //outputArea.setSize(400, 200);
        outputArea.append("Server esperando las conexiones. \n");
        outputArea.setEditable(false);

        //En la parte de arriba
        cartasArea = new JTextArea(15,10);
        add(new JScrollPane(cartasArea), BorderLayout.NORTH);
        //cartasArea.setSize(400, 200);
        cartasArea.setText("Aquí se mostrará el estado de los jugadores: \n");
        cartasArea.setEditable(false);

        this.setSize(500, 500);
        setVisible(true);

        d.createCards();
        handServer.addCard(d.getCard());
        handServer.addCard(d.getCard());

        cartasArea.append("El servidor recibe las cartas recibe las cartas: " + handServer.toString()+"\n");
        cartasArea.append("Con un valor de " + handServer.getValue() + "\n");

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
        private int perder;

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
                outputArea.append("Player " + playerNumber + " conectado SERVER\n");
                output.format("%s\n", playerNumber);
                output.flush();


                Hand handHuman = new Hand();

                handHuman.addCard(d.getCard());
                handHuman.addCard(d.getCard());


                String cartas = handHuman.toString();
                output.format("Recibes las cartas: " + cartas + "\n");
                output.flush();
                cartasArea.append("El jugador " + playerNumber + " recibe las cartas: " + handHuman.toString()+"\n");


                while (true)
                    {
                        accionBoton = input.nextInt();
                        String limpiar = input.nextLine();

                        if (accionBoton == 1)
                        {
                            handHuman.addCard(d.getCard());

                            String valorCartas = Integer.toString(handHuman.getValue());
                            String cartasActualizacion = handHuman.toString();
                            int valorVerificacion = handHuman.getValue();

                            output.format("Ahora tienes las cartas: " + cartasActualizacion + "\n");
                            output.flush();

                            output.format("Esta mano tiene un valor de " + valorCartas +"\n");
                            output.flush();

                            output.format("%d\n", valorVerificacion);
                            output.flush();

//                            if (valorVerificacion > 21)
//                            {
//                                perder = 1;
//                                output.format("%d\n", perder);
//                                output.flush();
//                            }
//                            else
//                            {
//                                perder = 0;
//                                output.format("%d\n", perder);
//                                output.flush();
//                                continue;
//                            }

//                            output.format("%d\n", valorVerificacion);
//                            output.flush();

                            cartasArea.append("El jugador " + playerNumber + " tiene las cartas: " + handHuman.toString()+"\n");

                            accionBoton = 0;
                        }

                        if(accionBoton == 2)
                        {
                            cartasArea.append("El jugador número " + playerNumber + " se ha retirado"+".\n");
                            accionBoton = 0;
                        }
                        if(accionBoton == 3)
                        {
                            d.createCards();
                            cartasArea.append("El jugador número " + playerNumber + " se ha retirado"+".\n");
                            accionBoton = 0;
                        }
                        if(accionBoton == 4)
                        {

                            accionBoton = 0;

                        }

                        input.reset();

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
