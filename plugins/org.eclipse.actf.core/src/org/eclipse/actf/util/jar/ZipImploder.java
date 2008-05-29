/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Barry Feigenbaum - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.util.jar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.actf.util.logging.LoggingUtil;


/**
 * @author Barry Feigenbaum
 */
public class ZipImploder
{

	protected int dirCount, fileCount;

	/**
	 * @return Returns the dirCount.
	 */
	public int getDirCount () {
		return dirCount;
	}

	/**
	 * @return Returns the fileCount.
	 */
	public int getFileCount () {
		return fileCount;
	}

	/**
	 * create a new imploder with no verbosity
	 *
	 */
	public ZipImploder () {
		this(false);
	}

	/**
	 * create a new imploder with the specified verbosity state
	 * 
	 * @param verbose - verbosity state
	 */
	public ZipImploder (boolean verbose) {
		setVerbose(verbose);
	}

	protected boolean verbose;

	/**
	 * get the verbose mode
	 * 
	 * @return verbosity mode
	 */
	public boolean getVerbose () {
		return verbose;
	}

	/**
	 * set the verbosity mode
	 * 
	 * @param f verbosity state
	 */
	public void setVerbose (boolean f) {
		verbose = f;
	}

	protected String baseDir;

	/**
	 * @return Returns the baseDir.
	 */
	public String getBaseDir () {
		return baseDir;
	}

	/**
	 * @param baseDir The baseDir to set.
	 * @throws IOException
	 */
	public void setBaseDir (String baseDir) throws IOException {
		if (baseDir != null) {
			baseDir = new File(baseDir).getCanonicalPath();
			baseDir = baseDir.replace('\\', '/');
		}
		this.baseDir = baseDir;
	}

	protected Manifest manifest;

	/**
	 * @return Returns the manifest
	 */
	public Manifest getManifest () {
		return manifest;
	}

	/**
	 * @param manifest The manifest to set.
	 */
	public void setManifest (Manifest manifest) {
		this.manifest = manifest;
	}

	protected boolean includeDirs;

	/**
	 * returns whether or not path information is included in .zip
	 *
	 * @return <code>true</code> if path information is included, <code>false</code> otherwise 
	 */
	public boolean getIncludeDirs () {
		return includeDirs;
	}

	/**
	 * set whether or not path information is included in .zip files
	 * 
	 * @param includeDirs include path inforamtion in .zip file
	 */
	public void setIncludeDirs (boolean includeDirs) {
		this.includeDirs = includeDirs;
	}

	/**
	 * implode source directory into .jar/.zip file
	 * @param zipName name of target file
	 * @param jarName name of target file
	 * @param sourceDir source directory name
	 * @exception IOException error creating a target file
	 */
	public void process (String zipName, String jarName, String sourceDir)
		throws IOException {
		dirCount = 0;
		fileCount = 0;
		if (zipName != null) {
			processZip(zipName, sourceDir);
		}
		if (jarName != null) {
			processJar(jarName, sourceDir);
		}
	}

	/**
	 * Implode target JAR file from a source directory
	 * @param jarName name of target file
	 * @param sourceDir source directory name
	 * @exception IOException error creating a target file
	 */
	public void processJar (String jarName, String sourceDir)
		throws IOException {
		processJar(jarName, sourceDir, null);
	}

	/**
	 * Implode target JAR file from a source directory
	 * @param jarName name of target file
	 * @param sourceDir source directory name (
	 * @param comment
	 * @exception IOException error creating a target file
	 */
	public void processJar (String jarName, String sourceDir, String comment)
		throws IOException {
		processJar(jarName, sourceDir, comment, -1, -1);
	}

	/**
	 * Implode target JAR file from a source directory
	 * 
	 * @param jarName - name of target .jar
	 * @param sourceDir - source directory
	 * @param comment - comment for .jar file
	 * @param method
	 * @param level
	 * @throws IOException
	 */
	public void processJar (String jarName, String sourceDir, String comment,
							int method, int level) throws IOException {
		String dest = setup(jarName, sourceDir);
		Manifest man = getManifest();
		JarOutputStream jos = man != null ? new JarOutputStream(new BufferedOutputStream(new FileOutputStream(dest)), man)
				: new JarOutputStream(new BufferedOutputStream(new FileOutputStream(dest)));
		configure(jos, comment, method, level);
		process(jos, new File(sourceDir));
	}

	/**
	 * Implode target JAR file from a source directory
	 * @param zipName name of target file
	 * @param sourceDir source directory name (
	 * @exception IOException error creating a target file
	 */
	public void processZip (String zipName, String sourceDir)
		throws IOException {
		processZip(zipName, sourceDir, null);
	}

	/**
	 * Implode target zip file from a source directory
	 * 
	 * @param zipName
	 * @param sourceDir
	 * @param comment
	 * @throws IOException
	 */
	public void processZip (String zipName, String sourceDir, String comment)
		throws IOException {
		processZip(zipName, sourceDir, comment, -1, -1);
	}

	/**
	 * Implode target zip file from a source directory
	 * 
	 * @param zipName
	 * @param sourceDir
	 * @param comment
	 * @param method
	 * @param level
	 * @throws IOException
	 */
	public void processZip (String zipName, String sourceDir, String comment,
							int method, int level) throws IOException {
		String dest = setup(zipName, sourceDir);
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(dest)));
		configure(zos, comment, method, level);
		process(zos, new File(sourceDir));
	}

	protected void configure (ZipOutputStream zos, String comment, int method,
								int level) {
		if (comment != null) {
			zos.setComment(comment);
		}
		if (method >= 0) {
			zos.setMethod(method);
		}
		if (level >= 0) {
			zos.setLevel(level);
		}
	}

	protected String setup (String zipName, String sourceDir)
		throws IOException {
		File dir = new File(sourceDir);
		if (!dir.exists() && !dir.isDirectory()) { throw new IOException("source must exist and be a directory: "
				+ dir); }
		String source = dir.getCanonicalPath();
		String dest = new File(zipName).getCanonicalPath();
		if (verbose) {
			LoggingUtil.println(LoggingUtil.PROCESS_ALL, "\n**** Imploding "
					+ source + " to " + dest);
		}
		return dest;
	}

	protected void process (ZipOutputStream zos, File dir) throws IOException {
		try {
			processDir(zos, dir);
		}finally {
			zos.close();
		}
	}

	protected String removeDrive (String path) {
		return path.length() >= 2 && path.charAt(1) == ':' ? path.substring(2)
				: path;
	}

	protected String removeLead (String path) {
		if (baseDir != null && path.startsWith(baseDir)) {
			path = path.substring(baseDir.length());
			if (path.length() >= 1) {
				if (path.charAt(0) == '/' || path.charAt(0) == '\\') {
					path = path.substring(1); // drop leading /
				}
			}
		}
		return path;
	}

	public void processDir (ZipOutputStream zos, File dir) throws IOException {
		String path = dir.getCanonicalPath();
		path = path.replace('\\', '/');
		if (includeDirs) {
			if (baseDir == null || path.length() > baseDir.length()) {
				String xpath = removeDrive(removeLead(path));
				if (xpath.length() > 0) {
					xpath += '/';
					if (verbose) {
						LoggingUtil.println(
							LoggingUtil.PROCESS_ALL, "\nProcessing directory "
									+ path + " to " + xpath);
					}
					ZipEntry ze = new ZipEntry(xpath);
					zos.putNextEntry(ze);
				}else {
					if (verbose) {
						LoggingUtil.println(
							LoggingUtil.PROCESS_ALL, "\nSkipping empty path");
					}
				}
			}else {
				if (verbose) {
					LoggingUtil.println(LoggingUtil.PROCESS_ALL, "\nDropping "
							+ path);
				}
			}
		}else {
			if (verbose) {
				LoggingUtil.println(LoggingUtil.PROCESS_ALL, "\nSkipping " + path);
			}
		}
		dirCount++;
		String[] files = dir.list();
		for (int i = 0; i < files.length; i++) {
			String file = files[i];
			File f = new File(dir, file);
			if (f.isDirectory()) {
				processDir(zos, f);
			}else {
				processFile(zos, f);
			}
		}
	}

	/**
	 * process a single file for  a .zip file
	 * 
	 * @param zos
	 * @param f
	 * @throws IOException
	 */
	public void processFile (ZipOutputStream zos, File f) throws IOException {
		String path = f.getCanonicalPath();
		path = path.replace('\\', '/');
		String xpath = removeDrive(removeLead(path));
		if (verbose) {
			LoggingUtil.println(LoggingUtil.PROCESS_ALL, "Processing file "
					+ path + " to " + xpath);
		}
		ZipEntry ze = new ZipEntry(xpath);
		ze.setTime(f.lastModified());
		ze.setSize(f.length());
		zos.putNextEntry(ze);
		fileCount++;
		try {
			copyFileEntry(zos, f);
		}finally {
			zos.closeEntry();
		}
	}

	protected void copyFileEntry (ZipOutputStream zos, File f)
		throws IOException {
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
		try {
			copyFileEntry(zos, dis);
		}finally {
			try {
				dis.close();
			}catch (IOException ioe) {
			}
		}
	}

	protected void copyFileEntry (ZipOutputStream zos, DataInputStream dis)
		throws IOException {
		byte[] bytes = readAllBytes(dis);
		LoggingUtil.println(LoggingUtil.ALL, "Writing " + bytes.length
				+ " bytes...");
		zos.write(bytes, 0, bytes.length);
	}

	// *** below may be slow for large files ***
	/** Read all the bytes in a stream */
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
			}
		}
		return bytes;
	}

	protected void print (String s) {
		System.out.print(s);
	}

	/** Print command help text. */
	public static void printHelp () {
		System.out.println();
		System.out.println("Usage: java " + ZipImploder.class.getName());
		System.out.println("       (-jar <jarName> {-manifest <manfile>} | -zip <zipName>)");
		System.out.println("       -dir <sourceDir> {-lead <leadDir>} {-doDirs} {-verbose}");
		System.out.println("Where:");
		System.out.println("  <jarName>     path to target jar");
		System.out.println("  <zipName>     path to target zip");
		System.out.println("  <manfile>     path to manifest file");
		System.out.println("  <sourceDir>   path to source directory; must exist");
		System.out.println("  <leadDir>     partial lead path to remove from stored entries; default: <sourceDir>");
		System.out.println("  <noDirs>      skip output of directory entries");
		System.out.println("  <verbose>     output progress information");
		System.out.println("Note: switch case or order is not important");
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
		String zipName = null;
		String jarName = null;
		String manName = null;
		String sourceDir = null;
		String leadDir = null;
		boolean jarActive = false, manActive = false, zipActive = false, sourceDirActive = false, leadDirActive = false;
		boolean verbose = false;
		boolean noDirs = false;
		// process arguments
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.charAt(0) == '-') { // switch
				arg = arg.substring(1);
				if (arg.equalsIgnoreCase("jar")) {
					jarActive = true;
					manActive = false;
					zipActive = false;
					sourceDirActive = false;
					leadDirActive = false;
				}else if (arg.equalsIgnoreCase("manifest")) {
					jarActive = false;
					manActive = true;
					zipActive = false;
					sourceDirActive = false;
					leadDirActive = false;
				}else if (arg.equalsIgnoreCase("zip")) {
					zipActive = true;
					manActive = false;
					jarActive = false;
					sourceDirActive = false;
					leadDirActive = false;
				}else if (arg.equalsIgnoreCase("dir")) {
					jarActive = false;
					manActive = false;
					zipActive = false;
					sourceDirActive = true;
					leadDirActive = false;
				}else if (arg.equalsIgnoreCase("lead")) {
					jarActive = false;
					manActive = false;
					zipActive = false;
					sourceDirActive = false;
					leadDirActive = true;
				}else if (arg.equalsIgnoreCase("noDirs")) {
					noDirs = true;
					jarActive = false;
					manActive = false;
					zipActive = false;
					sourceDirActive = false;
					leadDirActive = false;
				}else if (arg.equalsIgnoreCase("verbose")) {
					verbose = true;
					jarActive = false;
					manActive = false;
					zipActive = false;
					sourceDirActive = false;
					leadDirActive = false;
				}else {
					reportError("Invalid switch - " + arg);
				}
			}else {
				if (jarActive) {
					if (jarName != null) {
						reportError("Duplicate value - " + arg);
					}
					jarName = arg;
				}else if (manActive) {
					if (manName != null) {
						reportError("Duplicate value - " + arg);
					}
					manName = arg;
				}else if (zipActive) {
					if (zipName != null) {
						reportError("Duplicate value - " + arg);
					}
					zipName = arg;
				}else if (sourceDirActive) {
					if (sourceDir != null) {
						reportError("Duplicate value - " + arg);
					}
					sourceDir = arg;
				}else if (leadDirActive) {
					if (leadDir != null) {
						reportError("Duplicate value - " + arg);
					}
					leadDir = arg;
				}else {
					reportError("Too many parameters - " + arg);
				}
			}
		}
		if (sourceDir == null || (zipName == null && jarName == null)) {
			reportError("Missing parameters");
		}
		if (manName != null && zipName != null) {
			reportError("Manifests not supported on ZIP files");
		}
		if (leadDir == null) {
			leadDir = new File(sourceDir).getAbsolutePath().replace('\\', '/') + '/';
		}
		if (verbose) {
			System.out.println("Effective command: "
					+ ZipImploder.class.getName()
					+ (jarName != null ? " -jar " + jarName
							+ (manName != null ? " -manifest " + manName : "")
							: "") + (zipName != null ? " -zip " + zipName : "")
					+ " -dir " + sourceDir + " -lead " + leadDir
					+ (noDirs ? " -noDirs" : "") + (verbose ? " -verbose" : ""));
		}
		try {
			ZipImploder zi = new ZipImploder(verbose);
			if (leadDir != null) {
				zi.setBaseDir(leadDir);
			}
			if (manName != null) {
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(manName));
				try {
					zi.setManifest(new Manifest(bis));
				}finally {
					bis.close();
				}
			}
			zi.setIncludeDirs(!noDirs);
			zi.process(zipName, jarName, sourceDir);
			if (verbose) {
				System.out.println("\nDone Directories=" + zi.getDirCount()
						+ " Files=" + zi.getFileCount());
			}
		}catch (IOException ioe) {
			System.err.println("Exception - " + ioe.getMessage());
			//ioe.printStackTrace();  // *** debug ***
			System.exit(2);
		}
	}
}
