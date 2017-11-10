package me.jellylicious.elerion_network.factions.api.notification;

public class Notification {

//	Core core;
//	public Notification(Core core) {
//		this.core = core;
//	}
//	
//	public NotificationType notificationType;
//	
//	public enum NotificationType {
//		MAIL, ALLIANCE_REQUEST_RECEIVED;
//	}
//	
//	public List<String> playerNotifications(Player p) {
//		List<String> notifications = new ArrayList<String>();
//		String ymlName = p.getUniqueId().toString() + ".yml";
//		String filePath = "plugins//ENFactions//PlayerData//";
//		File f = core.getFileManager().getFile(ymlName, filePath);
//		if(f.exists()) {
//			FileConfiguration cfg = core.getFileManager().getConfiguration(ymlName, filePath);
//			if(cfg.isConfigurationSection("Notifications")) {
//				Set<String> n = cfg.getConfigurationSection("Notifications").getKeys(false);
//				notifications.addAll(n);
//				if(notifications.contains(" ")) notifications.remove(" ");
//				return notifications;
//			}
//			return notifications;
//		}
//		return notifications;
//	}
//	
//	public ConfigurationSection getNotification(String ymlName, String filePath, String notificationPath) {
//		File f = core.getFileManager().getFile(ymlName, filePath);
//		if(f.exists()) {
//			FileConfiguration cfg = core.getFileManager().getConfiguration(ymlName, filePath);
//			if(cfg.isConfigurationSection(notificationPath)) {
//				return cfg.getConfigurationSection(notificationPath);
//			}else{
//				return null;
//			}
//		}else{
//			return null;
//		}
//	}
//	
//	public void notify(Player target, String notification) {
//		if(target != null) {
//		}
//	}
//	
//	public NotificationType getNotificationType(ConfigurationSection notification) {
//		String nt = null;
//		if (notification.isSet(notification.getCurrentPath() + ".Type")) {
//			nt = notification.getString(notification.getCurrentPath() + ".Type");
//			if (NotificationType.valueOf(nt) != null) {
//				return NotificationType.valueOf(nt);
//			} else {
//				return NotificationType.UNKNOWN;
//			}
//		} else {
//			return NotificationType.UNKNOWN;
//		}
//	}
	
//	public String getMessage(ConfigurationSection notification) {
//			if(cfg.isConfigurationSection(notification)) {
//				if(getNotificationType(ymlName, filePath, pathToNotification).equals(NotificationType.PLAYER_RECEIVED_MAIL)) {
//					String sender = cfg.getString(pathToNotification + ".Sender");
//					String message = cfg.getString(pathToNotification + ".Message");
//					return "§a"
//				}
//			}
//		}
//	}
	

}
