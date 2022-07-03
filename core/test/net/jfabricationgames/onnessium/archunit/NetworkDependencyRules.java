package net.jfabricationgames.onnessium.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import net.jfabricationgames.onnessium.archunit.util.ArchUnitUtils;
import net.jfabricationgames.onnessium.archunit.util.ArchUnitUtils.DoNotIncludeTests;
import net.jfabricationgames.onnessium.archunit.util.PackageConstants;

@AnalyzeClasses(packages = PackageConstants.TOP_LEVEL_PACKAGE, importOptions = DoNotIncludeTests.class)
public class NetworkDependencyRules {
	
	/**
	 * Server code must not depend on libGdx packages.
	 */
	@ArchTest
	private final ArchRule serverPackagesMustNotDependOnOtherPackagesOutsideNetworkExceptDto = classes().that() //
			.resideInAPackage("..onnessium.network.server..") //
			.should().onlyDependOnClassesThat() //
			.resideInAnyPackage(ArchUnitUtils.addAllLibraryPackages("..onnessium.network..", "..onnessium..dto.."));
	
	/**
	 * Client code depends on libGdx code, but only for logging.
	 */
	@ArchTest
	private final ArchRule clientPackagesMustNotDependOnOtherPackagesOutsideNetwork = classes().that() //
			.resideInAPackage("..onnessium.network.client..") //
			.should().onlyDependOnClassesThat() //
			.resideInAnyPackage(ArchUnitUtils.addAllLibraryPackages("..onnessium.network..", "..onnessium..dto.."));    //
	
	@ArchTest
	private final ArchRule networkPackagesMustNotDependOnOtherPackagesOutsideNetwork = classes().that() //
			.resideInAPackage("..onnessium.network.shared..") //
			.should().onlyDependOnClassesThat() //
			.resideInAnyPackage(ArchUnitUtils.addAllLibraryPackages("..onnessium.network.."));
}
