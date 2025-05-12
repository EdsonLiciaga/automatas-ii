package etapas.codigoIntermedio;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import models.Token;
import services.CodigoIntermedioService;
import services.WriterService;

public class AppCodigoIntermedio 
{
	public static void main(String[] args) throws Exception 
	{

		String rutaArchivo = "files/tokensPractica5(modified).txt";
		List<Token> tokens = CodigoIntermedioService.getTokensFromFileAtInicio(rutaArchivo);
		Stack<Token> operadores = new Stack<Token>();
		List<Token> vci = new ArrayList<Token>();

		for (Token token : tokens) 
		{
			/*
			 * Caso 1: Si el token siguiente es ';', entonces se vacía la pila de
			 * operadores.
			 */
			if (token.numToken.equals("-75") && !operadores.isEmpty()) 
			{
				StackOperadores.emptyAndMoveToVci(operadores, vci);
			}

			/*
			 * Caso 2: Si el token siguiente es un operador o '('4, entonces se
			 * mueve a la pila de operadores
			 */
			boolean isOperador = token.isOperador();
			if (isOperador || token.numToken.equals("-73")) 
			{
				StackOperadores.moveToOperadores(vci, operadores, token);
			}

			/*
			 * Caso 3: Si el token siguiente es un identificador o una constante
			 */
			boolean isIdentificador = token.isIdentificador();
			boolean isConstante = token.isConstante();
			if (isIdentificador || isConstante) 
			{
				vci.add(token);
			}
		}
		
		WriterService.writeVci(vci);
	}
}
