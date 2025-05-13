package services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import models.Token;
import models.Variable;

public class WriterService 
{
	/* En esta clase se definen los metodos utilizados para escribir o reescribir sobre un 
	 * archivo .txt
	 */

	// Método que reescribe la posicion en tabla de un token variable en la tabla de tokens. 
	public static void rewriteTokenPosicionTabla(List<Variable> variables)
    {
        String rutaArchivo = "files/tablaTokens.txt";
        String rutaSalida = "files/tablaTokens(modified).txt"; 
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(rutaSalida));
            BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo)); 
            
            String linea;
            while ((linea = reader.readLine()) != null) 
            {
                String[] partes = linea.split("\t"); 
                
                try 
                {
                    if (partes[2].equals("-2"))
                    {
                        String numLinea = variables
                            .stream()
                            .filter(v -> v.variableLexema.equals(partes[0]))
                            .map(v -> String.valueOf(v.posicion))
                            .findFirst()
                            .orElse(null); 

                        if (numLinea != null) {
                            partes[2] = numLinea; 
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error al convertir número: " + partes[2]);
                }

                writer.write(String.join("\t", partes)); 
                writer.newLine();
            }
            reader.close();
            writer.close();
        }
        catch(Exception e)
        {
            System.err.println("Error al encontrar archivo");
        }
    }

	// Método que escribe la tabla de símbolos. 
	public static void writeTablaSimbolosToFile(List<Variable> variables)
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter("files/tablaSimbolos.txt"));

            for (Variable variable : variables) 
            {
                   writer.write(
                    variable.variableLexema + "\t" + 
                    variable.variableToken + "\t" +
                    "-" + "\t" +
                    "-" + "\t" +
                    "main" + "\t" + 
                    variable.posicion + "\n");
            }
    
            writer.flush();
            writer.close();

            rewriteTokenPosicionTabla(variables);
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

	// Método que escribe la tabla de direcciones.
	public static void writeTablaDireccionesToFile(List<Token> tokens)
    {
        try 
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter("files/tablaDirecciones.txt"));

            List<Token> tokensDirecciones = tokens
            .stream()
            .filter(t -> "-55".equals(t.numToken))
            .collect(Collectors.toList());  

            for (Token token : tokensDirecciones) 
            {
                writer.write(
                    token.lexema + "\t" +
                    token.numToken + "\t" +
                    token.numlinea + "\t" +
                    "0" + "\n"
                );    
            }

            writer.flush();
            writer.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }	

	public static void writeVci(List<Token> vci)
	{
		try 
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter("files/vci.txt"));	
            int posicion = 0;
			for (Token token : vci) 
			{
				writer.write(
					token.lexema + "\t" +
					token.numToken + "\t" +
                    posicion++ + "\n"
				);	
			}

			writer.flush();
            writer.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
