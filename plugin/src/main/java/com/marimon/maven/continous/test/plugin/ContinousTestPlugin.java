package com.marimon.maven.continous.test.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.surefire.SurefireExecutionParameters;
import org.apache.maven.plugin.surefire.SurefirePlugin;
import org.apache.maven.project.MavenProject;
import org.apache.maven.toolchain.ToolchainManager;

/**
 * Maven plugin to continously run tests. Developed following the guide at:
 * http://maven.apache.org/guides/plugin/guide-java-plugin-development.html
 * 
 * @goal retest
 * @phase test
 * @requiresDependencyResolution test
 */
public class ContinousTestPlugin extends AbstractMojo implements
        SurefireExecutionParameters {

    private final SurefirePlugin surefirePlugin;

    public ContinousTestPlugin() {
        surefirePlugin = new SurefirePlugin();
    }

    public void execute() throws
    // Thrown to cause a BUILD ERROR
            MojoExecutionException,
            // Thrown to cause a BUILD FAILURE
            MojoFailureException {
        getLog().info("Continous Test Plugin started...");
        int runs = 0;
        while (runs < 10) {
            getLog().info("Checking files...");
            surefirePlugin.execute();
            getLog().info("Files checked.");
            runs++;
        }
        getLog().info("Continous Test Plugin completed.");
    }

    /**
     * @see org.apache.maven.plugin.ContextEnabled#getPluginContext()
     */
    @Override
    public Map getPluginContext() {
        return surefirePlugin.getPluginContext();
    }

    /**
     * @see org.apache.maven.plugin.ContextEnabled#setPluginContext(java.util.Map)
     */
    @Override
    public void setPluginContext(final Map pluginContext) {
        surefirePlugin.setPluginContext(pluginContext);
    }

    /**
     * Information about this plugin, mainly used to lookup this plugin's
     * configuration from the currently executing project.
     * 
     * @parameter default-value="${plugin}"
     * @readonly
     * @since 2.12
     */
    private PluginDescriptor pluginDescriptor;

    /**
     * Set this to "true" to skip running tests, but still compile them. Its use
     * is NOT RECOMMENDED, but quite convenient on occasion.
     * 
     * @parameter default-value="false" expression="${skipTests}"
     * @since 2.4
     */
    private boolean skipTests;

    /**
     * This old parameter is just like <code>skipTests</code>, but bound to the
     * old property "maven.test.skip.exec".
     * 
     * @parameter expression="${maven.test.skip.exec}"
     * @since 2.3
     * @deprecated Use skipTests instead.
     */
    @Deprecated
    private boolean skipExec;

    /**
     * Set this to "true" to bypass unit tests entirely. Its use is NOT
     * RECOMMENDED, especially if you enable it using the "maven.test.skip"
     * property, because maven.test.skip disables both running the tests and
     * compiling the tests. Consider using the <code>skipTests</code> parameter
     * instead.
     * 
     * @parameter default-value="false" expression="${maven.test.skip}"
     */
    private boolean skip;

    /**
     * Set this to "true" to ignore a failure during testing. Its use is NOT
     * RECOMMENDED, but quite convenient on occasion.
     * 
     * @parameter default-value="false"
     *            expression="${maven.test.failure.ignore}"
     */
    private boolean testFailureIgnore;

    /**
     * The base directory of the project being tested. This can be obtained in
     * your unit test via System.getProperty("basedir").
     * 
     * @parameter default-value="${basedir}"
     */
    private File basedir;

    /**
     * The directory containing generated test classes of the project being
     * tested. This will be included at the beginning of the test classpath. *
     * 
     * @parameter default-value="${project.build.testOutputDirectory}"
     */
    private File testClassesDirectory;

    /**
     * The directory containing generated classes of the project being tested.
     * This will be included after the test classes in the test classpath.
     * 
     * @parameter default-value="${project.build.outputDirectory}"
     */
    private File classesDirectory;

    /**
     * The Maven Project Object.
     * 
     * @parameter default-value="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * List of dependencies to exclude from the test classpath. Each dependency
     * string must follow the format <i>groupId:artifactId</i>. For example:
     * <i>org.acme:project-a</i>
     * 
     * @parameter
     * @since 2.6
     */
    private List<String> classpathDependencyExcludes;

    /**
     * A dependency scope to exclude from the test classpath. The scope can be
     * one of the following scopes:
     * <p/>
     * <ul>
     * <li><i>compile</i> - system, provided, compile
     * <li><i>runtime</i> - compile, runtime
     * <li><i>test</i> - system, provided, compile, runtime, test
     * </ul>
     * 
     * @parameter default-value=""
     * @since 2.6
     */
    private String classpathDependencyScopeExclude;

    /**
     * Additional elements to be appended to the classpath.
     * 
     * @parameter
     * @since 2.4
     */
    private List<String> additionalClasspathElements;

    /**
     * Base directory where all reports are written to.
     * 
     * @parameter default-value="${project.build.directory}/surefire-reports"
     */
    private File reportsDirectory;

    /**
     * The test source directory containing test class sources.
     * 
     * @parameter default-value="${project.build.testSourceDirectory}"
     * @required
     * @since 2.2
     */
    private File testSourceDirectory;

    /**
     * Specify this parameter to run individual tests by file name, overriding
     * the <code>includes/excludes</code> parameters. Each pattern you specify
     * here will be used to create an include pattern formatted like
     * <code>**&#47;${test}.java</code>, so you can just type "-Dtest=MyTest" to
     * run a single test called "foo/MyTest.java".<br/>
     * This parameter overrides the <code>includes/excludes</code> parameters,
     * and the TestNG <code>suiteXmlFiles</code> parameter.
     * <p/>
     * Since 2.7.3, you can execute a limited number of methods in the test by
     * adding #myMethod or #my*ethod. For example, "-Dtest=MyTest#myMethod".
     * This is supported for junit 4.x and testNg.
     * 
     * @parameter expression="${test}"
     */
    private String test;

    /**
     * A list of &lt;include> elements specifying the tests (by pattern) that
     * should be included in testing. When not specified and when the
     * <code>test</code> parameter is not specified, the default includes will
     * be <code><br/>
     * &lt;includes><br/>
     * &nbsp;&lt;include>**&#47;Test*.java&lt;/include><br/>
     * &nbsp;&lt;include>**&#47;*Test.java&lt;/include><br/>
     * &nbsp;&lt;include>**&#47;*TestCase.java&lt;/include><br/>
     * &lt;/includes><br/>
     * </code> This parameter is ignored if the TestNG
     * <code>suiteXmlFiles</code> parameter is specified.
     * 
     * @parameter
     */
    private List<String> includes;

    /**
     * A list of &lt;exclude> elements specifying the tests (by pattern) that
     * should be excluded in testing. When not specified and when the
     * <code>test</code> parameter is not specified, the default excludes will
     * be <code><br/>
     * &lt;excludes><br/>
     * &nbsp;&lt;exclude>**&#47;*$*&lt;/exclude><br/>
     * &lt;/excludes><br/>
     * </code> (which excludes all inner classes).<br>
     * This parameter is ignored if the TestNG <code>suiteXmlFiles</code>
     * parameter is specified.
     * 
     * @parameter
     */
    private List<String> excludes;

    /**
     * ArtifactRepository of the localRepository. To obtain the directory of
     * localRepository in unit tests use System.getProperty("localRepository").
     * 
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * List of System properties to pass to the JUnit tests.
     * 
     * @parameter
     * @deprecated Use systemPropertyVariables instead.
     */
    @Deprecated
    private Properties systemProperties;

    /**
     * List of System properties to pass to the JUnit tests.
     * 
     * @parameter
     * @since 2.5
     */
    private Map<String, String> systemPropertyVariables;

    /**
     * List of System properties, loaded from a file, to pass to the JUnit
     * tests.
     * 
     * @parameter
     * @since 2.8.2
     */
    private File systemPropertiesFile;

    /**
     * List of properties for configuring all TestNG related configurations.
     * This is the new preferred method of configuring TestNG.
     * 
     * @parameter
     * @since 2.4
     */
    private Properties properties;

    /**
     * Map of plugin artifacts.
     * 
     * @parameter expression="${plugin.artifactMap}"
     * @required
     * @readonly
     */
    private Map<String, Artifact> pluginArtifactMap;

    /**
     * Map of project artifacts.
     * 
     * @parameter expression="${project.artifactMap}"
     * @required
     * @readonly
     */
    private Map<String, Artifact> projectArtifactMap;

    /**
     * Option to print summary of test suites or just print the test cases that
     * have errors.
     * 
     * @parameter expression="${surefire.printSummary}" default-value="true"
     */
    private boolean printSummary;

    /**
     * Selects the formatting for the test report to be generated. Can be set as
     * "brief" or "plain".
     * 
     * @parameter expression="${surefire.reportFormat}" default-value="brief"
     */
    private String reportFormat;

    /**
     * Add custom text into report filename:
     * TEST-testClassName-reportNameSuffix.xml,
     * testClassName-reportNameSuffix.txt and
     * testClassName-reportNameSuffix-output.txt. File
     * TEST-testClassName-reportNameSuffix.xml has changed attributes
     * 'testsuite'--'name' and 'testcase'--'classname' - reportNameSuffix is
     * added to the attribute value.
     * 
     * @parameter expression="${surefire.reportNameSuffix}" default-value=""
     */
    private String reportNameSuffix;

    /**
     * Option to generate a file test report or just output the test report to
     * the console.
     * 
     * @parameter expression="${surefire.useFile}" default-value="true"
     */
    private boolean useFile;

    /**
     * Set this to "true" to redirect the unit test standard output to a file
     * (found in reportsDirectory/testName-output.txt).
     * 
     * @parameter expression="${maven.test.redirectTestOutputToFile}"
     *            default-value="false"
     * @since 2.3
     */
    private boolean redirectTestOutputToFile;

    /**
     * Set this to "true" to cause a failure if there are no tests to run.
     * Defaults to "false".
     * 
     * @parameter expression="${failIfNoTests}"
     * @since 2.4
     */
    private Boolean failIfNoTests;

    /**
     * Set this to "true" to cause a failure if the none of the tests specified
     * in -Dtest=... are run. Defaults to "true".
     * 
     * @parameter expression="${surefire.failIfNoSpecifiedTests}"
     * @since 2.12
     */
    private Boolean failIfNoSpecifiedTests;

    /**
     * Option to specify the forking mode. Can be "never", "once", "always" or
     * "perthread". "none" and "pertest" are also accepted for backwards
     * compatibility. "always" forks for each test-class. "perthread" will
     * create "threadCount" parallel forks.
     * 
     * @parameter expression="${forkMode}" default-value="once"
     * @since 2.1
     */
    private String forkMode;

    /**
     * Option to specify the jvm (or path to the java executable) to use with
     * the forking options. For the default, the jvm will be a new instance of
     * the same VM as the one used to run Maven. JVM settings are not inherited
     * from MAVEN_OPTS.
     * 
     * @parameter expression="${jvm}"
     * @since 2.1
     */
    private String jvm;

    /**
     * Arbitrary JVM options to set on the command line.
     * 
     * @parameter expression="${argLine}"
     * @since 2.1
     */
    private String argLine;

    /**
     * Attach a debugger to the forked JVM. If set to "true", the process will
     * suspend and wait for a debugger to attach on port 5005. If set to some
     * other string, that string will be appended to the argLine, allowing you
     * to configure arbitrary debuggability options (without overwriting the
     * other options specified through the <code>argLine</code> parameter).
     * 
     * @parameter expression="${maven.surefire.debug}"
     * @since 2.4
     */
    private String debugForkedProcess;

    /**
     * Kill the forked test process after a certain number of seconds. If set to
     * 0, wait forever for the process, never timing out.
     * 
     * @parameter expression="${surefire.timeout}"
     * @since 2.4
     */
    private int forkedProcessTimeoutInSeconds;

    /**
     * Additional environment variables to set on the command line.
     * 
     * @parameter
     * @since 2.1.3
     */
    private final Map<String, String> environmentVariables =
        new HashMap<String, String>();

    /**
     * Command line working directory.
     * 
     * @parameter expression="${basedir}"
     * @since 2.1.3
     */
    private File workingDirectory;

    /**
     * When false it makes tests run using the standard classloader delegation
     * instead of the default Maven isolated classloader. Only used when forking
     * (forkMode is not "none").<br/>
     * Setting it to false helps with some problems caused by conflicts between
     * xml parsers in the classpath and the Java 5 provider parser.
     * 
     * @parameter expression="${childDelegation}" default-value="false"
     * @since 2.1
     */
    private boolean childDelegation;

    /**
     * (TestNG/JUnit47 provider with JUnit4.8+ only) Groups for this test. Only
     * classes/methods/etc decorated with one of the groups specified here will
     * be included in test run, if specified. <br/>
     * For JUnit, this parameter forces the use of the 4.7 provider<br/>
     * This parameter is ignored if the <code>suiteXmlFiles</code> parameter is
     * specified. .
     * 
     * @parameter expression="${groups}"
     * @since 2.2
     */
    private String groups;

    /**
     * (TestNG/JUnit47 provider with JUnit4.8+ only) Excluded groups. Any
     * methods/classes/etc with one of the groups specified in this list will
     * specifically not be run.<br/>
     * For JUnit, this parameter forces the use of the 4.7 provider<br/>
     * This parameter is ignored if the <code>suiteXmlFiles</code> parameter is
     * specified.
     * 
     * @parameter expression="${excludedGroups}"
     * @since 2.2
     */
    private String excludedGroups;

    /**
     * (TestNG) List of &lt;suiteXmlFile> elements specifying TestNG suite xml
     * file locations. Note that <code>suiteXmlFiles</code> is incompatible with
     * several other parameters of this plugin, like
     * <code>includes/excludes</code>.<br/>
     * This parameter is ignored if the <code>test</code> parameter is specified
     * (allowing you to run a single test instead of an entire suite).
     * 
     * @parameter
     * @since 2.2
     */
    private File[] suiteXmlFiles;

    /**
     * Allows you to specify the name of the JUnit artifact. If not set,
     * <code>junit:junit</code> will be used.
     * 
     * @parameter expression="${junitArtifactName}" default-value="junit:junit"
     * @since 2.3.1
     */
    private String junitArtifactName;

    /**
     * Allows you to specify the name of the TestNG artifact. If not set,
     * <code>org.testng:testng</code> will be used.
     * 
     * @parameter expression="${testNGArtifactName}"
     *            default-value="org.testng:testng"
     * @since 2.3.1
     */
    private String testNGArtifactName;

    /**
     * (forkMode=perthread or TestNG/JUnit 4.7 provider) The attribute
     * thread-count allows you to specify how many threads should be allocated
     * for this execution. Only makes sense to use in conjunction with the
     * <code>parallel</code> parameter. (forkMode=perthread does not
     * support/require the <code>parallel</code> parameter)
     * 
     * @parameter expression="${threadCount}"
     * @since 2.2
     */
    private int threadCount;

    /**
     * (JUnit 4.7 provider) Indicates that threadCount is per cpu core.
     * 
     * @parameter expression="${perCoreThreadCount}" default-value="true"
     * @since 2.5
     */
    private boolean perCoreThreadCount;

    /**
     * (JUnit 4.7 provider) Indicates that the thread pool will be unlimited.
     * The <code>parallel</code> parameter and the actual number of
     * classes/methods will decide. Setting this to "true" effectively disables
     * <code>perCoreThreadCount</code> and <code>threadCount</code>. Defaults to
     * "false".
     * 
     * @parameter expression="${useUnlimitedThreads}" default-value="false"
     * @since 2.5
     */
    private boolean useUnlimitedThreads;

    /**
     * (TestNG only) When you use the <code>parallel</code> attribute, TestNG
     * will try to run all your test methods in separate threads, except for
     * methods that depend on each other, which will be run in the same thread
     * in order to respect their order of execution.
     * <p/>
     * (JUnit 4.7 provider) Supports values "classes"/"methods"/"both" to run in
     * separate threads, as controlled by <code>threadCount</code>.
     * 
     * @parameter expression="${parallel}"
     * @since 2.2
     */
    private String parallel;

    /**
     * Whether to trim the stack trace in the reports to just the lines within
     * the test, or show the full trace.
     * 
     * @parameter expression="${trimStackTrace}" default-value="true"
     * @since 2.2
     */
    private boolean trimStackTrace;

    /**
     * Resolves the artifacts needed.
     * 
     * @component
     */
    private ArtifactResolver artifactResolver;

    /**
     * Creates the artifact.
     * 
     * @component
     */
    private ArtifactFactory artifactFactory;

    /**
     * The remote plugin repositories declared in the POM.
     * 
     * @parameter expression="${project.pluginArtifactRepositories}"
     * @since 2.2
     */
    private List<ArtifactRepository> remoteRepositories;

    /**
     * For retrieval of artifact's metadata.
     * 
     * @component
     */
    private ArtifactMetadataSource metadataSource;

    private Properties originalSystemProperties;

    /**
     * systemPropertyVariables + systemProperties
     */
    private final Properties internalSystemProperties = new Properties();

    /**
     * Flag to disable the generation of report files in xml format.
     * 
     * @parameter expression="${disableXmlReport}" default-value="false"
     * @since 2.2
     */
    private boolean disableXmlReport;

    /**
     * Option to pass dependencies to the system's classloader instead of using
     * an isolated class loader when forking. Prevents problems with JDKs which
     * implement the service provider lookup mechanism by using the system's
     * classloader.
     * 
     * @parameter expression="${surefire.useSystemClassLoader}"
     *            default-value="true"
     * @since 2.3
     */
    private boolean useSystemClassLoader;

    /**
     * By default, Surefire forks your tests using a manifest-only JAR; set this
     * parameter to "false" to force it to launch your tests with a plain old
     * Java classpath. (See
     * http://maven.apache.org/plugins/maven-surefire-plugin
     * /examples/class-loading.html for a more detailed explanation of
     * manifest-only JARs and their benefits.)
     * <p/>
     * Beware, setting this to "false" may cause your tests to fail on Windows
     * if your classpath is too long.
     * 
     * @parameter expression="${surefire.useManifestOnlyJar}"
     *            default-value="true"
     * @since 2.4.3
     */
    private boolean useManifestOnlyJar;

    /**
     * By default, Surefire enables JVM assertions for the execution of your
     * test cases. To disable the assertions, set this flag to "false".
     * 
     * @parameter expression="${enableAssertions}" default-value="true"
     * @since 2.3.1
     */
    private boolean enableAssertions;

    /**
     * The current build session instance.
     * 
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
     * (TestNG only) Define the factory class used to create all test instances.
     * 
     * @parameter expression="${objectFactory}"
     * @since 2.5
     */
    private String objectFactory;

    /**
     * @parameter default-value="${session.parallel}"
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private Boolean parallelMavenExecution;

    /**
     * Defines the order the tests will be run in. Supported values are
     * "alphabetical", "reversealphabetical", "random", "hourly" (alphabetical
     * on even hours, reverse alphabetical on odd hours), "failedfirst",
     * "balanced" and "filesystem".
     * <p/>
     * <p/>
     * Odd/Even for hourly is determined at the time the of scanning the
     * classpath, meaning it could change during a multi-module build.
     * <p/>
     * Failed first will run tests that failed on previous run first, as well as
     * new tests for this run.
     * <p/>
     * Balanced is only relevant with parallel=classes, and will try to optimize
     * the run-order of the tests to make all tests complete at the same time,
     * reducing the overall execution time.
     * <p/>
     * Note that the statistics are stored in a file named .surefire-XXXXXXXXX
     * beside pom.xml, and should not be checked into version control. The
     * "XXXXX" is the SHA1 checksum of the entire surefire configuration, so
     * different configurations will have different statistics files, meaning if
     * you change any config settings you will re-run once before new statistics
     * data can be established.
     * 
     * @parameter default-value="filesystem"
     * @since 2.7
     */
    private String runOrder;

    @Override
    public boolean isSkipTests() {
        return surefirePlugin.isSkipTests();
    }

    @Override
    public void setSkipTests(final boolean skipTests) {
        surefirePlugin.setSkipTests(skipTests);
    }

    @Override
    public boolean isSkipExec() {
        return surefirePlugin.isSkipExec();
    }

    @Override
    public void setSkipExec(final boolean skipExec) {
        surefirePlugin.setSkipExec(skipExec);
    }

    @Override
    public boolean isSkip() {
        return surefirePlugin.isSkip();
    }

    @Override
    public void setSkip(final boolean skip) {
        surefirePlugin.setSkip(skip);
    }

    @Override
    public File getBasedir() {
        return surefirePlugin.getBasedir();
    }

    @Override
    public void setBasedir(final File basedir) {
        surefirePlugin.setBasedir(basedir);
    }

    @Override
    public File getTestClassesDirectory() {
        return surefirePlugin.getTestClassesDirectory();
    }

    @Override
    public void setTestClassesDirectory(final File testClassesDirectory) {
        surefirePlugin.setTestClassesDirectory(testClassesDirectory);
    }

    @Override
    public File getClassesDirectory() {
        return surefirePlugin.getClassesDirectory();
    }

    @Override
    public void setClassesDirectory(final File classesDirectory) {
        surefirePlugin.setClassesDirectory(classesDirectory);
    }

    @Override
    public MavenProject getProject() {
        return surefirePlugin.getProject();
    }

    @Override
    public void setProject(final MavenProject project) {
        surefirePlugin.setProject(project);
    }

    @Override
    public List<String> getClasspathDependencyExcludes() {
        return surefirePlugin.getClasspathDependencyExcludes();
    }

    @Override
    public void setClasspathDependencyExcludes(
            final List<String> classpathDependencyExcludes) {
        surefirePlugin
            .setClasspathDependencyExcludes(classpathDependencyExcludes);
    }

    @Override
    public String getClasspathDependencyScopeExclude() {
        return surefirePlugin.getClasspathDependencyScopeExclude();
    }

    @Override
    public void setClasspathDependencyScopeExclude(
            final String classpathDependencyScopeExclude) {
        surefirePlugin
            .setClasspathDependencyScopeExclude(classpathDependencyScopeExclude);
    }

    @Override
    public List<String> getAdditionalClasspathElements() {
        return surefirePlugin.getAdditionalClasspathElements();
    }

    @Override
    public void setAdditionalClasspathElements(
            final List<String> additionalClasspathElements) {
        surefirePlugin
            .setAdditionalClasspathElements(additionalClasspathElements);
    }

    @Override
    public File getReportsDirectory() {
        return surefirePlugin.getReportsDirectory();
    }

    @Override
    public void setReportsDirectory(final File reportsDirectory) {
        surefirePlugin.setReportsDirectory(reportsDirectory);
    }

    @Override
    public File getTestSourceDirectory() {
        return surefirePlugin.getTestSourceDirectory();
    }

    @Override
    public void setTestSourceDirectory(final File testSourceDirectory) {
        surefirePlugin.setTestSourceDirectory(testSourceDirectory);
    }

    @Override
    public String getTest() {
        return surefirePlugin.getTest();
    }

    @Override
    public String getTestMethod() {
        return surefirePlugin.getTestMethod();
    }

    @Override
    public void setTest(final String test) {
        surefirePlugin.setTest(test);
    }

    @Override
    public List<String> getIncludes() {
        return surefirePlugin.getIncludes();
    }

    @Override
    public void setIncludes(final List<String> includes) {
        surefirePlugin.setIncludes(includes);
    }

    @Override
    public List<String> getExcludes() {
        return surefirePlugin.getExcludes();
    }

    @Override
    public void setExcludes(final List<String> excludes) {
        surefirePlugin.setExcludes(excludes);
    }

    @Override
    public ArtifactRepository getLocalRepository() {
        return surefirePlugin.getLocalRepository();
    }

    @Override
    public void setLocalRepository(final ArtifactRepository localRepository) {
        surefirePlugin.setLocalRepository(localRepository);
    }

    @Override
    public Properties getSystemProperties() {
        return surefirePlugin.getSystemProperties();
    }

    @Override
    public void setSystemProperties(final Properties systemProperties) {
        surefirePlugin.setSystemProperties(systemProperties);

    }

    @Override
    public Map<String, String> getSystemPropertyVariables() {
        return surefirePlugin.getSystemPropertyVariables();
    }

    @Override
    public void setSystemPropertyVariables(
            final Map<String, String> systemPropertyVariables) {
        surefirePlugin.setSystemPropertyVariables(systemPropertyVariables);
    }

    @Override
    public File getSystemPropertiesFile() {
        return surefirePlugin.getSystemPropertiesFile();
    }

    @Override
    public void setSystemPropertiesFile(final File systemPropertiesFile) {
        surefirePlugin.setSystemPropertiesFile(systemPropertiesFile);
    }

    @Override
    public Properties getProperties() {
        return surefirePlugin.getProperties();
    }

    @Override
    public void setProperties(final Properties properties) {
        surefirePlugin.setProperties(properties);
    }

    @Override
    public PluginDescriptor getPluginDescriptor() {
        return surefirePlugin.getPluginDescriptor();
    }

    @Override
    public Map<String, Artifact> getPluginArtifactMap() {
        return surefirePlugin.getPluginArtifactMap();
    }

    @Override
    public void setPluginArtifactMap(
            final Map<String, Artifact> pluginArtifactMap) {
        surefirePlugin.setPluginArtifactMap(pluginArtifactMap);
    }

    @Override
    public Map<String, Artifact> getProjectArtifactMap() {
        return surefirePlugin.getProjectArtifactMap();
    }

    @Override
    public void setProjectArtifactMap(
            final Map<String, Artifact> projectArtifactMap) {
        surefirePlugin.setProjectArtifactMap(projectArtifactMap);
    }

    @Override
    public boolean isPrintSummary() {
        return surefirePlugin.isPrintSummary();
    }

    @Override
    public void setPrintSummary(final boolean printSummary) {
        surefirePlugin.setPrintSummary(printSummary);
    }

    @Override
    public String getReportFormat() {
        return surefirePlugin.getReportFormat();
    }

    @Override
    public void setReportFormat(final String reportFormat) {
        surefirePlugin.setReportFormat(reportFormat);
    }

    @Override
    public String getReportNameSuffix() {
        return surefirePlugin.getReportNameSuffix();
    }

    @Override
    public void setReportNameSuffix(final String reportNameSuffix) {
        surefirePlugin.setReportNameSuffix(reportNameSuffix);
    }

    @Override
    public boolean isUseFile() {
        return surefirePlugin.isUseFile();
    }

    @Override
    public void setUseFile(final boolean useFile) {
        surefirePlugin.setUseFile(useFile);
    }

    @Override
    public boolean isRedirectTestOutputToFile() {
        return surefirePlugin.isRedirectTestOutputToFile();
    }

    @Override
    public void setRedirectTestOutputToFile(
            final boolean redirectTestOutputToFile) {
        surefirePlugin
            .setRedirectTestOutputToFile(redirectTestOutputToFile);
    }

    @Override
    public String getForkMode() {
        return surefirePlugin.getForkMode();
    }

    @Override
    public void setForkMode(final String forkMode) {
        surefirePlugin.setForkMode(forkMode);
    }

    @Override
    public String getJvm() {
        return surefirePlugin.getJvm();
    }

    @Override
    public void setJvm(final String jvm) {
        surefirePlugin.setJvm(jvm);
    }

    @Override
    public String getArgLine() {
        return surefirePlugin.getArgLine();
    }

    @Override
    public void setArgLine(final String argLine) {
        surefirePlugin.setArgLine(argLine);
    }

    @Override
    public String getDebugForkedProcess() {
        return surefirePlugin.getDebugForkedProcess();
    }

    @Override
    public void setDebugForkedProcess(final String debugForkedProcess) {
        surefirePlugin.setDebugForkedProcess(debugForkedProcess);
    }

    @Override
    public int getForkedProcessTimeoutInSeconds() {
        return surefirePlugin.getForkedProcessTimeoutInSeconds();
    }

    @Override
    public void setForkedProcessTimeoutInSeconds(
            final int forkedProcessTimeoutInSeconds) {
        surefirePlugin
            .setForkedProcessTimeoutInSeconds(forkedProcessTimeoutInSeconds);
    }

    @Override
    public Map<String, String> getEnvironmentVariables() {
        return surefirePlugin.getEnvironmentVariables();
    }

    @Override
    public void setEnvironmentVariables(
            final Map<String, String> environmentVariables) {
        surefirePlugin.setEnvironmentVariables(environmentVariables);
    }

    @Override
    public File getWorkingDirectory() {
        return surefirePlugin.getWorkingDirectory();
    }

    @Override
    public void setWorkingDirectory(final File workingDirectory) {
        surefirePlugin.setWorkingDirectory(workingDirectory);
    }

    @Override
    public boolean isChildDelegation() {
        return surefirePlugin.isChildDelegation();
    }

    @Override
    public void setChildDelegation(final boolean childDelegation) {
        surefirePlugin.setChildDelegation(childDelegation);
    }

    @Override
    public String getGroups() {
        return surefirePlugin.getGroups();
    }

    @Override
    public void setGroups(final String groups) {
        surefirePlugin.setGroups(groups);
    }

    @Override
    public String getExcludedGroups() {
        return surefirePlugin.getExcludedGroups();
    }

    @Override
    public void setExcludedGroups(final String excludedGroups) {
        surefirePlugin.setExcludedGroups(excludedGroups);
    }

    @Override
    public File[] getSuiteXmlFiles() {
        return surefirePlugin.getSuiteXmlFiles();
    }

    @Override
    public void setSuiteXmlFiles(final File[] suiteXmlFiles) {
        surefirePlugin.setSuiteXmlFiles(suiteXmlFiles);
    }

    @Override
    public String getJunitArtifactName() {
        return surefirePlugin.getJunitArtifactName();
    }

    @Override
    public void setJunitArtifactName(final String junitArtifactName) {
        surefirePlugin.setJunitArtifactName(junitArtifactName);
    }

    @Override
    public String getTestNGArtifactName() {
        return surefirePlugin.getTestNGArtifactName();
    }

    @Override
    public void setTestNGArtifactName(final String testNGArtifactName) {
        surefirePlugin.setTestNGArtifactName(testNGArtifactName);
    }

    @Override
    public int getThreadCount() {
        return surefirePlugin.getThreadCount();
    }

    @Override
    public void setThreadCount(final int threadCount) {
        surefirePlugin.setThreadCount(threadCount);
    }

    @Override
    public boolean getPerCoreThreadCount() {
        return surefirePlugin.getPerCoreThreadCount();
    }

    @Override
    public void setPerCoreThreadCount(final boolean perCoreThreadCount) {
        surefirePlugin.setPerCoreThreadCount(perCoreThreadCount);
    }

    @Override
    public boolean getUseUnlimitedThreads() {
        return surefirePlugin.getUseUnlimitedThreads();
    }

    @Override
    public void setUseUnlimitedThreads(final boolean useUnlimitedThreads) {
        surefirePlugin.setUseUnlimitedThreads(useUnlimitedThreads);
    }

    @Override
    public String getParallel() {
        return surefirePlugin.getParallel();
    }

    @Override
    public void setParallel(final String parallel) {
        surefirePlugin.setParallel(parallel);
    }

    @Override
    public boolean isTrimStackTrace() {
        return surefirePlugin.isTrimStackTrace();
    }

    @Override
    public void setTrimStackTrace(final boolean trimStackTrace) {
        surefirePlugin.setTrimStackTrace(trimStackTrace);
    }

    @Override
    public ArtifactResolver getArtifactResolver() {
        return surefirePlugin.getArtifactResolver();
    }

    @Override
    public void setArtifactResolver(final ArtifactResolver artifactResolver) {
        surefirePlugin.setArtifactResolver(artifactResolver);
    }

    @Override
    public ArtifactFactory getArtifactFactory() {
        return surefirePlugin.getArtifactFactory();
    }

    @Override
    public void setArtifactFactory(final ArtifactFactory artifactFactory) {
        surefirePlugin.setArtifactFactory(artifactFactory);
    }

    @Override
    public List<ArtifactRepository> getRemoteRepositories() {
        return surefirePlugin.getRemoteRepositories();
    }

    @Override
    public void setRemoteRepositories(
            final List<ArtifactRepository> remoteRepositories) {
        surefirePlugin.setRemoteRepositories(remoteRepositories);
    }

    @Override
    public ArtifactMetadataSource getMetadataSource() {
        return surefirePlugin.getMetadataSource();
    }

    @Override
    public void setMetadataSource(
            final ArtifactMetadataSource metadataSource) {
        surefirePlugin.setMetadataSource(metadataSource);
    }

    @Override
    public Properties getOriginalSystemProperties() {
        return surefirePlugin.getOriginalSystemProperties();
    }

    @Override
    public void setOriginalSystemProperties(
            final Properties originalSystemProperties) {
        surefirePlugin
            .setOriginalSystemProperties(originalSystemProperties);
    }

    @Override
    public Properties getInternalSystemProperties() {
        return surefirePlugin.getInternalSystemProperties();
    }

    @Override
    public void setInternalSystemProperties(
            final Properties internalSystemProperties) {
        surefirePlugin
            .setInternalSystemProperties(internalSystemProperties);
    }

    @Override
    public boolean isDisableXmlReport() {
        return surefirePlugin.isDisableXmlReport();
    }

    @Override
    public void setDisableXmlReport(final boolean disableXmlReport) {
        surefirePlugin.setDisableXmlReport(disableXmlReport);
    }

    @Override
    public boolean isUseSystemClassLoader() {
        return surefirePlugin.isUseSystemClassLoader();
    }

    @Override
    public void setUseSystemClassLoader(final boolean useSystemClassLoader) {
        surefirePlugin.setUseSystemClassLoader(useSystemClassLoader);
    }

    @Override
    public boolean isUseManifestOnlyJar() {
        return surefirePlugin.isUseManifestOnlyJar();
    }

    @Override
    public void setUseManifestOnlyJar(final boolean useManifestOnlyJar) {
        surefirePlugin.setUseManifestOnlyJar(useManifestOnlyJar);
    }

    @Override
    public boolean isEnableAssertions() {
        return surefirePlugin.isEnableAssertions();
    }

    @Override
    public void setEnableAssertions(final boolean enableAssertions) {
        surefirePlugin.setEnableAssertions(enableAssertions);
    }

    @Override
    public MavenSession getSession() {
        return surefirePlugin.getSession();
    }

    @Override
    public void setSession(final MavenSession session) {
        surefirePlugin.setSession(session);
    }

    @Override
    public String getObjectFactory() {
        return surefirePlugin.getObjectFactory();
    }

    @Override
    public void setObjectFactory(final String objectFactory) {
        surefirePlugin.setObjectFactory(objectFactory);
    }

    @Override
    public ToolchainManager getToolchainManager() {
        return surefirePlugin.getToolchainManager();
    }

    @Override
    public void setToolchainManager(final ToolchainManager toolchainManager) {
        surefirePlugin.setToolchainManager(toolchainManager);
    }

    @Override
    public Boolean getFailIfNoSpecifiedTests() {
        return surefirePlugin.getFailIfNoSpecifiedTests();
    }

    @Override
    public void setFailIfNoSpecifiedTests(
            final Boolean failIfNoSpecifiedTests) {
        surefirePlugin.setFailIfNoSpecifiedTests(failIfNoSpecifiedTests);
    }

    @Override
    public Boolean getFailIfNoTests() {
        return surefirePlugin.getFailIfNoTests();
    }

    @Override
    public void setFailIfNoTests(final Boolean failIfNoTests) {
        surefirePlugin.setFailIfNoTests(failIfNoTests);
    }

    @Override
    public boolean isMavenParallel() {
        return surefirePlugin.isMavenParallel();
    }

    @Override
    public void setRunOrder(final String runOrder) {
        surefirePlugin.setRunOrder(runOrder);
    }

    @Override
    public String getRunOrder() {
        return surefirePlugin.getRunOrder();
    }

}
