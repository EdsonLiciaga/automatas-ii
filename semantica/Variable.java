package semantica;

import java.util.ArrayList;
import java.util.List;

public class Variable
{    
    public Variable(String tokenVariable, String variableLexema) 
    {
        this.variableToken = tokenVariable; 
        this.variableLexema = variableLexema; 
    }

    public String variableToken; 
    public String variableLexema;
    public List<Valor> valor = new ArrayList<>(); 
    public int dimensiones; 
}
