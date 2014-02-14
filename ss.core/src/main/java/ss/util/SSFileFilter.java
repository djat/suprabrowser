package ss.util;

import java.io.File;
import javax.swing.filechooser.*;

public class SSFileFilter extends FileFilter {

	// Accept all directories and all gif, jpg, tiff, or png files.

	public boolean accept(File file) {

		if (file.isDirectory()) {

			return true;
		}
		String ext = getExtension(file);

		if (ext != null) {

			if (ext.equals(this.extension)) {

				return true;

			} else {

				return false;
			}
		}
		return false;
	}

	// The description of this filter
	public String getDescription() {

		return this.extension;
	}

	// The description of this filter
	public void setExtension(String ext) {

		this.extension = ext;

		return;
	}

	public static String getExtension(File file) {

		String ext = null;

		String fileName = file.getName();

		int i = fileName.lastIndexOf('.');

		if (i > 0 && i < fileName.length() - 1) {

			ext = fileName.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static String getExtension(String fileName) {

		String ext = null;

		int i = fileName.lastIndexOf('.');

		if (i > 0 && i < fileName.length() - 1) {

			ext = fileName.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	String extension;
}
