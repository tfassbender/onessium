package net.jfabricationgames.onnessium.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import net.jfabricationgames.onnessium.archunit.util.ArchUnitUtils;
import net.jfabricationgames.onnessium.archunit.util.ArchUnitUtils.DoNotIncludeTests;
import net.jfabricationgames.onnessium.archunit.util.PackageConstants;

@AnalyzeClasses(packages = PackageConstants.TOP_LEVEL_PACKAGE, importOptions = DoNotIncludeTests.class)
public class UtilDependencyRules {
	
	@ArchTest
	private final ArchRule utilPackageDependencies = classes().that() //
			.resideInAPackage("..onnessium.util..") //
			.should().onlyDependOnClassesThat() //
			.resideInAnyPackage(ArchUnitUtils.addDefaultLibraryPackages("..onnessium.util.."));
}
