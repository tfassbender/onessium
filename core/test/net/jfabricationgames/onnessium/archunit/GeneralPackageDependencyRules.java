package net.jfabricationgames.onnessium.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import net.jfabricationgames.onnessium.archunit.util.ArchUnitUtils.DoNotIncludeTests;
import net.jfabricationgames.onnessium.archunit.util.PackageConstants;

@AnalyzeClasses(packages = PackageConstants.TOP_LEVEL_PACKAGE, importOptions = DoNotIncludeTests.class)
public class GeneralPackageDependencyRules {
	
	@ArchTest
	private final ArchRule noDependenciesToTopLevelPackage = noClasses().that().resideInAPackage("..onnessium.*") //
			.should().dependOnClassesThat().resideInAPackage("..onnessium");
}
