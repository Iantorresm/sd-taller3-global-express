package py.una.server.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import py.una.entidad.Envio;
import py.una.bd.EnvioDAO;
import py.una.entidad.EnvioJSON;

public class UDPServer {

    public static void main(String[] args) {
        int puerto = 9876;

        // El try-with-resources garantiza que serverSocket.close() se llame
        // automáticamente
        try (DatagramSocket serverSocket = new DatagramSocket(puerto)) {
            System.out.println("Servidor UDP GlobalTrack escuchando en el puerto " + puerto + "...");

            // Instanciamos el DAO para consultar la base de datos local de Global Express
            EnvioDAO envioDao = new EnvioDAO();

            while (true) {
                byte[] receiveData = new byte[1024];

                // 1. Recibir la petición
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String datoRecibido = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("\n[GlobalTrack] Consulta recibida: " + datoRecibido);

                try {
                    // 2. Convertir el JSON entrante
                    Envio peticion = EnvioJSON.stringObjeto(datoRecibido);

                    // 3. Buscar en la BD
                    Envio envioBD = envioDao.buscarPorId(peticion.getIdEnvio());

                    String respuestaJSON;
                    if (envioBD != null) {
                        respuestaJSON = EnvioJSON.objetoString(envioBD);
                        System.out.println("[GlobalTrack] Envío encontrado. Estado: " + envioBD.getEstado());
                    } else {
                        peticion.setEstado("NO_ENCONTRADO");
                        respuestaJSON = EnvioJSON.objetoString(peticion);
                        System.out.println("[GlobalTrack] Envío " + peticion.getIdEnvio() + " no encontrado.");
                    }

                    // 4. Enviar la respuesta
                    byte[] sendData = respuestaJSON.getBytes();
                    InetAddress ipOrigen = receivePacket.getAddress();
                    int puertoOrigen = receivePacket.getPort();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipOrigen, puertoOrigen);
                    serverSocket.send(sendPacket);

                    System.out.println("[GlobalTrack] Respuesta enviada.");

                } catch (Exception e) {
                    System.err.println("Error procesando el JSON interno: " + e.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.println("Error crítico en el puerto " + puerto + ": " + ex.getMessage());
        }
    }
}