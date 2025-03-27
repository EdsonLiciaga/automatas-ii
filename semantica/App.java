package semantica;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class App
{
    private static Set<String> errores = new HashSet<>(); 

    public static void main(String[] args) throws Exception
    {
        List<Token> tokens = getTokensFromFile("tokensPractica5.txt");
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
                errores.add(String.format("La variable %s tiene un valor incorrecto asignado", variable.identificador)); 
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
    }

    private static List<Token> getTokensFromFile(String archivo) 
    {
        List<Token> tokens = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) 
        {
            String linea;

            while ((linea = br.readLine()) != null) 
            {
                String[] partes = linea.split("\\t");
                if (partes.length == 4) 
                {  
                    String lexema = partes[0].trim();
                    String numToken = partes[1].trim();
                    String posicion = partes[2].trim();
                    int numlinea = Integer.parseInt(partes[3].trim()); 

                    List<String> tokenValidos = List.of(
                        "-51",
                        "-52", 
                        "-53",
                        "-54"       
                    );

                    boolean isVariable = false;
                    if (tokenValidos.contains(partes[1])) {
                        isVariable = true; 
                    }

                    Token token = new Token(lexema, numToken, posicion, numlinea, isVariable); 
                    tokens.add(token);
                } else {
                    errores.add("Línea con formato incorrecto: " + linea); 
                }
            }
        } 
        catch (FileNotFoundException e) 
        {
            String message = String.format((char) 27 + "[31m" + "%s.%n" +  (char) 27 + "[0m", "Archivo no encontrado: " + archivo);
            System.out.println(message);
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            String message = String.format((char) 27 + "[31m" + "%s.%n" +  (char) 27 + "[0m", "Error al leer el archivo: " + e.getMessage());
            System.out.println(message);
            e.printStackTrace();
        }
        
        return tokens;
    }

    private static List<Variable> getVariablesFromTokens(List<Token> tokens) 
    {
        int lineaInicio = tokens
            .stream()
            .filter(t -> "-2".equals(t.numToken))
            .mapToInt(t -> t.numlinea) 
            .findFirst()
            .orElse(0);

        // Obtiene los tokens de las variables declaradas dentro del bloque variables
        List<Token> tokenVariableDeclaradas = tokens 
            .stream()
            .filter(t -> t.isVariable
                && t.numlinea < lineaInicio)
            .collect(Collectors.toList()); 

        List<Variable> variables = new ArrayList<>(); 
        for (Token token : tokenVariableDeclaradas) 
        {
            Variable variable = new Variable(token.numToken, token.lexema, true); 
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
                .filter(v -> v.identificador.equals(token.lexema))
                .findFirst()
                .orElse(null);

            if (variable != null) {
                variable.valor = valor;
            } 
        }
    }
    
    private static void addDimensionesToVariablesArray(List<Token> tokens, List<Variable> variables)
    {
        int lineaInicio = tokens
            .stream()
            .filter(t -> "-2".equals(t.numToken))
            .mapToInt(t -> t.numlinea) 
            .findFirst()
            .orElse(0);

        List<String> identificadores = variables
            .stream()
            .map(v -> v.identificador)
            .collect(Collectors.toList()); 
            
        for (int i = 0; i < lineaInicio; i++)
        {
            Token token = tokens.get(i); 

            if (!identificadores.contains(token.lexema)) {
                continue; 
            }

            Variable variable = variables
                .stream()
                .filter(v -> v.identificador.equals(token.lexema))
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
