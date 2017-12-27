package es.uvigo.esei.dai.hybridserver;

public enum PropertyNames {
	CLIENT_NUMBER("numClients"),
	PORT("port"),
	DB_URL("db.url"),
	DB_USER("db.user"),
	DB_PASSWORD("db.password");
	
	private String name;
	
	private PropertyNames(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
