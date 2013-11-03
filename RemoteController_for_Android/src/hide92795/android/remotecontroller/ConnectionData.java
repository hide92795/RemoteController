package hide92795.android.remotecontroller;

import java.io.Serializable;
import java.net.URI;

public class ConnectionData implements Serializable {
	private static final long serialVersionUID = -2002235175255239898L;
	private String address;
	private int port;
	private String username;
	private String password;

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public URI getURI() {
		URI uri = URI.create("ws://" + address + ":" + port);
		return uri;
	}

	@Override
	public String toString() {
		return address + ":" + port + " " + username;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ConnectionData) {
			ConnectionData cd = (ConnectionData) o;
			if (address.equals(cd.address) && port == cd.port && username.equals(cd.username)) {
				return true;
			}
		}
		return false;
	}
}
