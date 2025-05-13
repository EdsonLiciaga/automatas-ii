package etapas.codigoIntermedio;

import java.util.List;
import java.util.Stack;
import models.Token;

public class CodigoIntermedio 
{
	/*
	 * En esta clase se definen los métodos utilizados para revisar los token de la tabla de tokens en la etapa
	 * de codigo intermedio. 
	 */

	/* Método utilizado para revisar un token de la tabla de tokens. */
	public static void checkToken(Token token, List<Token> vci, Stack<Token> operadores) 
	{
		/*
		 * Si el token siguiente es ';', entonces se vacía la pila de
		 * operadores.
		 */
		if (token.numToken.equals("-75") && !operadores.isEmpty()) {
			StackOperadores.empty(operadores, vci);
		}

		/* 
		 * Si el token siguiente es ')', entonces se vacía la pila de 
		 * operadores hasta encontrar un '('
		*/
		if (token.numToken.equals("-74") && !operadores.isEmpty()) {
			StackOperadores.emptyUntilParentesis(operadores, vci);
		}

		/*
		 * Si el token siguiente es un operador o '('4, entonces se
		 * mueve a la pila de operadores
		 */
		boolean isOperador = token.isOperador();
		if (isOperador || token.numToken.equals("-73")) {
			StackOperadores.moveToOperadores(vci, operadores, token);
		}

		/*
		 * Si el token siguiente es un identificador o una constante
		 */
		boolean isIdentificador = token.isIdentificador();
		boolean isConstante = token.isConstante();
		if (isIdentificador || isConstante) 
		{
			vci.add(token);
		}
	}	
}
