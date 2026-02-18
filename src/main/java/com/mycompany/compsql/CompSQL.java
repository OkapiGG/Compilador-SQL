/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.compsql;

/**
 *
 * @author alancervantes
 */
public class CompSQL {
    public static void main(String[] args) {
        Lexico lexico = new Lexico();

        lexico.analizar("Insertar en tabla usuarios valores ( 1 , “ Emanuel ”,  “ Perez ” ,  ” 9621657244 ” )");
    }
}
