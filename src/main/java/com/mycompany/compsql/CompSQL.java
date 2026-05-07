package com.mycompany.compsql;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class CompSQL {

    public static void main(String[] args) {
        Lexico lexico = new Lexico();
        Connection conexion = ConexionBD.getInstancia();

        if (conexion != null) {
            System.out.println("Compilador conectado a postgres");
        } else {
            System.err.println("Fallo de conexion a la base de datos.");
            return; // Detiene el programa si no hay BD
        }

        // Configuramos el Scanner para leer desde la terminal
        Scanner scanner = new Scanner(System.in);
        StringBuilder comandoBuffer = new StringBuilder();

        System.out.println("=================================================");
        System.out.println("   BIENVENIDO AL COMPILADOR MINI SQL (ESPAÑOL)   ");
        System.out.println("=================================================");
        System.out.println("Escribe tus instrucciones y terminalas con ';'");
        System.out.println("Escribe 'salir' para cerrar el programa.\n");

        // Bucle infinito para que la consola se quede esperando tus comandos
        while (true) {
            if (comandoBuffer.length() == 0) {
                System.out.print("MiniSQL> ");
            } else {
                System.out.print("      -> ");
            }

            String linea = scanner.nextLine();

            // Condición para apagar el programa
            if (linea.trim().equalsIgnoreCase("salir")) {
                System.out.println("Cerrando compilador...");
                break;
            }

            // Vamos guardando lo que escribes
            comandoBuffer.append(linea).append(" ");

            // Si detecta el punto y coma (;), significa que terminaste la instrucción
            if (linea.trim().endsWith(";")) {
                String instruccionFinal = comandoBuffer.toString().trim();
                
                System.out.println("\n[TOKENS IDENTIFICADOS]");
                List<Token> tokens = lexico.analizar(instruccionFinal);
                for (Token t : tokens) {
                    System.out.println(t.getTipo() + " -> " + t.getLexema());
                }

                System.out.println("\n[EJECUCIÓN DEL COMPILADOR]");
                try {
                    // Intentamos ejecutar tu instrucción
                    ejecutarSentencia(lexico, instruccionFinal);
                } catch (Exception e) {
                    // Si hay un error sintáctico o semántico, lo atrapamos para que el programa no crashee
                    System.err.println("ERROR: " + e.getMessage());
                }

                System.out.println("-------------------------------------------------\n");
                // Limpiamos la memoria para tu siguiente comando
                comandoBuffer.setLength(0);
            }
        }

        scanner.close();
        ConexionBD.cerrarConexion();
    }

    private static void ejecutarSentencia(Lexico lexico, String codigo) {
        new Sintactico(lexico.analizar(codigo)).analizarPrograma();
    }
}