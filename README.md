# Global Express (GlobalTrack) - Sistema de Logística y Envíos

Este repositorio contiene el sistema backend de Global Express, encargado de administrar la trazabilidad de los envíos, recibir solicitudes de registro de entrega por parte de las ópticas asociadas y actualizar los estados de los paquetes.

## Tecnologías Utilizadas

* **Java** (JDK 8+)
* **PostgreSQL** (Base de datos relacional)
* **Maven** (Gestión de dependencias)
* **json-simple** (Serialización y deserialización de JSON)

## Estructura del Proyecto

* `py.una.entidad`: Clases de dominio (`Envio`) y sus conversores JSON (`EnvioJSON`).
* `py.una.bd`: Capa de persistencia (`EnvioDAO`, `Bd`) y pruebas unitarias (`TestEnvioDAO`).
* `py.una.server.tcp` / `udp`: Implementación de sockets para atención a clientes externos (ópticas).

## Configuración y Ejecución

1. Configura la conexión a tu base de datos PostgreSQL local en el archivo de configuración de conexión (`Bd.java`).
2. Ejecuta el script SQL correspondiente para crear la tabla `envio`.
3. Valida la persistencia ejecutando las pruebas unitarias (`TestEnvioDAO`).
4. Inicia los servidores de red (`UDPServer`, `TCPServer`) para empezar a recibir peticiones de los sistemas cliente.
