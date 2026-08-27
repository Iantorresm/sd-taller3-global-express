package py.una.entidad;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EnvioJSON {

    // Convierte un objeto Envio a un String en formato JSON
    @SuppressWarnings("unchecked")
    public static String objetoString(Envio envio) {
        JSONObject obj = new JSONObject();
        obj.put("idEnvio", envio.getIdEnvio());
        obj.put("idPedido", envio.getIdPedido());
        obj.put("descripcionProducto", envio.getDescripcionProducto());
        obj.put("receptor", envio.getReceptor());
        obj.put("direccion", envio.getDireccion());
        obj.put("contacto", envio.getContacto());
        obj.put("estado", envio.getEstado());

        return obj.toJSONString();
    }

    // Convierte un String en formato JSON a un objeto Envio
    public static Envio stringObjeto(String str) throws Exception {
        Envio envio = new Envio();
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(str);
            JSONObject jObj = (JSONObject) obj;

            // Casteo a Long primero porque json-simple trata los números enteros como Long
            // por defecto
            if (jObj.get("idEnvio") != null) {
                envio.setIdEnvio(((Long) jObj.get("idEnvio")).intValue());
            }
            if (jObj.get("idPedido") != null) {
                envio.setIdPedido(((Long) jObj.get("idPedido")).intValue());
            }
            if (jObj.get("descripcionProducto") != null) {
                envio.setDescripcionProducto((String) jObj.get("descripcionProducto"));
            }
            if (jObj.get("receptor") != null) {
                envio.setReceptor((String) jObj.get("receptor"));
            }
            if (jObj.get("direccion") != null) {
                envio.setDireccion((String) jObj.get("direccion"));
            }
            if (jObj.get("contacto") != null) {
                envio.setContacto((String) jObj.get("contacto"));
            }
            if (jObj.get("estado") != null) {
                envio.setEstado((String) jObj.get("estado"));
            }

        } catch (ParseException e) {
            throw new Exception("Error parseando el JSON de Envio: " + e.getMessage());
        }

        return envio;
    }
}