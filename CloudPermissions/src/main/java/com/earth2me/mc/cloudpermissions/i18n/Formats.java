package com.earth2me.mc.cloudpermissions.i18n;

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