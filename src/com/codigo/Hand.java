package com.codigo;

import java.util.ArrayList;
public class Hand {
    ArrayList<Card> arr= new ArrayList <Card>();
    int position = 0;

    public char addCard(Card c){
        arr.add(c);

        return 0;
    }

    public String toString(){
        String hand="";
        for (int i=0; i< arr.size();i++)
            hand += arr.get(i).toString()+" ";

        return hand;

    }
    public int getValue(){
        int val=0;
        boolean ace=false;
        for(int i=0; i<arr.size();i++){
            Card tmpCard=arr.get(i);
            int cardValue = tmpCard.getValue();
            if (cardValue>10)
                cardValue=10;
            if(cardValue==1)
                ace=true;
            val += cardValue;
        }
        if (ace== true && val + 10 <= 21)
            val= val +10;
        return val;

    }
    public int ganar() {
        int validar = 0;

        if (getValue() == 21) {
            validar = 1;
        }

        if (getValue() > 21) {
            validar = 0;
        }
        return validar;
    }


}
