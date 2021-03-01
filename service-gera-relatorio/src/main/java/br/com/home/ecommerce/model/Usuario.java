package br.com.home.ecommerce.model;

public class Usuario {
	
	private final String uuid;

	public Usuario(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	public String getRelatorioPath() {
		return String.format("target/%s-relatorio.txt", uuid);
	}

	@Override
	public String toString() {
		return "Usuario [uuid=" + uuid + "]";
	}
}
