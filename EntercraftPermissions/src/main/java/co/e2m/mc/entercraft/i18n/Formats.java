package co.e2m.mc.entercraft.i18n;

import lombok.Getter;


/**
 * Wraps localization keys.
 */
public enum Formats
{
	// Error/Generic
	Error_Generic_InstantiatedStaticType("%s is a static class, and cannot be instantiated."),
	Error_Generic_CannotOpenFile("Cannot open file: %s"),

	// Error/Locale
	Error_Locale_FileNonExistent("An expected locale file does not exist: %s"),
	Error_Locale_NodeInvalid("The specified locale node is invalid: %s"),

	// Error/State
	Error_State_LoadFailed("Component %s failed to load."),
	Error_State_EnableFailed("Component %s failed to enable."),
	Error_State_DisableFailed("Component %s failed to disable."),
	Error_State_ReloadFailed("Component %s failed to reload."),
	;

	public static final char NODE_SEPARATOR = '/';

	@Getter
	private final String key;
	@Getter
	private final String fallback;

	private Formats(final String fallback)
	{
		this.key = name().replace('_', NODE_SEPARATOR);
		this.fallback = fallback;
	}
}