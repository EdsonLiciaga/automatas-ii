package semantica;

public class Convert 
{
    public static boolean toEntero(String valor)
    {
        try 
        {
            Integer.parseInt(valor);
            return true;
        } catch (NumberFormatException e) 
        {
            return false;
        }
    }

    public static boolean toReal(String valor)
    {
        try
        {
            Double.parseDouble(valor);
            return true;
        } catch (NumberFormatException e)
        {
            return false; 
        }
    }
}
