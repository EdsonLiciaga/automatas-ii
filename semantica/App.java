package semantica;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class App
{
    public static void main(String[] args) throws Exception
    {
        Set<String> errores = new HashSet<>();
        List<Token> tokens = Services.getTokensFromFile("tokensPractica5.txt");
        List<Variable> variables = getVariablesFromTokens(tokens); 
         
        List<Token> variablesNoDeclaradas = Semantica.getVariablesNoDeclaradas(tokens);
        if (variablesNoDeclaradas.size() > 0) 
        {
            for (Token token : variablesNoDeclaradas) {
                errores.add(String.format("La variable %s no ha sido declarada", token.lexema));    
            }      
        }

        List<Token> variablesDuplicadas = Semantica.getVariablesDuplicadas(tokens); 
        if (variablesDuplicadas.size() > 0) 
        {
            for (Token token : variablesDuplicadas) {
                errores.add(String.format("La variable %s ya ha sido declarada en la linea %d", token.lexema, token.numlinea)); 
            }  
        }

        List<Variable> variablesValorInvalido = Semantica.getVariablesValorInvalido(variables);
        if (variablesValorInvalido.size() > 0)
        {
            for (Variable variable : variablesValorInvalido) {
                errores.add(String.format("La variable %s tiene un valor incorrecto asignado", variable.variableLexema)); 
            }
        } 

        addDimensionesToVariablesArray(tokens, variables);
        List<Token> variablesNoDimensionadas = Semantica.getVariablesArrayNoDimensionadas(tokens, variables); 
        if (variablesNoDimensionadas.size() > 0)
        {
            for (Token token : variablesNoDimensionadas) {
                errores.add(String.format("La variable %s no tiene un indice especificado", token.lexema)); 
            }
        }

        if (!errores.isEmpty()) 
        {
            String errorMessage = Error.getErrores(errores); 
            System.out.println(errorMessage);
        } else {
            System.out.println("Análisis semántico finalizado."); 
        }

        if (variables.size() > 0) {
            writeTablaSimbolosToFile(variables);
        }
    }

    private static List<Variable> getVariablesFromTokens(List<Token> tokens) throws Exception
    {
        int lineaInicio = Services.getLineaInicio(tokens);
        if (lineaInicio == 0) {
            throw new Exception("No se ha encontrado el token 'inicio' del código fuente.");
        }

        List<Token> tokenVariablesDeclaradas = Services.getTokenVariablesDeclaradas(tokens, lineaInicio); 
        if (tokenVariablesDeclaradas.isEmpty()) {
            throw new Exception("No se encontraron variables declaradas en el código fuente."); 
        }

        List<Variable> variables = new ArrayList<>(); 
        for (Token token : tokenVariablesDeclaradas) 
        {
            Variable variable = new Variable(token.numToken, token.lexema); 
            variables.add(variable); 
        }

        addValorToVariables(lineaInicio, tokens, variables);
        return variables; 
    }   

    private static void addValorToVariables(int lineaInicio, List<Token> tokens, List<Variable> variables)
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
    
    private static void addDimensionesToVariablesArray(List<Token> tokens, List<Variable> variables) throws Exception
    {
        int lineaInicio = Services.getLineaInicio(tokens);
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

    private static void writeTablaSimbolosToFile(List<Variable> variables) throws IOException
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter("tablaSimbolos.txt"));

            for (Variable variable : variables) 
            {
                   writer.write(
                    variable.variableLexema + "\t" + 
                    variable.variableToken + "\t" +
                    "0" + "\t" +
                    "main" + "\n");
            }
    
            writer.flush();
            writer.close();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
