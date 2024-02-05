package org.icatproject.ids.integration.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that describes any requirements the test places on the kind of
 * files stored by IDS.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FileRequirements {
    public enum Requirement {
        /**
         * Require that datafile 1 is large enough that Payara does not cache
         * the contents.  Empirical evidence suggests that 8 KiB is too small
         * and 16 KiB seems to work OK.
         */
        BIG_DATAFILES
    };

    Requirement[] value();
}
