package services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import models.Token;
import models.Valor;
import models.Variable;

public class SemanticaService extends BaseService
{
    /* En esta clase se definen los metodos que son utilizados en la etapa semantica
     * del compilador. 
    */

    // Método que devuelve una lista de variables de la lista de tokens
    public static List<Variable> getVariablesFromTokens(List<Token> tokens) throws Exception
    {
        int lineaInicio = getLineaInicio(tokens);
        if (lineaInicio == 0) {
            throw new Exception("No se ha encontrado el token 'inicio' del código fuente.");
        }

        List<Token> tokenVariablesDeclaradas = getTokenVariablesDeclaradas(tokens, lineaInicio); 
        if (tokenVariablesDeclaradas.isEmpty()) {
            throw new Exception("No se encontraron variables declaradas en el código fuente."); 
        }

        List<Variable> variables = new ArrayList<>(); 
        int posicion = 1; 
        for (Token token : tokenVariablesDeclaradas) 
        {
            Variable variable = new Variable(token.numToken, token.lexema); 
            variable.posicion = posicion++; 
            variables.add(variable); 
        }

        addValorToVariables(lineaInicio, tokens, variables);
        return variables; 
    }  

    // Método que agrega el valor almacenado en una variable.
	public static void addValorToVariables(int lineaInicio, List<Token> tokens, List<Variable> variables)
    {
        for (int i = lineaInicio; i < tokens.size()-1; i++)
        {
            Token token = tokens.get(i); 
            if (!token.isVariable) {
                continue; 
            }

            Token nextToken = tokens.get(i+1); 
            if (!nextToken.numToken.equals("-26")) {
                continue; 
            }

            List<Valor> valor = tokens
                .stream()
                .filter(t -> t.numlinea == token.numlinea)
                .map(t -> new Valor(t.numToken, t.lexema))
                .collect(Collectors.toList());  
                
            valor.removeFirst(); 
            valor.removeFirst();
            valor.removeLast();

            Variable variable = variables
                .stream()
                .filter(v -> v.variableLexema.equals(token.lexema))
                .findFirst()
                .orElse(null);

            if (variable != null) {
                variable.valor = valor;
            } 
        }
    }

	/* Metodo que devuelve la lista de token variables que estan declaradas dentro del bloque de 'variables
	del codigo fuente'. */
	public static List<Token> getTokenVariablesDeclaradas(List<Token> tokens, int lineaInicio)
	{
		List<Token> tokenVariablesDeclaradas = tokens 
            .stream()
            .filter(t -> t.isVariable
                && t.numlinea < lineaInicio)
            .collect(Collectors.toList()); 

		return tokenVariablesDeclaradas; 
	} 

	// Metodo que agrega el numero de dimensiones a las variables que son arrays. 
    public static void addDimensionesToVariablesList(List<Token> tokens, List<Variable> variables) throws Exception
    {
        int lineaInicio = getLineaInicio(tokens);
        if (lineaInicio == 0) {
            throw new Exception("No se ha encontrado el token 'inicio' del código fuente.");
        }

        List<String> identificadores = variables
            .stream()
            .map(v -> v.variableLexema)
            .collect(Collectors.toList()); 
            
        for (int i = 0; i < lineaInicio; i++)
        {
            Token token = tokens.get(i); 

            if (!identificadores.contains(token.lexema)) {
                continue; 
            }

            Variable variable = variables
                .stream()
                .filter(v -> v.variableLexema.equals(token.lexema))
                .findFirst()
                .orElse(null); 

            if (tokens.get(i+1).numToken.equals("-71") && tokens.get(i+2).numToken.equals("-72"))
            {
                if (tokens.get(i+3).numToken.equals("-71") && tokens.get(i+4).numToken.equals("-72"))
                {                        
                    variable.dimensiones = 2; 
                    continue; 
                }
                variable.dimensiones = 1; 
            }
        }
    }
}
