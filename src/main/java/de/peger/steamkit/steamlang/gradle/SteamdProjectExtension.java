package de.peger.steamkit.steamlang.gradle;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

/**
 * @author dpeger
 *
 */
public class SteamdProjectExtension {

    /**
     * Mappings from a steamd source to a target Java package relative to
     * {@link #mOutputBasePackage}. All classes/enums of the source are
     * generated in the respective target package.
     */
    @Input
    @Optional
    private Map<String, String> mOutputPackages = new HashMap<>();

    /**
     * Mappings from package names as they occur in the steamd source to
     * packages of the respective classes.
     */
    @Input
    @Optional
    private Map<String, String> mImportPackages = new HashMap<>();

    /**
     * @return the outputPackages
     */
    public Map<String, String> getOutputPackages() {
        return mOutputPackages;
    }

    /**
     * @param pOutputPackages
     *            the outputPackages to set
     */
    public void setOutputPackages(Map<String, String> pOutputPackages) {
        mOutputPackages = pOutputPackages;
    }

    /**
     * @return the importPackages
     */
    public Map<String, String> getImportPackages() {
        return mImportPackages;
    }

    /**
     * @param pImportPackages
     *            the importPackages to set
     */
    public void setImportPackages(Map<String, String> pImportPackages) {
        mImportPackages = pImportPackages;
    }
}
