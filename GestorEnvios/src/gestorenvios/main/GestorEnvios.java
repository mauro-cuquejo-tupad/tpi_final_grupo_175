/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gestorenvios.main;

import gestorenvios.config.ApplicationConfig;
import gestorenvios.dao.EnviosDAO;
import gestorenvios.dao.PedidosDAO;

/**
 *
 * @author Grupo 175
 */
public class GestorEnvios {

    /**
     * @param args the command line arguments
     */
    static void main(String[] args) {

        System.out.println(ApplicationConfig.get("db.url"));
        EnviosDAO enviosDAO = new EnviosDAO();
        PedidosDAO pedidosDAO = new PedidosDAO(enviosDAO);
        try {
            System.out.println(enviosDAO.buscarPorId(5L));
            System.out.println(pedidosDAO.buscarPorId(7L));
        } catch (Exception e) {
            System.out.println("Error al buscar el envio: " + e.getMessage());
        }

        AppMenu app = new AppMenu();
        app.run();


    }

}
