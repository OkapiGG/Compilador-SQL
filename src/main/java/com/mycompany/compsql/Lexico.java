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
    
    private final Map<String, TipoToken> Tokenizar;
    
    public Lexico(){
        this.Tokenizar = new HashMap<>();
    }

    public void mapeoToken(){
        Tokenizar.put("insertar", TipoToken.Insertar);
        Tokenizar.put("en", TipoToken.En);
        Tokenizar.put("tabla", TipoToken.Tabla);
        Tokenizar.put("usuarios", TipoToken.Identificador);
        Tokenizar.put("valores", TipoToken.Valores);
    }
    
}
