/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author alancervantes
 */
public class ConexionBD {
    
    private static final String URL = "jdbc:postgresql://localhost:5432/escuela";
    private static final String USER = "postgres";
    private static final String PASS = "maiz564";
    
    private static Connection conexion = null;
    
    private ConexionBD(){
    
    }
    
    public static Connection getInstancia(){
        try{
            if(conexion == null || conexion.isClosed()){
                Class.forName("org.postgresql.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Conexion establecida");
            }
        } catch (ClassNotFoundException e) {
            System.err.println(":No se encontro el driver de postgres");
        } catch (SQLException e) {
            System.err.println("Error de sql: " + e.getMessage());
        }
        return conexion;
    }
    
    public static void ejecutar(String sql) {
        try {
            Connection conn = getInstancia();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
                System.out.println("Comando ejecutado en PostgreSQL");
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar en Postgres: " + e.getMessage());
        }
    }
    
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexion cerrada");
            } catch (SQLException e) {
                System.err.println("No se pudo cerrar: " + e.getMessage());
            }
        }
    }
    
}
