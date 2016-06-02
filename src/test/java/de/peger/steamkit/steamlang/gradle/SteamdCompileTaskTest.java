package de.peger.steamkit.steamlang.gradle;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.file.SourceDirectorySet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

/**
 * @author dpeger
 */
@RunWith(MockitoJUnitRunner.class)
public class SteamdCompileTaskTest {

    @Mock
    private SourceDirectorySet mSourceBaseDirectoriesMock;

    // @InjectMocks
    // private SteamdCompileTask mSteamdCompileTask;

    @Test
    public void testExtractPackageFromFilePathHappyCase() throws Exception {

        final File tSrcBaseDir = new File("./src-dir");
        final Set<File> tSourceDirs = Sets.newHashSet(tSrcBaseDir);
        Mockito.when(mSourceBaseDirectoriesMock.getSrcDirs()).thenReturn(tSourceDirs);

        final File tSourceFile = new File(tSrcBaseDir, "test/package/use/less/Bla.java");
        final String tActualPackage = SteamdCompileTask.extractPackageFromFilePath(tSourceFile, tSourceDirs);

        Assert.assertEquals("Package name does not meet expectations.", "test.package.use.less", tActualPackage);
    }

    @Test(expected = IllegalStateException.class)
    public void testExtractPackageFromFilePathWithNoSourceDirsWillThrowException() throws Exception {

        final Set<File> tSourceDirs = Collections.emptySet();
        Mockito.when(mSourceBaseDirectoriesMock.getSrcDirs()).thenReturn(tSourceDirs);

        final File tSourceFile = new File(".");
        SteamdCompileTask.extractPackageFromFilePath(tSourceFile, tSourceDirs);
    }

    @Test(expected = IllegalStateException.class)
    public void testExtractPackageFromFilePathWithDifferentSourceDirWillThrowException() throws Exception {

        final File tSrcBaseDir = new File("./src-dir");
        final Set<File> tSourceDirs = Sets.newHashSet(tSrcBaseDir);
        Mockito.when(mSourceBaseDirectoriesMock.getSrcDirs()).thenReturn(tSourceDirs);

        final File tAnotherSrcBaseDir = new File("./another-src-dir");
        final File tSourceFile = new File(tAnotherSrcBaseDir, "test/package/use/less/Bla.java");
        SteamdCompileTask.extractPackageFromFilePath(tSourceFile, tSourceDirs);
    }

    @Test
    public void testExtractPackageFromFilePathWithMoreThanOneSourceDir() throws Exception {

        final File tSrcBaseDir = new File("./src-dir");
        final File tAnotherSrcBaseDir = new File("./another-src-dir");
        final Set<File> tSourceDirs = Sets.newHashSet(tSrcBaseDir, tAnotherSrcBaseDir);
        Mockito.when(mSourceBaseDirectoriesMock.getSrcDirs()).thenReturn(tSourceDirs);

        final File tSourceFile = new File(tAnotherSrcBaseDir, "test/package/my/test/works/MainClass.java");
        final String tActualPackage = SteamdCompileTask.extractPackageFromFilePath(tSourceFile, tSourceDirs);

        Assert.assertEquals("Package name does not meet expectations.", "test.package.my.test.works", tActualPackage);
    }

    @Test
    public void testExtractPackageFromFilePathWithSourceInRootPacakge() throws Exception {

        final File tSrcBaseDir = new File("./src-dir");
        final Set<File> tSourceDirs = Sets.newHashSet(tSrcBaseDir);
        Mockito.when(mSourceBaseDirectoriesMock.getSrcDirs()).thenReturn(tSourceDirs);

        final File tSourceFile = new File(tSrcBaseDir, "MainClass.java");
        final String tActualPackage = SteamdCompileTask.extractPackageFromFilePath(tSourceFile, tSourceDirs);

        Assert.assertEquals("Package name does not meet expectations.", StringUtils.EMPTY, tActualPackage);
    }

    @Test
    public void testJoinPackagesHappyCase() throws Exception {

        final String tBasePackage = "some.base.package.name";
        final String tMiddlePackage = "this.goes.mid";
        final String tEndPackage = "this.is.the.end";

        final String tActualPackage = SteamdCompileTask.joinPackages(tBasePackage, tMiddlePackage, tEndPackage);

        Assert.assertEquals("Package name does not meet expectations.",
                tBasePackage + "." + tMiddlePackage + "." + tEndPackage, tActualPackage);
    }

    @Test
    public void testJoinPackagesWithEmptyList() throws Exception {

        final String[] tPackageParts = new String[0];

        final String tActualPackage = SteamdCompileTask.joinPackages(tPackageParts);

        Assert.assertEquals("Package name does not meet expectations.", StringUtils.EMPTY, tActualPackage);
    }

    @Test
    public void testJoinPackagesWithEmptyElements() throws Exception {

        final String[] tPackageParts = new String[] { "test", "package", null, "with", "", "     ", "lots.of",
                "whitespace" };

        final String tActualPackage = SteamdCompileTask.joinPackages(tPackageParts);

        Assert.assertEquals("Package name does not meet expectations.", "test.package.with.lots.of.whitespace",
                tActualPackage);
    }

}
