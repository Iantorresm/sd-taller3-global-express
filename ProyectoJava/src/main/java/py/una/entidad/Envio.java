package py.una.entidad;

public class Envio {
	private Integer idEnvio;
	private Integer idPedido;
	private String descripcionProducto;
	private String receptor;
	private String direccion;
	private String contacto;
	private String estado;

	public Envio() {
	}

	public Envio(Integer idEnvio, Integer idPedido, String descripcionProducto, String receptor, String direccion,
			String contacto, String estado) {
		this.idEnvio = idEnvio;
		this.idPedido = idPedido;
		this.descripcionProducto = descripcionProducto;
		this.receptor = receptor;
		this.direccion = direccion;
		this.contacto = contacto;
		this.estado = estado;
	}

	// Getters y Setters
	public Integer getIdEnvio() {
		return idEnvio;
	}

	public void setIdEnvio(Integer idEnvio) {
		this.idEnvio = idEnvio;
	}

	public Integer getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public String getDescripcionProducto() {
		return descripcionProducto;
	}

	public void setDescripcionProducto(String descripcionProducto) {
		this.descripcionProducto = descripcionProducto;
	}

	public String getReceptor() {
		return receptor;
	}

	public void setReceptor(String receptor) {
		this.receptor = receptor;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getContacto() {
		return contacto;
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
}