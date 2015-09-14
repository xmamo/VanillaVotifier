package co.virtualdragon.vanillaVotifier.languagePack;

public interface LanguagePack {

	String getString(String key);

	String[] getStringArray(String key);

	Object getObject(String key);
}
