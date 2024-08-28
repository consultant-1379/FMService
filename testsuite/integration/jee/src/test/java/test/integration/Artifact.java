/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package test.integration;

import java.io.File;
import java.util.*;

import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

/**
 * Maven artifact constants
 * 
 * @author emaomic
 * 
 */
public class Artifact {
	/**
     * 
     */
	public static final String ORG_SLF4J___SLF4J_API_JAR = "org.slf4j:slf4j-api:jar";

	public static final String JAVAX___JMS_JAR = "javax.jms:jms:jar";

	public static final String ORG_SLF4J___JAVAX_API_JAR = "org.jboss.msc:jboss-msc:jar";

	public static final String COM_ERICSSON_OSS_ITPF_SDK___SERVICES_CORE_JAR = "com.ericsson.oss.itpf.sdk:sdk-services-core:jar";

	public static final String COM_ERICSSON_OSS_ITPF_SDK___CONFIG_API_JAR = "com.ericsson.oss.itpf.sdk:sdk-config-api:jar";

	public static final String COM_ERICSSON_OSS_ITPF_SDK___CONFIG_CORE_JAR = "com.ericsson.oss.itpf.sdk:sdk-config-core:jar";

	public static final String COM_ERICSSON_OSS_ITPF_SDK___CONFIG_IMPL_DEV_JAR = "com.ericsson.oss.itpf.sdk:sdk-config-impl-dev:jar";

	public static final String COM_ERICSSON_OSS_ITPF_SDK___EVENTBUS_API_JAR = "com.ericsson.oss.itpf.sdk:sdk-eventbus-api:jar";

	public static final String COM_ERICSSON_OSS_ITPF_SDK___EVENTBUS_CORE_JAR = "com.ericsson.oss.itpf.sdk:sdk-eventbus-core:jar";

	public static final String COM_ERICSSON_OSS_ITPF_SDK___EVENTBUS_JMS_JAR = "com.ericsson.oss.itpf.sdk:sdk-eventbus-jms:jar";

	public static final String COM_ERICSSON_OSS_FMSERVICE_JEE = "com.ericsson.nms.services:FMService-jee:jar";
	public static final String COM_ERICSSON_OSS_FMSERVICE_API = "com.ericsson.nms.services:FMService-api:jar";

	public static final String COM_ERICSSON_OSS_ITPF_SDK___MODELLEDEVENTBUS_API_JAR = "com.ericsson.oss.itpf.sdk:sdk-modeled-eventbus-api:jar";

	public static final String COM_ERICSSON_OSS_ITPF_SDK___MODELLEDEVENTBUS_JAR = "com.ericsson.oss.itpf.sdk:sdk-modeled-eventbus:jar";
	public static final String COM_ERICSSON_OSS_MEDIATION_CORE___API_JAR = "com.ericsson.nms.mediation:core-mediation-models-api:jar";

	/**
	 * Resolve artifact with given coordinates without any dependencies, this
	 * method should be used to resolve just the artifact with given name, and
	 * it can be used for adding artifacts as modules into EAR
	 * 
	 * If artifact can not be resolved, or the artifact was resolved into more
	 * then one file then the IllegalStateException will be thrown
	 * 
	 * 
	 * @param artifactCoordinates
	 *            in usual maven format
	 * 
	 *            <pre>
	 * {@code<groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}
	 * </pre>
	 * @return File representing resolved artifact
	 */

	private Artifact() {

	}

	public static File resolveArtifactWithoutDependencies(
			final String artifactCoordinates) {
		final File[] artifacts = getMavenResolver()
				.artifact(artifactCoordinates).exclusion("*").resolveAsFiles();
		if (artifacts == null) {
			throw new IllegalStateException("Artifact with coordinates "
					+ artifactCoordinates + " was not resolved");
		}

		if (artifacts.length != 1) {
			throw new IllegalStateException(
					"Resolved more then one artifact with coordinates "
							+ artifactCoordinates);
		}
		return artifacts[0];
	}

	/**
	 * Resolve dependencies for artifact with given coordinates, if artifact can
	 * not be resolved IllegalState exception will be thrown
	 * 
	 * @param artifactCoordinates
	 *            in usual maven format
	 * 
	 *            <pre>
	 * {@code<groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}
	 * </pre>
	 * 
	 * @return resolved dependencies
	 */
	public static File[] resolveArtifactDependencies(
			final String artifactCoordinates) {

		File[] artifacts = getMavenResolver().artifact(artifactCoordinates)
				.scope("compile").exclusion(artifactCoordinates)
				.resolveAsFiles();

		if (artifacts == null) {
			throw new IllegalStateException(
					"No dependencies resolved for artifact with coordinates "
							+ artifactCoordinates);
		}
		if (artifacts.length > 0) {
			// find artifact that has given coordinates
			final File artifact = resolveArtifactWithoutDependencies(artifactCoordinates);
			final List<File> filteredDeps = new ArrayList<File>(
					Arrays.asList(artifacts));
			// remove it from the list
			filteredDeps.remove(artifact);
			artifacts = new File[0];
			// return the resolved list with only dependencies
			return filteredDeps.toArray(artifacts);
		} else {
			return artifacts;
		}

	}

	/**
	 * Maven resolver that will try to resolve dependencies using pom.xml of the
	 * project where this Artifact class is located.
	 * 
	 * @return MavenDependencyResolver
	 */
	public static MavenDependencyResolver getMavenResolver() {
		return DependencyResolvers.use(MavenDependencyResolver.class)
				.loadMetadataFromPom("pom.xml");

	}
}
