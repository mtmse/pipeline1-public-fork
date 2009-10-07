package org_pef_dtbook2pef.system.tasks.layout.text;

import java.util.Locale;

/**
 * Custom Locale implementation. 
 * @author joha
 *
 */
public class Locale2 {
	private final String lang, country, variant, str;
	
	private Locale2(String lang, String country, String variant) {
		this.lang = lang.intern();
		this.country = country.intern();
		this.variant = variant.intern();
		this.str = (lang + (country.equals("") ? "" : "-" + country + (variant.equals("") ? "" : "-" + variant))).intern();
	}
	
	public static Locale2 parse(String locale) {
		if (!locale.matches("([a-zA-Z]{1,8}(\\-[0-9a-zA-Z]{1,8})*)?")) {
			throw new IllegalArgumentException("Not a valid locale as defined by IETF RFC 3066: " + locale);
		}
		String[] parts = locale.split("-", 3);
		String lang = parts[0].toLowerCase();
		String country = "";
		String variant = "";
		if (parts.length>=2) {
			country = parts[1].toUpperCase();
		}
		if (parts.length>=3) {
			variant = parts[2];
		}
		return new Locale2(lang, country, variant);
	}
	
	public Locale toLocale() {
		return new Locale(lang, country, variant);
	}
	
	public String getLanguage() {
		return lang;
	}
	
	public String getCountry() {
		return country;
	}
	
	public String getVariant() {
		return variant;
	}

	public boolean equals(Locale2 other) {
		// str is pooled using intern(), so str == other.str implies that str.equals(other.str)
		return str == other.str;
	}
	
	/**
	 * This locale is a subtype of the other locale
	 * @param other
	 * @return
	 */
	public boolean isA(Locale2 other) {
		// all strings are pooled so str == other.str implies that str.equals(other.str)
		if (other.variant != "" && this.variant != other.variant) {
			return false;
		}
		if (other.country != "" && this.country != other.country) {
			return false;
		}
		if (this.lang != other.lang) {
			return false;
		}
		return true;

	}

	public String toString() {
		return str;
	}


}
