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
    
    private final Map<String, TipoToken> mapaToken;
    
    public Lexico(){
        this.mapaToken = new HashMap<>();
        mapeoToken();
    }

    public void mapeoToken(){
        mapaToken.put("insertar", TipoToken.Insertar);
        mapaToken.put("en", TipoToken.En);
        mapaToken.put("tabla", TipoToken.Tabla);
        mapaToken.put("valores", TipoToken.Valores);
        mapaToken.put("(", TipoToken.ParentesisAbre);
        mapaToken.put(",", TipoToken.Coma);
        //mapaToken.put(" ", TipoToken.Texto);
        mapaToken.put("crear", TipoToken.Crear);
        mapaToken.put("entero", TipoToken.NumeroDecimal);
        mapaToken.put("llave primaria", TipoToken.Primaria);
        mapaToken.put(";", TipoToken.PuntoComa);
    }    
}
