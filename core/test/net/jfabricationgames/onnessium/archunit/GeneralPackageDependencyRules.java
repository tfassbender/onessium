package net.jfabricationgames.onnessium.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import net.jfabricationgames.onnessium.archunit.util.ArchUnitUtils.DoNotIncludeTests;
import net.jfabricationgames.onnessium.archunit.util.PackageConstants;

@AnalyzeClasses(packages = PackageConstants.TOP_LEVEL_PACKAGE, importOptions = DoNotIncludeTests.class)
public class GeneralPackageDependencyRules {
	
	@ArchTest
	private final ArchRule noDependenciesToTopLevelGameClass = noClasses().that().resideInAPackage("..onnessium.*") //
			.should().dependOnClassesThat().resideInAPackage("..onnessium");
	
	@ArchTest
	private final ArchRule noAccessesToUpperPackage = NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;
}
