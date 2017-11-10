package me.jellylicious.elerion_network.factions.api.number;

import me.jellylicious.elerion_network.factions.Core;

public class NumericAPI {

	Core core;
	
	public NumericAPI(Core core) {
		this.core = core;
	}
	
	public boolean isNumeric(String number) {
        try{
            Integer.parseInt(number);
        }catch(Exception ex) {
            return false;
        }
        return true;
    }
	
	public float percentage(int total, int points) {
		return Math.round((points * 100.0f) / total);
	}

}
