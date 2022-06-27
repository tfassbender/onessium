package net.jfabricationgames.onnessium.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import net.jfabricationgames.onnessium.archunit.util.ArchUnitUtils;
import net.jfabricationgames.onnessium.archunit.util.PackageConstants;

@AnalyzeClasses(packages = PackageConstants.TOP_LEVEL_PACKAGE)
public class InputDependencyRules {
	
	@ArchTest
	private final ArchRule inputPackageHasNoDependenciesToOtherPackages = classes().that() //
			.resideInAPackage("..onnessium.input") //
			.should().onlyDependOnClassesThat() //
			.resideInAnyPackage(ArchUnitUtils.addDefaultLibraryPackages("..onnessium.input", "..onnessium.input.."));
}
