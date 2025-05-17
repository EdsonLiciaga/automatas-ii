package models;

import java.util.List;

public class Token 
{
    public String lexema = "";
    public String numToken = "";
    public String posicion = ""; 
    public int numlinea = 0; 
    public boolean isVariable = false; 

    public Token() {}

    public Token(String lexema, String numToken) {
        this.lexema = lexema; 
        this.numToken = numToken; 
    }

    public Token(String lexema, String numToken, String posicion, int linea, boolean isVariable) 
    {
        this.lexema = lexema;
        this.numToken = numToken;
        this.posicion = posicion;
        this.numlinea = linea;
        this.isVariable = isVariable; 
    }

    @Override
    public String toString() 
    {
        return "Token [lexema = " + lexema + ", numToken = " + numToken + ", posicion = " + posicion + ", numlinea = " + numlinea + "]";
    }

    public boolean isOperador()
	{
		List<String> operadoresValidos = List.of("-21", "-22", 
			"-23", "-24", "-25", "-26", "-31", "-32 ", "-33", "-34", "-35", 
			"-36", "-41", "-42", "-43"); 

		return operadoresValidos.contains(numToken); 
	}	

    public boolean isIdentificador()
    {
        List<String> identificadoresValidos = List.of("-51", "-52",
        "-53", "-54"); 

        return identificadoresValidos.contains(numToken); 
    }

    public boolean isConstante()
    {
        List<String> constantesValidos = List.of("-61", "-62", 
        "-63", "-64", "-65"); 

        return constantesValidos.contains(numToken); 
    }
}
