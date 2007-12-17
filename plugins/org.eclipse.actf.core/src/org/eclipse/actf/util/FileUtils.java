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

package org.eclipse.actf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class FileUtils
{

	private static FilenameFilter dirFilter = new FilenameFilter() {

		public boolean accept (File path, String name) {
			return new File(path, name).isDirectory();
		}
	};

	// FormFilter
	private static FilenameFilter formFilter (final String fileDesc) {
		final String PUNCS = ".-%^$!~";
		StringBuffer descBuff = new StringBuffer(fileDesc);
		for (int c = 0; c < descBuff.length(); ++c) {
			if (descBuff.charAt(c) == '*') {
				descBuff.insert(c, '.');
				++c;
			}else if (descBuff.charAt(c) == '?') {
				descBuff.insert(c, '.');
				++c;
			}else if (PUNCS.indexOf(descBuff.charAt(c)) >= 0) {
				descBuff.insert(c, '\\');
				++c;
			}
		}
		final Pattern descPatt = Pattern.compile(descBuff.toString());
		FilenameFilter filter = new FilenameFilter() {

			public boolean accept (File parent, String name) {
				return descPatt.matcher(name).matches();
			}
		};
		return filter;
	} // formFilter

	/**
	 * Constructor
	 * 
	 * a no argument protected constructor
	 * 
	 */
	protected FileUtils () {
	}

	/**
	 * delete files or directories matching a specified pattern
	 * 
	 * @param baseDir -
	 *            root directory from which to start - must be existing
	 *            directory
	 * @param pattern -
	 *            the pattern of file names to erase
	 */
	public static void deleteFiles (File baseDir, String pattern) {
		FilenameFilter nameFilter = formFilter(pattern);
		File[] files = baseDir.listFiles(nameFilter);
		File[] directories = baseDir.listFiles(dirFilter);
		for (int cf = 0; files != null && cf < files.length; ++cf) {
			new File(baseDir, files[cf].getName()).delete();
		}
		for (int d = 0; directories != null && d < directories.length; ++d) {
			File dir = new File(baseDir, directories[d].getName());
			deleteFiles(dir, pattern);
			if (!dir.delete()) {
				Utils.println(
					Utils.WARNINGS, "Could not delete directory " + dir);
				// _launchEvent.addMessage(LaunchEvent.IO_STATUS_KEY, "Could not
				// delete directory " + dir);
			}
		}
	}

	/**
	 * Copy files matching a certain pattern from one directory to another
	 * @param baseDir -
	 *            the directory to copy from - must be existing
	 * @param newParent -
	 *            the directory to copy to - must be existing
	 * @param pattern -
	 *            the pattern of files to copy
	 */
	public static void copyFiles (File baseDir, File newParent, String pattern) {
		FilenameFilter nameFilter = formFilter(pattern);
		File[] files = baseDir.listFiles(nameFilter);
		File[] directories = baseDir.listFiles(dirFilter);
		for (int f = 0; files != null && f < files.length; ++f) {
			try {
				FileInputStream fis = new FileInputStream(files[f]);
				FileOutputStream fos = new FileOutputStream(new File(newParent, files[f].getName()));
				copyFile(fis, fos);
			}catch (IOException e) {
				Utils.println(Utils.WARNINGS, "Could not copy file "
						+ files[f].getName());
				// _launchEvent.addMessage(LaunchEvent.IO_STATUS_KEY, "Could not
				// copy file " + classFiles[cf].getName());
			}
		}
		for (int d = 0; directories != null && d < directories.length; ++d) {
			File newDir = new File(newParent, directories[d].getName());
			if (newDir.mkdir() || newDir.exists()) {
				copyFiles(directories[d], newDir, pattern);
			}else {
				Utils.println(
					Utils.WARNINGS, "Could not create directory "
							+ newDir.getName());
				// _launchEvent.addMessage(LaunchEvent.IO_STATUS_KEY, "Could not
				// create directory " + newDir.getName());
			}
		}
	} // copyFile

	/**
	 * Find files matching a specified pattern in a specified location
	 * 
	 * @param fileDesc -
	 *            the filename pattern you are searching for
	 * @param baseDir -
	 *            the directory to search - must be existing
	 * @return array of files
	 */
	public static File[] findFiles (String fileDesc, File baseDir) {
		List fileList = new LinkedList();
		findFiles(formFilter(fileDesc), baseDir, fileList);
		return (File[]) fileList.toArray(new File[fileList.size()]);
	}

	/**
	 * Find files matching a specified pattern in a specified location
	 * 
	 * @param filter -
	 *            filename filter of matching pattern
	 * @param baseDir -
	 *            the directory from which to start the search
	 * @param fileList -
	 *            a Linked List where the found files will be returned
	 */
	public static void findFiles (FilenameFilter filter, File baseDir,
									List fileList) {
		File[] directories = baseDir.listFiles(dirFilter);
		File[] files = baseDir.listFiles(filter);
		if (files != null && files.length > 0) {
			fileList.addAll(Arrays.asList(files));
		}
		for (int d = 0; directories != null && d < directories.length; ++d) {
			File dir = new File(baseDir, directories[d].getName());
			findFiles(filter, dir, fileList);
		}
	} // findFiles

	/**
	 * copy a file
	 * 
	 * @param source -
	 *            the source file input stream
	 * @param dest -
	 *            the destination file output stream
	 * @throws IOException
	 */
	public static void copyFile (FileInputStream source, FileOutputStream dest)
		throws IOException {
		int b = -1;
		while ((b = source.read()) != -1) {
			dest.write(b);
		}
		source.close();
		dest.close();
	} // copyFile
}
