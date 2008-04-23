/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  <a href="mailto:masquill@us.ibm.com>Mike Squillace</a> - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.util.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.actf.core.config.Version;
import org.eclipse.actf.util.Utils;
import org.eclipse.actf.util.logging.IReporter;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;


/**
 * an implementation for locating resources within the Eclipse framework.
 * 
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public class EclipseResourceLocator extends DefaultResourceLocator
{


	private List _bundles = new LinkedList();
	
	/**
	 * used to identify the bundles in which resources are to be found. Each bundle in 
	 * Eclipse has its own class loader and we leave it to the platform to choose the class loader to be used. Thus, calling this method means that a <code>ClassLoader</code> 
	 * should not be passed to <code>getResourceAsStream</code>. Also, not all methods for finding resources in bundles 
	 * use <code>ClassLoader</code> objects.
	 * 
	 * @param bundle - the name of the bundle (i.e. plug-in) that is to be searched for a resource
	 */
	public void registerBundleName (String bundle) {
		_bundles.add(bundle);
	}
	
	/**
	 * {@inheritDoc}
	 * Note that resources are located in the context of bundles in Eclipse.
	 */
	public InputStream getResourceAsStream (String id, ClassLoader loader) {
		return getResourceAsStream(id, null, null, loader);
	}

	public InputStream getResourceAsStream (String id, String base,
			String ext, ClassLoader loader) {
		return loader != null
			? super.getResourceAsStream(id, base, ext, loader)
			: getResourceAsStream(id, base, ext);
	}
	
	private InputStream getResourceAsStream (String id, String base, String ext) {
		InputStream stream = null;
		
		for (Iterator iter = _bundles.listIterator(); iter.hasNext() & stream == null; ){
			String bundleName = (String) iter.next();
			stream = getResourceAsStream(id, base, ext, bundleName);
		}
		
		return stream;
	}
	
	public InputStream getResourceAsStream (String id, String base,
											String ext, String bundleName) {
		InputStream stream = null; //Stream to be returned.
		String relativePath = getRelativePath(base, id, ext);
		IPath pathToFile = null;
		
		if (bundleName != null) {
			pathToFile = getPathToFile(bundleName, relativePath); 
		}
		if (pathToFile != null) {
			try {
				stream = new FileInputStream(pathToFile.toFile());
				Utils.println(Utils.ALL, "Created stream for file - " + relativePath + " using bundle " + bundleName);
			}catch (FileNotFoundException e) {
				//Don't need to report anything in this catch , the result of the call to
				//getPathToFile will have already made sure that this file exists.
			}
		} else {
			Utils.println(Utils.ALL, "unable to create stream for file - " + relativePath + " using bundle " + bundleName);
		}
		
		return stream;
	}
	
	private String getRelativePath (String base, String fileName, String ext) {
		String relativePath = null;

		//First, build the relative file path that we want to look for.	
		if (base == null) {
			//If we have a base of null, default to using the resources directory   		
			base = IResourceLocator.DEFAULT_ACTF_RESOURCES_DIR;
		}
		
		if (ext != null) {
			//The passed extension is not null, use it.    		
			relativePath = base + File.separator + fileName + "." + ext;
		}else {
			//Null for an extension means we default to using .properties   		
			relativePath = base + File.separator + fileName + ".properties";
		}
		
		return relativePath;
	}

	/**
	 * {@inheritDoc}
	 * <p>Search Algorithm is:</p>
	 * <p><ol> 
	 * <li>Search through the registered ClassLoaders for the resource
	 * <li>Search through the registered Bundles for the resource
	 * <li>Test if the name is actually a Bundle
	 * </ol></p>
	 */	
	public URL getResource (String name) {
		URL result = super.getResource(name);
		
		// If we didn't find the resource in any of the registered ClassLoaders
		// Search through the bundle list to see if the resource is in a bundle
		if ( result == null ) {
			IPath ipath = null;
			
			for (Iterator iter = _bundles.listIterator(); iter.hasNext() & ipath == null; ){
				String bundleName = (String) iter.next();
				if ( bundleName.equals(name)) {
					ipath = getPathToBundle( name );
				} else {
					ipath = getPathToFile(bundleName, name);
				}
			}
			
			// If the resource was not in any of the registered bundles
			// Check to see if the resource is a bundle name
			if ( ipath == null ) {
				ipath = getPathToBundle( name );
			}
		
			// If we found the resource then create a URL to return
			if ( ipath != null ) {
				try {
					result = ipath.toFile().toURL();
				} catch (MalformedURLException e) {
				}
			}
		}
		
		return result;
	}
		
	/**
	 * {@inheritDoc}
	 * Note: You may also pass a bundle name to this method to obtain a bundle path
	 */
	public String getPath ( String name ) {
		String result = null;
		
		URL url = getResource( name );
		
		result = convertToFileURL( url );
				
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getPaths( String name ) {
		String[] result = null;
		
		URL[] urls = getResources( name );
		
		if ( urls != null ) {
			result = new String[urls.length];
			
			for( int i=0; i < urls.length; i++ ) {
				result[i] = convertToFileURL( urls[i]);
			}
		}
		
		return result;
	}
	
	private String convertToFileURL( URL url ) {
		String result = null;
		
		if ( url != null ) {
			try {
				URL fileUrl = FileLocator.toFileURL(url);				
				result = fileUrl.getFile().substring(1); // remove "file:/"
			} catch( IOException e) {
				result = null;
			}
		}
		return result;
	}
	
	public IPath getPathToFile (String bundleName, String relativePath) {
		Path absolutePath = null;
		
		if (bundleName != null && bundleName.length() > 0) {
			Bundle bundle = Platform.getBundle(bundleName);
			if (bundle != null) {
				Path relativeFilePath = new Path(relativePath);
				URL pathUrl = Platform.find(bundle, relativeFilePath);
				
				if (pathUrl == null) {
					// look in resources directory
					relativeFilePath = new Path(DEFAULT_ACTF_RESOURCES_DIR + "/" + relativePath);
					pathUrl = Platform.find(bundle, relativeFilePath);
				}
				
				if (pathUrl != null) {
					try {
						pathUrl = Platform.resolve(pathUrl);
						absolutePath = new Path(new File(pathUrl.getFile()).getAbsolutePath());
						Utils.println(IReporter.DETAIL,
							"getPathToFile(), absolute path to " + relativePath + " = " + absolutePath.toString());
					}catch (IOException ioe) {
						Utils.println(IReporter.SYSTEM_NONFATAL, ioe);
					}
				}
			}
		}
		
		return absolutePath;
	}	
	
	private IPath getPathToBundle (String bundleName) {
		IPath absolutePath = null;
		if (bundleName == null || bundleName.length() == 0) { return absolutePath; }
		Version eclipseVersion = getEclipseVersion();
		Bundle bundle = Platform.getBundle(bundleName);
		if (bundle != null) {
			// In Eclipse 3.1 the bundle location is always relative. To get the
			// absolute path we
			// need start with the path of the Eclipse installation, append the
			// relative path
			// to the bundle and let the Path class resolve the path segments to
			// for the canonical
			// path.
			// In Eclipse 3.0 the bundle locations are always absolute.
			if ((eclipseVersion.getMajor() == 3)
					&& (eclipseVersion.getMinor() == 0)) {
				absolutePath = new Path("");
			}else {
				absolutePath = new Path(getEclipseInstallPath());
			}
			// The bundle location always begins with "update@" and ends with
			// the relative bundle location.
			// We have to strip off the "update@" before we append it to the
			// path.
			String[] bundlePaths = bundle.getLocation().split("@");
			absolutePath = absolutePath.append(bundlePaths[1]);
			if ((eclipseVersion.getMajor() == 3)
					&& (eclipseVersion.getMinor() == 0)) {
				IPath path = absolutePath.makeAbsolute();
				// String [] segments = path.segments();
				// for ( int i=0; i<path.segmentCount(); i++) {
				// ConfigUtils.logDebug("\tsegment [" + i + "] - " +
				// segments[i]);
				// }
				File file = path.toFile();
				absolutePath = new Path(file.getPath());
			}
			Utils.println(
				IReporter.DETAIL,
				"getPathToBundle(), path to bundle "
						+ bundleName + " = " + absolutePath.toString());
		}
		return absolutePath;
	}	
	
	private Version getEclipseVersion () {
		Version eclipseVersion = null;
		Bundle platformBundle = Platform.getBundle("org.eclipse.platform");
		Dictionary platformDict = platformBundle.getHeaders();
		if (!platformDict.isEmpty()) {
			String version = (String) platformDict.get(Version.bundleVersionKey);
			eclipseVersion = new Version(version);
		}else {
			eclipseVersion = new Version("0.0.0");
		}
		Utils.println(
			IReporter.DETAIL,
			"getEclipseVersion(), Eclipse version - "
					+ eclipseVersion.toString());
		return eclipseVersion;
	}
	
	private String getEclipseInstallPath () {
		String filePath = "";
		try {
			// The following code returns the Eclipse installation path.
			// The URL.getPath() method returns the path in the form of
			// /d:/eclipse.
			// We want to strip off the preceeding "/" and an easy way to do it
			// is to convert it to a Path and return the string representation.
			Location installLocation = Platform.getInstallLocation();
			URL url = installLocation.getURL();
			Path absolutePath = new Path(new File(url.getFile()).getAbsolutePath());
			filePath = absolutePath.toString();
		}catch (Exception e) {
			Utils.println(IReporter.SYSTEM_NONFATAL, e.getMessage(), e);
		}
		Utils.println(
			IReporter.DETAIL,
			"getEclipseInstallPath(), Eclipse installation path - " + filePath);
		return filePath;
	}
}