package werkzeugkasten.nlsgen.nls;

import org.eclipse.osgi.util.NLS;

public class Strings extends NLS {
	
	public static String GENERATE_CLASSES;

	static {
		Class<?> clazz = Strings.class;
		NLS.initializeMessages(clazz.getName(), clazz);
	}
}