/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Mike Squillace - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.util.jar;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.actf.util.logging.LoggingUtil;


/**
 * class for exploding jar/zip files onto the file system
 *
 *@author Barry Feigenbaum
 */
public class ZipExploder
{

	/**
	 *create a zip exploder for unpacking .jar/.zip files
	 */
	public ZipExploder () {
		this(false);
	}

	/**
	 * create a zip exploder for unpacking .jar/.zip files onto the file system
	 *
	 * @param verbose - set to <code>true</code> for verbose mode
	 */
	public ZipExploder (boolean verbose) {
		setVerbose(verbose);
	}

	/**
	 * create a zip exploder for unpacking .jar/.zip files onto the file system
	 *
	 * @param verbose - set to <code>true</code> for verbose mode
	 * @param sorted - set to <code>true</code> for sorted file mode
	 */
	public ZipExploder (boolean verbose, boolean sorted) {
		this(verbose);
		setSortNames(sorted);
	}

	protected boolean verbose;

	/**
	 * Get the verbose mode state.
	 *
	 * @return verbosity
	 */
	public boolean getVerbose () {
		return verbose;
	}

	/**
	 * set the verbose mode state
	 * 
	 * @param f - verbosity
	 */
	public void setVerbose (boolean f) {
		verbose = f;
	}

	protected boolean sortNames;

	/**
	 * @return Returns the sortNames.
	 */
	public boolean getSortNames () {
		return sortNames;
	}

	/**
	 * @param sortNames The sortNames to set.
	 */
	public void setSortNames (boolean sortNames) {
		this.sortNames = sortNames;
	}

	/**
	 * Explode source JAR and/or ZIP files into a target directory
	 * @param zipNames names of source files
	 * @param jarNames names of source files
	 * @param destDir target directory name (should already exist)
	 * @exception IOException error creating a target file
	 */
	public void process (String[] zipNames, String[] jarNames, String destDir)
		throws IOException {
		processZips(zipNames, destDir);
		processJars(jarNames, destDir);
	}

	/**
	 * Explode source JAR files into a target directory
	 * @param jarNames names of source files
	 * @param destDir target directory name (should already exist)
	 * @exception IOException error creating a target file
	 */
	public void processJars (String[] jarNames, String destDir)
		throws IOException {
		for (int i = 0; i < jarNames.length; i++) {
			processFile(jarNames[i], destDir);
		}
	}

	/**
	 * Explode source ZIP files into a target directory
	 * @param zipNames names of source files
	 * @param destDir target directory name (should already exist)
	 * @exception IOException error creating a target file
	 */
	public void processZips (String[] zipNames, String destDir)
		throws IOException {
		for (int i = 0; i < zipNames.length; i++) {
			processFile(zipNames[i], destDir);
		}
	}

	/**
	 * Explode source ZIP or JAR file into a target directory
	 * @param zipName names of source file
	 * @param destDir target directory name (should already exist)
	 * @exception IOException error creating a target file
	 */
	public void processFile (String zipName, String destDir) throws IOException {
		String source = new File(zipName).getCanonicalPath();
		String dest = new File(destDir).getCanonicalPath();
		if (verbose) {
			LoggingUtil.println(LoggingUtil.PROCESS_ALL, "\n**** Exploding "
					+ source + " to " + dest);
		}
		ZipFile f = null;
		try {
			f = new ZipFile(source);
			Map fEntries = getEntries(f);
			String[] names = (String[]) fEntries.keySet().toArray(
				new String[] {});
			if (sortNames) {
				Arrays.sort(names);
			}
			// copy all files
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				ZipEntry e = (ZipEntry) fEntries.get(name);
				copyFileEntry(dest, f, e);
			}
		}catch (IOException ioe) {
			String msg = ioe.getMessage();
			if (msg.indexOf(zipName) < 0) {
				msg += " - " + zipName;
			}
			throw new IOException(msg);
		}finally {
			if (f != null) {
				try {
					f.close();
				}catch (IOException ioe) {
				}
			}
		}
	}

	/** Get all the entries in a ZIP file. */
	protected Map getEntries (ZipFile zf) {
		Enumeration e = zf.entries();
		Map m = new HashMap();
		while (e.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) e.nextElement();
			m.put(ze.getName(), ze);
		}
		return m;
	}

	/**
	 * copy a single entry from the archive
	 * 
	 * @param destDir
	 * @param zf
	 * @param ze
	 * @throws IOException
	 */
	public void copyFileEntry (String destDir, ZipFile zf, ZipEntry ze)
		throws IOException {
		DataInputStream dis = new DataInputStream(new BufferedInputStream(zf.getInputStream(ze)));
		try {
			copyFileEntry(destDir, ze.isDirectory(), ze.getName(), dis);
		}finally {
			try {
				dis.close();
			}catch (IOException ioe) {
			}
		}
	}

	protected void copyFileEntry (String destDir, boolean destIsDir,
									String destFile, DataInputStream dis)
		throws IOException {
		byte[] bytes = readAllBytes(dis);
		LoggingUtil.println(LoggingUtil.ALL, "Writing " + bytes.length
				+ " bytes...");
		File file = new File(destFile);
		String parent = file.getParent();
		if (parent != null && parent.length() > 0) {
			File dir = new File(destDir, parent);
			if (dir != null) {
				LoggingUtil.println(LoggingUtil.ALL, "Creating directory path "
						+ dir.getAbsolutePath());
				dir.mkdirs();
				LoggingUtil.println(LoggingUtil.ALL, "Created directory path "
						+ dir.getAbsolutePath());
			}
		}
		File outFile = new File(destDir, destFile);
		if (destIsDir) {
			LoggingUtil.println(LoggingUtil.ALL, "Creating directory "
					+ outFile.getAbsolutePath());
			outFile.mkdir();
			LoggingUtil.println(LoggingUtil.ALL, "Created directory "
					+ outFile.getAbsolutePath());
		}else {
			LoggingUtil.println(LoggingUtil.ALL, "Creating file "
					+ outFile.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(outFile);
			try {
				fos.write(bytes, 0, bytes.length);
				if (verbose) {
					LoggingUtil.println(LoggingUtil.ALL, "Copied file "
							+ outFile.getAbsolutePath());
				}
			}finally {
				try {
					fos.close();
				}catch (IOException ioe) {
				}
			}
		}
	}

	// *** below may be slow for large files ***
	/** Read all the bytes in a ZIPed file */
	protected byte[] readAllBytes (DataInputStream is) throws IOException {
		byte[] bytes = new byte[0];
		for (int len = is.available(); len > 0; len = is.available()) {
			byte[] xbytes = new byte[len];
			int count = is.read(xbytes);
			LoggingUtil.println(LoggingUtil.ALL, "readAllBytes: " + len + " vs. "
					+ count);
			if (count > 0) {
				byte[] nbytes = new byte[bytes.length + count];
				System.arraycopy(bytes, 0, nbytes, 0, bytes.length);
				System.arraycopy(xbytes, 0, nbytes, bytes.length, count);
				bytes = nbytes;
			}else if (count < 0) {
				// accommodate apparent bug in IBM JVM where
				// available() always returns positive value on some files
				break;
			}
		}
		return bytes;
	}

	protected void print (String s) {
		System.out.print(s);
	}

	/** Print command help text. */
	protected static void printHelp () {
		System.out.println();
		System.out.println("Usage: java "
				+ ZipExploder.class.getName()
				+ " (-jar jarFilename... | -zip zipFilename...)... -dir destDir {-verbose}");
		System.out.println("Where:");
		System.out.println("  jarFilename path to source jar, may repeat");
		System.out.println("  zipFilename path to source zip, may repeat");
		System.out.println("  destDir    path to target directory; should exist");
		System.out.println("Note: one -jar or -zip is required; switch case or order is not important");
	}

	protected static void reportError (String msg) {
		System.err.println(msg);
		//printHelp();
		System.exit(1);
	}

	/** 
	 * Main command line entry point.
	 * @param args 
	 */
	public static void main (final String[] args) {
		if (args.length == 0) {
			printHelp();
			System.exit(0);
		}
		List zipNames = new ArrayList();
		List jarNames = new ArrayList();
		String destDir = null;
		boolean jarActive = false, zipActive = false, destDirActive = false;
		boolean verbose = false;
		// process arguments
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.charAt(0) == '-') { // switch
				arg = arg.substring(1);
				if (arg.equalsIgnoreCase("jar")) {
					jarActive = true;
					zipActive = false;
					destDirActive = false;
				}else if (arg.equalsIgnoreCase("zip")) {
					zipActive = true;
					jarActive = false;
					destDirActive = false;
				}else if (arg.equalsIgnoreCase("dir")) {
					jarActive = false;
					zipActive = false;
					destDirActive = true;
				}else if (arg.equalsIgnoreCase("verbose")) {
					verbose = true;
				}else {
					reportError("Invalid switch - " + arg);
				}
			}else {
				if (jarActive) {
					jarNames.add(arg);
				}else if (zipActive) {
					zipNames.add(arg);
				}else if (destDirActive) {
					if (destDir != null) {
						reportError("duplicate argument - " + "-destDir");
					}
					destDir = arg;
				}else {
					reportError("Too many parameters - " + arg);
				}
			}
		}
		if (destDir == null || (zipNames.size() + jarNames.size()) == 0) {
			reportError("Missing parameters");
		}
		if (verbose) {
			System.out.println("Effective command: "
					+ ZipExploder.class.getName() + " "
					+ (jarNames.size() > 0 ? "-jars " + jarNames + " " : "")
					+ (zipNames.size() > 0 ? "-zips " + zipNames + " " : "")
					+ "-dir " + destDir);
		}
		try {
			ZipExploder ze = new ZipExploder(verbose);
			ze.process(
				(String[]) zipNames.toArray(new String[zipNames.size()]),
				(String[]) jarNames.toArray(new String[jarNames.size()]),
				destDir);
		}catch (IOException ioe) {
			System.err.println("Exception - " + ioe.getMessage());
			ioe.printStackTrace(); // *** debug ***
			System.exit(2);
		}
	}
}
