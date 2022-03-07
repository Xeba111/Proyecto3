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
    private JTextArea numeroJugador;
    private JTextArea display; //Muestra las cartas
    private Socket connection; //Coneccion al servidor
    private Scanner input; //Input del servidor
    private Formatter output; //output para el servidor
    private String blackjackHost; //nombre para el servidor host
    private String numero; //numero de este jugador
    private final static int[] numerosJugadores = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    int decision = 0;

    public BlackjackClient(String host) {
        blackjackHost = host;
        display = new JTextArea(15, 10);
        display.setEditable(false);
        add(new JScrollPane(display), BorderLayout.SOUTH);

        botones = new JPanel();


        pedir = new JButton("Pedir");
        reiniciar = new JButton("Salir");
        apostar = new JButton("Apostar");
        retirarse = new JButton("Dejar de pedir");

        botones.add(apostar);
        botones.add(pedir);
        botones.add(retirarse);
        botones.add(reiniciar);

        pedir.setEnabled(true);
        retirarse.setEnabled(true);
        apostar.setEnabled(true);
        reiniciar.setEnabled(true);
        add(botones, BorderLayout.CENTER);


        numeroJugador = new JTextArea(3,10);
        numeroJugador.setEditable(false);
        add(numeroJugador, BorderLayout.NORTH);


        //display.setSize(300,200);
        this.setSize(400, 500);
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

        numeroJugador.setText("Eres el jugador: " + numero + "\n");
        display.append(cartas + "\n");

        pedir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String actualizacionCartas;
                String valorCartas;
                int valorCartasInt;

                decision = 1;
                output.format("%d\n", decision);
                output.flush();

                actualizacionCartas = input.nextLine();

                display.append(actualizacionCartas + "\n");

                valorCartas = input.nextLine();

                display.append(valorCartas + "\n");

                valorCartasInt = input.nextInt();

                String limpiar = input.nextLine();

                if (valorCartasInt > 21)
                {
                     apostar.setEnabled(false);
                     pedir.setEnabled(false);
                     retirarse.setEnabled(false);
                     display.append("PERDISTE! \n");

                }

//                if (perder == 1)
//                {
//                     apostar.setEnabled(false);
//                     pedir.setEnabled(false);
//                     retirarse.setEnabled(false);
//                     System.out.println("YA PERDIO");
//                     display.append("PERDISTE! \n");
//                }
//                else
//                {
//                    display.append("\n");
//                }
//
//                input.reset();
                decision = 0;
            }
        });

        //Dejar de pedir
        retirarse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                decision = 2;
                output.format("%d\n", decision);
                output.flush();
                decision = 0;
            }
        });

        reiniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                decision = 3;
                output.format("%d\n", decision);
                output.flush();
                decision = 0;
                System.exit(1);
            }
        });

        apostar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decision = 4;
                output.format("%d\n", decision);
                output.flush();
                decision = 0;

            }
        });
    }
}


//    private void displayMessage(final String messageToDisplay) {
//        SwingUtilities.invokeLater(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        display.append(messageToDisplay);
//                    }
//                }
//        );
//    }
//}


