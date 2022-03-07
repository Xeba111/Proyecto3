package com.codigo;

import java.util.Arrays;
import java.util.Collections;
public class Deck {
    Card cards[]= new Card[52];
    int position;

    public void createCards(){
        String symbols ="23456789TJQKA";
        String types = "CDTP";
        int index=0;

        for (int i=0; i< types.length();i++){
            for(int j=0;j< symbols.length();j++){
                Card tempCard = new Card();
                tempCard.setSymbol(symbols.charAt(j));
                tempCard.setType(types.charAt(i));
                this.cards[index]= tempCard;
                index ++;
            }
        }
        shuffleCards();
    }

    private void shuffleCards(){
        Collections.shuffle(Arrays.asList(this.cards));


    }
    public Card getCard(){
        if(position==51){
            System.out.println("No hay mas cartas para repartir");
            return null;
        }
        if (position > 26){
            shuffleCards();
        }
        Card tempCard = this.cards[position];
        position++;
        return tempCard;

    }

}
