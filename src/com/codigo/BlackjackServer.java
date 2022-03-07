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
import javax.swing.*;


public class BlackjackServer extends JFrame
{

    private JTextArea outputArea;
    private JTextArea cartasArea;
    private Player[] players ;
    private ServerSocket server;
    private ExecutorService runGame;
    private Lock gameLock;
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
        private int apuesta;


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

                        if (accionBoton == 1)
                        {
                            handHuman.addCard(d.getCard());

                            String valorCartas = Integer.toString(handHuman.getValue());
                            String cartasActualizacion = handHuman.toString();
                            int valorVerificacion = handHuman.getValue();
                            int anuncio;

                            output.format("Ahora tienes las cartas: " + cartasActualizacion + "\n");
                            output.flush();

                            output.format("Esta mano tiene un valor de " + valorCartas +"\n");
                            output.flush();

                            output.format("%d\n", valorVerificacion);
                            output.flush();

                            cartasArea.append("El jugador " + playerNumber + " tiene las cartas: " + handHuman.toString()+"\n");

                            anuncio = input.nextInt();
                            String limpiar = input.toString();

                            if(anuncio == 1)
                            {
                                cartasArea.append("El jugador número " + playerNumber + " pierde por BUST!\n");
                                cartasArea.append("El jugador número " + playerNumber + " ha perdido un total de " + apuesta + "\n");
                            }
                            else if (anuncio == 0)
                            {
                                cartasArea.append("");
                            }

                            accionBoton = 0;
                        }

                        if(accionBoton == 2)
                        {
                            int valorHumano = handHuman.getValue();
                            int valorServer = handServer.getValue();
                            cartasArea.append("El jugador número " + playerNumber + " tiene un valor de " + valorHumano +".\n");
                            while (valorHumano > valorServer)
                            {
                                if(valorServer < 15)
                                {
                                    handServer.addCard(d.getCard());
                                }
                                else
                                {
                                    break;
                                }
                                valorServer = handServer.getValue();
                            }

                            valorServer = handServer.getValue();

                            cartasArea.append("El servidor tiene las cartas: " + handServer.toString()+"\n");
                            cartasArea.append("Con un valor de " + handServer.getValue() + "\n");

                            if(valorServer > 21)
                            {
                                cartasArea.append("La casa pierde por BUST!\n");
                                cartasArea.append("El jugador número " + playerNumber + " ha ganado un total de " + apuesta + "\n");
                            }
                            else if(valorHumano > 21 && valorServer < 21)
                            {
                                cartasArea.append("El jugador número " + playerNumber + " pierde por BUST!\n");
                                cartasArea.append("El jugador número " + playerNumber + " ha perdido un total de " + apuesta + "\n");
                            }
                            else if(valorServer > valorHumano)
                            {
                                cartasArea.append("El jugador número " + playerNumber + " pierde ante el server!\n");
                                cartasArea.append("El jugador número " + playerNumber + " ha perdido un total de " + apuesta + "\n");
                            }
                            else if(valorHumano > valorServer)
                            {
                                cartasArea.append("El jugador número " + playerNumber + " gana ante el server!\n");
                                cartasArea.append("El jugador número " + playerNumber + " ha ganado un total de " + apuesta + "\n");
                            }


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
                            apuesta = input.nextInt();
                            String limpiar = input.nextLine();

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
