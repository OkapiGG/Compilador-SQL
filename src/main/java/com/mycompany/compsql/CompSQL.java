/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.compsql;

import java.util.List;

/**
 *
 * @author alancervantes
 */
public class CompSQL {

    public static void main(String[] args) {
        Lexico lexico = new Lexico();

        // String codigo = "seleccionar nombre de usuarios donde edad > 18 y nombre = \"Alan\";";
        // String codigo = "crear tablsa usuarios ( id entero primaria, nombre texto Nulo);";
        //String codigo = "crear base mibd;";

        //List<Token> tokensGenerados = lexico.analizar(codigo);
        List<Token> tokensGenerados = lexico.analizar("insertar en tabla Doctores valores (1, \"Dr. House\", \"Diagnostico\", falso);");
//        List<Token> tokensGenerados = lexico.analizar("crear tabla Usuarios (\n"
//                + "    id entero primaria auto_incremento,\n"
//                + "    nombre texto no_nulo,\n"
//                + "    correo texto unico,\n"
//                + "    edad entero,\n"
//                + "    es_cliente booleano defecto verdadero\n"
//                + ");");

        System.out.println("Tokens Generados: " + tokensGenerados.size());
        for (Token t : tokensGenerados) {
            System.out.println(t.getTipo() + " -> " + t.getLexema());
        }
        System.out.println("\nANALISIS:");

        Sintactico sintactico = new Sintactico(tokensGenerados);
        sintactico.analizarPrograma();
    }
}
