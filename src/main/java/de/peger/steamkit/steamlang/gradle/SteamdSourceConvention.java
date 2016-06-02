package de.peger.steamkit.steamlang.gradle;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;

/**
 * Definitions for steamd source directories
 * 
 * @author dpeger
 */
public class SteamdSourceConvention {

    private SourceDirectorySet mSteamd;

    public SteamdSourceConvention(final String mDisplayName, final SourceDirectorySetFactory mFactory) {

        final String tDisplayString = String.format("%s steamd sources", mDisplayName);
        final SourceDirectorySet tSteamd = mFactory.create(tDisplayString);
        tSteamd.getFilter().include("**/*." + SteamdPlugin.STEAMD_SOURCE_SUFFIX);

        mSteamd = tSteamd;
    }

    /**
     * @return the steamd
     */
    public SourceDirectorySet getSteamd() {
        return mSteamd;
    }

    /**
     * @param pSteamd
     *            the steamd to set
     */
    public void setSteamd(SourceDirectorySet pSteamd) {
        mSteamd = pSteamd;
    }

    public SteamdSourceConvention antlr(final Closure<SourceDirectorySet> configureClosure) {
        ConfigureUtil.configure(configureClosure, getSteamd());
        return this;
    }

}
