package hide92795.bukkit.plugin.remotecontroller;

import hide92795.bukkit.plugin.corelib.Localizable;

public enum Type implements Localizable {
	RELOADED_SETTING("ReloadedSetting"), ERROR_RELOAD_SETTING("ErrorReloadSetting"), CHAT_PREFIX("ChatPrefix"), USERNAME("Username"), PASSWORD("Password"), USAGE_USER_ADD("UsageUserAdd"), USAGE_USER_REMOVE(
			"UsageUserRemove"), USAGE_USER_LIST("UsageUserList"), USAGE_RELOAD_SETTING("UsageReloadSetting"), USER_NOT_FOUND("UserNotFound"), USER_ALREADY_EXIST("UserAlreadyExist"), USER_ADD_SUCCESS(
			"UserAddSuccess"), USER_REMOVE_SUCCESS("UserRemoveSuccess"), USAGE_SUMMON("UsageSummon"), MESSAGE("Message"), SENDED_SUMMON_REQUEST("SendedSummonRequest");
	private final String type;

	private Type(String type) {
		this.type = type;
	}

	@Override
	public String getName() {
		return type;
	}
}
