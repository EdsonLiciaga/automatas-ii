package semantica;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Semantica
{
    public static List<Token> getVariablesArrayNoDimensionadas(List<Token> tokens, List<Variable> variables) throws Exception
    {
        int lineaInicio = Services.getLineaInicio(tokens);
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
                if (!tokens.get(i+1).numToken.equals("-71") && !tokens.get(i+2).numToken.equals("-72")) {
                    variablesNoDimensionadas.add(token); 
                }
            }

            if (variable.dimensiones == 2)
            {
                if (!tokens.get(i+1).numToken.equals("-71") 
                    && !tokens.get(i+2).numToken.equals("-72")
                    && !tokens.get(i+3).numToken.equals("-71") 
                    && !tokens.get(i+4).numToken.equals("-72")) 
                {
                    variablesNoDimensionadas.add(token); 
                }
            }
        }

        return variablesNoDimensionadas; 
    }

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
                            "-74"
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

    public static List<Token> getVariablesDuplicadas(List<Token> tokens) throws Exception
    {
        int lineaInicio = Services.getLineaInicio(tokens);
        if (lineaInicio == 0) {
            throw new Exception("No se ha encontrado el token 'inicio' del código fuente");
        }

        List<Token> tokenVariablesDeclaradas = Services.getTokenVariablesDeclaradas(tokens, lineaInicio); 
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

    public static List<Token> getVariablesNoDeclaradas(List<Token> tokens) throws Exception 
    {
        int lineaInicio = Services.getLineaInicio(tokens);
        if (lineaInicio == 0) {
            throw new Exception("No se ha encontrado el token 'inicio' del código fuente");
        }
 
        List<Token> tokenVariablesDeclaradas = Services.getTokenVariablesDeclaradas(tokens, lineaInicio); 
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
