package net.jfabricationgames.onnessium.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import net.jfabricationgames.onnessium.archunit.util.ArchUnitUtils;

@AnalyzeClasses(packages = ArchUnitUtils.TOP_LEVEL_PACKAGE)
public class PackageDependencyRules {
	
	@ArchTest
	private final ArchRule noDependenciesToTopLevelGameClass = noClasses().that().resideInAPackage("..onnessium..") //
			.should().dependOnClassesThat().resideInAPackage("..onnessium");
	
	@ArchTest
	private final ArchRule inputPackageHasNoDependenciesToOtherPackages = classes().that() //
			.resideInAPackage("..onnessium.input") //
			.should().onlyDependOnClassesThat() //
			.resideInAnyPackage(ArchUnitUtils.addDefaultLibraryPackages("..onnessium.input", "..onnessium.input.."));
}
