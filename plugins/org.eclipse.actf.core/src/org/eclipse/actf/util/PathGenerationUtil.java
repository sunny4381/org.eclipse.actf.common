package org.eclipse.actf.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Utility class for generating the class path
 * @author administrator
 *
 */
public class PathGenerationUtil
{
	/**
	 * get path for all jars from a given bundle
	 * @param bundleName
	 * @return
	 */
	public String getJarPathsFromBundle (String bundleName) {
		
		StringBuffer buffer = new StringBuffer();
		String platformLoc = Platform.getInstallLocation().getURL().getPath();
		
		
		Bundle bundle = Platform.getBundle(bundleName);
		if (bundle == null) {
			return null;
		}
		Enumeration en = bundle.findEntries("/", "*.jar", true);
		while (en != null && en.hasMoreElements()) {
			try {
				URL url = FileLocator.resolve((URL) en.nextElement());
				String urlPath = url.getPath();
				String jarPath = null;
					
				int bang = urlPath.indexOf('!');
				if (bang > 0) {
					// jar is nested so just use containing jar
					jarPath = urlPath.substring(1, bang);
				} else {
					jarPath = urlPath.substring(1);
				}
				if ( buffer.length() > 0 ) {
					buffer.append( File.pathSeparator );
				}
				buffer.append( jarPath );
			} catch (IOException e) {
			}
		
			// TODO needs to be a more reliable way to resolve these plugin locations
			if (en == null && bundle != null) {
				String bundleLoc = bundle.getLocation();
				if ( buffer.length() > 0 ) {
					buffer.append( File.pathSeparator );
				}
				buffer.append(platformLoc.substring(1));
				buffer.append(bundleLoc.substring(bundleLoc.indexOf('@') + 1));
			}
       }

		return buffer.toString();
	}

	/**
	 * get path for given jar from the bundle
	 * @param jarName
	 * @param bundleName
	 * @return
	 */
    public String getJarPathFromBundle (String jarName, String bundleName) {
		
    	StringBuffer buffer = new StringBuffer();
        
			Bundle aBundle = Platform.getBundle(bundleName);
		 
		    URL url = aBundle.getEntry(jarName);
		    String jarPath = null;
		    
		    try {
		      jarPath = Platform.resolve(url).getPath();
		    } catch(IOException exc) {
		    	
		    }
		    return jarPath;
			/**
			Enumeration en = aBundle.findEntries("/", "*.jar", true);
			//String bundleLoc = aBundle.getLocation();
			if (en != null) {
			    while (en.hasMoreElements()) {
				    try {
					    String jarPath = Platform.resolve((URL) en.nextElement()).getPath();
					    if ( buffer.length() > 0 ) {
						    buffer.append( File.pathSeparator );
					    }
					    buffer.append( jarPath );
				    } catch (IOException e) {
				    	
				    }
				 }
			 } */
        
	}
    
    /**
     * get jar path for the given bundle which itself is a jar deployed directly in the plugin directory
     * @param bundleName
     * @return
     */
    public String getJarPathFromJarBundle (String bundleName) {
    	
    	String platformLoc = Platform.getInstallLocation().getURL().getPath();
        String pluginLoc = platformLoc + "plugins/";
        StringBuffer buffer = new StringBuffer();
        
        File dir = new File(pluginLoc);
        String [] plugins = dir.list();
        for (int j =0; j < plugins.length; j++)  {
            File file = new File(plugins[j]);
            if ((file.isDirectory())||(!plugins[j].endsWith(".jar"))) {
                continue;
            }
            
            if (plugins[j].startsWith(bundleName))  {
           	      if ( buffer.length() > 0 ) {
		  	         buffer.append( File.pathSeparator );
			      }
                  buffer.append(pluginLoc+plugins[j]);
                  
            }   
            			   
		 }
        
        return buffer.toString();
    }
}