package co.virtualdragon.vanillaVotifier.impl;

import co.virtualdragon.vanillaVotifier.LanguagePack;
import java.io.InputStream;
import java.util.ResourceBundle;

public class PropertiesLanguagePack implements LanguagePack {

	private final ResourceBundle bundle;

	public PropertiesLanguagePack(String languagePackName) {
		bundle = ResourceBundle.getBundle(languagePackName);
	}

	@Override
	public String getString(String key) {
		if (bundle.containsKey(key)) {
			return bundle.getString(key);
		} else if (bundle.containsKey(key + "-location")) {
			InputStream in = PropertiesLanguagePack.class.getResourceAsStream(bundle.getString(key + "-location"));
			String string = "";
			int i;
			try {
				while ((i = in.read()) != -1) {
					string += (char) i;
				}
				return string;
			} catch (Exception e) {
			}
		}
		return null;
	}
}
