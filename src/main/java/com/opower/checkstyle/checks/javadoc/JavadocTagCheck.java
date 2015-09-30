package com.opower.checkstyle.checks.javadoc;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.Scope;
import com.puppycrawl.tools.checkstyle.api.TextBlock;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTag;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTagInfo;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTags;
import com.puppycrawl.tools.checkstyle.utils.JavadocUtils;
import com.puppycrawl.tools.checkstyle.utils.ScopeUtils;

/**
 * Checks the tags in Javadoc.
 * <p>
 * Example:
 *
 * <pre>
 * {@code
 *      <module name="JavadocTag">
 *          <property name="tagName" value="since" />
 *          <property name="format" value="^(\d+)\.(\d+)\.(\d+)$" />
 *      </module>
 * }
 * </pre>
 *
 * @since 1.0.0
 */
public class JavadocTagCheck extends Check {

    /** The scope to check for. */
    private Scope mScope = Scope.PRIVATE;

    private JavadocTagInfo mTag;

    /** Compiled regexp to match tag content. **/
    private Pattern mFormatPattern;

    /** Regexp to match tag content. */
    private String mFormat;

    /**
     * Sets the scope to check.
     *
     * @param scope string to set scope from
     */
    public void setScope(String scope) {
        this.mScope = Scope.getInstance(scope);
    }

    /**
     * Set the tag name.
     *
     * @param aTagName a <code>String</code> value
     * @throws IllegalArgumentException unable to parse aTagName
     */
    public void setTagName(String aTagName) throws IllegalArgumentException {
        try {
            this.mTag = JavadocTagInfo.valueOf(aTagName.toUpperCase());
        }
        catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported tag name: " + aTagName, e);
        }
    }

    /**
     * Set the tag pattern.
     *
     * @param aFormat a <code>String</code> value
     * @throws PatternSyntaxException unable to parse aFormat
     */
    public void setFormat(String aFormat) throws PatternSyntaxException {
        this.mFormat = aFormat;
        this.mFormatPattern = Pattern.compile(aFormat);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
                TokenTypes.INTERFACE_DEF,
                TokenTypes.CLASS_DEF,
                TokenTypes.ENUM_DEF,
                TokenTypes.PACKAGE_DEF
        };
    }

    @Override
    public void visitToken(DetailAST aAST) {
        if (!shouldCheck(aAST)) {
            return;
        }
        final FileContents contents = getFileContents();

        int lineNo = aAST.getLineNo();
        if (aAST.getType() == TokenTypes.PACKAGE_DEF) {
            lineNo = contents.getLines().length;
        }
        final TextBlock cmt = contents.getJavadocBefore(lineNo);
        if (cmt == null) {
            log(lineNo, "javadoc.missing");
        }
        else if (ScopeUtils.isOuterMostType(aAST)) {
            final List<JavadocTag> tags = getJavadocTags(cmt);
            checkTag(lineNo, tags, this.mTag.getName(), this.mFormatPattern, this.mFormat);
        }
    }

    /**
     * Whether we should check this node.
     *
     * @param aAST a given node.
     * @return whether we should check a given node.
     */
    private boolean shouldCheck(final DetailAST aAST) {

        if (this.mTag == null || this.mFormatPattern == null) {
            return false;
        }

        if (aAST.getType() == TokenTypes.PACKAGE_DEF) {
            return getFileContents().getFileName().endsWith("package-info.java");
        }
        final DetailAST mods = aAST.findFirstToken(TokenTypes.MODIFIERS);
        final Scope declaredScope = ScopeUtils.getScopeFromMods(mods);
        final Scope scope = ScopeUtils.isInInterfaceOrAnnotationBlock(aAST) ? Scope.PUBLIC : declaredScope;
        final Scope surroundingScope = ScopeUtils.getSurroundingScope(aAST);
        return scope.isIn(this.mScope) && ((surroundingScope == null) || surroundingScope.isIn(this.mScope));
    }

    /**
     * Gets all standalone tags from a given javadoc.
     *
     * @param aCmt the Javadoc comment to process.
     * @return all standalone tags from the given javadoc.
     */
    private List<JavadocTag> getJavadocTags(TextBlock aCmt) {
        final JavadocTags tags = JavadocUtils.getJavadocTags(aCmt, JavadocUtils.JavadocTagType.BLOCK);
        return tags.getValidTags();
    }

    /**
     * Verifies that a type definition has a required tag.
     *
     * @param aLineNo the line number for the type definition.
     * @param aTags tags from the Javadoc comment for the type definition.
     * @param aTag the required tag name.
     * @param aFormatPattern regexp for the tag value.
     * @param aFormat pattern for the tag value.
     */
    private void checkTag(int aLineNo, List<JavadocTag> aTags, String aTag, Pattern aFormatPattern, String aFormat) {
        if (aFormatPattern == null) {
            return;
        }

        int tagCount = 0;
        for (int i = aTags.size() - 1; i >= 0; i--) {
            final JavadocTag tag = aTags.get(i);
            if (tag.getTagName().equals(aTag)) {
                tagCount++;
                if (!aFormatPattern.matcher(tag.getFirstArg()).find()) {
                    log(aLineNo, "type.tagFormat", "@" + aTag, aFormat);
                }
            }
        }
        if (tagCount == 0) {
            log(aLineNo, "type.missingTag", "@" + aTag);
        }
    }
}
