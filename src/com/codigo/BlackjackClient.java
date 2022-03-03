package com.codigo;

import java.awt.BorderLayout;
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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class BlackjackClient extends JFrame implements Runnable
{
    private JTextField numeroJugador;
    private JTextArea display; //Muestra las cartas
    private Socket connection; //Coneccion al servidor
    private Scanner input; //Input del servidor
    private Formatter output; //output para el servidor
    private String blackjackHost; //nombre para el servidor host
    private String numero; //numero de este jugador
    private boolean myTurn; //determina de quién es el turno

    public BlackjackClient(String host)
    {
        blackjackHost = host;
        display = new JTextArea(4,30);
        display.setEditable(false);
        add(new JScrollPane(display), BorderLayout.SOUTH);


        numeroJugador = new JTextField();
        numeroJugador.setEditable(false);
        add(numeroJugador, BorderLayout.NORTH);

        this.setSize(300,300);
        setVisible(true);

        startClient();

    }

    //Inicializar el thread
    public void startClient()
    {
        try
        {
            //Coneccion al server
            connection = new Socket(InetAddress.getByName(blackjackHost), 12345);

            //Streams de input y output
            input = new Scanner(connection.getInputStream());
            output = new Formatter(connection.getOutputStream());
        }
        catch (IOException ioException)
        {
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

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        numeroJugador.setText("Eres el jugador \"" + numero + "\"");
                    }
                }
        );

        myTurn = (numero.equals)
    }

}
