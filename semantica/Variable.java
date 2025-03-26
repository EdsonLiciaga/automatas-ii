package semantica;

import java.util.ArrayList;
import java.util.List;

public class Variable
{    
    public Variable(String tokenVariable, String identificador, boolean isDeclarated) 
    {
        this.variableToken = tokenVariable; 
        this.identificador = identificador; 
        this.isDeclarated = isDeclarated;  
    }

    public String variableToken; 
    public String identificador;
    public boolean isDeclarated = false; 
    public List<Valor> valor = new ArrayList<>(); 
    public int dimensiones; 
}
