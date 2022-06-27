package net.jfabricationgames.onnessium.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import net.jfabricationgames.onnessium.archunit.util.PackageConstants;

@AnalyzeClasses(packages = PackageConstants.TOP_LEVEL_PACKAGE)
public class NetworkDependencyRules {
	
	/**
	 * Server code must not depend on libGdx packages.
	 */
	@ArchTest
	private final ArchRule serverPackagesMustNotDependOnOtherPackagesOutsideNetwork = classes().that() //
			.resideInAPackage("..onnessium.network.server..") //
			.should().onlyDependOnClassesThat() //
			.resideInAnyPackage("..onnessium.network..", PackageConstants.LIBRARY_JAVA, PackageConstants.LIBRARY_JFG_CDI, //
					PackageConstants.LIBRARY_KRYONET, PackageConstants.LIBRARY_SLF4J); //
	
	/**
	 * Client code depends on libGdx code, but only for logging.
	 */
	@ArchTest
	private final ArchRule clientPackagesMustNotDependOnOtherPackagesOutsideNetwork = classes().that() //
			.resideInAPackage("..onnessium.network.server..") //
			.should().onlyDependOnClassesThat() //
			.resideInAnyPackage("..onnessium.network..", PackageConstants.LIBRARY_JAVA, PackageConstants.LIBRARY_JFG_CDI, //
					PackageConstants.LIBRARY_KRYONET, PackageConstants.LIBRARY_SLF4J, //
					"com.badlogic.gdx"); //
	
	@ArchTest
	private final ArchRule networkPackagesMustNotDependOnOtherPackagesOutsideNetwork = classes().that() //
			.resideInAPackage("..onnessium.network.network..") //
			.should().onlyDependOnClassesThat() //
			.resideInAnyPackage("..onnessium.network..", PackageConstants.LIBRARY_JAVA, //
					PackageConstants.LIBRARY_KRYO, PackageConstants.LIBRARY_KRYONET); //
	
}
