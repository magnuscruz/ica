package br.gov.dataprev.sibe.batch.importbatim.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CachedConfigurationManager {

	private static CachedConfigurationManager instance;
	private Map<String, Properties> cached = new HashMap<String, Properties>();

	public static CachedConfigurationManager getInstance() {
		if (instance == null) {
			instance = new CachedConfigurationManager();
		}
		return instance;
	}

	public Properties getProperties(String nome) {

		Properties prop = null;
		if ((prop = cached.get(nome)) == null) {
			prop = new Properties();

			try {
				FileInputStream input = new FileInputStream(new File(nome));

				// load a properties file
				prop.load(input);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return prop;
	}
}
