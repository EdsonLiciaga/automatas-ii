package services;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import models.Token;

public class CodigoIntermedioService extends BaseService
{
	// En esta clase se definen los métodos utilizados para revisar los token de la tabla de tokens en la etapa
	// de codigo intermedio. 

	// Metodo que devuelve los token de la condicion de una estructura de control
	public static List<Token> getTokensFromCondition(int linea, List<Token> tokens)
	{
		List<Token> tokensCondition = tokens
			.stream()
			.filter(t -> t.numlinea == linea)
			.collect(Collectors.toList());  
		
		if (tokensCondition.getFirst().numToken.equals("-8")) {
			tokensCondition.removeLast(); 
		}
		tokensCondition.removeFirst(); 

		return tokensCondition; 
	}

	// Metodo que mueve un token vacio y uno de estatuto al vci. Tambien guarda la direccion del token vacio
	// en la pila de direcciones
	public static void moveTokenVacioAndEstatutoToVci(Token token, List<Token> vci, Stack<Integer> direcciones)
	{
		Token tokenVacio = new Token();
		
		vci.add(tokenVacio); 
		vci.add(token); 
		direcciones.push(vci.indexOf(tokenVacio));
	}

	// Metodo que guarda el valor del apuntador del vci en un token vacio
	public static void addDireccionToTokenVacio(Stack<Integer> direcciones, List<Token> vci, int apuntador)
	{
		int direccionTokenVacio = direcciones.pop();
		Token tokenVacio = vci.get(direccionTokenVacio); 
		String lexemaDireccion = String.valueOf(apuntador);
		tokenVacio.lexema = lexemaDireccion;
	}

	// Metodo que guarda una direccion de la pila de direcciones en el apuntador del vci
	public static void addDireccionToApuntador(Stack<Integer> direcciones, List<Token> vci)
	{
		String direccion = String.valueOf(direcciones.pop()); 
        Token tokenDireccion = new Token(direccion); 
		vci.add(tokenDireccion);
	}

	// Método que revisa un token de la tabla de tokens 
	public static void checkToken(Token token, List<Token> vci, Stack<Token> operadores) 
	{
		
		// Si el token es ';', entonces se vacía la pila de operadores 
		if (token.numToken.equals("-75") && !operadores.isEmpty()) {
			OperadoresService.empty(operadores, vci);
		}
 
		// Si el token es ')', entonces se vacía la pila de operadores hasta encontrar un '('
		if (token.numToken.equals("-74") && !operadores.isEmpty()) {
			OperadoresService.emptyUntilParentesis(operadores, vci);
		}

		// Si el token es un operador o '(', entonces se mueve a la pila de operadores
		boolean isOperador = token.isOperador();
		if (isOperador || token.numToken.equals("-73")) {
			OperadoresService.moveToOperadores(vci, operadores, token);
		}

		// Si el token es un identificador o una constante se mueve directamente al vci
		boolean isIdentificador = token.isIdentificador();
		boolean isConstante = token.isConstante();
		if (isIdentificador || isConstante) 
		{
			vci.add(token);
		}
	}	
}
