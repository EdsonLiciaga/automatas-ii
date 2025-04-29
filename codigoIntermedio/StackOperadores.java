package codigoIntermedio;

import java.util.List;

import semantica.Token;

public class StackOperadores
{
	public static int getPrioridad(String operador)
	{
		List<String> aritmeticos60 = List.of(
			"*", 
			"/", 
			"%");
		if (aritmeticos60.contains(operador)) {
			return 60; 
		}
		
		List<String> aritmeticos50 = List.of(
			"+", 
			"-"); 
		if (aritmeticos50.contains(operador)) {
			return 50; 
		}

		List<String> relacionales40 = List.of(
			"<", 
			">", 
			"<=", 
			">=", 
			"==", 
			"!="); 
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

	public static void prioridadIsGreatherThan(Token peekToken, Token nextToken) 
	{
		
	}

}
