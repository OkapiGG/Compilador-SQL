/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author nimyn
 */

public class Lexico2{
    int i = 0;
    
    private final Map<String, TipoToken> mapaToken;
    
    public Lexico2(){
        this.mapaToken = new HashMap<>();
        mapeoToken();
    }
    
    public void mapeoToken(){
        mapaToken.put("insertar", TipoToken.Insertar);
        mapaToken.put("en", TipoToken.En);
        mapaToken.put("tabla", TipoToken.Tabla);
        mapaToken.put("valores", TipoToken.Valores);
        mapaToken.put("crear", TipoToken.Crear);
        mapaToken.put("entero", TipoToken.Entero);
        mapaToken.put("decimal", TipoToken.Decimal);
    }    
    
    public void analizar(String codigo){
        for(i = 0; i < codigo.length(); i++){
            char actual = codigo.charAt(i);
            if(Character.isWhitespace(actual)){
                continue;
            }
            if(Character.isDigit(actual) || actual == '-'){
                String numero = "";
                boolean esDecimal = false;
                if(actual == '-'){
                    numero += actual;
                    i++;
                }
                while(i < codigo.length() && Character.isDigit(codigo.charAt(i))){
                    numero += codigo.charAt(i);
                    i++;
                }
                if(i < codigo.length() && codigo.charAt(i) == '.'){
                    esDecimal = true;
                    numero += '.';
                    i++;
                    while(i < codigo.length() && Character.isDigit(codigo.charAt(i))){
                        numero += codigo.charAt(i);
                        i++;
                    }
                }
                i--;
                if(esDecimal){
                    System.out.println("Token: " + TipoToken.NumeroDecimal + " (" + numero + ")");
                } else {
                    System.out.println("Token: " + TipoToken.NumeroEntero + " (" + numero + ")");
                }
                continue;
            }
            if(Character.isLetter(actual)){
                String lexema = "";

                while(i < codigo.length() && Character.isLetter(codigo.charAt(i))){
                    lexema += codigo.charAt(i);
                    i++;
                }
                i--;
                if(mapaToken.containsKey(lexema.toLowerCase())){
                    System.out.println("Token: " + mapaToken.get(lexema.toLowerCase()) + " (" + lexema + ")");
                } else {
                    System.out.println("Token: " + TipoToken.Identificador + " (" + lexema + ")");
                }
                continue;
            }
            switch(actual){
                case '(':
                    System.out.println("Token: " + TipoToken.ParentesisAbre + " ( )");
                    break;
                case ')':
                    System.out.println("Token: " + TipoToken.ParentesisCierra + " ( )");
                    break;
                case ',':
                    System.out.println("Token: " + TipoToken.Coma + " (,)");
                    break;
                case ';':
                    System.out.println("Token: " + TipoToken.PuntoComa + " (;)");
                    break;
            }
        }
    }
}
