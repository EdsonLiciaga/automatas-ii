package automatas1;
// Archivo: Practica5.java
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Practica5 {
    private static Set<String> errorMessages = new HashSet<>();
    private static List<Integer> tokens;
    private static int currentTokenIndex = 0;

    public static void main(String[] args) {
        String rutaArchivoTokens = "tokensPractica5.txt";

        tokens = leerTokensDesdeArchivo(rutaArchivoTokens);
        if (tokens == null || tokens.isEmpty()) {
            System.out.println("No se pudieron leer los tokens o el archivo está vacío.");
            return;
        }

        analizar();
        if (errorMessages.isEmpty()) {
            aceptar();
        } else {
            mostrarErrores();
        }
    }

    private static void analizar() {
        parseBloque1();
    }

    private static void error(String mensaje) {
        if (!errorMessages.contains(mensaje)) {
            errorMessages.add(mensaje);
            System.out.println((char) 27 + "[31m" + "ERROR SINTÁCTICO! " + mensaje + (char) 27 + "[0m");
        }
    }

    private static void aceptar() {
        System.out.println((char) 27 + "[32m" + "El análisis sintáctico ha finalizado sin errores." + (char) 27 + "[0m");
    }

    private static void mostrarErrores() {
        System.out.println((char) 27 + "[31m" + "Se encontraron errores durante el análisis sintáctico:" + (char) 27 + "[0m");
        for (String mensaje : errorMessages) {
            System.out.println((char) 27 + "[31m" + mensaje + (char) 27 + "[0m");
        }
    }

    private static List<Integer> leerTokensDesdeArchivo(String rutaArchivo) {
        List<Integer> tokens = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\t");
                if (partes.length >= 2) {
                    try {
                        tokens.add(Integer.parseInt(partes[1].trim()));
                    } catch (NumberFormatException e) {
                        error("Formato de token inválido en la línea: " + linea);
                    }
                } else {
                    error("Línea con formato incorrecto: " + linea);
                }
            }
        } catch (FileNotFoundException e) {
            error("Archivo no encontrado: " + rutaArchivo);
            e.printStackTrace();
        } catch (IOException e) {
            error("Error al leer el archivo: " + e.getMessage());
            e.printStackTrace();
        }

        // **Depuración: Imprimir todos los tokens leídos**
        System.out.println("Tokens leídos:");
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println("Índice " + i + ": " + tokens.get(i));
        }

        return tokens;
    }

    private static void parseBloque1() {
        // Regla: programa nombre;
        expectToken(-1, "Se esperaba la palabra clave 'programa'");
        expectToken(-55, "Se esperaba un identificador para el nombre del programa");
        expectToken(-75, "Se esperaba ';' después del nombre del programa");

        parseBloque2();
        parseInicio();
        expectToken(-3, "Se esperaba la palabra clave 'fin' al final del programa");
    }

    private static void parseBloque2() {
        // Regla: variables { tipo identificador ( , identificador )* ; }*
        if (peekToken() == -15) { // Token para "variables"
            avanza(); // Consume "variables"
            while (isTipoToken(peekToken())) {
                parseDeclaracionVariable(); // Procesa una declaración de tipo con identificadores
            }
        }
    }

    private static void parseDeclaracionVariable() {
        // Regla: tipo identificador (, identificador)* ;
        expectTipoToken();

        // Consume el primer identificador
        if (isIdentificadorToken(peekToken())) {
            avanza();
        } else {
            error("Se esperaba un identificador válido en índice: " + currentTokenIndex);
            // Intentar continuar
        }

        // Maneja identificadores separados por comas
        while (peekToken() == -76) { // Token para ","
            avanza(); // Consume la coma
            if (isIdentificadorToken(peekToken())) {
                avanza(); // Consume el siguiente identificador
            } else {
                error("Se esperaba un identificador después de ',' en índice: " + currentTokenIndex);
                avanza(); // Consumir el token inesperado para continuar
            }
        }

        // Asegúrate de que haya un punto y coma al final
        expectToken(-75, "Se esperaba ';' al final de la declaración de variables");
    }

    private static void parseInicio() {
        // Regla: inicio { sentencias } fin
        expectToken(-2, "Se esperaba la palabra clave 'inicio'");

        while (peekToken() != -3 && currentTokenIndex < tokens.size()) { // Mientras no se encuentre "fin"
            parseSentencia();
        }
    }

    private static void parseSentencia() {
        manejarTokensInvalidos(); // Ignora tokens inválidos al inicio

        int token = peekToken();
        if (isIdentificadorToken(token)) { // Asignación o expresión
            if (isNextTokenEquals()) {
                parseAsignacion(); // Procesa como asignación
            } else {
                parseExpression(); // Procesa como expresión
                expectToken(-75, "Se esperaba ';' después de la expresión");
            }
        } else if (token == -6) { // "si"
            parseEstructuraSi();
        } else if (token == -8) { // "mientras"
            parseEstructuraMientras();
        } else if (token == -9) { // "repetir"
            parseEstructuraRepetir();
        } else if (token == -4 || token == -5) { // "leer" o "escribir"
            parseFuncion();
        } else if (token == -75) { // Ignorar punto y coma extra
            System.out.println("Advertencia: Punto y coma inesperado en índice: " + currentTokenIndex);
            avanza();
        } else {
            // Emite advertencia y sigue procesando en lugar de lanzar excepción de inmediato
            error("Sentencia inesperada en índice: " + currentTokenIndex + ". Token encontrado: " + token);
            manejarTokensInesperados(); // Ignora tokens inesperados
        }
    }

    private static boolean isNextTokenEquals() {
        if (currentTokenIndex + 1 < tokens.size()) {
            return tokens.get(currentTokenIndex + 1) == -26; // '='
        }
        return false;
    }

    private static void parseAsignacion() {
        // Verifica que el token actual sea un identificador
        if (isIdentificadorToken(peekToken())) {
            avanza(); // Consume el identificador
        } else {
            error("Se esperaba un identificador al inicio de la asignación en índice: " + currentTokenIndex);
            avanza(); // Consumir el token inesperado para continuar
            return;
        }

        // Verifica si el siguiente token es '='
        if (peekToken() == -26) { // Si es '=', procede con la asignación
            avanza(); // Consume '='
            if (isConstanteToken(peekToken()) || isIdentificadorToken(peekToken()) || peekToken() == -73) {
                parseExpression(); // Procesa la expresión a la derecha del '='
            } else {
                error("Se esperaba una expresión válida después de '=' en índice: " + currentTokenIndex);
            }

            // Verifica que la asignación termine con un punto y coma
            expectToken(-75, "Se esperaba ';' al final de la asignación");
        } else {
            error("Se esperaba '=' después del identificador en índice: " + currentTokenIndex);
            // Intentar continuar
        }
    }

    private static void parseEstructuraSi() {
        // Regla: si ( condición ) entonces bloque_si [sino bloque_sino]
        expectToken(-6, "Se esperaba la palabra clave 'si'");
        expectToken(-73, "Se esperaba '(' después de 'si'");
        parseCondicion(); // Procesa la condición
        expectToken(-74, "Se esperaba ')' después de la condición");
        expectToken(-16, "Se esperaba la palabra clave 'entonces'");
        parseBloque(); // Procesa el bloque asociado al "entonces"

        // Maneja el bloque opcional "sino"
        if (peekToken() == -7) { // Si el siguiente token es "sino"
            avanza(); // Consume el token "sino"
            parseBloque(); // Procesa el bloque asociado al "sino"
        }
    }

    private static void parseEstructuraMientras() {
        // Regla: mientras ( condición ) hacer bloque
        expectToken(-8, "Se esperaba la palabra clave 'mientras'");
        expectToken(-73, "Se esperaba '(' después de 'mientras'");
        parseCondicion();
        expectToken(-74, "Se esperaba ')' después de la condición");
        expectToken(-10, "Se esperaba la palabra clave 'hacer'");
        parseBloque();
    }

    private static void parseEstructuraRepetir() {
        // Regla: repetir bloque hasta (condición)
        expectToken(-9, "Se esperaba la palabra clave 'repetir'");
        parseBloque(); // Procesa el bloque de instrucciones dentro de "repetir"

        int siguienteToken = peekToken();

        // Si el siguiente token es "hasta", continúa con el análisis
        if (siguienteToken == -10) {
            avanza(); // Consume el token "hasta"
            expectToken(-73, "Se esperaba '(' después de 'hasta'");
            parseCondicion(); // Procesa la condición dentro de los paréntesis
            expectToken(-74, "Se esperaba ')' después de la condición");
        }
        // Si encuentra "hacer", ignóralo como un error y continúa
        else if (siguienteToken == -17) {
            error("'hacer' encontrado después de 'repetir'. Se esperaba 'hasta'.");
            avanza(); // Consume el token incorrecto
            expectToken(-10, "Se esperaba 'hasta' después de 'repetir'");
            expectToken(-73, "Se esperaba '(' después de 'hasta'");
            parseCondicion(); // Procesa la condición dentro de los paréntesis
            expectToken(-74, "Se esperaba ')' después de la condición");
        }
        // Si encuentra otro token inesperado, registra un error
        else {
            error("Se esperaba 'hasta' pero se encontró: " + siguienteToken + " en índice: " + currentTokenIndex);
            avanza(); // Consumir el token inesperado para continuar
        }
    }

    private static void parseBloque() {
        // Regla: inicio { sentencias } fin
        expectToken(-2, "Se esperaba la palabra clave 'inicio'");

        while (peekToken() != -3 && peekToken() != -10 && currentTokenIndex < tokens.size()) { // Detente si encuentras "fin" o "hasta"
            parseSentencia();
        }
        expectToken(-3, "Se esperaba la palabra clave 'fin'");
    }

    private static void parseFuncion() {
        // Regla: leer (identificador); o escribir (identificador o constante);
        int token = peekToken();
        if (token == -4) { // "leer"
            avanza(); // Consume "leer"
            expectToken(-73, "Se esperaba '(' después de 'leer'");
            if (isIdentificadorToken(peekToken())) {
                avanza(); // Consume el identificador
            } else {
                error("Se esperaba un identificador en la función 'leer' en índice: " + currentTokenIndex);
                // Intentar continuar
            }
            expectToken(-74, "Se esperaba ')' después del identificador en 'leer'");
            expectToken(-75, "Se esperaba ';' después de 'leer'");
        } else if (token == -5) { // "escribir"
            avanza(); // Consume "escribir"
            expectToken(-73, "Se esperaba '(' después de 'escribir'");
            if (isIdentificadorToken(peekToken()) || isConstanteToken(peekToken())) {
                avanza(); // Consume el identificador o constante
            } else {
                error("Se esperaba un identificador o constante en la función 'escribir' en índice: " + currentTokenIndex);
                // Intentar continuar
            }
            expectToken(-74, "Se esperaba ')' después del identificador o constante en 'escribir'");
            expectToken(-75, "Se esperaba ';' después de 'escribir'");
        } else {
            error("Token inesperado en función: " + token + " en índice: " + currentTokenIndex);
            avanza(); // Consumir el token inesperado para continuar
        }
    }

    // Métodos para el análisis de condiciones y expresiones con precedencia adecuada
    private static void parseCondicion() {
        parseLogicalOr();
    }

    private static void parseLogicalOr() {
        parseLogicalAnd();
        while (peekToken() == -42) { // '||'
            avanza(); // Consume '||'
            parseLogicalAnd();
        }
    }

    private static void parseLogicalAnd() {
        parseRelational();
        while (peekToken() == -41) { // '&&'
            avanza(); // Consume '&&'
            parseRelational();
        }
    }

    private static void parseRelational() {
        parseAdditive();
        if (isRelationalOperator(peekToken())) {
            avanza(); // Consume operador relacional
            parseAdditive();
        }
    }

    private static boolean isRelationalOperator(int token) {
        return token == -31 || token == -32 || token == -33 || token == -34 || token == -35 || token == -36;
    }

    private static void parseAdditive() {
        parseMultiplicative();
        while (peekToken() == -24 || peekToken() == -25) { // '+' o '-'
            avanza(); // Consume '+' o '-'
            parseMultiplicative();
        }
    }

    private static void parseMultiplicative() {
        parseFactor();
        while (peekToken() == -21 || peekToken() == -22) { // '*' o '/'
            avanza(); // Consume '*' o '/'
            parseFactor();
        }
    }

    private static void parseFactor() {
        int token = peekToken();
        if (isIdentificadorToken(token) || isConstanteToken(token)) {
            avanza(); // Consume identificador o constante
        } else if (token == -73) { // '('
            avanza(); // Consume '('
            parseExpression(); // Procesa la expresión dentro de los paréntesis
            expectToken(-74, "Se esperaba ')' después de la expresión");
        } else {
            error("Se esperaba un identificador, constante o '(' en la expresión en índice: " + currentTokenIndex);
            avanza(); // Consumir el token inesperado para continuar
        }
    }

    private static void parseExpression() {
        parseLogicalOr();
        // Después de parseLogicalOr, esperamos que la expresión haya sido consumida completamente
    }

    private static boolean isLogicalOperator(int token) {
        return token == -41 || token == -42 || token == -43; // '&&', '||', etc.
    }

    private static boolean isIdentificadorToken(int token) {
        // Verifica si el token es un identificador válido
        return token == -51 || token == -52 || token == -53 || token == -54 || token == -55;
    }

    private static boolean isConstanteToken(int token) {
        // Incluye el nuevo token '-66' para 'false'
        return token == -61 || token == -62 || token == -63 || token == -64 || token == -65 || token == -66;
    }

    private static boolean isTipoToken(int token) {
        return token == -11 || token == -12 || token == -13 || token == -14;
    }

    private static boolean isOperadorToken(int token) {
        return token == -21 || token == -22 || token == -23 || token == -24 ||
                token == -25 || token == -26 || token == -31 || token == -32 ||
                token == -33 || token == -34 || token == -35 || token == -36 ||
                token == -41 || token == -42 || token == -43 || token == -63 || token == -66; // Incluye operadores lógicos y aritméticos
    }


    private static void manejarTokensInvalidos() {
        while (peekToken() == -50) { // Token inválido
            error("Token inválido '-50' encontrado en índice: " + currentTokenIndex);
            avanza(); // Ignora el token -50
        }
    }

    private static void manejarTokensInesperados() {
        int token = peekToken();
        while (token != -3 && token != -2 && !isIdentificadorToken(token) &&
                token != -4 && token != -5 && token != -6 && token != -8 && token != -9 && token != -75) {
            error("Token inesperado '" + token + "' en índice: " + currentTokenIndex);
            avanza();
            token = peekToken();
        }
    }

    private static void expectToken(int expectedToken, String mensajeError) {
        if (peekToken() == expectedToken) {
            avanza(); // Consume el token esperado
        } else {
            error(mensajeError + " Se esperaba el token '" + expectedToken + "' pero se encontró '" + peekToken()
                    + "' en índice: " + currentTokenIndex);
            // Intentar continuar
            avanza(); // Consumir el token inesperado para evitar bucles infinitos
        }
    }

    private static void expectTipoToken() {
        if (isTipoToken(peekToken())) {
            avanza(); // Consume el tipo de dato
        } else {
            error("Se esperaba un tipo de dato en índice: " + currentTokenIndex);
            // Intentar continuar
            avanza(); // Consumir el token inesperado
        }
    }

    private static int peekToken() {
        if (currentTokenIndex < tokens.size()) {
            int token = tokens.get(currentTokenIndex);
            System.out.println("Revisando token: " + token + " en índice: " + currentTokenIndex);
            return token;
        }
        System.out.println("Fin de archivo alcanzado.");
        return -1; // Fin de archivo
    }


    private static void avanza() {
        if (currentTokenIndex < tokens.size()) {
            System.out.println("Consumiendo token: " + tokens.get(currentTokenIndex) + " en índice: " + currentTokenIndex);
            currentTokenIndex++;
        }
    }
}