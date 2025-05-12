package services;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import models.Token;

public class BaseService 
{
	/* En esta clase se definen los metodos que devuelven valores que pueden ser
     * utilizados en las distintas etapas del compilador. 
     */

    // Metodo que devuelve una lista de tokens del archivo que contiene la tabla de tokens
	public static List<Token> getTokensFromFile(String archivo) 
    {
        List<Token> tokens = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) 
        {
            String linea;

            while ((linea = br.readLine()) != null) 
            {
                String[] partes = linea.split("\\t");

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
	
	// Metodo que devuelve la linea del token 'inicio' del programa en el código fuente
	public static int getLineaInicio(List<Token> tokens)
	{
		int lineaInicio = tokens
            .stream()
            .filter(t -> "-2".equals(t.numToken))
            .mapToInt(t -> t.numlinea) 
            .findFirst()
            .orElse(0);

		return lineaInicio; 
	}

    public static List<Token> getTokensFromFileAtInicio(String archivo) throws Exception
    {
        List<Token> tokens = getTokensFromFile(archivo); 
        int lineaInicio = getLineaInicio(tokens); 
        if (lineaInicio == 0) {
            throw new Exception("No se ha encontrado el token 'inicio' del código fuente.");
        }

        List<Token> tokensAtInicio = tokens
            .stream()
            .filter(t -> t.numlinea >= lineaInicio)
            .collect(Collectors.toList()); 
        
        return tokensAtInicio; 
    }
}
