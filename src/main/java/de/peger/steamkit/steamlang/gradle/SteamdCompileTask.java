package de.peger.steamkit.steamlang.gradle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.plugins.ide.eclipse.EclipsePlugin;

import de.peger.steamkit.steamlang.codegen.SteamdCodeGenContext;
import de.peger.steamkit.steamlang.codegen.SteamdGenerator;

/**
 * Compiles the steamd sources using the {@link SteamdGenerator}
 * 
 * @author dpeger
 */
public class SteamdCompileTask extends SourceTask {

    /**
     * Base directories for the sources compiled with this task.
     */
    private SourceDirectorySet mSourceBaseDirectories;

    /**
     * Generated files will be written to this directory. This path will be the
     * root package of the generated sources.
     */
    private File mOutputBaseDirectory;

    @TaskAction
    public void compileSteamd() throws IOException {

        final SteamdProjectExtension tExtension = getProject().getExtensions().findByType(SteamdProjectExtension.class);

        final SteamdCodeGenContext tContext = new SteamdCodeGenContext();
        tContext.setOutputBaseDir(getOutputBaseDirectory());
        tContext.setImportPackageMappings(tExtension.getImportPackages());

        final Set<File> tSourceFiles = getSource().getFiles();

        addOutputPackageMappings(tExtension.getOutputPackages(), tSourceFiles, mSourceBaseDirectories, tContext);

        final SteamdGenerator tGenerator = new SteamdGenerator(tContext);
        tGenerator.generate(tSourceFiles.toArray(new File[tSourceFiles.size()]));
    }

    static void addOutputPackageMappings(final Map<String, String> pOutputPackages, final Collection<File> pSourceFiles,
            final SourceDirectorySet pSourceBaseDirectories, final SteamdCodeGenContext pContext) throws IOException {

        for (final File tSourceFile : pSourceFiles) {
            final String tBasePackage = extractPackageFromFilePath(tSourceFile, pSourceBaseDirectories.getSrcDirs());
            final String tOutputPackage = pOutputPackages.get(tSourceFile.getName());

            final String tPackage = joinPackages(tBasePackage, tOutputPackage);
            pContext.addOutputPackageMapping(tSourceFile.getName(), tPackage);
        }
    }

    static String joinPackages(final String... tPackageParts) {
        final String tPackage = Arrays.asList(tPackageParts).stream().filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("."));
        return tPackage;
    }

    static String extractPackageFromFilePath(final File pSourceFile, final Set<File> pSrcDirs) throws IOException {

        final File tCanonicalSourceFile = pSourceFile.getCanonicalFile();
        File tSourceFilePath = tCanonicalSourceFile.getParentFile();
        final List<String> tPackageList = new ArrayList<>();
        while (!containsCanonicalFile(pSrcDirs, tSourceFilePath)) {
            tPackageList.add(0, tSourceFilePath.getName());
            tSourceFilePath = Optional.ofNullable(tSourceFilePath.getParentFile())
                    .orElseThrow(() -> new IllegalStateException("The source file '" + tCanonicalSourceFile.getPath()
                            + "' does not reside in any of the task's source directories."));

        }

        final String tBasePackage = StringUtils.join(tPackageList, '.');
        return tBasePackage;
    }

    private static boolean containsCanonicalFile(final Set<File> pSrcDirs, File tSourceFilePath) {
        return pSrcDirs.stream().filter((f) -> {
            try {
                return f.getCanonicalFile().equals(tSourceFilePath);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }).findAny().isPresent();
    }

    /**
     * @param pOutputBaseDirectory
     *            the outputBaseDirectory to set
     */
    public void setOutputBaseDirectory(final File pOutputBaseDirectory) {
        mOutputBaseDirectory = pOutputBaseDirectory;
    }

    /**
     * @return the outputBaseDirectory
     */
    @OutputDirectory
    public File getOutputBaseDirectory() {
        return mOutputBaseDirectory;
    }

    @Override
    public void setSource(Object pSource) {
        super.setSource(pSource);
        if (pSource instanceof SourceDirectorySet) {
            mSourceBaseDirectories = (SourceDirectorySet) pSource;
        }
    }

    /**
     * @return the source
     */
    @Override
    @InputFiles
    @SkipWhenEmpty
    public FileTree getSource() {
        // This method is here as the Gradle DSL generation can't handle
        // properties with setters and getters in different classes.
        return super.getSource();
    }

}
