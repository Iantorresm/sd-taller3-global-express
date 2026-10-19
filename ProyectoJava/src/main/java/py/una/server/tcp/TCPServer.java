package py.una.server.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import py.una.entidad.Envio;
import py.una.bd.EnvioDAO;
import py.una.entidad.EnvioJSON;

public class TCPServer {

    // --- CLASE BLOCKING QUEUE APLICADA ---
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
            System.out.printf("[Productor %s] Tiempo de espera PUT: %.3f ms\n",
                    Thread.currentThread().getName(), waitTimeMs);

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
            System.out.printf("\t[Consumidor %s] Tiempo de espera TAKE: %.3f ms\n",
                    Thread.currentThread().getName(), waitTimeMs);

            return item;
        }
    }

    // --- HILO TRABAJADOR (CONSUMIDOR) ---
    static class WorkerThread extends Thread {
        private BlockingQueue<Socket> queue;
        private EnvioDAO envioDAO;

        public WorkerThread(BlockingQueue<Socket> queue, String name) {
            super(name);
            this.queue = queue;
            this.envioDAO = new EnvioDAO();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Socket clientSocket = queue.take();

                    try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(clientSocket.getInputStream()))) {

                        String datoRecibido = in.readLine();
                        if (datoRecibido != null) {
                            System.out.println("\n[" + getName() + "] [IN] Nueva peticion TCP recibida.");
                            System.out.println("[" + getName() + "] [INFO] Parseando JSON: " + datoRecibido);

                            Envio nuevoEnvio = EnvioJSON.stringObjeto(datoRecibido);

                            System.out.println("[" + getName() + "] [BD] Persistiendo en PostgreSQL...");
                            envioDAO.insertar(nuevoEnvio);

                            System.out.println("[" + getName() + "] [EXITO] Guardado con Tracking ID autogenerado: "
                                    + nuevoEnvio.getIdEnvio());

                            String respuestaJSON = EnvioJSON.objetoString(nuevoEnvio);
                            out.println(respuestaJSON);
                        }
                    } finally {
                        clientSocket.close();
                    }
                } catch (Exception e) {
                    System.err.println("Error en Worker: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        int puerto = 4444;
        BlockingQueue<Socket> queue = new BlockingQueue<>(50);

        for (int i = 1; i <= 5; i++) {
            new WorkerThread(queue, "Hilo-TCP-" + i).start();
        }

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor TCP Concurrente Global Express escuchando en el puerto " + puerto + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                queue.put(clientSocket);
            }
        } catch (Exception ex) {
            System.err.println("Error fatal en servidor TCP: " + ex.getMessage());
        }
    }
}