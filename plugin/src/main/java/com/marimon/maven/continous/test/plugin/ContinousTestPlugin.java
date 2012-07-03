package com.marimon.maven.continous.test.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

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

            @SuppressWarnings("rawtypes")
            ConcurrentHashMap ctx = new ConcurrentHashMap();
            ctx.putAll(getPluginContext());
            surefirePlugin.setPluginContext(ctx);

            surefirePlugin.execute();
            // updateTarget();
            getLog().info("Files checked. " + runs);
            runs++;
        }
        getLog().info("Continous Test Plugin completed.");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map getPluginContext() {
        return super.getPluginContext();
    }

    @Override
    public void setPluginContext(
            @SuppressWarnings("rawtypes") final Map pluginContext) {
        super.setPluginContext(pluginContext);

    }

    /**
     * @parameter default-value="${plugin}"
     * @readonly
     * @since 2.12
     */
    private PluginDescriptor pluginDescriptor;

    /**
     * @parameter default-value="false" expression="${skipTests}"
     * @since 2.4
     */
    private boolean skipTests;

    /**
     * @parameter expression="${maven.test.skip.exec}"
     * @since 2.3
     * @deprecated Use skipTests instead.
     */
    @Deprecated
    private boolean skipExec;

    /**
     * @parameter default-value="false" expression="${maven.test.skip}"
     */
    private boolean skip;

    /**
     * @parameter default-value="false"
     *            expression="${maven.test.failure.ignore}"
     */
    private boolean testFailureIgnore;

    /**
     * @parameter default-value="${basedir}"
     */
    private File basedir;

    /**
     * @parameter default-value="${project.build.testOutputDirectory}"
     */
    private File testClassesDirectory;

    /**
     * @parameter default-value="${project.build.outputDirectory}"
     */
    private File classesDirectory;

    /**
     * @parameter default-value="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter
     * @since 2.6
     */
    private List<String> classpathDependencyExcludes;

    /**
     * @parameter default-value=""
     * @since 2.6
     */
    private String classpathDependencyScopeExclude;

    /**
     * @parameter
     * @since 2.4
     */
    private List<String> additionalClasspathElements;

    /**
     * @parameter default-value="${project.build.directory}/surefire-reports"
     */
    private File reportsDirectory;

    /**
     * @parameter default-value="${project.build.testSourceDirectory}"
     * @required
     * @since 2.2
     */
    private File testSourceDirectory;

    /**
     * @parameter expression="${test}"
     */
    private String test;

    /**
     * @parameter
     */
    private List<String> includes;

    /**
     * @parameter
     */
    private List<String> excludes;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter
     * @deprecated Use systemPropertyVariables instead.
     */
    @Deprecated
    private Properties systemProperties;

    /**
     * @parameter
     * @since 2.5
     */
    private Map<String, String> systemPropertyVariables;

    /**
     * @parameter
     * @since 2.8.2
     */
    private File systemPropertiesFile;

    /**
     * @parameter
     * @since 2.4
     */
    private Properties properties;

    /**
     * @parameter expression="${plugin.artifactMap}"
     * @required
     * @readonly
     */
    private Map<String, Artifact> pluginArtifactMap;

    /**
     * @parameter expression="${project.artifactMap}"
     * @required
     * @readonly
     */
    private Map<String, Artifact> projectArtifactMap;

    /**
     * @parameter expression="${surefire.printSummary}" default-value="true"
     */
    private boolean printSummary;

    /**
     * @parameter expression="${surefire.reportFormat}" default-value="brief"
     */
    private String reportFormat;

    /**
     * @parameter expression="${surefire.reportNameSuffix}" default-value=""
     */
    private String reportNameSuffix;

    /**
     * @parameter expression="${surefire.useFile}" default-value="true"
     */
    private boolean useFile;

    /**
     * @parameter expression="${maven.test.redirectTestOutputToFile}"
     *            default-value="false"
     * @since 2.3
     */
    private boolean redirectTestOutputToFile;

    /**
     * @parameter expression="${failIfNoTests}"
     * @since 2.4
     */
    private Boolean failIfNoTests;

    /**
     * @parameter expression="${surefire.failIfNoSpecifiedTests}"
     * @since 2.12
     */
    private Boolean failIfNoSpecifiedTests;

    /**
     * @parameter expression="${forkMode}" default-value="once"
     * @since 2.1
     */
    private String forkMode;

    /**
     * @parameter expression="${jvm}"
     * @since 2.1
     */
    private String jvm;

    /**
     * @parameter expression="${argLine}"
     * @since 2.1
     */
    private String argLine;

    /**
     * @parameter expression="${maven.surefire.debug}"
     * @since 2.4
     */
    private String debugForkedProcess;

    /**
     * @parameter expression="${surefire.timeout}"
     * @since 2.4
     */
    private int forkedProcessTimeoutInSeconds;

    /**
     * @parameter
     * @since 2.1.3
     */
    private final Map<String, String> environmentVariables =
        new HashMap<String, String>();

    /**
     * @parameter expression="${basedir}"
     * @since 2.1.3
     */
    private File workingDirectory;

    /**
     * @parameter expression="${childDelegation}" default-value="false"
     * @since 2.1
     */
    private boolean childDelegation;

    /**
     * @parameter expression="${groups}"
     * @since 2.2
     */
    private String groups;

    /**
     * @parameter expression="${excludedGroups}"
     * @since 2.2
     */
    private String excludedGroups;

    /**
     * @parameter
     * @since 2.2
     */
    private File[] suiteXmlFiles;

    /**
     * @parameter expression="${junitArtifactName}" default-value="junit:junit"
     * @since 2.3.1
     */
    private String junitArtifactName;

    /**
     * @parameter expression="${testNGArtifactName}"
     *            default-value="org.testng:testng"
     * @since 2.3.1
     */
    private String testNGArtifactName;

    /**
     * @parameter expression="${threadCount}"
     * @since 2.2
     */
    private int threadCount;

    /**
     * @parameter expression="${perCoreThreadCount}" default-value="true"
     * @since 2.5
     */
    private boolean perCoreThreadCount;

    /**
     * @parameter expression="${useUnlimitedThreads}" default-value="false"
     * @since 2.5
     */
    private boolean useUnlimitedThreads;

    /**
     * @parameter expression="${parallel}"
     * @since 2.2
     */
    private String parallel;

    /**
     * @parameter expression="${trimStackTrace}" default-value="true"
     * @since 2.2
     */
    private boolean trimStackTrace;

    /**
     * @component
     */
    private ArtifactResolver artifactResolver;

    /**
     * @component
     */
    private ArtifactFactory artifactFactory;

    /**
     * @parameter expression="${project.pluginArtifactRepositories}"
     * @since 2.2
     */
    private List<ArtifactRepository> remoteRepositories;

    /**
     * @component
     */
    private ArtifactMetadataSource metadataSource;

    private Properties originalSystemProperties;

    /**
     * systemPropertyVariables + systemProperties
     */
    private final Properties internalSystemProperties = new Properties();

    /**
     * @parameter expression="${disableXmlReport}" default-value="false"
     * @since 2.2
     */
    private boolean disableXmlReport;

    /**
     * @parameter expression="${surefire.useSystemClassLoader}"
     *            default-value="true"
     * @since 2.3
     */
    private boolean useSystemClassLoader;

    /**
     * @parameter expression="${surefire.useManifestOnlyJar}"
     *            default-value="true"
     * @since 2.4.3
     */
    private boolean useManifestOnlyJar;

    /**
     * @parameter expression="${enableAssertions}" default-value="true"
     * @since 2.3.1
     */
    private boolean enableAssertions;

    /**
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
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
