/*******************************************************************************
* Copyright (c) 2004, 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* IBM Corporation - initial API and implementation                                         * - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.util.jar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/****************************************************************************
 *                                                                          *
 *  InstallJars - a utility to download and install files, Jars and Zips.   *
 *                                                                          *
 * @author Barry Feigenbaum, Ph.D.                                          *
 ***************************************************************************/
public class InstallJars
{

	public static final int BLOCK_SIZE = 512;

	public static final int BLOCK_COUNT = 20;

	// *** must be a multiple of BLOCK_SIZE ***
	public static int bufferSize = 128 * (2 * BLOCK_SIZE);

	// *** need to NLS enable all user messages ***
	/**
	 * Constructor.  Expand, run and verbose output requested.
	 */
	public InstallJars () {
		this(true, true, true, "InstallJars.properties", "cmd /c java");
	}

	/**
	 * Contstructor.
	 * @param expand <code>true</code> if the archive is t be expanded in the target
	 * @param verbose <code>true</code> if messages are to be generated
	 * @param run <code>true</code> if file is to be executed
	 * @param propName properties file with items to install
	 * @param javaParams java parameters
	 */
	public InstallJars (boolean expand, boolean verbose, boolean run,
						String propName, String javaParams) {
		setExpand(expand);
		setVerbose(verbose);
		setRunMode(run);
		setPropFilename(propName);
		setJavaParams(javaParams);
	}

	protected boolean verbose;

	/** 
	 * Get the verbose mode state.
	 * @return is in verbose mode
	 */
	public boolean getVerbose () {
		return verbose;
	}

	/** 
	 * Set the verbose mode state.
	 * @param f value 
	 */
	public void setVerbose (boolean f) {
		verbose = f;
	}

	protected boolean run;

	/** 
	 * Get the run mode state.
	 * @return is in run mode
	 */
	public boolean getRunMode () {
		return run;
	}

	/** 
	 * Set the run mode state.
	 * @param f value
	 */
	public void setRunMode (boolean f) {
		run = f;
	}

	protected boolean expand;

	/** 
	 * Get the expand mode state.
	 * @return is expanded 
	 */
	public boolean getExpand () {
		return expand;
	}

	/** 
	 * Set the expand mode state.
	 * @param f value
	 */
	public void setExpand (boolean f) {
		expand = f;
	}

	protected String propFilename;

	/** 
	 * Get the propFilename mode state.
	 * @return prooperty file name 
	 */
	public String getPropFilename () {
		return propFilename;
	}

	/** 
	 * Set the propFilename mode state.
	 * @param name
	 */
	public void setPropFilename (String name) {
		propFilename = name;
	}

	protected String javaParams = "cmd /c java";

	/** 
	 * Get the JavaParams mode state.
	 * @return java parameters
	 */
	public String getJavaParams () {
		return javaParams;
	}

	/** 
	 * Set the JavaParams mode state.
	 * @param p value
	 */
	public void setJavaParams (String p) {
		javaParams = p;
	}

	protected void print (String s) {
		if (verbose) {
			System.out.print(s);
		}
	}

	protected void println (String s) {
		if (verbose) {
			System.out.println(s);
		}
	}

	protected void println () {
		println("");
	}

	/**
	 * Install based on a properties file<br>
	 * @return recommended classpath
	 * @exception IOException Thrown if a JAR file access error occurs
	 **/
	public String install () throws IOException {
		StringBuffer classpath = new StringBuffer();
		Properties prop = new Properties();
		prop.load(new BufferedInputStream(new FileInputStream(propFilename)));
		for (Iterator i = prop.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			String value = prop.getProperty(key);
			String xurl = null;
			String xdir = null;
			String xcp = null;
			boolean xexpand = expand, xrun = run;
			if (value != null) {
				value = value.trim();
				if (value.length() > 0) {
					String delim = value.substring(0, 1);
					StringTokenizer st = new StringTokenizer(value.substring(1), delim);
					xurl = st.nextToken();
					xdir = (st.hasMoreTokens() ? st.nextToken() : ".").trim();
					if (xdir.length() == 0) {
						xdir = ".";
					}
					xcp = (st.hasMoreTokens() ? st.nextToken() : xdir).trim();
					if (xcp.length() == 0) {
						xcp = xdir;
					}
					classpath.append(xcp);
					classpath.append(";");
					while (st.hasMoreTokens()) {
						String xoption = st.nextToken().trim();
						if (xoption.equalsIgnoreCase("expand")) {
							xexpand = true;
						}else if (xoption.equalsIgnoreCase("noexpand")) {
							xexpand = false;
						}else if (xoption.equalsIgnoreCase("run")) {
							xrun = true;
						}else if (xoption.equalsIgnoreCase("norun")) {
							xrun = false;
						}else {
							throw new IllegalArgumentException("invalid install property - "
									+ key + "=" + value);
						}
					}
				}
			}
			if (xurl == null || xurl.length() == 0) { throw new IllegalArgumentException("missing install property - "
					+ key + "=" + value); }
			System.out.print("\nInstalling " + key);
			if (verbose) {
				System.out.print(" using URL=" + xurl + "; target=" + xdir
						+ "; classpath=" + xcp + "; "
						+ (xexpand ? "expand" : "noexpand") + "; "
						+ (xrun ? "run" : "norun"));
			}
			System.out.println("...");
			installFile(xurl, xdir, xexpand, xrun);
		}
		return classpath.toString();
	}

	/**
	 * Install a Zip/Jar file.
	 * 
	 * @param fileUrl The file/zip/jar file
	 * @param targetPath root of directory or file to install into
	 * @param doExpand
	 * @param doRun
	 * @exception IOException Thrown if a JAR file access error occurs
	 */
	public void installFile (String fileUrl, String targetPath,
								boolean doExpand, boolean doRun)
		throws IOException {
		String targetFilename = new File(targetPath).getCanonicalPath().replace(
			'\\', '/');
		println("Installing in " + targetFilename);
		URL url = new URL(fileUrl);
		URLConnection conn = url.openConnection();
		//System.out.println("Conn = " + conn);
		String ctype = conn.getContentType();
		println("Content type is " + ctype);
		String extension = getExtension(fileUrl);
		if (extension.equals("class")) {
			installClass(conn, targetFilename, doExpand, doRun);
			//println("Installed class file " + fileUrl + "; please run");
		}else if (extension.equalsIgnoreCase("zip")) {
			installZip(conn, targetFilename, doExpand, doRun);
			//println("Installed ZIP file " + fileUrl + "; ZIP expanded");
		}else if (extension.equalsIgnoreCase("gz")) {
			installGZip(conn, targetFilename, doExpand, doRun);
			//println("Installed GZIP file " + fileUrl + "; ZIP expanded");
		}else if (extension.equalsIgnoreCase("jar")) {
			installJar(conn, targetFilename, doExpand, doRun);
			//System.out.println("Installed JAR file " + fileUrl + "; please add to CLASSPATH");
		}else {
			throw new IllegalArgumentException("Unknown extension - "
					+ extension);
		}
	}

	public void installClass (URLConnection conn, String target,
								boolean doExpand, boolean doRun)
		throws IOException {
		// doExpand not used on htis type
		print("Installing class file " + target + " from "
				+ conn.getURL().toExternalForm());
		copyStream(conn, target);
		println();
		if (doRun) {
			runTarget(target, false);
		}
	}

	protected void runTarget (String target, boolean isJar) throws IOException {
		// *** add run code ***
		if (isJar) {
			System.out.println("runTarget(" + target + "," + isJar
					+ ") not currently implemented");
		}else {
			try {
				String name = removeExtension(getFile(target));
				String cp = "-cp " + removeFile(target);
				String command = javaParams + " " + cp + " " + name + " >"
						+ name + ".out 2>" + name + ".err";
				//String command = javaParams + " " + cp + " " + name;
				System.out.println("Running " + command + "...");
				Process p = Runtime.getRuntime().exec(command);
				int rc = p.waitFor();
				System.out.println("Return code=" + rc);
			}catch (Exception e) {
				System.out.println("Exception - " + e.getMessage());
			}
		}
	}

	public void installJar (URLConnection conn, String target,
							boolean doExpand, boolean doRun) throws IOException {
		if (doExpand) {
			println("Expanding JAR file " + target + " from "
					+ conn.getURL().toExternalForm());
			// *** may need to specialize for JAR format ***
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(conn.getInputStream(), BLOCK_SIZE
					* BLOCK_COUNT));
			int count = 0;
			prepDirs(target, true);
			try {
				while (zis.available() > 0) {
					ZipEntry ze = zis.getNextEntry();
					copyEntry(target, zis, ze);
					count++;
				}
			}finally {
				try {
					zis.close();
				}catch (IOException ioe) {
				}
			}
			println("Installed " + count + " files/directories");
		}else {
			print("Installing JAR file " + target + " from "
					+ conn.getURL().toExternalForm());
			copyStream(conn, target);
			println();
			if (doRun) {
				runTarget(target, true);
			}
		}
	}

	public void installZip (URLConnection conn, String target,
							boolean doExpand, boolean doRun) throws IOException {
		// doRun not used on htis type
		if (doExpand) {
			String ctype = conn.getContentType();
			if (!ctype.equals("application/zip")) { throw new IllegalArgumentException("Unkexpected content type - "
					+ ctype); }
			println("Expanding ZIP file to " + target + " from "
					+ conn.getURL().toExternalForm());
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(conn.getInputStream(), BLOCK_SIZE
					* BLOCK_COUNT));
			int count = 0;
			prepDirs(target, true);
			try {
				for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
					copyEntry(target, zis, ze);
					//zis.closeEntry();
					count++;
				}
			}finally {
				try {
					zis.close();
				}catch (IOException ioe) {
				}
			}
			println("Installed " + count + " files/directories");
		}else {
			print("Installing ZIP file " + target + " from "
					+ conn.getURL().toExternalForm());
			copyStream(conn, target);
			println();
		}
	}

	public void installGZip (URLConnection conn, String target,
								boolean doExpand, boolean doRun)
		throws IOException {
		// doRun not used on htis type
		if (doExpand) {
			String ctype = conn.getContentType();
			if (!ctype.equals("application/x-tar")) { throw new IllegalArgumentException("Unkexpected content type - "
					+ ctype); }
			print("Expanding GZIP file to " + target + " from "
					+ conn.getURL().toExternalForm());
			prepDirs(target, false);
			GZIPInputStream zis = new GZIPInputStream(new BufferedInputStream(conn.getInputStream(), BLOCK_SIZE
					* BLOCK_COUNT));
			try {
				//BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(target), BLOCK_SIZE * BLOCK_COUNT);
				//try {
				//  byte[] buf = new byte[bufferSize];
				//  for (int size = zis.read(buf, 0, buf.length), count = 0; 
				//       size >= 0; 
				//       size = zis.read(buf, 0, buf.length), count++) {
				//      //if (count % 4 == 0) print(".");
				//      os.write(buf, 0, size);
				//  }
				//}
				//finally {
				//    try { os.flush(); os.close(); } catch (IOException ioe) {}
				//}
				pumpGZip(target, zis);
			}finally {
				try {
					zis.close();
				}catch (IOException ioe) {
				}
			}
			println();
		}else {
			print("Installing GZIP file " + target + " from "
					+ conn.getURL().toExternalForm());
			copyStream(conn, target);
			println();
		}
	}

	/** Copy a zip entry. */
	protected void copyEntry (String target, ZipInputStream zis, ZipEntry ze)
		throws IOException {
		String name = ze.getName();
		boolean isDir = false;
		if (name.endsWith("/")) {
			name = name.substring(0, name.length() - 1);
			isDir = true;
		}
		String path = target + File.separator + name;
		path = path.replace('\\', '/');
		String mod = ze.getSize() > 0 ? ("[" + ze.getCompressedSize() + ":"
				+ ze.getSize() + "]") : "";
		print("Expanding " + ze + mod + " to " + path);
		prepDirs(path, isDir);
		if (!isDir) {
			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(path), BLOCK_SIZE
					* BLOCK_COUNT);
			try {
				byte[] buf = new byte[bufferSize];
				for (int size = zis.read(buf, 0, buf.length), count = 0; size >= 0; size = zis.read(
					buf, 0, buf.length), count++) {
					//if (count % 4 == 0) print(".");
					os.write(buf, 0, size);
				}
			}finally {
				try {
					os.flush();
					os.close();
				}catch (IOException ioe) {
				}
			}
		}
		println();
	}

	public void copyStream (URLConnection conn, String target)
		throws IOException {
		prepDirs(target, false);
		BufferedInputStream is = new BufferedInputStream(conn.getInputStream(), BLOCK_SIZE
				* BLOCK_COUNT);
		BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(target), BLOCK_SIZE
				* BLOCK_COUNT);
		byte[] buf = new byte[bufferSize];
		for (int size = is.read(buf), count = 0; size >= 0; size = is.read(buf), count++) {
			//if (count % 4 == 0) print(".");
			os.write(buf, 0, size);
		}
		os.flush();
		os.close();
		is.close();
	}

	protected static final int OFFSET_NAME = 0;

	protected static final int OFFSET_MODE = OFFSET_NAME + 100;

	protected static final int OFFSET_UID = OFFSET_MODE + 8;

	protected static final int OFFSET_GID = OFFSET_UID + 8;

	protected static final int OFFSET_SIZE = OFFSET_GID + 8;

	protected static final int OFFSET_MTIME = OFFSET_SIZE + 12;

	protected static final int OFFSET_CHKSUM = OFFSET_MTIME + 12;

	protected static final int OFFSET_TYPE = OFFSET_CHKSUM + 8;

	protected static final int OFFSET_LINKNAME = OFFSET_TYPE + 1;

	protected static final int OFFSET_MAGIC = OFFSET_LINKNAME + 100;

	protected static final int OFFSET_VERSION = OFFSET_MAGIC + 6;

	protected static final int OFFSET_UNAME = OFFSET_VERSION + 2;

	protected static final int OFFSET_GNAME = OFFSET_UNAME + 32;

	protected static final int OFFSET_DEVMAJOR = OFFSET_GNAME + 32;

	protected static final int OFFSET_DEVMINOR = OFFSET_DEVMAJOR + 8;

	protected static final int OFFSET_PREFIX = OFFSET_DEVMINOR + 8;

	protected static final int OFFSET_END = OFFSET_PREFIX + 155;

	protected static final String MAGIC = "USTAR";

	protected void pumpGZip (String target, GZIPInputStream zis)
		throws IOException {
		String curName = null;
		long curSize = 0, remainingSize = 0;
		char curType = 0;
		int curMajor = 0, curMinor = 0;
		boolean inFile = false;
		BufferedOutputStream curOs = null;
		int instFiles = 0, instDirs = 0;
		byte[] buf = new byte[bufferSize];
		top: while (true) {
			int loaded = loadBytes(buf, zis);
			if (loaded < 0) {
				break;
			}
			//System.out.println("pumpGZip: loaded=" + loaded);
			// process each buffer of data
			for (int index = 0; index < loaded; index += BLOCK_SIZE) {
				//System.out.println("pumpGZip: infile=" + inFile + ", remaining=" + remainingSize);
				if (inFile && remainingSize > 0) { // process body part
					int xsize = Math.min((int) remainingSize, BLOCK_SIZE);
					if (curOs != null) {
						curOs.write(buf, index, xsize);
					}
					remainingSize -= xsize;
				}else { // process header block
					if (inFile) {
						inFile = false;
						if (curOs != null) {
							try {
								curOs.flush();
								curOs.close();
							}catch (IOException ioe) {
							}
							println();
						}
					}
					if (isEmptyBlock(buf, index)) { // check logical end of archive
						break top;
					}
					//System.out.println("pumpGZip: header=" + (new String(buf, 0, index, 512)));
					curName = extractString(buf, index + OFFSET_NAME, 100);
					curType = extractChar(buf, index + OFFSET_TYPE);
					curSize = extractLong(buf, index + OFFSET_SIZE, 12);
					remainingSize = curSize;
					if (remainingSize > Integer.MAX_VALUE) { throw new IOException("entry size too large - "
							+ remainingSize); }
					String mod = "";
					String magic = extractString(buf, index + OFFSET_MAGIC, 6);
					if (magic.equals(MAGIC)) {
						curName = extractString(buf, index + OFFSET_PREFIX, 155)
								+ curName;
						extractInt(buf, index + OFFSET_VERSION, 2);
						curMajor = extractInt(buf, index + OFFSET_DEVMAJOR, 8);
						curMinor = extractInt(buf, index + OFFSET_DEVMINOR, 8);
						if (curMajor > 0 || curMinor > 0) {
							mod = "[" + curMajor + '.' + curMinor + "]";
						}
					}
					//System.out.println("pumpGZip: " + 
					//                   magic + "," +
					//                   curName + "," +
					//                   curType + "," +
					//                   curSize + "," +
					//                   curVersion + "," +
					//                   curMajor + "," +
					//                   curMinor);
					String path = target + File.separator + curName;
					path = path.replace('\\', '/');
					curOs = null;
					if (curType == 0 || curType == '0') { // a file
						print("Copying " + curName + mod + " to " + path);
						prepDirs(path, false);
						curOs = new BufferedOutputStream(new FileOutputStream(path), BLOCK_SIZE
								* BLOCK_COUNT);
						inFile = true;
						instFiles++;
					}else if (curType == '1' || curType == '2') { // a link
						if (curSize > 0) { throw new IOException("link entries cannot have content - "
								+ curSize); }
						println("Link ignored - " + curName + mod);
					}else if (curType == '5') { // a directory
						if (path.endsWith("/")) {
							path = path.substring(0, path.length() - 1);
						}
						println("Mkdir " + curName + mod + " to " + path);
						prepDirs(path, true);
						instDirs++;
					}else {
						if (curSize > 0) {
							//throw new IOException("entry type " + curType + " cannot have a content - size=" + curSize);
							inFile = true;
						}
						print("Entry type " + curType + " ignored - " + curName
								+ mod);
					}
				}
			}
		}
		println("Installed " + instFiles + " files and " + instDirs
				+ " directories");
	}

	protected int loadBytes (byte[] buf, GZIPInputStream zis)
		throws IOException {
		int loaded = -1;
		for (int size = zis.read(buf, 0, buf.length), count = 0; size > 0; size = zis.read(
			buf, loaded, buf.length - loaded), count++) {
			//if (count % 4 == 0) print(".");
			//System.out.println("loadBytes: loaded=" + loaded);
			if (loaded < 0) {
				loaded = 0;
			}
			loaded += size;
		}
		return loaded;
	}

	protected boolean isEmptyBlock (byte[] buf, int index) {
		boolean r = true;
		for (int i = 0; r && i < BLOCK_SIZE; i++) {
			r = buf[index++] == 0;
		}
		//System.out.println("isEmptyBlock: " + r);
		return r;
	}

	protected char extractChar (byte[] buf, int index) throws IOException {
		return (char) buf[index];
	}

	protected int extractInt (byte[] buf, int index, int length)
		throws IOException {
		return (int) extractLong(buf, index, length);
	}

	protected long extractLong (byte[] buf, int index, int length)
		throws IOException {
		String xsize = extractString(buf, index, length);
		long v = 0;
		for (int i = 0; i < xsize.length(); i++) {
			char c = xsize.charAt(i);
			if (c != ' ') {
				if (c < '0' || c > '7') { throw new IOException("non-octal digit found - "
						+ c); }
				v = v * 8 + (c - '0');
			}
		}
		return v;
	}

	protected String extractString (byte[] buf, int index, int length)
		throws IOException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, xindex = index; i < length; i++, xindex++) {
			int c = buf[xindex];
			if (c == 0) {
				break;
			}
			sb.append((char) c);
		}
		//System.out.println("extractString(" + index + "," + length + "): " + sb.toString());
		return sb.toString();
	}

	protected String getFile (String name) {
		int posn = name.lastIndexOf("/");
		return posn > 0 ? name.substring(posn + 1) : name;
	}

	protected String removeFile (String name) {
		int posn = name.lastIndexOf("/");
		return posn > 0 ? name.substring(0, posn) : name;
	}

	protected String removeExtension (String name) {
		int posn1 = name.lastIndexOf("/");
		int posn2 = name.lastIndexOf(".");
		return (posn2 > 0 && posn2 > posn1) ? name.substring(0, posn2) : name;
	}

	protected String extraceFile (String name) {
		int posn = name.lastIndexOf(File.separator);
		return posn >= 0 ? name.substring(posn + 1) : null;
	}

	protected String getExtension (String name) {
		int posn = name.lastIndexOf('.');
		return posn >= 0 ? name.substring(posn + 1) : "";
	}

	protected void prepDirs (String name) {
		prepDirs(name, expand);
	}

	protected void prepDirs (String name, boolean includeLast) {
		File f = new File(includeLast ? name : removeFile(name));
		//System.out.print("(Making " + f + ")");
		f.mkdirs();
	}

	protected void printUsage () {
		println("Effective command: " + getClass().getName() + " "
				+ propFilename + (expand ? " -expand" : " -noexpand")
				+ (run ? " -run" : " -norun") + " -java \"" + javaParams + "\""
				+ (verbose ? " -verbose" : " -quiet "));
	}

	/** Print command help text. */
	protected static void printHelp () {
		System.out.println();
		System.out.println("Usage: java "
				+ InstallJars.class.getName()
				+ " {propFilename} {-expand | -noexpand} {-run | -norun} {-quiet | -verbose} {-java <params>}");
		System.out.println("Where:");
		System.out.println("  propFilename    path to properties file (default=InstallJars.properties)");
		System.out.println("  -expand         expand any top level JAR/ZIP/GZIP (default)");
		System.out.println("  -noexpand       do not expand any top level JAR/ZIP/GZIP");
		System.out.println("  -run            run class or JAR files (default)");
		System.out.println("  -norun          do not run class or JAR files");
		System.out.println("  -verbose        output progress messages (default)");
		System.out.println("  -quiet          suppress most messages");
		System.out.println("  -java           sets java runtime paramters");
		System.out.println();
		System.out.println("Properties file entry format: name=!url{!target{!classpath{!option}...}}");
		System.out.println("Where:");
		System.out.println("  name      name displayed while installing");
		System.out.println("  url       source of items to download and install");
		System.out.println("  target    root of install directory or file (default=.)");
		System.out.println("  classpath class path entry to use for this directrory or file (default=target}");
		System.out.println("  option    one of the following options: expand, noexpand, run, norun");
		System.out.println("            if omitted, the command line default is used");
		System.out.println("! is a delimiter, the first non-whitespace character is used.");
		System.out.println("Options expand and run may not apply to all types of files.");
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
		String propName = null;
		boolean expand = true;
		boolean verbose = true;
		boolean run = true;
		String params = "cmd /c java";
		// process arguments
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.charAt(0) == '-') { // switch
				arg = arg.substring(1);
				if (arg.equalsIgnoreCase("quiet")) {
					verbose = false;
				}else if (arg.equalsIgnoreCase("verbose")) {
					verbose = true;
				}else if (arg.equalsIgnoreCase("expand")) {
					expand = true;
				}else if (arg.equalsIgnoreCase("noexpand")) {
					expand = false;
				}else if (arg.equalsIgnoreCase("run")) {
					run = true;
				}else if (arg.equalsIgnoreCase("norun")) {
					run = false;
				}else if (arg.equalsIgnoreCase("java")) {
					run = false;
					if (i < args.length - 1) {
						params = args[++i];
					}
				}else {
					System.err.println("Invalid switch - " + arg);
					System.exit(1);
				}
			}else {
				if (propName == null) {
					propName = arg;
				}else {
					System.err.println("Too many parameters - " + arg);
					System.exit(1);
				}
			}
		}
		if (propName == null) {
			propName = "InstallJars.properties";
		}
		// do the install
		try {
			InstallJars ij = new InstallJars(expand, verbose, run, propName, params);
			ij.printUsage();
			String cp = ij.install();
			System.out.println("\nRecomended additions to your classpath - "
					+ cp);
		}catch (Exception e) {
			System.err.println("\n" + e.getClass().getName() + ": "
					+ e.getMessage());
			if (verbose) {
				e.printStackTrace(); // *** debug ***
			}
			System.exit(2);
		}
	}
}
