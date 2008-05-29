/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.ui.util;

import java.io.File;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public abstract class AbstractUIPluginACTF extends AbstractUIPlugin {

	private File tmpDir = null;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		createTempDirectory();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		deleteFiles(tmpDir);
	}

	protected void createTempDirectory() {
		if (tmpDir == null) {
			String tmpS = getStateLocation().toOSString() + File.separator
					+ "tmp";
			if (isAvailableDirectory(tmpS)) {
				tmpDir = new File(tmpS);
			} else {
				System.err
						.println(toString() + " : can't open tmp Directory ("+tmpDir+")");
				tmpDir = new File(System.getProperty("java.io.tmpdir")
						+ File.separator + "ACTF");
			}
		}
	}

	public File createTempFile(String prefix, String suffix) throws Exception {
		if (tmpDir == null) {
			createTempDirectory();
		}
		return (File.createTempFile(prefix, suffix, tmpDir));
	}

	public File getTempDirectory() {
		if (tmpDir == null) {
			createTempDirectory();
		}
		return tmpDir;
	}
	
    private void deleteFiles(File rootDir) {
        if (rootDir != null) {
            File[] fileList = rootDir.listFiles();

            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    deleteFiles(fileList[i]);
                }
                fileList[i].delete();
            }
        }
    }

    private boolean isAvailableDirectory(String path) {
        File testDir = new File(path);
        if ((!testDir.isDirectory() || !testDir.canWrite()) && !testDir.mkdirs()) {
            System.err.println(path + " is not available.");
            return false;
        }
        return true;
    }

}
