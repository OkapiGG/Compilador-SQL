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
                analizarCrear();
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
        if (tokenActual == null) {
            throw new RuntimeException("Error: Fin inesperado");
        }

        if (tokenActual.getTipo() == TipoToken.Cadena) {
            emparejar(TipoToken.Cadena);
        } else if (tokenActual.getTipo() == TipoToken.NumeroEntero) {
            emparejar(TipoToken.NumeroEntero);
        } else if (tokenActual.getTipo() == TipoToken.NumeroDecimal) {
            emparejar(TipoToken.NumeroDecimal);
        } else if (tokenActual.getTipo() == TipoToken.Verdadero) {
            emparejar(TipoToken.Verdadero);
        } else if (tokenActual.getTipo() == TipoToken.Falso) {
            emparejar(TipoToken.Falso);
        } else if (tokenActual.getTipo() == TipoToken.Nulo) {
            emparejar(TipoToken.Nulo);
        } else {
            throw new RuntimeException("Error Sintáctico: Se esperaba un valor pero se encontro " + tokenActual.getLexema());
        }
    }

    private void analizarCrear() {
        emparejar(TipoToken.Crear);

        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba 'tabla' o 'base'");
        }

        if (tokenActual.getTipo() == TipoToken.Tabla) {
            analizarCrearTabla();
        } else if (tokenActual.getTipo() == TipoToken.Base_de_Datos
                || tokenActual.getTipo() == TipoToken.Base
                || tokenActual.getTipo() == TipoToken.BaseDeDatos) {
            analizarCrearBase();
        } else {
            throw new RuntimeException("Error: después de 'crear' se esperaba 'tabla' o 'base' pero se encontró " + tokenActual.getLexema());
        }
    }

    private void analizarSeleccionar() {
        emparejar(TipoToken.Seleccionar);

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Distinto) {
            analizarDistinto();
        }

        analizarListaItemsSelect();
        emparejar(TipoToken.De);
        emparejar(TipoToken.Identificador);

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Donde) {
            analizarOpcWhere();
        }

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.PuntoComa) {
            emparejar(TipoToken.PuntoComa);
        }
    }

    private void analizarActualizar() {
        emparejar(TipoToken.Actualizar);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.Establecer);

        analizarListaAsignaciones();

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Donde) {
            analizarOpcWhere();
        }
        emparejar(TipoToken.PuntoComa);
    }

    private void analizarEliminar() {
        emparejar(TipoToken.Eliminar);
        emparejar(TipoToken.De);
        emparejar(TipoToken.Identificador);

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Donde) {
            analizarOpcWhere();
        }
        emparejar(TipoToken.PuntoComa);
    }

    private void analizarCrearTabla() {
        emparejar(TipoToken.Tabla);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.ParentesisAbre);

        analizarDefinicionesCol();

        emparejar(TipoToken.ParentesisCierra);
        emparejar(TipoToken.PuntoComa);
    }

    private void analizarCrearBase() {
        if (tokenActual.getTipo() == TipoToken.Base_de_Datos) {
            emparejar(TipoToken.Base_de_Datos);
        } else if (tokenActual.getTipo() == TipoToken.Base) {
            emparejar(TipoToken.Base);
        } else if (tokenActual.getTipo() == TipoToken.BaseDeDatos) {
            emparejar(TipoToken.BaseDeDatos);
        } else {
            throw new RuntimeException("Error: se esperaba 'base'");
        }

        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.PuntoComa);
    }

    private void analizarTruncar() {
        emparejar(TipoToken.Truncar);
        emparejar(TipoToken.Tabla);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.PuntoComa);
    }

    private void analizarDistinto() {
        emparejar(TipoToken.Distinto);
    }

    private void analizarColumnas() {
        emparejar(TipoToken.Identificador);
        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);
            emparejar(TipoToken.Identificador);
        }
    }

    private void analizarListaItemsSelect() {
        analizarColumnas();
    }

    private void analizarItemSelect() {
        analizarExpresionCol();
        analizarAlias();
    }

    private void analizarExpresionCol() {
        if (tokenActual == null) {
            throw new RuntimeException("Fin inesperado en EXPRESION_COL");
        }

        if (tokenActual.getTipo() == TipoToken.Identificador) {
            emparejar(TipoToken.Identificador);
        } else if (tokenActual.getTipo() == TipoToken.Contar
                || tokenActual.getTipo() == TipoToken.Suma
                || tokenActual.getTipo() == TipoToken.Promedio
                || tokenActual.getTipo() == TipoToken.Maximo
                || tokenActual.getTipo() == TipoToken.Minimo) {
            analizarAgregacion();
            emparejar(TipoToken.ParentesisAbre);
            emparejar(TipoToken.Identificador);
            emparejar(TipoToken.ParentesisCierra);
        } else {
            throw new RuntimeException("EXPRESION_COL invalida: " + tokenActual.getLexema());
        }
    }

    private void analizarAgregacion() {
        if (tokenActual == null) {
            throw new RuntimeException("Fin inesperado en AGREGACION");
        }

        switch (tokenActual.getTipo()) {
            case Contar:
            case Suma:
            case Promedio:
            case Maximo:
            case Minimo:
                emparejar(tokenActual.getTipo());
                break;
            default:
                throw new RuntimeException("Agregacion inválida: " + tokenActual.getLexema());
        }
    }

    private void analizarAlias() {
        if (tokenActual != null && (tokenActual.getTipo() == TipoToken.Como
                || tokenActual.getTipo() == TipoToken.As
                || tokenActual.getTipo() == TipoToken.Alias)) {
            emparejar(tokenActual.getTipo());
            emparejar(TipoToken.Identificador);
        }
    }

    private void analizarJoin() {
        if (tokenActual == null) {
            return;
        }
        if (tokenActual.getTipo() == TipoToken.UnirInterno
                || tokenActual.getTipo() == TipoToken.UnirIzquierdo
                || tokenActual.getTipo() == TipoToken.UnirDerecho
                || tokenActual.getTipo() == TipoToken.Unir) {
            analizarTipoJoin();
            emparejar(TipoToken.Unir);
            emparejar(TipoToken.Identificador);
            emparejar(TipoToken.En);
            analizarCondicion();
        }
        throw new UnsupportedOperationException("Falta implementar: analizarJoin");
    }

    private void analizarTipoJoin() {
        if (tokenActual == null) {
            return;
        }

        switch (tokenActual.getTipo()) {
            case UnirInterno:
            case UnirIzquierdo:
            case UnirDerecho:
                emparejar(tokenActual.getTipo());
                break;
            default:
                break;
        }
        throw new UnsupportedOperationException("Falta implementar: analizarTipoJoin");
    }

    private void analizarListaAsignaciones() {
        analizarAsignacion();
        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);
            analizarAsignacion();
        }
    }

    private void analizarAsignacion() {
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.Igual);

        if (tokenActual == null) {
            throw new RuntimeException("Fin inesperado en ASIGNACION");
        }

        if (tokenActual.getTipo() == TipoToken.NumeroEntero
                || tokenActual.getTipo() == TipoToken.NumeroDecimal
                || tokenActual.getTipo() == TipoToken.Cadena
                || tokenActual.getTipo() == TipoToken.Verdadero
                || tokenActual.getTipo() == TipoToken.Falso
                || tokenActual.getTipo() == TipoToken.Nulo) {
            emparejar(tokenActual.getTipo());
        } else {
            throw new RuntimeException("Valor inválido en ASIGNACION: " + tokenActual.getLexema());
        }
    }

    // Ese es mio
    private void analizarDefinicionesCol() {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba definición de columna");
        }

        if (tokenActual.getTipo() == TipoToken.LlaveForanea) {
            analizarFk();
        } else {
            analizarColumnaDef();
        }

        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);

            if (tokenActual == null) {
                throw new RuntimeException("Error: se esperaba otra definición después de la coma");
            }

            if (tokenActual.getTipo() == TipoToken.LlaveForanea) {
                analizarFk();
            } else {
                analizarColumnaDef();
            }
        }
    }

    private void analizarColumnaDef() {
        emparejar(TipoToken.Identificador);
        analizarTipoDato();
        analizarRestricciones();
    }

    private void analizarTipoDato() {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba un tipo de dato pero se encontró fin de código");
        }

        if (tokenActual.getTipo() == TipoToken.Entero) {
            emparejar(TipoToken.Entero);
        } else if (tokenActual.getTipo() == TipoToken.Texto) {
            emparejar(TipoToken.Texto);
        } else if (tokenActual.getTipo() == TipoToken.Decimal) {
            emparejar(TipoToken.Decimal);
        } else if (tokenActual.getTipo() == TipoToken.Fecha) {
            emparejar(TipoToken.Fecha);
        } else if (tokenActual.getTipo() == TipoToken.Booleano) {
            emparejar(TipoToken.Booleano);
        } else {
            throw new RuntimeException("Error sintáctico: tipo de dato no válido: " + tokenActual.getLexema());
        }
    }

    private void analizarRestricciones() {
        while (tokenActual != null
                && (tokenActual.getTipo() == TipoToken.NoNulo
                || tokenActual.getTipo() == TipoToken.Nulo
                || tokenActual.getTipo() == TipoToken.Unico
                || tokenActual.getTipo() == TipoToken.LlavePrimaria
                || tokenActual.getTipo() == TipoToken.AutoIncremento
                || tokenActual.getTipo() == TipoToken.Defecto)) {
            analizarRestriccion();
        }
    }

    private void analizarRestriccion() {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba una restricción pero se encontró fin de código");
        }

        if (tokenActual.getTipo() == TipoToken.NoNulo) {
            emparejar(TipoToken.NoNulo);
        } else if (tokenActual.getTipo() == TipoToken.Nulo) {
            emparejar(TipoToken.Nulo);
        } else if (tokenActual.getTipo() == TipoToken.Unico) {
            emparejar(TipoToken.Unico);
        } else if (tokenActual.getTipo() == TipoToken.LlavePrimaria) {
            emparejar(TipoToken.LlavePrimaria);
        } else if (tokenActual.getTipo() == TipoToken.AutoIncremento) {
            emparejar(TipoToken.AutoIncremento);
        } else if (tokenActual.getTipo() == TipoToken.Defecto) {
            emparejar(TipoToken.Defecto);
            analizarValor();
        } else {
            throw new RuntimeException("Error sintáctico: restricción no válida: " + tokenActual.getLexema());
        }
    }

    private void analizarFk() {
        emparejar(TipoToken.LlaveForanea);
        emparejar(TipoToken.ParentesisAbre);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.ParentesisCierra);
        emparejar(TipoToken.Referencia);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.ParentesisAbre);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.ParentesisCierra);
    }

    private void analizarOpcWhere() {
        emparejar(TipoToken.Donde);
        analizarExpresionLogica();
    }

    private void analizarExpresionLogica() {
        analizarCondicion();

        while (tokenActual != null
                && (tokenActual.getTipo() == TipoToken.Y
                || tokenActual.getTipo() == TipoToken.O
                || tokenActual.getTipo() == TipoToken.AND
                || tokenActual.getTipo() == TipoToken.OR)) {
            analizarOperadorLogico();
            analizarCondicion();
        }
    }

    private void analizarOperadorLogico() {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba operador lógico pero se encontró fin de código");
        }

        if (tokenActual.getTipo() == TipoToken.Y) {
            emparejar(TipoToken.Y);
        } else if (tokenActual.getTipo() == TipoToken.O) {
            emparejar(TipoToken.O);
        } else if (tokenActual.getTipo() == TipoToken.AND) {
            emparejar(TipoToken.AND);
        } else if (tokenActual.getTipo() == TipoToken.OR) {
            emparejar(TipoToken.OR);
        } else {
            throw new RuntimeException("Error sintáctico: se esperaba operador lógico y/o pero se encontró " + tokenActual.getLexema());
        }
    }

    private void analizarCondicion() {
        emparejar(TipoToken.Identificador);

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Entre) {
            emparejar(TipoToken.Entre);
            analizarValor();
            emparejar(TipoToken.Y);
            analizarValor();
        } else {
            analizarOpRel();
            analizarValor();
        }
    }

    private void analizarOpRel() {
        if (tokenActual == null) {
            throw new RuntimeException("Error: Se esperaba un operador relacional pero se encontro fin de codigo");
        }

        if (tokenActual.getTipo() == TipoToken.Igual) {
            emparejar(TipoToken.Igual);
        } else if (tokenActual.getTipo() == TipoToken.Diferente) {
            emparejar(TipoToken.Diferente);
        } else if (tokenActual.getTipo() == TipoToken.MayorQue) {
            emparejar(TipoToken.MayorQue);
        } else if (tokenActual.getTipo() == TipoToken.MenoQue) {
            emparejar(TipoToken.MenoQue);
        } else if (tokenActual.getTipo() == TipoToken.MayorIgualQue) {
            emparejar(TipoToken.MayorIgualQue);
        } else if (tokenActual.getTipo() == TipoToken.MenorIgualQue) {
            emparejar(TipoToken.MenorIgualQue);
        } else {
            throw new RuntimeException("Error Sintactico: Se esperaba un operador relacional (=, >, <, etc) pero se encontro " + tokenActual.getLexema());
        }
    }

    private void analizarGroupBy() {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Grupo) {
            emparejar(TipoToken.Grupo);
            emparejar(TipoToken.Por);

            analizarListaId();
            analizarHaving();
        }

        throw new UnsupportedOperationException("Falta implementar: analizarGroupBy");
    }

    private void analizarHaving() {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Tener) {
            emparejar(TipoToken.Tener);
            analizarExpresionLogica();
        }

        throw new UnsupportedOperationException("Falta implementar: analizarHaving");
    }

    private void analizarOrderBy() {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Ordernar) {
            emparejar(TipoToken.Ordernar);
            emparejar(TipoToken.Por);

            analizarListaOrden();

        }
        throw new UnsupportedOperationException("Falta implementar: analizarOrderBy");
    }

    private void analizarListaOrden() {

        emparejar(TipoToken.Identificador);

        analizarSentido();

        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);
            emparejar(TipoToken.Identificador);
            analizarSentido();
        }

        throw new UnsupportedOperationException("Falta implementar: analizarListaOrden");
    }

    private void analizarSentido() {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Asc) {
            emparejar(TipoToken.Asc);
        } else if (tokenActual != null && tokenActual.getTipo() == TipoToken.Desc) {
            emparejar(TipoToken.Desc);
        }

        throw new UnsupportedOperationException("Falta implementar: analizarSentido");
    }

    private void analizarLimit() {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Limite) {
            emparejar(TipoToken.Limite);

            emparejar(TipoToken.NumeroEntero);
        }
        throw new UnsupportedOperationException("Falta implementar: analizarLimit");
    }

    private void analizarListaId() {

        emparejar(TipoToken.Identificador);

        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);
            emparejar(TipoToken.Identificador);
        }
    }
}