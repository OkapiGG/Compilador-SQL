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
        if (tokenActual == null) {
            System.out.println("No hay tokens para analizar.");
            return;
        }

        while (tokenActual != null) {
            if (tokenActual.getTipo() == TipoToken.Insertar) {
                analizarInsertar();
            } else if (tokenActual.getTipo() == TipoToken.Crear) {
                analizarCrearTabla();
            } else if (tokenActual.getTipo() == TipoToken.Seleccionar) {
                analizarSeleccionar();
            } else if (tokenActual.getTipo() == TipoToken.Actualizar) {
                analizarActualizar();
            } else if (tokenActual.getTipo() == TipoToken.Eliminar) {
                analizarEliminar();
            } else if (tokenActual.getTipo() == TipoToken.Truncar) {
                analizarTruncar();
            } else {
                throw new RuntimeException("Comando principal no reconocido o falta implementarlo: " + tokenActual.getLexema());
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
        if (tokenActual == null) throw new RuntimeException("Error: Fin inesperado");

        if (tokenActual.getTipo() == TipoToken.Cadena) {
            emparejar(TipoToken.Cadena);
        } else if (tokenActual.getTipo() == TipoToken.NumeroEntero) {
            emparejar(TipoToken.NumeroEntero);
        } else if (tokenActual.getTipo() == TipoToken.NumeroDecimal) {
            emparejar(TipoToken.NumeroDecimal);
        } else {
            throw new RuntimeException("Error Sintáctico: Se esperaba un valor pero se encontro " + tokenActual.getLexema());
        }
    }

    private void analizarSeleccionar() {
        throw new UnsupportedOperationException("Falta implementar: analizarSeleccionar");
    }

    private void analizarActualizar() {
        throw new UnsupportedOperationException("Falta implementar: analizarActualizar");
    }

    private void analizarEliminar() {
        throw new UnsupportedOperationException("Falta implementar: analizarEliminar");
    }

    private void analizarCrearTabla() {
        throw new UnsupportedOperationException("Falta implementar: analizarCrearTabla");
    }

    private void analizarTruncar() {
        throw new UnsupportedOperationException("Falta implementar: analizarTruncar");
    }

    private void analizarDistinto() {
        throw new UnsupportedOperationException("Falta implementar: analizarDistinto");
    }

    private void analizarColumnas() {
        throw new UnsupportedOperationException("Falta implementar: analizarColumnas");
    }

    private void analizarListaItemsSelect() {
        throw new UnsupportedOperationException("Falta implementar: analizarListaItemsSelect");
    }

    private void analizarItemSelect() {
        throw new UnsupportedOperationException("Falta implementar: analizarItemSelect");
    }

    private void analizarExpresionCol() {
        throw new UnsupportedOperationException("Falta implementar: analizarExpresionCol");
    }

    private void analizarAgregacion() {
        throw new UnsupportedOperationException("Falta implementar: analizarAgregacion");
    }

    private void analizarAlias() {
        throw new UnsupportedOperationException("Falta implementar: analizarAlias");
    }

    private void analizarJoin() {
        throw new UnsupportedOperationException("Falta implementar: analizarJoin");
    }

    private void analizarTipoJoin() {
        throw new UnsupportedOperationException("Falta implementar: analizarTipoJoin");
    }

    private void analizarListaAsignaciones() {
        throw new UnsupportedOperationException("Falta implementar: analizarListaAsignaciones");
    }

    private void analizarAsignacion() {
        throw new UnsupportedOperationException("Falta implementar: analizarAsignacion");
    }

    // Ese es mio
    private void analizarDefinicionesCol() {
        throw new UnsupportedOperationException("Falta implementar: analizarDefinicionesCol");
    }

    private void analizarColumnaDef() {
        throw new UnsupportedOperationException("Falta implementar: analizarColumnaDef");
    }

    private void analizarTipoDato() {
        throw new UnsupportedOperationException("Falta implementar: analizarTipoDato");
    }

    private void analizarRestricciones() {
        throw new UnsupportedOperationException("Falta implementar: analizarRestricciones");
    }

    private void analizarRestriccion() {
        throw new UnsupportedOperationException("Falta implementar: analizarRestriccion");
    }

    private void analizarFk() {
        throw new UnsupportedOperationException("Falta implementar: analizarFk");
    }

    private void analizarOpcWhere() {
        throw new UnsupportedOperationException("Falta implementar: analizarOpcWhere");
    }

    private void analizarExpresionLogica() {
        throw new UnsupportedOperationException("Falta implementar: analizarExpresionLogica");
    }

    // Hasta aqui
    private void analizarOperadorLogico() {
        throw new UnsupportedOperationException("Falta implementar: analizarOperadorLogico");
    }

    private void analizarCondicion() {
        throw new UnsupportedOperationException("Falta implementar: analizarCondicion");
    }

    private void analizarOpRel() {
        throw new UnsupportedOperationException("Falta implementar: analizarOpRel");
    }

    private void analizarGroupBy() {
        throw new UnsupportedOperationException("Falta implementar: analizarGroupBy");
    }

    private void analizarHaving() {
        throw new UnsupportedOperationException("Falta implementar: analizarHaving");
    }

    private void analizarOrderBy() {
        throw new UnsupportedOperationException("Falta implementar: analizarOrderBy");
    }

    private void analizarListaOrden() {
        throw new UnsupportedOperationException("Falta implementar: analizarListaOrden");
    }

    private void analizarSentido() {
        throw new UnsupportedOperationException("Falta implementar: analizarSentido");
    }

    private void analizarLimit() {
        throw new UnsupportedOperationException("Falta implementar: analizarLimit");
    }
}