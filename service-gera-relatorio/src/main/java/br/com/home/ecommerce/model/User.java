package br.com.home.ecommerce.model;

public class User {
	
	private final String uuid;

	public User(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	public String getRelatorioPath() {
		return String.format("target/%s-relatorio.txt", uuid);
	}
	

}
