package br.com.caelum.vraptor.ioc.spring;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class WebinfClassesPatternResolver extends
		PathMatchingResourcePatternResolver {

	private static final Logger logger = LoggerFactory
			.getLogger(WebinfClassesPatternResolver.class);
	
	private final URL webinfClassesDirectory;

	public WebinfClassesPatternResolver(URL webinfClassesDirectory) {
		this.webinfClassesDirectory = webinfClassesDirectory;
		
	}

	@Override
	protected Set<Resource> doFindPathMatchingJarResources(
			Resource rootDirResource, String subPattern) throws IOException {
		logger.debug("Not looking inside " + rootDirResource);
		return Collections.emptySet();
	}

	@Override
	protected Resource[] findPathMatchingResources(String locationPattern)
			throws IOException {
		logger.debug(" X finding matches " + locationPattern);
		return super.findPathMatchingResources(locationPattern);
	}

	@Override
	protected Set<Resource> doFindMatchingFileSystemResources(File rootDir,
			String subPattern) throws IOException {
		logger.debug("match files system rsources  " + rootDir + " "
				+ subPattern);
		return super.doFindMatchingFileSystemResources(rootDir, subPattern);
	}

	@Override
	protected Set<Resource> doFindPathMatchingFileResources(
			Resource rootDirResource, String subPattern) throws IOException {
		logger.debug("matching files resources " + rootDirResource + " "
				+ subPattern);
		return super.doFindPathMatchingFileResources(rootDirResource,
				subPattern);
	}

	@Override
	protected String determineRootDir(String location) {
		logger.debug("determining root dir " + location);

		return super.determineRootDir(location);
	}

	@Override
	protected Set<File> retrieveMatchingFiles(File rootDir, String pattern)
			throws IOException {
		logger.debug("retrieving matchinh files " + rootDir + " " + pattern);
		return super.retrieveMatchingFiles(rootDir, pattern);
	}

	@Override
	protected JarFile getJarFile(String jarFileUrl) throws IOException {
		logger.debug("getting jar file " + jarFileUrl);
		return super.getJarFile(jarFileUrl);
	}

	@Override
	protected void doRetrieveMatchingFiles(String fullPattern, File dir,
			Set<File> result) throws IOException {
		logger.debug("Matching Files  " + fullPattern + " " + dir);
		super.doRetrieveMatchingFiles(fullPattern, dir, result);
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		logger.debug("getting resources for " + locationPattern);
		return super.getResources(locationPattern);
	}

	@Override
	protected boolean isJarResource(Resource resource) throws IOException {

		boolean x = super.isJarResource(resource);
		logger.debug(resource + " " + x);
		return x;
	}

	protected Resource[] findAllClassPathResources(String location)
			throws IOException {
		logger.debug("XXXXXXX finding all " + location);
		return new Resource[]{new UrlResource(webinfClassesDirectory)};
	}

}
