package py.una.bd;

import py.una.entidad.Envio;

public class TestEnvioDAO {
    public static void main(String[] args) {
        EnvioDAO dao = new EnvioDAO();

        // Prueba 1: Crear un envío
        Envio nuevoEnvio = new Envio(null, 101, "Lentes de contacto", "Juan Perez", "Calle 123", "0981234567",
                "REGISTRADO");
        dao.insertar(nuevoEnvio);
        System.out.println("Envio insertado con ID: " + nuevoEnvio.getIdEnvio());

        // Prueba 2: Buscar el envío recién creado
        if (nuevoEnvio.getIdEnvio() != null) {
            Envio buscado = dao.buscarPorId(nuevoEnvio.getIdEnvio());
            System.out.println("Estado del envío encontrado: " + buscado.getEstado());
        }
    }
}