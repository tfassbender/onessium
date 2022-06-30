package net.jfabricationgames.onnessium.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import net.jfabricationgames.onnessium.archunit.util.ArchUnitUtils.DoNotIncludeTests;
import net.jfabricationgames.onnessium.archunit.util.PackageConstants;

@AnalyzeClasses(packages = PackageConstants.TOP_LEVEL_PACKAGE, importOptions = DoNotIncludeTests.class)
public class UserDependencyRules {
	
	@ArchTest
	private final ArchRule screenPackagesDependencies = classes().that() //
			.resideInAPackage("..onnessium.user..") //
			.should().onlyDependOnClassesThat() //
			.resideInAnyPackage(PackageConstants.LIBRARY_JAVA, PackageConstants.LIBRARY_LIBGDX, PackageConstants.LIBRARY_JFG_CDI, //
					"..onnessium.network..", "..onnessium.user..");  //
}
