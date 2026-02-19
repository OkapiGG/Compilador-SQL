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
        mapaToken.put("(", TipoToken.ParentesisAbre);
        mapaToken.put(",", TipoToken.Coma);
        mapaToken.put("crear", TipoToken.Crear);
        mapaToken.put("entero", TipoToken.NumeroDecimal);
        mapaToken.put("llave primaria", TipoToken.Primaria);
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
                }
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
        return resultado; // por el momento
    }
}
