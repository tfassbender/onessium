package net.jfabricationgames.onnessium.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import net.jfabricationgames.onnessium.archunit.util.ArchUnitUtils;
import net.jfabricationgames.onnessium.archunit.util.ArchUnitUtils.DoNotIncludeTests;
import net.jfabricationgames.onnessium.archunit.util.PackageConstants;

@AnalyzeClasses(packages = PackageConstants.TOP_LEVEL_PACKAGE, importOptions = DoNotIncludeTests.class)
public class ScreenDependencyRules {
	
	@ArchTest
	private final ArchRule screenPackagesDependencies = classes().that() //
			.resideInAPackage("..onnessium.screen..") //
			.should().onlyDependOnClassesThat() //
			.resideInAnyPackage(ArchUnitUtils.addDefaultLibraryPackages("..onnessium.network..", "..onnessium.input..", //
					"..onnessium.screen..", "..onnessium.user..", "..onnessium.chat.."));
}
