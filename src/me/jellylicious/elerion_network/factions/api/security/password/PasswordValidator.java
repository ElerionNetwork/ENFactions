package me.jellylicious.elerion_network.factions.api.security.password;

public class PasswordValidator {
	
	public boolean isStrongEnough(String password) {
		if(password.length() >= 8) {
			if(password.matches(".*[A-Z].*")) {
				if(password.matches(".*[a-z].*")) {
					if(password.matches(".*\\d.*")) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean passwordsMatch(String password1, String password2) {
		return password1.equals(password2);
	}

}
