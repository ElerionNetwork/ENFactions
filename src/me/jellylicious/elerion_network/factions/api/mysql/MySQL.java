package me.jellylicious.elerion_network.factions.api.mysql;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.configuration.file.FileConfiguration;

import me.jellylicious.elerion_network.factions.Core;

public class MySQL {

    private static Core plugin;
    private static String pr = "§3MySQL§8> ";
    private static String HOST = "";
    private static String DATABASE ="";
    private static String USER = "";
    private static String PASSWORD = "";
    private static String PORT = "";

    private static java.sql.Connection con;
    private boolean isConnected;
    
    public MySQL(Core jp) {
        plugin = jp;
    }

    public void createMySQLFile(String path) {
    	plugin.getFileManager().createNewFile("mysql.yml", path);
        File f = plugin.getFileManager().getFile("mysql.yml", path);
        FileConfiguration cfg = plugin.getFileManager().getConfiguration("mysql.yml", path);
        cfg.options().copyDefaults(true);
        cfg.addDefault("username", "root");
        cfg.addDefault("password", "");
        cfg.addDefault("database", "elerion_network");
        cfg.addDefault("host", "localhost");
        cfg.addDefault("port", "3306");
        try{
            cfg.save(f);
        }catch(IOException ex) {
            plugin.getServer().getConsoleSender().sendMessage(pr + "§cCould not save file 'mysql.yml'.");
        }
    }

    public void connect() {
        FileConfiguration cfg = plugin.getFileManager().getConfiguration("mysql.yml", "plugins//ENFactions//");
        HOST = cfg.getString("host");
        DATABASE = cfg.getString("database");
        USER = cfg.getString("username");
        PASSWORD = cfg.getString("password");
        PORT = cfg.getString("port");
        if(!isConnected) {
        	try{
        		con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?autoReconnect=true", USER, PASSWORD);
        		plugin.getServer().getConsoleSender().sendMessage(pr + "§aSuccessfully connected to MySQL database!");
        	}catch(SQLException ex) {
        		plugin.getServer().getConsoleSender().sendMessage(pr + "§cCould not connect to MySQL database, please check your MySQL settings!");
        	}
        }
    }

    public void close() {
    	if(isConnected()) {
    		try{
    			con.close();
    			con = null;
    			plugin.getServer().getConsoleSender().sendMessage(pr + "§aSuccessfully closed MySQL connection!");
    		}catch(SQLException ex) {
    			ex.printStackTrace();
    		}
    	}
    }
    
    public boolean isConnected() {
    	return con != null;
    }

    public void update(String qry) {
    	if(isConnected()) {
    		try{
    			con.createStatement().executeUpdate(qry);
    		}catch(SQLException ex) {
    			ex.printStackTrace();
    		}
    	}
    }

    public ResultSet getResult(String qry) {
    	if(isConnected()){
    		try{
    			return con.createStatement().executeQuery(qry);
    		}catch(SQLException ex) {
    			ex.printStackTrace();
    		}
    	}
    	return null;
    }
    
    public int countRows(String tableName) throws SQLException {
    	Statement stmt = null;
    	ResultSet rs = null;
    	int rowCount = -1;
    	try{
    		stmt = con.createStatement();
    		rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
    		rs.next();
    		rowCount = rs.getInt(1);
    	}finally{
    		rs.close();
    		stmt.close();
    	}
    	return rowCount;
    }
}
