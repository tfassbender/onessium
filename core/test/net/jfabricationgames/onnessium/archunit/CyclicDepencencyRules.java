package net.jfabricationgames.onnessium.archunit;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import net.jfabricationgames.onnessium.archunit.util.PackageConstants;

@AnalyzeClasses(packages = PackageConstants.TOP_LEVEL_PACKAGE)
public class CyclicDepencencyRules {
	
	@ArchTest
	private final ArchRule noCyclesBetweenPackages = slices() //
			.matching(PackageConstants.TOP_LEVEL_PACKAGE + ".(*)..") //
			.should().beFreeOfCycles();
}
