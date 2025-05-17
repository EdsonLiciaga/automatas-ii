package services;

import java.util.List;
import java.util.Stack;

import models.Token;

public class OperadoresService
{
	// En esta clase se definen los metodos utilizados para revisar operadores en la pila de operadores

	// Metodo que devuelve la prioridad de un token operador
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

		List<String> relacionales = List.of("<", ">", "<=", 
		">=", "==", "!="); 
		if (relacionales.contains(operador)) { 
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

	// Metodo que compara la prioridad del token peek de la pila de operadores con el token 
	// operador siguiente
	public static boolean prioridadIsGreaterThan(int prioridadPeekToken, int prioridadNextToken) {
		return prioridadPeekToken >= prioridadNextToken; 
	}

	// Metodo que vacía la pila de operadores y los mueve al vci
	public static void empty(Stack<Token> operadores, List<Token> vci)
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

	// Metodo que vacia la pila de operadores y los mueve al vci hasta encontrar un '('
	public static void emptyUntilParentesis(Stack<Token> operadores, List<Token> vci)
	{
		while (!operadores.isEmpty()) 
		{
			Token token = operadores.pop(); 
			if (token.numToken.equals("-73")) {
				break; 
			}
			
			vci.add(token); 
		}
	}

	// Metodo que mueve un token a la pila de operadores
	public static void moveToOperadores(List<Token> vci, Stack<Token> operadores, Token token) 
	{
		
		// Si la pila de operadores está vacía o el token siguiente es '(', entonces
		// el token siguiente entra directo a la pila de operadores.

		// Si no, entonces compara la prioridad del token peek de la pila de operadores
		// con el token siguiente:
		 
		// Si la prioridad del token peek es mayor que la del token siguiente, entonces
		// pasa el token peek al vci y entra el token siguiente a la pila de operadores.
		 
		// El proceso se repite hasta que la prioridad del token peek sea menor a la del
		// token siguiente.

		
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
			int prioridadPeekToken = getPrioridad(peekToken.lexema);
			int prioridadNextToken = getPrioridad(token.lexema);

			boolean prioridadIsGreater = prioridadIsGreaterThan(prioridadPeekToken, prioridadNextToken);

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
					prioridadPeekToken = getPrioridad(peekToken.lexema);
					prioridadNextToken = getPrioridad(token.lexema);

					prioridadIsGreater = prioridadIsGreaterThan(prioridadPeekToken, prioridadNextToken);
				} while (prioridadIsGreater);
			}

			operadores.add(token);
		}
	}
}
