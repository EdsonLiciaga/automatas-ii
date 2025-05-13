package etapas.semantica;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import errors.Error;
import models.Token;
import models.Variable;
import services.SemanticaService;
import services.WriterService;

public class AppSemantica
{
    public static void main(String[] args) throws Exception
    {
        Set<String> errores = new HashSet<>();
        List<Token> tokens = SemanticaService.getTokensFromFile("files/tablaTokens.txt");
        List<Variable> variables = SemanticaService.getVariablesFromTokens(tokens); 
        SemanticaService.addDimensionesToVariablesList(tokens, variables);
        SemanticaService.addValorToVariables(tokens, variables);
         
        // Verifica variables no declaradas
        List<Token> variablesNoDeclaradas = Semantica.getVariablesNoDeclaradas(tokens);
        if (variablesNoDeclaradas.size() > 0) 
        {
            for (Token token : variablesNoDeclaradas) {
                errores.add(String.format("La variable %s no ha sido declarada", token.lexema));    
            }      
        }

        // Verifica variables duplicadas
        List<Token> variablesDuplicadas = Semantica.getVariablesDuplicadas(tokens); 
        if (variablesDuplicadas.size() > 0) 
        {
            for (Token token : variablesDuplicadas) {
                errores.add(String.format("La variable %s ya ha sido declarada en la linea %d", token.lexema, token.numlinea)); 
            }  
        }

        // Verifica variables con valor invalido
        List<Variable> variablesValorInvalido = Semantica.getVariablesValorInvalido(variables);
        if (variablesValorInvalido.size() > 0)
        {
            for (Variable variable : variablesValorInvalido) {
                errores.add(String.format("La variable %s tiene un valor incorrecto asignado", variable.variableLexema)); 
            }
        } 

        // Verifica las dimensiones de una variable
        List<Token> variablesNoDimensionadas = Semantica.getVariablesArrayNoDimensionadas(tokens, variables); 
        if (variablesNoDimensionadas.size() > 0)
        {
            for (Token token : variablesNoDimensionadas) {
                errores.add(String.format("La variable %s no tiene un indice especificado", token.lexema)); 
            }
        }

        // Imprime los errores semanticos existentes en el código fuente
        if (!errores.isEmpty()) 
        {
            String errorMessage = Error.getSemanticaErrores(errores); 
            System.out.println(errorMessage);
        } else {
            System.out.println("Análisis semántico finalizado."); 
        }

        // Genera la tabla de simbolos y la tabla de direcciones
        if (variables.size() > 0) {
            WriterService.writeTablaSimbolosToFile(variables);
        }
        if (tokens.size() > 0) {
            WriterService.writeTablaDireccionesToFile(tokens);
        }
    }     
}
