package com.codigo;

import javax.swing.JFrame;

public class BlackjackClientTest
{
    public static void main(String[] args)
    {
        BlackjackClient application;

        if(args.length ==0)
        {
            application = new BlackjackClient("127.0.0.1");
        }
        else
        {
            application = new BlackjackClient(args[0]);
        }

        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
