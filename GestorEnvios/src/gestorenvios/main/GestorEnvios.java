/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gestorenvios.main;

import gestorenvios.config.ApplicationConfig;
import gestorenvios.dao.EnvioDAO;
import gestorenvios.dao.PedidoDAO;

/**
 *
 * @author Grupo 175
 */
public class GestorEnvios {

    private GestorEnvios(){
        // Constructor privado para evitar instanciación
    }
    /**
     */
    static void main() {

        System.out.println(ApplicationConfig.get("db.url"));
        EnvioDAO envioDAO = new EnvioDAO();
        PedidoDAO pedidoDAO = new PedidoDAO(envioDAO);
        try {
            System.out.println(envioDAO.buscarPorId(5L));
            System.out.println(pedidoDAO.buscarPorId(7L));
        } catch (Exception e) {
            System.out.println("Error al buscar el envio: " + e.getMessage());
        }

        AppMenu app = new AppMenu();
        app.run();


    }

}
