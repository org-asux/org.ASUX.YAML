/*
 BSD 3-Clause License
 
 Copyright (c) 2019, Udaybhaskar Sarma Seetamraju
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 
 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.
 
 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.
 
 * Neither the name of the copyright holder nor the names of its
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.ASUX.yaml;

import java.util.regex.*;
import java.util.stream.IntStream;
//import java.io.Cloneable;
import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/** This class encapsulates a YAML Path/Pattern, and makes it super-easy to parse and manipulate it.
 *  In fact, this class makes it very safe to assume that the user's input (for the YAMLPath/pattern) is valid and squeaky-clean.
 * That is, you should expect minimal run-time errors.
 *
 *  <p>A <b>YAML Path</b> (also referred to as: <b>YAML Path-Pattern</b>) is composed of one or more Path-<b>ELEMENT</b>-Patterns separated by "delimiter" (like period/dot/".")</p>
 <p>If you'd like to use delimiters other than period/dot/"." then you'll be foreced to write Java-code, as commandline does Not support any other delimiter (for now).</p>
 <p>Here are rules for YAML Path Pattern</p>
 <ol>
 <li>Except for the 2nd rule (below) all RegExp symbols(like <code><b>'+'</b></code> or <code><b>"[{]}</b></code> as defined and supported by java.util.regex) it'll work - guaranteed!</li>
 <li><p><b>Important - there is ONLY 1 deviation/substitution</b>: Whenever a star/asterisk/<code><b>'*'</b></code> with a delimiter on either side (that is, the Path-ELEMENT is = exactly star/asterisk/<code><b>'+'</b></code>).. .. is detected, it is <b>automatically</b> replaced with <code><b>".*"</b></code></p><p>This substitution is allowed ONLY for human convenience.</p></li>
 <li>Any other use of RegExp compatible star/asterisk/<code><b>'*'</b></code> (example: <code>paths./pet*.{get|put|post}.responses.200</code>) will be used as is - to match YAML elements.</li>
 <li>Note: <code><b>**</b></code> (<b>double</b> star/asterisk/<code><b>"*"</b></code>) implies unlimited-match prefix.  It's a Special case: <code>"**"</code> represents a deviation of java.util.regexp specs on what qualifies as a regular-expression.  This deviation is a very human-friendly easy-2-understand need.</li>
 </ol>
 <p>Example: <code>paths.*.*.responses.200"</code>.  <b>ATTENTION: This is a human readable pattern, NOT a 100% proper RegExp-pattern</b></p>
 <p>Example: <code>paths./pet*.{get|put|post}.responses.200</code>.  <b>ATTENTION: This is 100% proper RegExp-pattern</b></p>
 *
 *<pre>
 public static void main(String[] args) {
    cmdLineArgs = new CmdLineArgs(args);
    .. ..
    for loop ..
        if ( ! _yamlPath.hasNext() ) return false; // YAML path has ended
        final String yamlPathElemStr = _yamlPath.get(); // current path-element (a substring of full this.yamlPathStr)
        System.out.println(CLASSNAME + ": @# " + _yamlPath.index() +"\t"+ _yamlPath.getPrefix() +"\t"+ _yamlPath.get() +"\t"+ _yamlPath.getSuffix() + "\t  matched '"
        .. ..
        final YAMLPath cloneOfYAMLPath = YAMLPath.deepClone(_yamlPath); // to keep _yamlPath intact as we recurse in and out of sub-yaml-elements
</pre>
 *
 * @see org.ASUX.yaml.Cmd
 */
public class YAMLPath implements Serializable {

    public static final String DEFAULTDELIMITER = "\\.";
    public static final String DEFAULTPRINTDELIMITER = "\t";
    public static final String MATCHANYSINGLEPATHELEMENT = ".*";

    // Note: These constants are also duplicated into BatchFileGrammer.java
	public static final String REGEXP_NAMESUFFIX  =     "[${}@%a-zA-Z0-9\\.,:_/-]+";
	public static final String REGEXP_NAME = "[a-zA-Z$]" + REGEXP_NAMESUFFIX;
    // public static final String ROOTLEVEL = "<RootLevel:("+ REGEXP_NAME +")>";
    public static final String ROOTLEVEL = "/";

	public static final String CLASSNAME = "org.ASUX.yaml.YAMLPath";

    //------------------------------------------------------------------------------
    /** <p>Whether you want deluge of debug-output onto System.out.</p><p>Set this via the constructor.</p>
     *  <p>It's read-only (final data-attribute).</p>
     */
    public final boolean verbose;

    public boolean isValid = false;
    public final String yamlPathStr;
    public final String delimiter;
    public final String prntDelimiter;
    public String[] yamlElemArr = new String[]{"UNinitialized", "yamlElemArr"};

    protected int indexPtr = -1;

    //------------------------------------------------------------------------------
    /**
     * This Exception type is thrown exclusively by YAMLPath.java class.
     * That way, you can report better errors to the end-user
     */
    public static class YAMLPathException extends Exception {
        private static final long serialVersionUID = 10L;
        public YAMLPathException(String _s) { super(_s); }
    }

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

    /** <p>Constructor takes a YAML Path like <code>paths./pet.put.consumes</code></p>
     *  <p>It breaks it up into regexpressions separated by the default DELIMITER = "."</p>
     *  <p>Special case: <code>"**"</code> represents a deviation of java.util.regexp specs on what qualifies as a regular-expression.  This deviation is a very human-friendly easy-2-understand need.</p>
     *  @param _verbose Whether you want deluge of debug-output onto System.out
     *  @param _yp example: "<code>paths.*.*.responses.200</code>"  where the delimiter is fixed to be the period/dot "." - - <b>ATTENTION: This is a human readable pattern, NOT a proper RegExp-pattern</b>
     *  @throws org.ASUX.yaml.YAMLPath.YAMLPathException if Pattern provided for YAML-Path is either semantically empty or is NOT java.util.Pattern compatible.. .. or, invalid delimiter, etc..
     */
    public YAMLPath( final boolean _verbose, String _yp ) throws YAMLPathException {
        this( _verbose, _yp, DEFAULTDELIMITER );
    }

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

    /** <p>Constructor takes a YAML Path like <code>paths./pet.put.consumes</code></p>
     *  <p>It breaks it up into regexpressions separated by DELIMITER</p>
     *  @param _verbose Whether you want deluge of debug-output onto System.out
     *  @param _yp example: "<code>paths.*.*.responses.200</code>" - - <b>ATTENTION: This is a human readable pattern, NOT a proper RegExp-pattern</b>
     *  @param _delim examples are "." or "\t"  or "," .. ..
     *  @throws org.ASUX.yaml.YAMLPath.YAMLPathException if Pattern provided for YAML-Path is either semantically empty or is NOT java.util.Pattern compatible.. .. or, invalid delimiter, etc..
     */
    public YAMLPath( final boolean _verbose, String _yp, final String _delim ) throws YAMLPathException {
        this.verbose = _verbose;
        _yp = _yp.trim(); // strip leading and trailing whitesapce (Java11 user strip(), Java<11, use trim()
        if ( _yp.length() <= 0 )
            throw new YAMLPathException( CLASSNAME +" Constructor: semantically EMPTY Pattern (java.util.Pattern compatible) provided ["+ _yp +"]" ); // invalid YAML Path.  Let "this.isValid" stay as false
        this.delimiter = _delim;
        this.yamlPathStr = _yp; //save it
        this.prntDelimiter = DEFAULTPRINTDELIMITER; // _delim.replaceAll("\\\\", ""); // save it in human-readable form (to print out paths -- and for NO OTHER purpose)
        // System.out.println( "x\\.y".replaceAll​("\\\\", "") );

        // Sanity check of "_delim"
        try {
            Pattern p = Pattern.compile(_delim);
        }catch(PatternSyntaxException e){
            e.printStackTrace(System.err);
            System.err.println( CLASSNAME +": Constructor: Invalid delimiter-pattern '"+ _delim +"' provided to constructor of "+CLASSNAME);
            return; // invalid YAML Path.  Let "this.isValid" stay as false
        }

        if (this.verbose) System.out.println(CLASSNAME + ": Sanity check completed for yp=["+ _yp +"]" );
        //        boolean b = Pattern.matches("a*b", "aaaaab");

        if (this.verbose) System.out.println(CLASSNAME + ": about to split '"+_yp+"' with delimiter '"+_delim+"'");
        this.yamlElemArr = _yp.split(_delim);

        if (this.verbose) System.out.println(CLASSNAME + ": this.yamlElemArr has length '"+this.yamlElemArr.length+"'");
        if (this.verbose) System.out.println(CLASSNAME + ": this.yamlElemArr[0] = '"+this.yamlElemArr[0]+"'");

        for(int ix=0; ix < this.yamlElemArr.length; ix++ ) {
            String elem = this.yamlElemArr[ix];
            try {
                if (this.verbose) System.out.println(CLASSNAME+": checking on .. YAML-element '"+ elem +"'.");
                if (elem.equals("**") ) {
                    // nothing to validate, as its NOT a valid Regular-expression.  Let "**" through!
                }else {
                    if (elem.equals("*") ) {
                        elem = MATCHANYSINGLEPATHELEMENT; // convert human-friendly * into formal-regexp .*
                        this.yamlElemArr[ix] = elem;
                    }
                    if (this.verbose) System.out.println(CLASSNAME+": YAML-element='"+ this.yamlElemArr[ix] +"'.");
                    final Pattern p = Pattern.compile(elem); // not using this, but if 'elem' is invalid, exception thrown
                }
            }catch(PatternSyntaxException e){
                e.printStackTrace(System.err);
                throw new YAMLPathException( CLASSNAME+": Constructor: Invalid YAML-element '"+ elem +"' provided." );
            }
        } // for

        this.isValid = (this.yamlElemArr.length > 0) ? true : false;
        this.indexPtr = (this.yamlElemArr.length > 0) ? 0 : -1;
    } // Constructor

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

    /**
     * Ths function will ensure this object behaves as if.. you are going to call hasNext() and next() for the 1st time (on this specific instance/object).
     */
    public void rewind() {
        this.indexPtr = 0;
    }

    /** Whether the instance of this class is valid (in case you are passed this object by some other code, this is your sanity check).. .. before you invoke any of the other functions in ths class and end up with runtime errors
     *  @return true means all the methods in this class are GUARANTEED to NOT Throw any runtime exception :-)
     */
    public boolean isValid() {
        return this.isValid;
    }

    //=======================================================================
    /** For example strings like "<code>paths.*.*.responses.200</code>", your first call will return true.  If you call {@link next} <b>up to 4 times</b>, this function will return true.  After you call next() a 5th time, this function will return false.
     *  @return true means {@link get} will return a valid string, GUARANTEED to NOT Throw any runtime exception :-)
     */
    public boolean hasNext() {
        // System.out.println(CLASSNAME + ":hasNext(): Starting.");
        if ( ! this.isValid ) return false;
        if ( this.indexPtr < this.yamlElemArr.length )
            return true;
        else
            return false;
    }

    /** For example strings like "<code>paths.*.*.responses.200</code>", your <b>1st 5 invocations</b> will make this object point to valid Path-Elements (call {@link #get} to get those valid Path-Element-strings.  After you call <code>next()</code> a 6th time (for same example), this object will point to null(String) and from then onwards, {@link #get} will return null.
     */
    public void next() {
//        String retstr = null;
//        if ( this.hasNext() ) {
//            retstr = this.yamlElemArr[this.indexPtr];
//            this.indexPtr ++;
//        }
//        return retstr;
        if ( this.hasNext() ) {
            this.indexPtr ++;
        }
        return;
    }


    /** For example strings like "<code>paths.*.*.responses.200</code>", this will make this object point to the last element"<code>200</code>"".
     */
    public void skip2end() {
        if ( ! this.isValid ) return;
        // if ( this.index() >= this.yamlElemArr.length ) // perhaps we fully iterated this.hasNext() to the end .. already.
        //     this.rewind();
        // for ( int ix=index(); ix< this.yamlElemArr.length; ix++ )
        //     ypNoMatches.next(); // if we loop all the way to 'this.yamlElemArr.length' then we'll end up with this,index() pointing WELL beyond the 
        this.indexPtr = this.yamlElemArr.length - 1;
        return;
    }

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

    /** For example strings like "<code>paths.*.*.responses.200</code>", after your first call to next(), this will return "paths".  For the 2nd call to next(), this function will return "*".  After you call next() a 5th time(or more), this function will return null(String).
     *  @return a string that does NOT have periods/dots/delimiter in it.  The string may be (based on example above) = "*".
     */
    public String get() {
        if ( ! this.isValid ) return null;
        if ( this.indexPtr < this.yamlElemArr.length )
            return this.yamlElemArr[this.indexPtr];
        else
            return null;
    }

    /** For example: if the cmdline or batch-yaml files provide a YAML-Path pattern strings like "<code>paths.*.*.responses.200</code>", this function returns EXACTLY that (as-is)
     * @return String as provided to constructor
    */
    public String getRaw() {
        return this.yamlPathStr;
    }

    /** For example strings like "<code>paths.*.*.responses.200</code>", your first call will return 0 (index numbering per C/Java array-index standard).  Every call to next() will increment the return value of this function.  When you call next() a 5th/6th/7th/../100th time for above example, this function will return the same value then onwards (= # of elements in the YAMLPath-string.  In this example, that is 5)
     *  @return integer &gt;= 0 (if things are working) and -1 is things are screwed up.
     */
    public int index() {
        if ( ! this.isValid ) return -1;
        if ( this.indexPtr < this.yamlElemArr.length )
            return this.indexPtr;
        else
            return this.yamlElemArr.length;
    }

    /** For example strings like "<code>paths.*.*.responses.200</code>", before your 1st call to next(), this function will return ""(empty string).  After the 1st call to next(), this function will return "paths".  After the 2nd call to next(), this will return "paths.*".  After you call next() a 5th time (or more), this function will return "<code>paths.*.*.responses.200</code>".
     *  @return a string that does NOT have periods/dots in it.  The string may be (based on example above) = "*".
     */
    public String getPrefix() {
        if ( ! this.isValid ) return null;
        if ( this.indexPtr < this.yamlElemArr.length ) {
            String retstr = "";
            // Compiler error: local variables referenced from a lambda expression must be final or effectively final
            // IntStream.range(0, this.indexPtr).forEach(i -> retstr+= this.yamlElemArr[i]);
            final int[] range = IntStream.range(0, this.indexPtr).toArray();
            for(int ix : range )
                retstr += this.yamlElemArr[ix] + this.prntDelimiter;
            return retstr;
        }else{
            return null; // We've a problem if we're here
        }
    }

    /** For example: strings like "<code>**.xml</code>", before your 1st call to next(), this function will return true.  After the 1st call to next(), this function will return "true".  After the 2nd call .., this will return false
     *  @return whether the previous YAML-Element was "**" or Not.
     */
    public boolean hasWildcardPrefix() {
        if ( ! this.isValid ) return false;
        if ( this.indexPtr < this.yamlElemArr.length ) {
            if ( this.indexPtr <= 0 ) {
                return false; // We are the beginning of the YAML path.  So, SEMANTICALLY false!
            } else {
                if ( this.yamlElemArr[ this.indexPtr - 1 ].equals("**") )
                    return true;
                else
                    return false;
            }
        }else{
            return false; // We've a problem if we're here
        }
    }
    
    /** For example strings like "<code>paths.*.*.responses.200</code>", before your 1st call to next(), this function will return "<code>paths.*.*.responses.200</code>".  After the 1st call to next(), this function will return "<code>*.*.responses.200</code>".  After the 3rd call to next(), this will return "<code>responses.200</code>".  After you call next() a 5th time(or more), this function will return null(String).
     *  @return a string that does NOT have periods/dots in it.  The string may be (based on example above) = "*".
     */
    public String getSuffix() {
        if ( ! this.isValid ) return null;
        if ( this.indexPtr < this.yamlElemArr.length ) {
            String retstr = "";
            // Compiler error: local variables referenced from a lambda expression must be final or effectively final
            // IntStream.range(this.indexPtr, this.yamlElemArr.length).forEach(i -> retstr+= this.yamlElemArr[i]);
            int[] range = IntStream.range(this.indexPtr, this.yamlElemArr.length).skip(1).toArray();
            for(int ix : range )
                retstr += this.prntDelimiter + this.yamlElemArr[ix];
            return retstr;
        }else{
            return null; // We've a problem if we're here
        }
    }

    /**
     * Implements the Object.toString() operation .. in a superior manner for debugging.
     */
    public String toString() {
        if ( this.isValid )
            return this.getPrefix() +"\t@"+ this.index() +":"+ this.get() +"\t"+ this.getSuffix();
        else
            return "Invalid object of "+CLASSNAME;
    }

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

    /** This deepClone function is unnecessary, if you can invoke org.apache.commons.lang3.SerializationUtils.clone(this)
     *  @param _orig what you want to deep-clone
     *  @return a deep-cloned copy, created by serializing into a ByteArrayOutputStream and reading it back (leveraging ObjectOutputStream)
     */
    public static YAMLPath deepClone(YAMLPath _orig) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(_orig);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (YAMLPath) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

    /** See also {@link #equals}.
     *  This equality function is needed for efficient processing within InsertYamlProcessor.java.
     *  This function does NOT assume any common objects/strings.
     *  It does a TRUE value-based comparison.
     *  This does !!!NOT!!! tke advantage of the fact that this class is java.io.Streamable.
     *  So.. basically this function simply compares the prefix and suffixes of LHS and RHS (the underlying implementation for java.io.Streamable).
     *  By implications, if you have cloned a YAMLPath instance and called next() on the clone, both the original and clone areEquivalent===true;
     *  @param _lhs left hand side
     *  @param _rhs right hand side
     *  @return true or fale
     */
    public static boolean areEquivalent( YAMLPath _lhs, YAMLPath _rhs ) {
        if ( _lhs == null && _rhs == null ) return true;
        if ( _lhs == null || _lhs.getPrefix() == null || _lhs.getSuffix() == null ) return false;
        return _lhs.getPrefix().equals(_rhs.getPrefix()) && _lhs.getSuffix().equals(_rhs.getSuffix());
    }

    /** See also {@link #areEquivalent}.
     *  This function does NOT assume any common objects/strings.
     *  It does a TRUE value-based comparison - by taking advantage of the fact that this class is java.io.Streamable.
     *  By implications, if you have cloned a YAMLPath instance and called next() on the clone, they are NOT equal.
     *  @param _lhs left hand side
     *  @param _rhs right hand side
     *  @return true or fale
     */
    public static boolean equals( YAMLPath _lhs, YAMLPath _rhs ) {
        try {
            ByteArrayOutputStream baosLHS = new ByteArrayOutputStream();
            ObjectOutputStream oosLHS = new ObjectOutputStream(baosLHS);
            oosLHS.writeObject(_lhs);
            ByteArrayOutputStream baosRHS = new ByteArrayOutputStream();
            ObjectOutputStream oosRHS = new ObjectOutputStream(baosRHS);
            oosRHS.writeObject(_rhs);
            return java.util.Arrays.equals( baosLHS.toByteArray(), baosRHS.toByteArray() );
        } catch (IOException e) {
            return false;
        // } catch (ClassNotFoundException e) {
        //     return null;
        }
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    // /**
    //  * If the YAML-Path-pattern is exactly equal to {@link #ROOTLEVEL} then it means the YAML-command (most likely INSERT) wants to work at the very top-level YAML-element.
    //  * @return Either null or the "new Root-level Key-name", whether the user entered the value "<RootLevel:NewRootKeyName>" (exactly as defined by {@link #ROOTLEVEL}).
    //  */
    // public String atRootLevel() {
        // // return ROOTLEVEL.matches(this.yamlPathStr);
        // try {
        //     Pattern rootLevelPattern = Pattern.compile( YAMLPath.ROOTLEVEL );
        //     Matcher rootLevelMatcher    = rootLevelPattern.matcher( this.yamlPathStr );
        //     if (rootLevelMatcher.find()) {
        //         if ( this.verbose ) System.out.println( CLASSNAME +": atEndOfInput(): I found the text "+ rootLevelMatcher.group() +" starting at index "+  rootLevelMatcher.start() +" and ending at index "+ rootLevelMatcher.end() );    
        //         final String rootLevelKey = rootLevelMatcher.group(1); // line.substring( rootLevelMatcher.start(), rootLevelMatcher.end() );
        //         if ( this.verbose ) System.out.println( CLASSNAME + ": atEndOfInput(): \t rootLevelKey=[" + rootLevelKey +"]" );
        //         // rootLevelKey = MacroYamlProcessor.evaluateMacros( cmd_AsIs, ___.AllProps ).trim();
        //         // ATTENTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //         // Above evaluateMacros() invocation cannot be (and IMPORTANTLY, SHOULD NOT) done.. 
        //         // .. as we do NOT (and IMPORTANTLY, SHOULD NOT) have access to 'AllProps' like BatchYamlProcessor.java or Cmd.java do!
        //         // Instead, make BatchYamlProcess pre-process the parameters passed to this InsertYamlEntry.java
        //         return rootLevelKey;
        //     } else
        //         return null;

        // } catch (PatternSyntaxException e) {
        //     e.printStackTrace(System.err);
        //     System.err.println( CLASSNAME + ": atRootLevel("+ this.yamlPathStr +"): Unexpected Internal ERROR 1 while parsing pattern "+ YAMLPath.ROOTLEVEL );
        //     System.exit(91); // This is a serious failure. Shouldn't be happening.
        // }
        // return null;
    // }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

//    public static void main(String[] args) {
//        // System.out.println(CLASSNAME + ": started with '"+args[0]+"'");
//        YAMLPath yp = new YAMLPath(args[0]);
//        // System.out.println(CLASSNAME + ": parsing complete");
//        while (yp.hasNext()) {
//            System.out.println("@# " + yp.index() +"\t"+ yp.getPrefix() +"\t"+ yp.get() +"\t"+ yp.getSuffix() );
//            yp.next();
//        }
//    }

}
