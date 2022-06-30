package net.jfabricationgames.onnessium.archunit.util;

import java.util.regex.Pattern;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;

public class ArchUnitUtils {
	
	private ArchUnitUtils() {}
	
	public static String[] addDefaultLibraryPackages(String... packages) {
		String[] allPackages = new String[packages.length + 3];
		System.arraycopy(packages, 0, allPackages, 0, packages.length);
		
		allPackages[packages.length] = PackageConstants.LIBRARY_JAVA;
		allPackages[packages.length + 1] = PackageConstants.LIBRARY_LIBGDX;
		allPackages[packages.length + 2] = PackageConstants.LIBRARY_JFG_CDI;
		
		return allPackages;
	}
	
	public static class DoNotIncludeTests implements ImportOption {
		
		private static final String TEST_PATH = ".*/bin/test/.*";
		
		@Override
		public boolean includes(Location location) {
			return !Pattern.matches(TEST_PATH, location.asURI().getPath());
		}
	}
}
