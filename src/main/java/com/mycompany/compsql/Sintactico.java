/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

import java.util.List;

/**
 *
 * @author alancervantes
 */
public class Sintactico {
    private List<Token> tokens;
    private int posicionActual;
    private Token tokenActual;
        
    public Sintactico(List<Token> tokens) {
        this.tokens = tokens;
        this.posicionActual = 0;
        if (!tokens.isEmpty()) {
            this.tokenActual = tokens.get(0);
        }
    }
    
    private void avanzar() {
        posicionActual++;
        if (posicionActual < tokens.size()) {
            tokenActual = tokens.get(posicionActual);
        } else {
            tokenActual = null;
        }
    }

    private void emparejar(TipoToken tipoEsperado) {
        if (tokenActual != null && tokenActual.getTipo() == tipoEsperado) {
            avanzar();
        } else {
            String lexema = (tokenActual != null) ? tokenActual.getLexema() : "Fin de codigo";
            throw new RuntimeException("Error Sintactico, se esperaba " + tipoEsperado + " pero se encontro '" + lexema + "'");
        }
    }
    
    public void analizarPrograma() {
        while (tokenActual != null) {
            if (tokenActual.getTipo() == TipoToken.Insertar) {
                analizarInsertar(); 
            } else if (tokenActual.getTipo() == TipoToken.Crear) {
                //analizarCrearTabla();
                throw new RuntimeException("CREAR TABLA aun no implementado");
            } else {
                throw new RuntimeException("Comando no reconocido: " + tokenActual.getLexema());
            }
        }
        System.out.println("Analisis sintactico correcto.");
    }
    
    private void analizarInsertar() {
        emparejar(TipoToken.Insertar);
        emparejar(TipoToken.En);
        emparejar(TipoToken.Tabla);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.Valores);
        emparejar(TipoToken.ParentesisAbre);
        
        analizarListaValores();
        
        emparejar(TipoToken.ParentesisCierra);
        emparejar(TipoToken.PuntoComa);
    }
    
    private void analizarListaValores() {
        analizarValor();

        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);
            analizarValor();
        }
    }
    
    private void analizarValor() {
        if (tokenActual.getTipo() == TipoToken.Cadena) {
            emparejar(TipoToken.Cadena);
        } else if (tokenActual.getTipo() == TipoToken.NumeroEntero) {
            emparejar(TipoToken.NumeroEntero);
        } else if (tokenActual.getTipo() == TipoToken.NumeroDecimal) {
            emparejar(TipoToken.NumeroDecimal);
        } else {
             throw new RuntimeException("Error Sintáctico: Se esperaba un valor (Cadena o Número) pero se encontro " + tokenActual.getLexema());
        }
    }
}
