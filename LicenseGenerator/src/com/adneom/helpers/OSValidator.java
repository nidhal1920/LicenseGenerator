package com.adneom.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import com.verhas.licensor.License;

/**
 * A static class grouping the methods where OS dependencies may be encountered.
 * 
 * @author Nidhal Chayeb
 * @since LicenseGenerator1.0
 */
public class OSValidator {

	private static String WINDOWS_DEFAULT_KEYSTORE_PATH = "C:\\Documents and Settings\\" + System.getProperty("user.name") + "\\Application Data\\gnupg";
	private static String LINUX_DEFAULT_KEYSTORE_PATH = "/home/" + System.getProperty("user.name") + "/.gnupg/";

	private static String WINDOW_GPG_DEFAULT_PATH = "";
	private static String LINUX_GPG_DEFAULT_PATH = "";

	public enum OS {
		WINDOWS, UNIX, POSIX_UNIX, MAC, OTHER;

		private String version;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}

	private static OS os = OS.OTHER;

	static {
		try {
			String osName = System.getProperty("os.name");
			if (osName == null) {
				throw new IOException("os.name not found");
			}
			osName = osName.toLowerCase(Locale.ENGLISH);
			if (osName.contains("windows")) {
				os = OS.WINDOWS;
			} else if (osName.contains("linux") || osName.contains("mpe/ix") || osName.contains("freebsd") || osName.contains("irix") || osName.contains("digital unix") || osName.contains("unix")) {
				os = OS.UNIX;
			} else if (osName.contains("mac os")) {
				os = OS.MAC;
			} else if (osName.contains("sun os") || osName.contains("sunos") || osName.contains("solaris")) {
				os = OS.POSIX_UNIX;
			} else if (osName.contains("hp-ux") || osName.contains("aix")) {
				os = OS.POSIX_UNIX;
			} else {
				os = OS.OTHER;
			}

		} catch (Exception ex) {
			os = OS.OTHER;
		} finally {
			os.setVersion(System.getProperty("os.version"));
		}
	}

	public static OS getOs() {
		return os;
	}

	public static String getDefaultKeyStorePath() {
		if (OS.WINDOWS.equals(getOs())) {
			return WINDOWS_DEFAULT_KEYSTORE_PATH;
		} else if (OS.UNIX.equals(getOs())) {
			return LINUX_DEFAULT_KEYSTORE_PATH;
		} else
			return System.getProperty("java.io.tmpdir");
	}

	public static String getDefaultGPGPath() {
		if (OS.WINDOWS.equals(getOs())) {
			return WINDOW_GPG_DEFAULT_PATH;
		} else if (OS.UNIX.equals(getOs())) {
			return LINUX_GPG_DEFAULT_PATH;
		} else
			return "GPG";
	}

	public static void executeKeyGen(String genScriptPath, String secKeyAbsPath, String pubKeyAbsPath) {
		String cmd = "gpg";
		Process process;
		try {
			process = new ProcessBuilder(cmd, "--no-default-keyring", "--batch", "--keyring", pubKeyAbsPath, "--secret-keyring", secKeyAbsPath, "--gen-key", genScriptPath).start();
			process.waitFor();

		} catch (IOException e) {
			AlertHelper.alertException(AlertHelper.ERROR_GEN_KEYS + "\nPlease verify the user r/w permissions on:" + new File(secKeyAbsPath).getParent(), e);
		} catch (Exception e) {
			AlertHelper.alertException(AlertHelper.ERROR_GEN_KEYS, e);
		}

	}

	public static boolean executeLicenseGen(String outputDestFile, String licenseFile, String keyFile, String key, String passPhrase) {
		OutputStream os = null;

		try {
			os = new FileOutputStream(outputDestFile);
			os.write((new License().setLicense(new File(licenseFile)).loadKey(keyFile, key).encodeLicense(passPhrase)).getBytes("utf-8"));
			os.close();
			return true;

		} catch (IllegalArgumentException e) {
			AlertHelper.alertException(AlertHelper.ERROR_GEN_LIC + "\n Please verify the identification parameters (ID,passphrase,secret key,...)", e);
			deleteFile(outputDestFile);
			return false;
		} catch (Exception e) {
			AlertHelper.alertException(AlertHelper.ERROR_GEN_LIC, e);
			deleteFile(outputDestFile);
			return false;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// nothing to do
				}
			}
		}
	}

	private static void deleteFile(String filePath) {
		if (Files.exists(Paths.get(filePath))) {
			try {
				Files.deleteIfExists(Paths.get(filePath));
			} catch (IOException e) {
				// nothing to do
			}
		}
	}

}