package com.marimon.maven.continous.test.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.surefire.SurefirePlugin;

/**
 * Maven plugin to continously run tests. Developed following the guide at:
 * http://maven.apache.org/guides/plugin/guide-java-plugin-development.html
 * 
 * @goal retest
 * @phase test
 * @requiresDependencyResolution test
 */
public class ContinousTestPlugin extends AbstractMojo {

    /**
     * target/ folder were data will be read/written.
     * 
     * @parameter expression="${basedir}" default-value="./target"
     */
    private final String targetFolder = "./target";

    /**
     * target/ folder were data will be read/written.
     * 
     * @parameter expression="${project.build.testOutputDirectory}"
     *            default-value="./target/test-classes"
     */
    private final String targetTestClassesFolder = targetFolder
        + "/test-classes";

    // * @parameter expression="${maven.compiler.testSource}"

    /**
     * Allows you to specify the name of the JUnit artifact. If not set,
     * <code>junit:junit</code> will be used.
     * 
     * @parameter expression="${junitArtifactName}" default-value="junit:junit"
     * @since 2.3.1
     */
    private final String junitArtifactName = "junit:junit";

    /**
     * Map of plugin artifacts.
     * 
     * @parameter expression="${plugin.artifactMap}"
     * @required
     * @readonly
     */
    private final Map<String, Artifact> pluginArtifactMap =
        new HashMap<String, Artifact>();

    public void execute() throws
    // Thrown to cause a BUILD ERROR
            MojoExecutionException,
            // Thrown to cause a BUILD FAILURE
            MojoFailureException {
        getLog().info("Continous Test Plugin started...");
        SurefirePlugin surefirePlugin = new SurefirePlugin();
        surefirePlugin.setWorkingDirectory(new File(targetFolder));
        surefirePlugin.setTestClassesDirectory(new File(
            targetTestClassesFolder));
        surefirePlugin.setJunitArtifactName(junitArtifactName);

        surefirePlugin.execute();
        getLog().info("Continous Test Plugin completed.");
    }
}
