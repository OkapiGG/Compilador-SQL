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
        // String codigo = "crear tabla usuarios ( id entero primaria, nombre texto Nulo);";
        String codigo = "crear base mibd;";

        //List<Token> tokensGenerados = lexico.analizar(codigo);

        List<Token> tokensGenerados = lexico.analizar("crear tabla usuarios ( 1 , “ Emanuel ”,  “ Perez ” ,  ” 9621657244 ” );");
    
        System.out.println("Tokens Generados: " + tokensGenerados.size());
        for (Token t : tokensGenerados) {
            System.out.println(t.getTipo() + " -> " + t.getLexema());
        }
        System.out.println("\nANALISIS:");

        Sintactico sintactico = new Sintactico(tokensGenerados);
        sintactico.analizarPrograma();
    }
}
