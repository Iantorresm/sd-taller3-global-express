package py.una.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import py.una.entidad.Envio;

public class EnvioDAO {

	public void insertar(Envio envio) {
		String sql = "INSERT INTO envio (id_pedido, descripcion_producto, receptor, direccion, contacto, estado) VALUES (?, ?, ?, ?, ?, ?) RETURNING id_envio";
		try (Connection conn = Bd.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, envio.getIdPedido());
			pstmt.setString(2, envio.getDescripcionProducto());
			pstmt.setString(3, envio.getReceptor());
			pstmt.setString(4, envio.getDireccion());
			pstmt.setString(5, envio.getContacto());
			pstmt.setString(6, envio.getEstado());

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				envio.setIdEnvio(rs.getInt("id_envio"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Envio buscarPorId(Integer idEnvio) {
		String sql = "SELECT * FROM envio WHERE id_envio = ?";
		Envio envio = null;
		try (Connection conn = Bd.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, idEnvio);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				envio = new Envio(
						rs.getInt("id_envio"),
						rs.getInt("id_pedido"),
						rs.getString("descripcion_producto"),
						rs.getString("receptor"),
						rs.getString("direccion"),
						rs.getString("contacto"),
						rs.getString("estado"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return envio;
	}
}