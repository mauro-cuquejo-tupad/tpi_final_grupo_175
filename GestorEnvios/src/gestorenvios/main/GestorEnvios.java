/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gestorenvios.main;

import gestorenvios.config.ApplicationConfig;

/**
 *
 * @author Grupo 175
 */
public class GestorEnvios {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        System.out.println(ApplicationConfig.get("db.url"));
    }
    
}
