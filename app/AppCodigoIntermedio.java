package app;

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
		String rutaArchivo = "files/tablaTokens(modified).txt";
		List<Token> tokens = CodigoIntermedioService.getTokensFromFileAtInicio(rutaArchivo);
		Stack<Token> operadores = new Stack<Token>();
		Stack<Integer> direcciones = new Stack<Integer>(); 
		Stack<Token> estatutos = new Stack<Token>(); 
		List<Token> vci = new ArrayList<Token>();
		
		int apuntador = 0; 
		for (int i = 0; i <= tokens.size()-1; i++) 
		{
			apuntador = vci.size(); 
			Token token = tokens.get(i);

			// Revisa un token para validar si es un operador, constante o identificador.
			CodigoIntermedioService.checkToken(token, vci, operadores);

			List<String> estatutosValidos = List.of(
				"-6", 
				"-8");
			// Si el token es 'if' o 'while'
			if (estatutosValidos.contains(token.numToken)) 
			{
				estatutos.push(token); 		

				// Si el token es 'while' guarda la direccion donde empieza el ciclo en 
				// la pila de direcciones
				if (token.numToken.equals("-8")) {
					direcciones.push(apuntador); 
				}

				// Revisa los token de la condición
				List<Token> tokensCondition = CodigoIntermedioService.getTokensFromCondition(token.numlinea, tokens);	

				for (Token t : tokensCondition) {
					CodigoIntermedioService.checkToken(t, vci, operadores);
					i++; 
				}  

				// Genera un token vacio y el token de un estatuto en el vci
				CodigoIntermedioService.moveTokenVacioAndEstatutoToVci(token, vci, direcciones);
			} 

			if (token.numToken.equals("-17"))
			{
				estatutos.push(token); 
				direcciones.push(apuntador);
			}

			// Si el token es 'fin'
			if (token.numToken.equals("-3"))
			{
				if (estatutos.isEmpty()) {
					continue; 
				}
				
				/* 
				 * Si el estatuto obtenido es un 'if' revisa el token siguiente
				 * y si es un 'else', pasa el control a este, sino guarda
				 * el valor del apuntador al token vacío
				 */ 
				Token tokenEstatuto = estatutos.pop();
				if (tokenEstatuto.numToken.equals("-6"))
				{
					Token nextToken = tokens.get(i+1); 
					if (nextToken.numToken.equals("-7")) {
						// apuntador++;
						continue; 
					} 
					else 
					{
						// Guarda el valor del apuntador en un token vacio
						CodigoIntermedioService.addDireccionToTokenVacio(direcciones, vci, apuntador);
					}
				}

				if (tokenEstatuto.numToken.equals("-7")) 
				{
					// Guarda el valor del apuntador en un token vacio
					CodigoIntermedioService.addDireccionToTokenVacio(direcciones, vci, apuntador);
				}

				if (tokenEstatuto.numToken.equals("-8"))
				{
					CodigoIntermedioService.addDireccionToTokenVacio(direcciones, vci, apuntador+2);
					CodigoIntermedioService.addDireccionToApuntador(direcciones, vci); 
					Token tokenFinW = new Token("finWhile", "-3"); 
					vci.add(tokenFinW); 
				}

				if (tokenEstatuto.numToken.equals("-17"))
				{
					List<Token> tokensCondition = CodigoIntermedioService.getTokensFromCondition(token.numlinea+1, tokens);
					for (Token t : tokensCondition) {
						CodigoIntermedioService.checkToken(t, vci, operadores);
						i++; 
					}  

					CodigoIntermedioService.addDireccionToApuntador(direcciones, vci);
					Token tokenFinW = new Token("finDoWhile", "-3"); 
					vci.add(tokenFinW);
				}
			}

			 
			// Si el token es 'else' guarda el valor del apuntador+2 a un token vacio 
			if (token.numToken.equals("-7"))
			{
				estatutos.push(token); 
				// Guarda el valor del apuntador+2 en un token vacio 
				CodigoIntermedioService.addDireccionToTokenVacio(direcciones, vci, apuntador+2);
				// Genera un token vacio y el token de un estatuto en el vci
				CodigoIntermedioService.moveTokenVacioAndEstatutoToVci(token, vci, direcciones);
			}
		}
		
		WriterService.writeVci(vci);
	}
}
