package semantica;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Services 
{
	/* En esta clase se definen los metodos que devuelven valores que son utilizados 
	tanto en la etapa semántica como en las siguientes etapas */

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
	
	// Metodo que devuelve el valor de la linea donde se encuentra el token de inicio del codigo fuente
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

	/* Metodo que devuelve la lista de token variables que estan declaradas dentro del bloque de 'variables
	del codigo fuente' */
	public static List<Token> getTokenVariablesDeclaradas(List<Token> tokens, int lineaInicio)
	{
		List<Token> tokenVariablesDeclaradas = tokens 
            .stream()
            .filter(t -> t.isVariable
                && t.numlinea < lineaInicio)
            .collect(Collectors.toList()); 

		return tokenVariablesDeclaradas; 
	} 
}
