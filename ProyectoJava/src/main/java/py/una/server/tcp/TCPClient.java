package py.una.server.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.simple.JSONObject;

import py.una.bd.EnvioDAO;

public class TCPClient {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        String ipServidor = "localhost";
        int puertoServidor = 4445;

        // Simulando un cambio de estado en la logística
        int idPedidoAsociado = 101;
        String nuevoEstado = "ENTREGADO";

        JSONObject jsonActualizacion = new JSONObject();
        jsonActualizacion.put("idPedido", idPedidoAsociado);
        jsonActualizacion.put("estado", nuevoEstado);

        String payload = jsonActualizacion.toJSONString();

        try (Socket socket = new Socket(ipServidor, puertoServidor);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("[Global Express] Notificando cambio de estado a Prolens: " + payload);

            // 1. Enviar JSON al servidor de Prolens
            out.println(payload);

            // 2. Leer la confirmación
            String respuesta = in.readLine();
            System.out.println("[Global Express] Respuesta de Prolens: " + respuesta);

            // Opcional: Actualizar el estado también en la base local usando
            // EnvioDAO.actualizarEstado()
            // 3. Leer la confirmación de Prolens
            System.out.println("[Global Express] Respuesta de Prolens: " + respuesta);

            // 4. Actualizar la base de datos local de Global Express
            EnvioDAO envioDAO = new EnvioDAO();
            int idEnvioActualizado = 1; // El ID interno del envío que acabas de entregar
            envioDAO.actualizarEstado(idEnvioActualizado, nuevoEstado);
            System.out.println("[Global Express] Estado actualizado en BD local a: " + nuevoEstado);

        } catch (Exception e) {
            System.err.println("Error en el cliente TCP de logística: " + e.getMessage());
        }
        
    }
}