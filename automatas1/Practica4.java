package automatas1;
 import java.io.*;
import java.util.regex.*;

public class Practica4 {

    // Patrones de expresión regular
    private static final Pattern patronComentario = Pattern.compile("/\\.?\\*/", Pattern.DOTALL);
    private static final Pattern patronIdentificador = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*[$%@&#]?$");
    private static final Pattern patronOperadorAritmetico = Pattern.compile("[*+\\-/=%]");
    private static final Pattern patronOperadorRelacional = Pattern.compile("==|!=|<=|>=|<|>");
    private static final Pattern patronOperadorLogico = Pattern.compile("&&|\\|\\||!");
    private static final Pattern patronPalabrasReservadas = Pattern.compile("\\b(programa|inicio|fin|leer|escribir|entero" +
            "|real|cadena|logico|si|entonces|sino|mientras|hacer|repetir|hasta|variables)\\b");
    private static final Pattern patronEspecial = Pattern.compile("[();,\\[\\]]");
    private static final Pattern patronNumeroEntero = Pattern.compile("\\b\\d+\\b");
    private static final Pattern patronNumeroDecimal = Pattern.compile("\\b\\d+\\.\\d+\\b");
    private static final Pattern patronConstanteCadena = Pattern.compile("\"[^\"]*\"");
    private static final Pattern patronConstanteLogica = Pattern.compile("\\b(true|false)\\b");

    /**
     * Método que analiza una línea de texto.
     * Genera tokens o errores.
     * @param linea     con este comando sirve para el Contenido de la línea a analizar
     * @param numLinea  con este comando genera el Numero de la línea actual en el archivo de entrada
     * @param bwTokens  este es el Buffer para escribir tokens correctos
     * @param bwErrores este es el Buffer para escribir tokens con error
     */
    public static void analizar(String linea, int numLinea, BufferedWriter bwTokens, BufferedWriter bwErrores) throws IOException {
        // 1) Eliminar comentarios (/* ... */) antes de cualquier otra cosa.
        Matcher matcherComentario = patronComentario.matcher(linea);
        linea = matcherComentario.replaceAll("");

        // 2) Primero extraemos cualquier literal de cadena: "..."
        Matcher matcherCadena = patronConstanteCadena.matcher(linea);
        StringBuilder sbLinea = new StringBuilder(linea);

        while (matcherCadena.find()) {
            String cadena = matcherCadena.group();
            int start = matcherCadena.start();

            // ConstanteCadena -> Token -63, pts = -1
            bwTokens.write(
                    cadena + "\t"  // Lexema
                            + (-63) + "\t" // Token
                            + (-1) + "\t"  // pts (no se registra en tabla de símbolos)
                            + numLinea + "\n"
            );

            // Reemplazamos la cadena por espacios para no afectar posiciones
            for (int i = start; i < start + cadena.length(); i++) {
                sbLinea.setCharAt(i, ' ');
            }
        }

        // Actualizamos la línea limpia (sin cadenas)
        linea = sbLinea.toString();

        // // 3) Partimos la línea por espacios y/o separadores
        // String[] tokens = linea.split("\\s+|(?=[(){};,])|(?<=[(){};,\\[\\]])");
        String[] tokens = linea.split("\\s+|(?=[(){};,\\[\\]])|(?<=[(){};,\\[\\]])");

        // Se podría seguir la posición, pero no es obligatorio para la práctica
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) {
                continue;
            }

            int tokenValue;
            int pts; // -2 para identificadores, -1 para el resto

            // Clasificamos
            if (patronPalabrasReservadas.matcher(token).matches()) {
                tokenValue = obtenerTokenPalabraReservada(token);
                pts = -1;
            } else if (patronIdentificador.matcher(token).matches()) {
                // Identificador
                tokenValue = obtenerTokenIdentificador(token);
                pts = -2;  // los identificadores van a la tabla de símbolos
            } else if (patronOperadorAritmetico.matcher(token).matches()) {
                tokenValue = obtenerTokenOperadorAritmetico(token);
                pts = -1;
            } else if (patronOperadorRelacional.matcher(token).matches()) {
                tokenValue = obtenerTokenOperadorRelacional(token);
                pts = -1;
            } else if (patronOperadorLogico.matcher(token).matches()) {
                tokenValue = obtenerTokenOperadorLogico(token);
                pts = -1;
            } else if (patronEspecial.matcher(token).matches()) {
                tokenValue = obtenerTokenCaracterEspecial(token);
                pts = -1;
            } else if (patronNumeroEntero.matcher(token).matches()) {
                tokenValue = -61; // Constante entera
                pts = -1;
            } else if (patronNumeroDecimal.matcher(token).matches()) {
                tokenValue = -62; // Constante real
                pts = -1;
            } else if (patronConstanteLogica.matcher(token).matches()) {
                tokenValue = token.equals("true") ? -64 : -65; // Constante lógica
                pts = -1;
            } else {
                // Token no reconocido -> error
                bwErrores.write("Error en la línea " + numLinea + ": Token no reconocido -> " + token + "\n");
                continue;
            }

            // Guardamos el token en el archivo
            bwTokens.write(
                    token + "\t" +
                            tokenValue + "\t" +
                            pts + "\t" +
                            numLinea + "\n"
            );
        }
    }

    // ---------------------------------------------------
    //  Métodos auxiliares
    // ---------------------------------------------------

    private static int obtenerTokenPalabraReservada(String palabra) {
        switch (palabra) {
            case "programa":   return -1;
            case "inicio":     return -2;
            case "fin":        return -3;
            case "leer":       return -4;
            case "escribir":   return -5;
            case "si":         return -6;
            case "sino":       return -7;
            case "mientras":   return -8;
            case "repetir":    return -9;
            case "hasta":      return -10;
            case "entero":     return -11;
            case "real":       return -12;
            case "cadena":     return -13;
            case "logico":     return -14;
            case "variables":  return -15;
            case "entonces":   return -16;
            case "hacer":      return -17;
            default:           return -99; // Valor arbitrario para indicar no reconocido
        }
    }

    private static int obtenerTokenIdentificador(String token) {
        // Por defecto usamos -50 (identificador genérico).
        // Dependiendo de la terminación, asignamos los valores indicados en la práctica.
        if (token.endsWith("$")) {
            return -53; // Cadena
        } else if (token.endsWith("%")) {
            return -52; // Real
        } else if (token.endsWith("&")) {
            return -51; // Entero
        } else if (token.endsWith("#")) {
            return -54; // Lógico
        } else if (token.endsWith("@")) {
            return -55; // Programa
        } else {
            return -50; // Identificador genérico sin terminación
        }
    }

    private static int obtenerTokenCaracterEspecial(String caracter) {
        switch (caracter) {
            case "[": 
                return -71; 
            case "]": 
                return -72; 
            case "(":
                return -73;
            case ")":
                return -74;
            case ";":
                return -75;
            case ",":
                return -76;
            default:
                return -99;
        }
    }

    private static int obtenerTokenOperadorAritmetico(String operador) {
        switch (operador) {
            case "*":
                return -21;
            case "/":
                return -22;
            case "%":
                return -23;
            case "+":
                return -24;
            case "-":
                return -25;
            case "=":
                return -26;
            default:
                return -99;
        }
    }

    private static int obtenerTokenOperadorRelacional(String operador) {
        switch (operador) {
            case "<":
                return -31;
            case "<=":
                return -32;
            case ">":
                return -33;
            case ">=":
                return -34;
            case "==":
                return -35;
            case "!=":
                return -36;
            default:
                return -99;
        }
    }

    private static int obtenerTokenOperadorLogico(String operador) {
        switch (operador) {
            case "&&":
                return -41;
            case "||":
                return -42;
            case "!":
                return -43;
            default:
                return -99;
        }
    }

    // ---------------------------------------------------
    //  Método main
    // ---------------------------------------------------
    public static void main(String[] args) {
        // Ajusta estos nombres o rutas a tu gusto
        String nombreArchivoEntrada = "files/codigoFuente.txt";
        String nombreArchivoTokens = "files/tablaTokens.txt";
        String nombreArchivoErrores = "files/erroresSalida.txt";

        // Comprobar si el archivo de entrada existe
        File archivoEntrada = new File(nombreArchivoEntrada);
        if (!archivoEntrada.exists()) {
            System.out.println("El archivo de entrada '" + nombreArchivoEntrada
                    + "' no existe en la ruta: " + archivoEntrada.getAbsolutePath());
            System.out.println("Por favor, crea el archivo o revisa la ruta.");
            return; // Terminamos el programa si no hay archivo de entrada
        }

        // Iniciamos lectura y escritura en archivos
        try (
                BufferedWriter bwTokens = new BufferedWriter(new FileWriter(nombreArchivoTokens));
                BufferedWriter bwErrores = new BufferedWriter(new FileWriter(nombreArchivoErrores));
                BufferedReader br = new BufferedReader(new FileReader(nombreArchivoEntrada))
        ) {
            String linea;
            int numLinea = 1;
            while ((linea = br.readLine()) != null) {
                analizar(linea, numLinea, bwTokens, bwErrores);
                numLinea++;
            }
            System.out.println("Análisis terminado.\nRevisa los archivos: "
                    + nombreArchivoTokens + " y " + nombreArchivoErrores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}