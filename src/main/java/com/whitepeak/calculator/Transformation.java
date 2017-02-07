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
        if(parenthesesControl()) {
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
        }
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
    
    public boolean parenthesesControl () {
        int openPars = 0;
        int closedPars = 0;
        for(int x=0; x<input.length(); x++){
            switch(input.charAt(x)){
                case '(':
                    openPars++;
                    if(x>0 &&(input.charAt(x-1) == '.' || Character.isDigit(input.charAt(x-1)) ))
                        return false;
                    break;
                case ')':
                    closedPars++;
                    if((x+1<input.length())&&(input.charAt(x+1) == '.' || Character.isDigit(input.charAt(x+1)) ))
                        return false;
                    break;
            }
        }
        return openPars == closedPars;
    }
}
