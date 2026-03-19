/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

/**
 *
 * @author alancervantes
 */
public class Token {
    
    private TipoToken tipo;
    private String lexema;

    public Token(TipoToken tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }
    
    public TipoToken getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    @Override
    public String toString() {
        return "Token{" + "tipo=" + tipo + ", lexema=" + lexema + '}';
    }
}
