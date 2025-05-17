package services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import models.Token;
import models.Valor;
import models.Variable;

public class SemanticaService extends BaseService
{
    // En esta clase se definen los metodos que son utilizados en la etapa semantica

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

    // Método que agrega el valor almacenado en una variable
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

	// Metodo que devuelve la lista de token variables que estan declaradas dentro del bloque de 'variables'
	// del codigo fuente
	public static List<Token> getTokenVariablesDeclaradas(List<Token> tokens, int lineaInicio)
	{
		List<Token> tokenVariablesDeclaradas = tokens 
            .stream()
            .filter(t -> t.isVariable
                && t.numlinea < lineaInicio)
            .collect(Collectors.toList()); 

		return tokenVariablesDeclaradas; 
	} 

	// Metodo que agrega el numero de dimensiones a las variables que son arrays
    public static void addDimensionesToVariablesList(List<Token> tokens, List<Variable> variables) throws Exception
    {
        List<String> identificadores = variables
            .stream()
            .map(v -> v.variableLexema)
            .collect(Collectors.toList()); 

        List <Token> tokensBeforeInicio = getTokensBeforeInicio(tokens); 

        for (int i = 0; i < tokensBeforeInicio.size()-1; i++) 
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

    // Metodo que devuelve una lista de variables array no dimensionadas en el codigo fuente
    public static List<Token> getVariablesArrayNoDimensionadas(List<Token> tokens, List<Variable> variables) throws Exception
    {
        int lineaInicio = BaseService.getLineaInicio(tokens);
        if (lineaInicio == 0) {
            throw new Exception("No se ha encontrado el token 'inicio' del código fuente");
        }

        List<Token> variablesNoDimensionadas = new ArrayList<>(); 
        for (int i = lineaInicio; i < tokens.size()-1; i++)
        {
            Token token = tokens.get(i);

            Variable variable = variables
                .stream()
                .filter(v -> v.variableLexema.equals(token.lexema))
                .findFirst()
                .orElse(null);   
            
            if (variable == null || variable.dimensiones == 0) {
                continue;
            }

            if (variable.dimensiones == 1)
            {
                if (!tokens.get(i+1).numToken.equals("-71") && !tokens.get(i+3).numToken.equals("-72")) {
                    variablesNoDimensionadas.add(token); 
                }
            }

            if (variable.dimensiones == 2)
            {
                if (!tokens.get(i+1).numToken.equals("-71") 
                    && !tokens.get(i+3).numToken.equals("-72")
                    && !tokens.get(i+4).numToken.equals("-71") 
                    && !tokens.get(i+6).numToken.equals("-72")) 
                {
                    variablesNoDimensionadas.add(token); 
                }
            }
        }

        return variablesNoDimensionadas; 
    }

    // Metodo que devuelve una lista de variables que guardan un valor que no corresponde
    // al tipo de dato de la variable en el codigo fuente
    public static List<Variable> getVariablesValorInvalido(List<Variable> variables)
    {
        List<Variable> variablesValorInvalidos = new ArrayList<>();
        for (Variable variable : variables) 
        {
            List<Valor> valor = variable.valor; 
            final List<String> tokenValidos; 
            if (valor.size() == 1)
            {
                switch (variable.variableToken) 
                {
                    case "-51":
                        tokenValidos = new ArrayList<>(List.of("-51", "-61")); 
                        break;
                    case "-52": 
                        tokenValidos = new ArrayList<>(List.of("-52", "-62"));
                        break; 
                    case "-53": 
                        tokenValidos = new ArrayList<>(List.of("-53", "-63"));
                        break; 
                    case "-54": 
                        tokenValidos = new ArrayList<>(List.of("-54", "-64", "-65")); 
                        break;
                    default:
                        tokenValidos = new ArrayList<>();
                        break;
                }

                String valorToken = valor.getFirst().valorToken; 
                if (tokenValidos.contains(valorToken)) {
                    continue; 
                } else {
                    variablesValorInvalidos.add(variable); 
                }
            }
            else if (valor.size() % 2 == 1)
            {
                switch (variable.variableToken) 
                {
                    case "-51":
                        tokenValidos = new ArrayList<>(List.of(
                            "-51", 
                            "-61" , 
                            "-21", 
                            "-22", 
                            "-24",
                            "-25", 
                            "-73", 
                            "-74"
                        )); 
                        break;
                    case "-52": 
                        tokenValidos = new ArrayList<>(List.of(
                            "-52", 
                            "-62", 
                            "-21", 
                            "-22", 
                            "-24",
                            "-25", 
                            "-73", 
                            "-74", 
                            "-61"
                        )); 
                        break; 
                    case "-53": 
                        tokenValidos = new ArrayList<>(List.of("-53", "-63", "-24"));
                        break; 
                    case "-54": 
                        tokenValidos = new ArrayList<>(List.of(
                            "-51", 
                            "-52", 
                            "-53",
                            "-54", 
                            "-61", 
                            "-62", 
                            "-64", 
                            "-65", 
                            "-21", 
                            "-22", 
                            "-24",
                            "-25", 
                            "-73", 
                            "-74"
                        )); 
                        break; 
                    default:
                        tokenValidos = new ArrayList<>(); 
                        break;   
                }
                
                boolean valorIsValid = valor
                    .stream()
                    .allMatch(v -> tokenValidos.contains(v.valorToken));

                if (valorIsValid) {
                    continue; 
                } else {
                    variablesValorInvalidos.add(variable); 
                } 
            }
        }

        return variablesValorInvalidos; 
    }

    // Metodo que devuelve una lista de variables que estan duplicadas en el codigo fuente
    public static List<Token> getVariablesDuplicadas(List<Token> tokens) throws Exception
    {
        int lineaInicio = getLineaInicio(tokens);
        if (lineaInicio == 0) {
            throw new Exception("No se ha encontrado el token 'inicio' del código fuente");
        }

        List<Token> tokenVariablesDeclaradas = getTokenVariablesDeclaradas(tokens, lineaInicio); 
        if (tokenVariablesDeclaradas.isEmpty()) {
            throw new Exception("No se encontraron variables declaradas en el código fuente."); 
        }

        Set<String> unicos = new HashSet<>(); 
        
        List<Token> variablesDuplicadas = tokenVariablesDeclaradas
            .stream()
            .filter(t -> !unicos.add(t.lexema))
            .collect(Collectors.toList());

        return variablesDuplicadas; 
    }

    // Metodo que devuelve una lista de variables que no han sido declaradas en el codigo fuente
    public static List<Token> getVariablesNoDeclaradas(List<Token> tokens) throws Exception 
    {
        int lineaInicio = getLineaInicio(tokens);
        if (lineaInicio == 0) {
            throw new Exception("No se ha encontrado el token 'inicio' del código fuente");
        }
 
        List<Token> tokenVariablesDeclaradas = getTokenVariablesDeclaradas(tokens, lineaInicio); 
        if (tokenVariablesDeclaradas.isEmpty()) {
            throw new Exception("No se encontraron variables declaradas en el código fuente."); 
        } 

        List<String> tokenVariablesDeclaradasLexema = tokenVariablesDeclaradas
            .stream()
            .map(tvd -> tvd.lexema)
            .collect(Collectors.toList());

        List<Token> tokenVariables = tokens
            .stream()
            .filter(t -> t.isVariable
                && t.numlinea > lineaInicio)
            .distinct()
            .collect(Collectors.toList());

        List<Token> tokenVariablesNoDeclaradas = tokenVariables
            .stream()
            .filter(t -> !tokenVariablesDeclaradasLexema.contains(t.lexema))
            .collect(Collectors.toList());
        
        return tokenVariablesNoDeclaradas; 
    }
}
