/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alancervantes
 */
public class Lexico {

    int i = 0;

    private final Map<String, TipoToken> mapaToken;

    public Lexico() {
        this.mapaToken = new HashMap<>();
        mapeoToken();
    }

    public void mapeoToken() {
        mapaToken.put("insertar", TipoToken.Insertar);
        mapaToken.put("en", TipoToken.En);
        mapaToken.put("tabla", TipoToken.Tabla);
        mapaToken.put("valores", TipoToken.Valores);
        mapaToken.put("crear", TipoToken.Crear);
        mapaToken.put("entero", TipoToken.Entero);
        mapaToken.put("texto", TipoToken.Texto);
        mapaToken.put("llaveprimaria", TipoToken.LlavePrimaria);
        mapaToken.put("llaveforanea", TipoToken.LlaveForanea);
        mapaToken.put("(", TipoToken.ParentesisAbre);
        mapaToken.put(")", TipoToken.ParentesisCierra);
        mapaToken.put(",", TipoToken.Coma);
        mapaToken.put(";", TipoToken.PuntoComa);
    }

    public void analizar(String codigo) {
        for (i = 0; i < codigo.length(); i++) {
            char actual = codigo.charAt(i);

            if (Character.isWhitespace(actual)) {
                continue;
            }

            if (Character.isLetter(actual)) {
                String lexema = obtenerIdentificador(codigo);
                if (mapaToken.containsKey(lexema.toLowerCase())) {
                    System.out.println("Token: " + mapaToken.get(lexema.toLowerCase()) + " (" + lexema + ")");
                }else {
                    System.out.println("Token: " + TipoToken.Identificador + " (" + lexema + ")");
                }
                continue;
            }   
            
            if (Character.isDigit(actual) || (actual == '-' && i + 1 < codigo.length() && Character.isDigit(codigo.charAt(i + 1)))) {
                obtenerNumero(codigo, actual);
                continue;
            }
            
            if (actual == '"' || actual == '“' || actual == '”') {
                obtenerCadena(codigo, actual);
                continue;
            }
            
            String simbolo = String.valueOf(actual);
            if(mapaToken.containsKey(simbolo)){
                System.out.println("Token: " + mapaToken.get(simbolo) + " (" + simbolo + ")");
            } else{
                System.out.println("Simbolo no reconocido: " + actual);
            }
                
            
        }
    }

    public String obtenerIdentificador(String codigo) {
        String resultado = "";
        while (i < codigo.length() && (Character.isLetter(codigo.charAt(i)) || Character.isDigit(codigo.charAt(i)))) {
            resultado += codigo.charAt(i);
            i++;
        }
        i--;
        return resultado;
    }
    
    public void obtenerNumero(String codigo, char actual) {
        String resultado = "";
        boolean esDecimal = false;

        if (actual == '-') {
            resultado += actual;
            i++;
        }

        while (i < codigo.length() && Character.isDigit(codigo.charAt(i))) {
            resultado += codigo.charAt(i);
            i++;
        }

        if (i < codigo.length() && codigo.charAt(i) == '.') {
            esDecimal = true;
            resultado += '.';
            i++;
            
            while (i < codigo.length() && Character.isDigit(codigo.charAt(i))) {
                resultado += codigo.charAt(i);
                i++;
            }
        }
        i--;

        if (esDecimal) {
            System.out.println("Token: " + TipoToken.NumeroDecimal + " (" + resultado + ")");
        } else {
            System.out.println("Token: " + TipoToken.NumeroEntero + " (" + resultado + ")");
        }
    }
    
    public void obtenerCadena(String codigo, char actual){
        StringBuilder lexema = new StringBuilder();
        i++;
        
        while(i < codigo.length()){
            char c = codigo.charAt(i);
            
            if(c=='"'||c=='”'||c =='“'){
                break;
            }
            
            lexema.append(c);
            i++;
        }
        
        System.out.println("Token: " + TipoToken.Cadena + " (" + lexema.toString() + ")");
    }
    
}