package semantica;

public class Token 
{
    public String lexema;
    public String numToken;
    public String posicion; 
    public int numlinea; 
    public boolean isVariable; 
    public Variable variable; 

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
}
