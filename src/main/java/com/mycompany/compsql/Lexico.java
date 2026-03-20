/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        //Consultas
        mapaToken.put("seleccionar", TipoToken.Seleccionar);
        mapaToken.put("de", TipoToken.De);
        mapaToken.put("donde", TipoToken.Donde);
        mapaToken.put("y", TipoToken.Y);
        mapaToken.put("o", TipoToken.O);
        mapaToken.put("ordenar", TipoToken.Ordernar);
        mapaToken.put("por", TipoToken.Por);
        mapaToken.put("asc", TipoToken.Asc);
        mapaToken.put("desc", TipoToken.Desc);
        mapaToken.put("limite", TipoToken.Limite);
        mapaToken.put("grupo", TipoToken.Grupo);
        mapaToken.put("tener", TipoToken.Tener);
        mapaToken.put("distinto", TipoToken.Distinto);
        mapaToken.put("como", TipoToken.Como);
        mapaToken.put("insertar", TipoToken.Insertar);
        mapaToken.put("en", TipoToken.En);
        mapaToken.put("valores", TipoToken.Valores);
        mapaToken.put("actualizar", TipoToken.Actualizar);
        mapaToken.put("establecer", TipoToken.Establecer);
        mapaToken.put("eliminar", TipoToken.Eliminar);
        mapaToken.put("truncar", TipoToken.Truncar);
        mapaToken.put("borrar", TipoToken.Borrar);
        mapaToken.put("modificar", TipoToken.Modificar);
        mapaToken.put("agregar", TipoToken.Agregar);
        mapaToken.put("renombrar", TipoToken.Renombrar);

        // Declaracion de datos
        mapaToken.put("crear", TipoToken.Crear);
        mapaToken.put("tabla", TipoToken.Tabla);
        mapaToken.put("columna", TipoToken.Columna);
        mapaToken.put("tipo", TipoToken.Tipo);
        mapaToken.put("entero", TipoToken.Entero);
        mapaToken.put("texto", TipoToken.Texto);
        mapaToken.put("decimal", TipoToken.Decimal);
        mapaToken.put("fecha", TipoToken.Fecha);
        mapaToken.put("booleanolc", TipoToken.Booleano);
        
        // Restriccione
        mapaToken.put("defecto", TipoToken.Defecto);
        mapaToken.put("nulo", TipoToken.Nulo);
        mapaToken.put("unico", TipoToken.Unico);
        mapaToken.put("clave", TipoToken.Clave);
        mapaToken.put("primaria", TipoToken.LlavePrimaria); 
        mapaToken.put("foranea", TipoToken.LlaveForanea);
        mapaToken.put("referencia", TipoToken.Referencia);
        mapaToken.put("auto_incremento", TipoToken.AutoIncremento);
        mapaToken.put("no_nulo", TipoToken.NoNulo);
        mapaToken.put("verdadero", TipoToken.Verdadero);
        mapaToken.put("falso", TipoToken.Falso);

        // funciones
        mapaToken.put("incrementar", TipoToken.Incrementar);
        mapaToken.put("decrementar", TipoToken.Decrementar);
        mapaToken.put("entre", TipoToken.Entre);
        mapaToken.put("en_lista", TipoToken.EnLista);
        mapaToken.put("existe", TipoToken.Existe);
        mapaToken.put("como_texto", TipoToken.ComoTexto);
        mapaToken.put("longitud", TipoToken.Longitud);
        mapaToken.put("mayusculas", TipoToken.Mayusculas);
        mapaToken.put("minusculas", TipoToken.Minusculas);
        mapaToken.put("contar", TipoToken.Contar);
        mapaToken.put("suma", TipoToken.Suma);
        mapaToken.put("promedio", TipoToken.Promedio);
        mapaToken.put("maximo", TipoToken.Maximo);
        mapaToken.put("minimo", TipoToken.Minimo);
        
        // joins
        mapaToken.put("unir", TipoToken.Unir);
        mapaToken.put("unir_interno", TipoToken.UnirInterno);
        mapaToken.put("unir_izquierdo", TipoToken.UnirIzquierdo);
        mapaToken.put("unir_derecho", TipoToken.UnirDerecho);

        // transacciones
        mapaToken.put("iniciar_transaccion", TipoToken.IniciarTransaccion);
        mapaToken.put("confirmar", TipoToken.Confirmar);
        mapaToken.put("cancelar", TipoToken.Cancelar);
        mapaToken.put("alias", TipoToken.Alias);
        mapaToken.put("and", TipoToken.AND);
        mapaToken.put("or", TipoToken.OR);

        // puntuacion
        mapaToken.put("(", TipoToken.ParentesisAbre);
        mapaToken.put(")", TipoToken.ParentesisCierra);
        mapaToken.put(",", TipoToken.Coma);
        mapaToken.put(";", TipoToken.PuntoComa);
        mapaToken.put("=", TipoToken.Igual);
        mapaToken.put(">", TipoToken.MayorQue);
        mapaToken.put("<", TipoToken.MenoQue);
        mapaToken.put(">=", TipoToken.MayorIgualQue);
        mapaToken.put("<=", TipoToken.MenorIgualQue);
        mapaToken.put("!=", TipoToken.Diferente);
    }

    public List<Token> analizar(String codigo) {
        List<Token> listaTokens = new ArrayList<>();
        
        for (i = 0; i < codigo.length(); i++) {
            char actual = codigo.charAt(i);

            if (Character.isWhitespace(actual)) {
                continue;
            }

            if (Character.isLetter(actual)) {
                String lexema = obtenerIdentificador(codigo);
                if (mapaToken.containsKey(lexema.toLowerCase())) {
                    listaTokens.add(new Token(mapaToken.get(lexema.toLowerCase()), lexema));
                } else {
                    listaTokens.add(new Token(TipoToken.Identificador, lexema));
                }
                continue;
            }   
            
            if (Character.isDigit(actual) || (actual == '-' && i + 1 < codigo.length() && Character.isDigit(codigo.charAt(i + 1)))) {
                listaTokens.add(obtenerNumero(codigo, actual));
                continue;
            }
            
            if (actual == '"' || actual == '“' || actual == '”') {
                listaTokens.add(obtenerCadena(codigo, actual));
                continue;
            }
            
            String simbolo = String.valueOf(actual);
            if(mapaToken.containsKey(simbolo)){
                listaTokens.add(new Token(mapaToken.get(simbolo), simbolo));
            } else {
                System.out.println("Simbolo no reconocido: " + actual);
            }  
        }
        return listaTokens;
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
    
    public Token obtenerNumero(String codigo, char actual) {
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
            return new Token(TipoToken.NumeroDecimal, resultado);
        } else {
            return new Token(TipoToken.NumeroEntero, resultado);
        }
    }
    
    public Token obtenerCadena(String codigo, char actual){
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
     
        return new Token(TipoToken.Cadena, lexema.toString());
    }
}