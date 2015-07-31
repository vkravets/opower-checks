package com.opower.checkstyle.checks.javadoc;

import com.opower.checkstyle.checks.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.JavadocTagInfo;
import org.apache.commons.beanutils.ConversionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test cases to cover {@link JavadocTagCheck}.
 *
 * @since 1.0.0
 */
public class TestJavadocTagCheck extends BaseCheckTestSupport {

    private static final String NOT_EXISTING_TAG_NAME = "qwe";

    /** "@since" tag will be use for testing. */
    private static final String SINCE_TAG_NAME = JavadocTagInfo.SINCE.getName();

    @Test
    public void testSetNotExistingTagName() throws Exception {
        final JavadocTagCheck javadocTagCheck = new JavadocTagCheck();
        try {
            javadocTagCheck.setTagName(NOT_EXISTING_TAG_NAME);
            fail("Expected exception, but nothing happened.");
        }
        catch (final Exception e) {
            assertEquals(ConversionException.class, e.getClass());
            assertEquals("Unsupported tag name: qwe", e.getMessage());
        }
    }

    @Test
    public void testSetTagName() throws Exception {
        final JavadocTagCheck javadocTagCheck = new JavadocTagCheck();
        javadocTagCheck.setTagName(SINCE_TAG_NAME);
    }

    @Test
    public void testSetWrongRegularExInFormat() throws Exception {
        final JavadocTagCheck javadocTagCheck = new JavadocTagCheck();
        try {
            javadocTagCheck.setFormat("^(\\d");
            fail("Expected exception, but nothing happened.");
        }
        catch (final Exception e) {
            assertEquals(ConversionException.class, e.getClass());
            assertEquals("Failed to initialise regular expression ^(\\d", e.getMessage());
        }
    }

    @Test
    public void testSetFormat() throws Exception {
        final JavadocTagCheck javadocTagCheck = new JavadocTagCheck();
        javadocTagCheck.setFormat("^(\\d+)\\.(\\d+)\\.(\\d+)$");
    }

    @Test
    public void testFileNotExist() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(JavadocTagCheck.class);
        final String[] expected = {
                "0: File not found!",
        };
        verify(checkConfig, getSrcPath("NotExistingfile.java"), expected);
    }

    @Test
    public void testMissingJavadocComment() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(JavadocTagCheck.class);
        checkConfig.addAttribute("tagName", SINCE_TAG_NAME);
        checkConfig.addAttribute("format", "\\S");
        final String[] expected = {
                "3: Missing a Javadoc comment",
                "6: Missing a Javadoc comment",
                "9: Missing a Javadoc comment",
                "12: Missing a Javadoc comment",
        };
        verify(checkConfig, getSrcPath("withoutjavadoc/WithoutJavadoc.java"), expected);
    }

    @Test
    public void testMissingJavadocCommentInPackageInfo() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(JavadocTagCheck.class);
        checkConfig.addAttribute("tagName", SINCE_TAG_NAME);
        checkConfig.addAttribute("format", "\\S");
        final String[] expected = {
                "1: Missing a Javadoc comment"
        };
        verify(checkConfig, getSrcPath("withoutjavadoc/package-info.java"), expected);
    }

    @Test
    public void testMissingFormat() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(JavadocTagCheck.class);
        checkConfig.addAttribute("tagName", SINCE_TAG_NAME);
        final String[] expected = {
                };
        verify(checkConfig, getSrcPath("withoutjavadoc/WithoutJavadoc.java"), expected);
    }

    @Test
    public void testMissingTagName() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(JavadocTagCheck.class);
        checkConfig.addAttribute("format", "\\S");
        final String[] expected = {
                };
        verify(checkConfig, getSrcPath("withoutjavadoc/WithoutJavadoc.java"), expected);
    }

    @Test
    public void testSinceRequired() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(JavadocTagCheck.class);
        checkConfig.addAttribute("tagName", SINCE_TAG_NAME);
        checkConfig.addAttribute("format", "\\S");
        final String[] expected = {
                };
        verify(checkConfig, getSrcPath("withsince/WithSince.java"), expected);
    }

    @Test
    public void testSinceErrors() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(JavadocTagCheck.class);
        checkConfig.addAttribute("tagName", SINCE_TAG_NAME);
        checkConfig.addAttribute("format", "\\S");
        final String[] expected = {
                "11: Type Javadoc comment is missing an @since tag.",
                "18: Type Javadoc comment is missing an @since tag.",
                "25: Type Javadoc comment is missing an @since tag.",
                "32: Type Javadoc comment is missing an @since tag."
        };
        verify(checkConfig, getSrcPath("withoutsince/WithoutSince.java"), expected);
    }

    @Test
    public void testSinceRegularExError() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(JavadocTagCheck.class);
        checkConfig.addAttribute("tagName", SINCE_TAG_NAME);
        checkConfig.addAttribute("format", "ABC");
        final String[] expected = {
                "12: Type Javadoc tag @since must match pattern 'ABC'.",
                "20: Type Javadoc tag @since must match pattern 'ABC'.",
                "28: Type Javadoc tag @since must match pattern 'ABC'.",
                "36: Type Javadoc tag @since must match pattern 'ABC'."
        };
        verify(checkConfig, getSrcPath("withsince/WithSince.java"), expected);
    }

    @Test
    public void testSinceRequiredInPackageInfo() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(JavadocTagCheck.class);
        checkConfig.addAttribute("tagName", SINCE_TAG_NAME);
        checkConfig.addAttribute("format", "\\S");
        final String[] expected = {
                };
        verify(checkConfig, getSrcPath("withsince/package-info.java"), expected);
    }

    @Test
    public void testSinceErrorsInPackageInfo() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(JavadocTagCheck.class);
        checkConfig.addAttribute("tagName", SINCE_TAG_NAME);
        checkConfig.addAttribute("format", "\\S");
        final String[] expected = {
                "4: Type Javadoc comment is missing an @since tag."
        };
        verify(checkConfig, getSrcPath("withoutsince/package-info.java"), expected);
    }

    @Test
    public void testSinceRegularExErrorInPackageInfo() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(JavadocTagCheck.class);
        checkConfig.addAttribute("tagName", SINCE_TAG_NAME);
        checkConfig.addAttribute("format", "ABC");
        final String[] expected = {
                "5: Type Javadoc tag @since must match pattern 'ABC'."
        };
        verify(checkConfig, getSrcPath("withsince/package-info.java"), expected);
    }
}
