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
import java.awt.event.*;
import javax.swing.*;


public class BlackjackClient extends JFrame implements Runnable {

    private JButton pedir;
    private JButton apostar;
    private JButton retirarse;
    private JButton reiniciar;
    private JPanel botones;
    private JPanel botones1;
    private JTextField numeroJugador;
    private JTextField jtextfield;
    private JTextArea display; //Muestra las cartas
    private Socket connection; //Coneccion al servidor
    private Scanner input; //Input del servidor
    private Formatter output; //output para el servidor
    private String blackjackHost; //nombre para el servidor host
    private String numero; //numero de este jugador
    private final static int[] numerosJugadores = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    int decision = 0;







    public BlackjackClient(String host) {

        botones1= new JPanel();
        blackjackHost = host;
        display = new JTextArea(4, 30);
        JTextField jtextfield = new JTextField();

        jtextfield.setText("Inserte su apuesta");
        jtextfield.setEditable(true);
        jtextfield.setHorizontalAlignment(JTextField.LEFT);
        display.setEditable(false);
        add(new JScrollPane(display), BorderLayout.SOUTH);


        botones = new JPanel();


        pedir = new JButton("Pedir");
        reiniciar = new JButton("Reiniciar");
        apostar = new JButton("Apostar");
        retirarse = new JButton("Retirarse");
        botones.add(jtextfield, null);
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



        this.setSize(500, 500);
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
    public void run()
    {

        numero = input.nextLine();
        String cartas = input.nextLine();

        numeroJugador.setText("Eres el jugador \n" + numero + "\n");
        display.append(cartas + "\n");

            pedir.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String actualizacionCartas;

                    decision = 1;
                    output.format("%d\n", decision);
                    output.flush();

                    actualizacionCartas = input.nextLine();

                    display.append(actualizacionCartas + "\n");

                    decision = 0;
                }
            });

        retirarse.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {


                decision = 2;
                output.format("%d\n", decision);
                output.flush();
                decision = 0;
                System.exit(0);
            }
        });

        reiniciar.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                decision = 3;
                output.format("%d\n", decision);
                output.flush();
                decision = 0;

            }
        });

        apostar.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String valor= jtextfield.getText();


                decision = 3;
                output.format("%d\n", decision);
                output.flush();
               // if ()
               // a = input.nextLine();

                display.append("Valor a apostar"+valor + "\n");
                decision = 0;

            }
        });


//        while (true)
//        {
//            if (decision == 1)
//            {
//                String actualizacionCartas = input.nextLine();
//
//                display.append(actualizacionCartas + "\n");
//
//                decision = 0;
//            }
//        }


//                        while(true)
//                        {
//                            if (decision == 1)
//                            {
//                                String actualizacionCartas = input.nextLine();
//
//                                display.append(actualizacionCartas + "\n");
//                                System.out.println(actualizacionCartas);
//                            }
//                        }


                    }



//    private void processMessage(String message) {
//        if (message.equals("Recibes una carta ")) {
//            displayMessage("Carta valida: \n");
//        } else if (message.equals("Te retiras del juego.")) {
//            displayMessage("Te retiras del juego. \n");
//        }
//
//    }


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








