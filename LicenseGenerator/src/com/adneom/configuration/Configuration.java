package com.adneom.configuration;

/**
 * @author Nidhal Chayeb
 * @since LicenseGenerator1.0
 */

public class Configuration {

	public static String DEFAULT_KEY_EXT = "*.gpg";
	public static String DEFAULT_LICENSE_EXT = ".out";
	public static String DEFAULT_LICENSE_DATE_FORMAT = "yyyy.MM.dd";
	public static String DEFAULT_PUBLIC_KEY_EXT = ".pub";
	public static String DEFAULT_PRIVATE_KEY_EXT = ".sec";
	public static String DEFAULT_PRIVATE_KEY_EXT_FILTER = "*.sec";
	public static String DEFAULT_GENERATED_LICENSE_FOLDER = "WTBLicense";

	public enum KeyType {
		DSA("DSA"), RSA("RSA");

		private String value;

		KeyType(String newValue) {
			value = newValue;
		}

		public String getValue() {
			return value;
		}
	}

	private KeyType keyType = KeyType.DSA;

	public KeyType getKeyType() {
		return keyType;
	}

	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}

	private int keySize = 2048;

	public int getKeySize() {
		return keySize;
	}

	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}

	private String validityPeriod = "0";

	public String getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(String validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	private String userID;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	private String email = "";

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private String passPhrase;

	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}

}
