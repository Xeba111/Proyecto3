package com.codigo;

import java.awt.BorderLayout;
import java.awt.event.*;
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
    private JTextField apuestas;
    private JTextArea display; //Muestra las cartas
    private Socket connection; //Coneccion al servidor
    private Scanner input; //Input del servidor
    private Formatter output; //output para el servidor
    private String blackjackHost; //nombre para el servidor host
    private String numero; //numero de este jugador
    int decision = 0;
    boolean apostado = false;
    int dinero = 500;
    int apuestaInt;


    public BlackjackClient(String host) {
        blackjackHost = host;
        display = new JTextArea(15, 10);
        display.setEditable(false);
        add(new JScrollPane(display), BorderLayout.SOUTH);

        botones = new JPanel();
        apuestas = new JTextField("Ingresa la apuesta");
        pedir = new JButton("Pedir");
        reiniciar = new JButton("Salir");
        apostar = new JButton("Apostar");
        retirarse = new JButton("Dejar");

        botones.add(apostar);
        botones.add(pedir);
        botones.add(retirarse);
        botones.add(reiniciar);
        botones.add(apuestas);

        pedir.setEnabled(true);
        retirarse.setEnabled(true);
        apostar.setEnabled(true);
        reiniciar.setEnabled(true);
        apuestas.setEnabled(true);
        add(botones, BorderLayout.CENTER);


        numeroJugador = new JTextArea(3,10);
        numeroJugador.setEditable(false);
        add(numeroJugador, BorderLayout.NORTH);


        //display.setSize(300,200);
        this.setSize(500, 400);
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
                int anuncio;

                if (apostado) {


                    decision = 1;
                    output.format("%d\n", decision);
                    output.flush();

                    actualizacionCartas = input.nextLine();

                    display.append(actualizacionCartas + "\n");

                    valorCartas = input.nextLine();

                    display.append(valorCartas + "\n");

                    valorCartasInt = input.nextInt();
                    String limpiar = input.nextLine();

                    if (valorCartasInt > 21) {
                        apostar.setEnabled(false);
                        pedir.setEnabled(false);
                        retirarse.setEnabled(false);
                        display.append("PERDISTE! \n");
                        anuncio = 1;
                        output.format("%d\n", anuncio);
                        output.flush();
                        dinero = dinero - apuestaInt;
                        display.append("El jugador ahora tiene " + dinero + " de dinero. \n");
                    }
                    else
                    {
                        anuncio = 0;
                        output.format("%d\n", anuncio);
                        output.flush();
                    }

                    decision = 0;
                }
                else if (!apostado)
                {
                    display.append("TIENE QUE HACER UNA APUESTA ANTES \n");
                }
            }
        });

        //Dejar de pedir
        retirarse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (apostado)
                {
                    decision = 2;
                    output.format("%d\n", decision);
                    output.flush();
                }
                else if (!apostado)
                {
                    display.append("TIENE QUE HACER UNA APUESTA ANTES \n");
                }



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

                apostado = true;

                //ASUMIMOS QUE SIEMPRE SEA NUMEROS
                String apuesta = apuestas.getText();
                apuestaInt = Integer.parseInt(apuesta);

                display.append("La apuesta es de " + apuestaInt + "\n");

                output.format("%d\n", apuestaInt);
                output.flush();

                decision = 0;

            }
        });
    }
}



