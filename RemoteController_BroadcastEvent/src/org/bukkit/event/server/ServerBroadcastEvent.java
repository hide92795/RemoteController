package org.bukkit.event.server;

import org.bukkit.event.HandlerList;

public class ServerBroadcastEvent extends ServerEvent {
	private static final HandlerList handlers = new HandlerList();
	private String message;

	public ServerBroadcastEvent(final String message) {
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
