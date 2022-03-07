package com.codigo;

public class Card {
    private char symbol;
    private char type;

    public void setSymbol(char s){
        this.symbol =s;

    }
    public char getSymbol(){
        return this.symbol;
    }

    public void setType(char t){
        this.type = t;

    }
    public char getType(){return this.type;}

    public  int getValue(){

        if (this.symbol == 'T') return 10;
        else if(this.symbol=='J') return 11;
        else if(this.symbol=='Q') return 12;
        else if(this.symbol=='K') return 13;
        else if(this.symbol=='A') return 1;
        else return Integer.parseInt(this.symbol+"");

    }

    public String toString(){
        return this.symbol+""+this.type;

    }

}