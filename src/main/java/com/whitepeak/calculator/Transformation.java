package com.whitepeak.calculator;

import com.whitepeak.calculator.enums.Operators;

import static com.whitepeak.calculator.enums.Operators.*;


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
        String safeInput = formatInput();
        if(!safeInput.equalsIgnoreCase("Invalid input")) {
            for (int i = 0; i < safeInput.length(); i++) {
                char ch = safeInput.charAt(i);
                Operators op = Operators.get(ch);
                switch (op) {
                    case PLUS:
                    case MINUS:
                        operator(ch, 1);
                        break;

                    case TIMES:
                    case DIVIDE:
                    case EXP:
                        operator(ch, 2);
                        break;

                    case LPAR:
                        stack.push(ch);
                        negOrPos = true;
                        if(!lastChar.equals("") && !lastChar.equals(" ")){
                            output = output + " " ;
                            lastChar = " ";
                        }
                        break;

                    case RPAR:
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
                if (topOperation == LPAR.toChar()) {
                    stack.push(topOperation);
                    if(!lastChar.equals(" ")){
                        output = output + " ";
                        lastChar = " ";
                    }
                    break;
                } else {
                    int topType;
                    if (topOperation == PLUS.toChar() || topOperation == MINUS.toChar())
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
            if (ch == LPAR.toChar())
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

    public String formatInput () {
	   StringBuffer formatted = new StringBuffer(input);
	   if(formatted.length()>0){
		   //fix input starting with + or -
		   if(formatted.charAt(0) == PLUS.toChar() || formatted.charAt(0) == MINUS.toChar())
			   formatted.insert(0, '0');

		   //int openPars = 0;
	       //int closedPars = 0;
	       for(int x=0; x<formatted.length(); x++){
	    	   switch(Operators.get(formatted.charAt(x))){
	               case LPAR:
	                   //openPars++;
	                   if(x>1 &&(formatted.charAt(x-1) == '.' || Character.isDigit(formatted.charAt(x-1)) ))
	                       return "Invalid input";
	                   break;

	               case RPAR:
	                   //closedPars++;
	                   if((x+1<formatted.length())&&(formatted.charAt(x+1) == '.' || Character.isDigit(formatted.charAt(x+1)) ))
	                	   return "Invalid input";
	                   break;

	               //fix input with redundant or consecutive operators
	               case PLUS:
	            	   if(x>1){
		            	   if((formatted.charAt(x-1) == MINUS.toChar()) || (formatted.charAt(x-1) == PLUS.toChar())){
		            		   formatted.deleteCharAt(x);
		            		   x--;
		            	   }
	            	   }
	            	   break;


	               case MINUS:
	            	   if(x>1){
		            	   if((formatted.charAt(x-1) == MINUS.toChar())){
		            		   formatted.setCharAt(x, PLUS.toChar());
		            		   formatted.deleteCharAt(x-1);
		            		   x--;
		            		   break;
		            	   }

		            	   if(x>0 && (formatted.charAt(x-1) == PLUS.toChar())){
		            		   formatted.deleteCharAt(x-1);
		            		   x--;
		            	   }
	            	   }
	            	   break;
        	   }

    	   }
	       for (int x = 0; x<formatted.length(); x++){
		       if(x>1){
		    	   if((formatted.charAt(x-1) == TIMES.toChar()) || (formatted.charAt(x-1) == DIVIDE.toChar()) || (formatted.charAt(x-1) == EXP.toChar())){
	        		   if(formatted.charAt(x) != LPAR.toChar()){
	        			   formatted.insert(x, LPAR);
		        		   int beforeClosing = 2;
		        		   for(int i=0; i<formatted.length();i++){
		        			   if(x+2+i>formatted.length()-1)
		        				   break;
		        			   if(formatted.charAt(x+2+i)=='.' || Character.isDigit(formatted.charAt(x+2+i)))
		        				   beforeClosing++;
		        			   else
		        				   break;
		        		   }

		        		   if(x+beforeClosing>formatted.length()-1)
		        			   formatted.append(RPAR);
		        		   else
		        			   formatted.insert(x+beforeClosing, RPAR);
	        		   }

	    		   }
	    	   }
	       }
       }
       return String.valueOf(formatted);
   }
}
