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

        lexico.analizar("Insertar en tabla usuarios valores ( 1 , “ Emanuel ”,  “ Perez ” ,  ” 9621657244 ” );" +
                "Insertar en tabla usuarios valores ( 2 , “ Emanuel ”,  “ Perez ” ,  ” 9621657244 ” );" +
                "Insertar en tabla usuarios valores ( 3 , “ Emanuel ”,  “ Perez ” ,  ” 9621657244 ” );" +
                "Insertar en tabla usuarios valores ( 4 , “ Emanuel ”,  “ Perez ” ,  ” 9621657244 ” );" +
                
                "crear tabla alumnos" +
                "	id entero llavePrimaria," +
                "	nombre texto," +
                "	carrera texto," +
                "	edad entero" +
                ");" +
                
                "insertar en tabla alumnos valores (1, \" Ana \" , \" Sistemas \" , 20);" +
                "insertar en tabla alumnos valores (2, “ Perla ” , “ Contaduria ” , 19);" +
                "insertar en tabla alumnos valores (3, “ Aylin ” , “ Administracion ” , 22);" +
                "insertar en tabla alumnos valores (4, “ Ashley ” , \" Sistemas \", 23);"
        );
    }
}
