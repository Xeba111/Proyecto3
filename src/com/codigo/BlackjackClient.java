package com.codigo;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class BlackjackClient extends JFrame implements Runnable {

    private JButton pedir;
    private JButton apostar;
    private JButton retirarse;
    private JButton reiniciar;
    private JPanel botones;
    private JTextField numeroJugador;
    private JTextArea display; //Muestra las cartas
    private Socket connection; //Coneccion al servidor
    private Scanner input; //Input del servidor
    private Formatter output; //output para el servidor
    private String blackjackHost; //nombre para el servidor host
    private String numero; //numero de este jugador
    private boolean myTurn; //determina de quién es el turno
    private final static int[] numerosJugadores = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};


    public BlackjackClient(String host) {
        blackjackHost = host;
        display = new JTextArea(4, 30);

        display.setEditable(false);
        add(new JScrollPane(display), BorderLayout.SOUTH);

        botones = new JPanel();


        pedir = new JButton("Pedir");
        reiniciar = new JButton("Reiniciar");
        apostar = new JButton("Apostar");
        retirarse = new JButton("Retirarse");

        botones.add(apostar);
        botones.add(pedir);
        botones.add(retirarse);
        botones.add(reiniciar);

        pedir.setEnabled(true);
        retirarse.setEnabled(true);
        apostar.setEnabled(true);
        reiniciar.setEnabled(true);
        add(botones, BorderLayout.CENTER);


        numeroJugador = new JTextField();
        numeroJugador.setEditable(false);
        add(numeroJugador, BorderLayout.NORTH);


        retirarse.addActionListener(new retirarsefuncion());
        apostar.addActionListener(new apostarfuncion());
        reiniciar.addActionListener(new reiniciarfuncion());

        this.setSize(300, 300);
        setVisible(true);

        startClient();

    }

    //Inicializar el thread
    public void startClient() {
        try {
            //Coneccion al server
            connection = new Socket(InetAddress.getByName(blackjackHost), 8008);

            //Streams de input y output
            input = new Scanner(connection.getInputStream());
            output = new Formatter(connection.getOutputStream());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        //Se crea thread trabajador para este cliente
        ExecutorService worker = Executors.newFixedThreadPool(1);
        worker.execute(this);

    }

    //Thread de control que permite el update del TextArea
    public void run() {
        numero = input.nextLine();

        String cartas = input.nextLine();
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {

                        numeroJugador.setText("Eres el jugador \n" + numero + "\n");
                        display.setText(cartas + "\n");
                        pedir.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int decision = 1;
                                output.format("%d\n", decision);
                                output.flush();

                                String cartas = input.nextLine();

                                display.setText(cartas + "\n");
                                System.out.println(cartas);

                            }
                        });


                    }
                }
        );

        myTurn = (numero.equals(String.valueOf(numerosJugadores[Integer.parseInt(numero)])));

        while (true) {
            if (input.hasNextLine())
                processMessage(input.nextLine());
        }
    }

    private void processMessage(String message) {
        if (message.equals("Recibes una carta ")) {
            displayMessage("Carta valida: \n");
            myTurn = true;
        } else if (message.equals("Te retiras del juego.")) {
            displayMessage("Te retiras del juego. \n");
        }

    }


    private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        display.append(messageToDisplay);
                    }
                }
        );
    }
}

    class reiniciarfuncion implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {


        }
    }



    class apostarfuncion implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {


        }
    }

    class retirarsefuncion implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {


        }
    }

