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
		String rutaArchivo = "files/tablaTokens(modified).txt";
		List<Token> tokens = CodigoIntermedioService.getTokensFromFileAtInicio(rutaArchivo);
		Stack<Token> operadores = new Stack<Token>();
		Stack<Integer> direcciones = new Stack<Integer>(); 
		Stack<String> estatutos = new Stack<String>(); 
		List<Token> vci = new ArrayList<Token>();
		
		int apuntador = 0; 
		for (; apuntador <= tokens.size()-1; apuntador++) 
		{
			Token token = tokens.get(apuntador);  
			// Revisa un token para validar si es un operador, constante o identificador.
			CodigoIntermedio.checkToken(token, vci, operadores);

			// Si el token siguiente es 'if'
			if (token.numToken.equals("-6")) 
			{
				estatutos.push(token.numToken); 	
				 
				List<Token> tokensConditionList = CodigoIntermedioService.getTokensFromCondition(token.numlinea, tokens);			
				for (Token tokenCondition : tokensConditionList) {
					CodigoIntermedio.checkToken(tokenCondition, vci, operadores);
					apuntador++; 
				}  
			} 
		}
		
		WriterService.writeVci(vci);
	}
}
