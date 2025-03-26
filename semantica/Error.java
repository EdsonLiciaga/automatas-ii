package semantica;

import java.util.Set;

public class Error 
{
    public static String getErrores(Set<String> errores)
    {
        StringBuilder message = new StringBuilder(); 

        message.append((char) 27 + "[31m" + "Se encontraron errores durante el análisis semántico:\n" + (char) 27 + "[0m");                
        for (String error : errores) {
            message.append(String.format((char) 27 + "[31m" + "%s.%n" +  (char) 27 + "[0m", error));
        }

        return message.toString(); 
    }
}
