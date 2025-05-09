package etapas.codigoIntermedio;

import java.util.List;
import java.util.Stack;

import models.Token;

public class StackOperadores
{
	public static int getPrioridad(String operador)
	{
		List<String> aritmeticos60 = List.of("*", "/", "%");
		if (aritmeticos60.contains(operador)) {
			return 60; 
		}
		
		List<String> aritmeticos50 = List.of("+", "-"); 
		if (aritmeticos50.contains(operador)) {
			return 50; 
		}

		List<String> relacionales40 = List.of("<", ">", "<=", 
		">=", "==", "!="); 
		if (relacionales40.contains(operador)) { 
			return 40; 
		}

		if (operador.equals( "!")) {
			return 30; 
		} 
 
		if (operador.equals("&&")) {
			return 20; 
		}

		if (operador.equals("||")) {
			return 10; 
		}
		
		return 0;
	}

	public static boolean prioridadIsGreaterThan(int prioridadPeekToken, int prioridadNextToken) {
		return prioridadPeekToken >= prioridadNextToken; 
	}

	public static void emptyAndMoveToVci(Stack<Token> operadores, List<Token> vci)
	{
		while (!operadores.isEmpty())
		{
			Token token = operadores.pop(); 
			if (token.numToken.equals("-73")) {
				continue; 
			}

			vci.add(token); 
		}
	}

	public static void moveToOperadores(List<Token> vci, Stack<Token> operadores, Token token) 
	{
		/*
		 * Si la pila de operadores está vacía o el token siguiente es '(', entonces
		 * el token siguiente entra directo a la pila de operadores.
		 * 
		 * Si no, entonces compara la prioridad del token peek de la pila de operadores
		 * con el token siguiente:
		 * 
		 * Si la prioridad del token peek es mayor que la del token siguiente, entonces
		 * pasa el token peek al vci y entra el token siguiente a la pila de operadores.
		 * 
		 * El proceso se repite hasta que la prioridad del token peek sea menor a la del
		 * token siguiente.
		 */

		
		if (operadores.isEmpty() ||
		token.numToken.equals("-73")) 
		{
			operadores.add(token);
		} 
		else if (operadores.getLast().numToken.equals("-73"))
		{
			operadores.add(token);  
		}
		else 
		{
			Token peekToken = operadores.getLast();
			int prioridadPeekToken = StackOperadores.getPrioridad(peekToken.lexema);
			int prioridadNextToken = StackOperadores.getPrioridad(token.lexema);

			boolean prioridadIsGreater = StackOperadores.prioridadIsGreaterThan(prioridadPeekToken, prioridadNextToken);

			if (prioridadIsGreater) 
			{
				do {
					operadores.remove(operadores.indexOf(peekToken));

					if (peekToken.numToken.equals("-73")) {
						continue; 
					}

					vci.add(peekToken);

					if (operadores.isEmpty()) {
						break;
					}

					peekToken = operadores.getLast();
					prioridadPeekToken = StackOperadores.getPrioridad(peekToken.lexema);
					prioridadNextToken = StackOperadores.getPrioridad(token.lexema);

					prioridadIsGreater = StackOperadores.prioridadIsGreaterThan(prioridadPeekToken, prioridadNextToken);
				} while (prioridadIsGreater);
			}

			operadores.add(token);
		}
	}
}
