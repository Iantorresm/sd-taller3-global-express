package py.una.server.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.Queue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import py.una.entidad.Envio;
import py.una.bd.EnvioDAO;
import py.una.entidad.EnvioJSON;

public class UDPServer {

    public static class BlockingQueue<T> {
        private Queue<T> queue = new LinkedList<T>();
        private int capacity;

        public BlockingQueue(int capacity) {
            this.capacity = capacity;
        }

        public synchronized void put(T element) throws InterruptedException {
            long startTime = System.nanoTime();
            while (queue.size() == capacity) {
                wait();
            }
            queue.add(element);
            long endTime = System.nanoTime();
            double waitTimeMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("[Productor UDP] Tiempo PUT: %.3f ms\n", waitTimeMs);
            notify();
        }

        public synchronized T take() throws InterruptedException {
            long startTime = System.nanoTime();
            while (queue.isEmpty()) {
                wait();
            }
            T item = queue.remove();
            notify();
            long endTime = System.nanoTime();
            double waitTimeMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("\t[Consumidor UDP %s] Tiempo TAKE: %.3f ms\n", Thread.currentThread().getName(),
                    waitTimeMs);
            return item;
        }
    }

    static class UDPWorker extends Thread {
        private BlockingQueue<DatagramPacket> queue;
        private DatagramSocket socket;
        private EnvioDAO envioDAO;

        public UDPWorker(BlockingQueue<DatagramPacket> queue, DatagramSocket socket, String name) {
            super(name);
            this.queue = queue;
            this.socket = socket;
            this.envioDAO = new EnvioDAO();
        }

        @Override
        public void run() {
            JSONParser parser = new JSONParser();
            while (true) {
                try {
                    DatagramPacket packet = queue.take();
                    String datoRecibido = new String(packet.getData(), 0, packet.getLength());

                    JSONObject jsonIn = (JSONObject) parser.parse(datoRecibido);
                    Long idEnvio = (Long) jsonIn.get("idEnvio");

                    System.out.println("\n[" + getName()
                            + "] [BUSQUEDA] Peticion UDP: Consultando BD para Tracking ID: " + idEnvio);
                    Envio envio = envioDAO.buscarPorId(idEnvio.intValue());
                    String respuestaStr;

                    if (envio != null) {
                        System.out.println(
                                "[" + getName() + "] [OK] Paquete encontrado. Estado actual: " + envio.getEstado());
                        respuestaStr = EnvioJSON.objetoString(envio);
                    } else {
                        System.out.println("[" + getName() + "] [ERROR] Paquete NO encontrado en la BD.");
                        respuestaStr = "{\"error\": \"Envio no encontrado\"}";
                    }

                    byte[] sendData = respuestaStr.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(),
                            packet.getPort());
                    socket.send(sendPacket);

                } catch (Exception e) {
                    System.err.println("Error en UDP Worker: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        int puerto = 9876;
        BlockingQueue<DatagramPacket> queue = new BlockingQueue<>(100);

        try (DatagramSocket serverSocket = new DatagramSocket(puerto)) {
            System.out.println("Servidor UDP Concurrente Global Express en puerto " + puerto + "...");

            for (int i = 1; i <= 3; i++) {
                new UDPWorker(queue, serverSocket, "Hilo-UDP-" + i).start();
            }

            byte[] receiveData = new byte[1024];
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                byte[] dataCopy = new byte[receivePacket.getLength()];
                System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), dataCopy, 0,
                        receivePacket.getLength());
                DatagramPacket queuedPacket = new DatagramPacket(dataCopy, dataCopy.length, receivePacket.getAddress(),
                        receivePacket.getPort());

                queue.put(queuedPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}