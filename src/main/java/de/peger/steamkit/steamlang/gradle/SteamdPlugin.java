package de.peger.steamkit.steamlang.gradle;

import java.io.File;

import javax.inject.Inject;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.plugins.ide.eclipse.EclipsePlugin;

/**
 * @author dpeger
 */
public class SteamdPlugin implements Plugin<Project> {

    public static final String STEAMD_CONFIGURATION_NAME = "steamd";
    public static final String STEAMD_EXTENSION_NAME = "steamd";
    public static final String STEAMD_SOURCE_SUFFIX = "steamd";

    private final SourceDirectorySetFactory mSourceDirectorySetFactory;

    @Inject
    public SteamdPlugin(final SourceDirectorySetFactory pSourceDirectorySetFactory) {
        mSourceDirectorySetFactory = pSourceDirectorySetFactory;
    }

    @Override
    public void apply(final Project pProject) {
        pProject.getPlugins().apply(BasePlugin.class);
        pProject.getPlugins().apply(JavaPlugin.class);

        pProject.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().stream()
                .filter(this::isDefaultSourceSet).forEach(sourceSet -> {

                    final SteamdSourceConvention tSourceConvention = new SteamdSourceConvention(sourceSet.getName(),
                            mSourceDirectorySetFactory);
                    new DslObject(sourceSet).getConvention().getPlugins().put(STEAMD_EXTENSION_NAME, tSourceConvention);
                    final String tSrcDir = String.format("src/%s/%s", sourceSet.getName(), STEAMD_EXTENSION_NAME);
                    tSourceConvention.getSteamd().srcDir(tSrcDir);
                    sourceSet.getAllSource().source(tSourceConvention.getSteamd());

                    final String tTaskName = sourceSet.getTaskName("compile", "Steamd");
                    SteamdCompileTask tCompileTask = pProject.getTasks().create(tTaskName, SteamdCompileTask.class);
                    tCompileTask.setDescription("Processes the " + sourceSet.getName() + " steamd files.");

                    tCompileTask.setSource(tSourceConvention.getSteamd());

                    final String outputDirectoryName = String.format("gen-src/%s/%s", sourceSet.getName(),
                            STEAMD_EXTENSION_NAME);
                    final File outputDirectory = new File(pProject.getBuildDir(), outputDirectoryName);
                    tCompileTask.setOutputBaseDirectory(outputDirectory);
                    sourceSet.getJava().srcDir(outputDirectory);

                    pProject.getTasks().getByName(sourceSet.getCompileJavaTaskName()).dependsOn(tCompileTask);

                    if (pProject.getPlugins().hasPlugin(EclipsePlugin.class)) {
                        tCompileTask
                                .finalizedBy(pProject.getTasks().getByName(EclipsePlugin.getECLIPSE_CP_TASK_NAME()));
                    }
                });

        pProject.getExtensions().create("steamd", SteamdProjectExtension.class);
    }

    private boolean isDefaultSourceSet(SourceSet s) {
        return s.getName().equals(SourceSet.MAIN_SOURCE_SET_NAME) || s.getName().equals(SourceSet.TEST_SOURCE_SET_NAME);
    }
}
