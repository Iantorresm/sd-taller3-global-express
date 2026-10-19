package py.una.server.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;
import org.json.simple.JSONObject;
import py.una.bd.Bd;

public class TCPClient {
    public static void main(String[] args) {
        String ipServidor = "localhost";
        int puertoServidor = 4445;
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== SISTEMA LOGISTICO: NOTIFICACION DE ENTREGA ===");
        System.out.print("Ingrese el ID del Pedido (de la optica) que fue entregado: ");
        int idPedido = scanner.nextInt();

        // 1. CHEQUEO Y ACTUALIZACION EN LA BASE DE DATOS LOCAL DE GLOBAL EXPRESS
        try (Connection conn = Bd.connect();
                PreparedStatement ps = conn
                        .prepareStatement("UPDATE envio SET estado = 'ENTREGADO' WHERE id_pedido = ?")) {
            ps.setInt(1, idPedido);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("[ERROR] No existe el ID de Pedido " + idPedido
                        + " en la base de datos de logistica. Abortando notificacion.");
                scanner.close();
                return;
            }
            System.out.println("[BD] El paquete cambio a ENTREGADO en la BD de Logistica de forma persistente.");
        } catch (Exception e) {
            System.err.println("Error de base de datos: " + e.getMessage());
            scanner.close();
            return;
        }

        // 2. ENVIAR NOTIFICACION TCP A LA OPTICA
        JSONObject jsonNotificacion = new JSONObject();
        jsonNotificacion.put("idPedido", idPedido);
        jsonNotificacion.put("estado", "ENTREGADO");

        String payload = jsonNotificacion.toJSONString();

        try (Socket socket = new Socket(ipServidor, puertoServidor);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Notificando a Prolens via TCP...");
            out.println(payload);

            String respuesta = in.readLine();
            System.out.println("Respuesta de la optica: " + respuesta);

        } catch (Exception e) {
            System.err.println("Error de conexion con la optica: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}