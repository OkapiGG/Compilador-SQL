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

        List<Token> tokensGenerados = lexico.analizar("Insertar en tabla alumno valores ( 1, ” Alan ”,  “ Cervantes ” ,  ” 9621657244 ” );");
    
        System.out.println("Tokens Generados: " + tokensGenerados.size());
        
        Sintactico sintactico = new Sintactico(tokensGenerados);
        sintactico.analizarPrograma();
    }
}
