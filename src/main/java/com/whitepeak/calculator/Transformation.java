package com.whitepeak.calculator;

/**
 *
 * @author Stefano Galli
 */
public class Transformation {
    private TranStack stack;
    private String input, lastChar, output;
    private boolean negOrPos;
    
    
    public Transformation(String in) {
       input = in;
       output = "";
       negOrPos = true;
       lastChar = "";
       int stackSize = input.length();
       stack = new TranStack(stackSize);
    }
    
    public String toPostfix() {
        //String formString = this.formatInput();
        //System.out.println("FORMATTATA: "+formString+'\n');
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '+': 
                case '-':
                    operator(ch, 1); 
                    break;
                    
                case '*': 
                case '/':
                case '^':
                    operator(ch, 2); 
                    break; 
                
                case '(': 
                    stack.push(ch);
                    negOrPos = true;
                    if(!lastChar.equals("") && !lastChar.equals(" ")){
                        output = output + " " ;
                        lastChar = " ";
                    }
                    break;
                
                case ')': 
                    parentheses(); 
                    break;
                
                default: 
                    if((lastChar.equals("+")||lastChar.equals("-")||
                       lastChar.equals("*")||lastChar.equals("/")||
                       lastChar.equals("^"))&& negOrPos==false)
                        output = output + " " + ch;
                    else
                        output = output + ch;
                    lastChar = String.valueOf(ch);
                    negOrPos = false;
                    break;
            }
        }
        while (!stack.isEmpty()) {
            char ch = stack.pop();
            if(lastChar.equals(" "))
                output = output + ch;    
            else
                output = output + " " + ch;
            lastChar = String.valueOf(ch); 
                
        }
        System.out.println(output);
        return output; 
    }
    
    public void operator(char inputOp, int opType) {
        if(negOrPos == false){
            boolean wasEmpty = true;
            while (!stack.isEmpty()) {
                wasEmpty = false;
                char topOperation = stack.pop();
                if (topOperation == '(') {
                    stack.push(topOperation);
                    if(!lastChar.equals(" ")){
                        output = output + " ";      
                        lastChar = " ";
                    }
                    break;
                } else {
                    int topType;
                    if (topOperation == '+' || topOperation == '-')
                        topType = 1;
                    else
                        topType = 2;
                    if (topType < opType) {
                        stack.push(topOperation);
                        if(!lastChar.equals(" ")){
                            output = output + " ";     
                            lastChar = " ";
                        }
                        break;
                    } 
                    else{ 
                        if(!lastChar.equals(" "))
                            output = output + " " + topOperation;   
                        else
                            output = output + topOperation;
                        lastChar = String.valueOf(topOperation);
                        break;
                    }
                }
            }
            stack.push(inputOp);
            //aggiunge uno sazio solo se lo stack era vuoto e se l'ultimo carattere è diverso dall'iniziale o dallo spazio
            //serve per evitare errore 4-4 ----> 44 -
            if (wasEmpty == true && !lastChar.equals("") && !lastChar.equals(" ")){
                output = output + " ";
                lastChar = " ";
            }
            
        }
        else{
            output = output + inputOp;
            lastChar = String.valueOf(inputOp);
        }    //aggiunto questo if else in modo da assegnare non come operatore ma come segno del numero
    }
    
    public void parentheses() { 
        while (!stack.isEmpty()) {
            char ch = stack.pop();
            if (ch == '(') 
                break; 
            else{ 
                if(lastChar.equals(" "))
                    output = output + ch + " ";
                else
                    output = output + " " + ch + " ";
                lastChar = " ";
            }
        }
    }
    //evito di avere i - prima delle parentesi in fase di trasformazione
    public String formatInput(){
        StringBuilder formatted = new StringBuilder(input);
        String minus = "-";
        String plus= "+";
        boolean needFormat = true;
        int length = formatted.length();
        //itero tutta la stringa partendo dal secondo carattere
        for (int i=1; i<length; i++){
            String current = String.valueOf(formatted.charAt(i));
            String prev = String.valueOf(formatted.charAt(i-1));
            //valuto il caso in cui la stringa necessiterebbe di essere formattata
            if(current.equals("(") && prev.equals("-")){
                //inverto il segno prima della parentesi
                formatted.setCharAt(i-1, plus.charAt(0));
                int nested = 0;
                //itero l stringa a partire dal carattere dopo la tonda per cambiare ogni segno in parentesi
                //tralasciando le innestate, finchè la prima non viene chiusa
                int x = i + 1;
                while(nested != -1){
                    char ch = formatted.charAt(x);
                    switch(ch){
                        case '+':
                            if (nested == 0)
                                formatted.setCharAt(x, minus.charAt(0));
                            x++;
                            break;
                            
                        case '-':
                            if (nested == 0)
                                formatted.setCharAt(x, plus.charAt(0));
                            x++;
                            break;
                            
                        case '(':
                            nested = nested+1;
                            x++;
                            break;
                            
                        case ')':
                            nested = nested-1;
                            x++;
                            break;
                         
                        default:    //Se è un numero positivo senza segno ovvero numero intero
                                    //preceduto da ( o * o / o ^ devo trasformarlo in negativo e
                                    //metterlo in parentesi se preceduto da * o / o ^
                            try {
                                Integer.parseInt(String.valueOf(ch));
                                if(nested == 0){
                                    switch(formatted.charAt(x-1)){
                                        case '(':
                                            formatted.insert(x, "-");
                                            length++;
                                            x++;
                                            System.out.println("Inserito il meno " + '\n');
                                            break;
                                            
                                        case '*':
                                        case '/':
                                        case '^':
                                            formatted.insert(x, "-");
                                            formatted.insert(x, "(");
                                            length = length+2;
                                            x = x+2;
                                            //scorro per stabilire dove chiudere la parentesi, in base a quante cifre ha il n
                                            boolean close = false;
                                            int n = x + 1;
                                            while (close == false){
                                                try{
                                                    Integer.parseInt(String.valueOf(formatted.charAt(n)));
                                                    n++;
                                                 
                                                } catch (NumberFormatException e) {
                                                    //Se non trovo un numero chiudo la parentesi
                                                    formatted.insert(n, ")");
                                                    length++;
                                                    x++;
                                                    close = true;
                                                }
                                                
                                            }
                                            System.out.println("Inserito il meno " + '\n');
                                            break;
                                            
                                    }
                                }
                            } catch (NumberFormatException e) {
                                //non fare niente se trova . * / ^
                            }
                            x++; 
                            break;
                    }
                }
            }
        }
        return String.valueOf(formatted);
    }
}
