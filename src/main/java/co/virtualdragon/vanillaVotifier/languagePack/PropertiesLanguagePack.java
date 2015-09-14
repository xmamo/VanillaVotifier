package co.virtualdragon.vanillaVotifier.languagePack;

import java.util.ResourceBundle;

public class PropertiesLanguagePack implements LanguagePack {
	
	private ResourceBundle bundle;

	public PropertiesLanguagePack(String languagePackName) {
		bundle = ResourceBundle.getBundle(languagePackName);
	}

	@Override
	public String getString(String key) {
		if (bundle.containsKey(key)) {
			return bundle.getString(key);
		} else if (bundle.containsKey(key + "-location")) {
			return bundle.getString(key + "-location");
		}
		return null;
	}
	
	@Override
	public String[] getStringArray(String key) {
		if (bundle.containsKey(key)) {
			return bundle.getStringArray(key);
		} else if (bundle.containsKey(key + "-location")) {
			return bundle.getStringArray(key + "-location");
		}
		return null;
	}
	
	@Override
	public Object getObject(String key) {
		if (bundle.containsKey(key)) {
			return bundle.getObject(key);
		} else if (bundle.containsKey(key + "-location")) {
			return bundle.getObject(key + "-location");
		}
		return null;
	}
}
