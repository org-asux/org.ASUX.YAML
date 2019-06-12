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

import org.ASUX.common.Tuple;
import org.ASUX.common.Utils;

import java.util.regex.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Properties;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import static org.junit.Assert.*;

/**
 *  <p>This is part of org.ASUX.yaml GitHub.com project and the <a href="https://github.com/org-asux/org-ASUX.github.io/wiki">org.ASUX.cmdline</a> GitHub.com projects.</p>
 *  <p>This class is used by org.ASUX.YAML.NodeImpl.BatchYamlProcessor and by org.ASUX.YAML.CollectionImpl.BatchYamlProcessor</p>
 * <p>This 
 *
 * @see org.ASUX.yaml.Cmd
 * @see org.ASUX.common.ConfigFileScanner
 */
public class BatchFileGrammer extends org.ASUX.common.ScriptFileScanner {

    private static final long serialVersionUID = 5L;
    public static final String CLASSNAME = "org.ASUX.yaml.BatchFileGrammer";

    public static final String FOREACH_PROPERTIES = "foreach_loop.properties";

    public static final String REGEXP_YAMLLIBRARY = "^\\s*useYAMLLibrary\\s+("+ YAML_Libraries.list("|") +")\\s*$";
    public static final String REGEXP_MKNEWROOT = "^\\s*makeNewRoot\\s+("+ REGEXP_NAME +")\\s*$";
    public static final String REGEXP_BATCH = "^\\s*batch\\s+("+ REGEXP_FILENAME +")\\s*$";
    public static final String REGEXP_SAVETO = "^\\s*saveTo\\s+("+ REGEXP_OBJECT_REFERENCE +")\\s*$";
    public static final String REGEXP_USEASINPUT = "^\\s*useAsInput\\s+("+ REGEXP_OBJECT_REFERENCE +"|"+ REGEXP_INLINEVALUE +")\\s*$";
    public static final String REGEXP_VERBOSE = "^\\s*verbose\\s+(on|off)\\s*$";
    public static final String REGEXP_PRINTDASH = "^\\s*print\\s+[-]\\s*$";

    //--------------------------------------------------------

    public enum BatchCmdType { Cmd_MakeNewRoot, Cmd_SubBatch, Cmd_Foreach, Cmd_End, Cmd_SaveTo, Cmd_UseAsInput, Cmd_YAMLLibrary, Cmd_Verbose, Cmd_PrintDash, Cmd_Any };
    private BatchCmdType whichCmd = BatchCmdType.Cmd_Any;

    private YAML_Libraries YAMLLibrary = YAML_Libraries.ASUXYAML_Library;

    private String saveTo = null;
    private String useAsInput = null;
    private String makeNewRoot = null;
    private String subBatchFile = null;
    private boolean batchVerbose = false;

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    /** <p>The basic constructor - that does __NOT__ allow you to evaluate Macro-expressions like ${XYZ}</p>
     *  @param _verbose Whether you want deluge of debug-output onto System.out.
     *  @param _propsSet a REFERENCE to an instance of LinkedHashMap, whose object-lifecycle is maintained by some other class (as in, creating new LinkedHashMap&lt;&gt;(), putting content into it, updating content as File is further processed, ..)
     */
    public BatchFileGrammer( boolean _verbose, final LinkedHashMap<String,Properties> _propsSet ) {
        super( _verbose, _propsSet );
    }

    /**
     * Since super-classes are abstract classes, parse() line - when it encounters a 'include @filename' - needs to invoke a constructor that creates a 2nd object.
     * @return an object of the subclass of this ConfigFileScannerL2.java
     */
    @Override
    protected BatchFileGrammer   create() {
        final BatchFileGrammer newobj = new BatchFileGrammer( this.verbose, super.allProps );
        return newobj;
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /** This function is exclusively for use within the go() - the primary function within this class - to make this very efficient when responding to the many isXXX() methods in this class.
     */
    @Override
    protected void resetFlagsForEachLine() {
        super.resetFlagsForEachLine();

        this.whichCmd = BatchCmdType.Cmd_Any;
        this.YAMLLibrary = YAML_Libraries.ASUXYAML_Library;

        this.saveTo = null;
        this.useAsInput = null;
        this.makeNewRoot = null;
        this.subBatchFile = null;
        this.batchVerbose = false;
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    // /**
    //  *  <p>This method is used to simply tell whether 'current-line' matches the REGEXP patterns that parseLine() will be processing 'internally' within this class</p>
    //  *  <p>In this class, those would be the REGEXP for 'print ...' and 'include @...'</p>
    //  *  @param nextLn current line or 'peek-forward' line
    //  *  @return true if the line will be processed 'internally'
    //  */
    // @Override
    // protected boolean isBuiltInCommand( final String nextLn )
    // {   final String HDR = CLASSNAME +": isBuiltInCommand(): ";

    //     if ( super.isBuiltInCommand(nextLn) )
    //         return true;

    //     if ( nextLn == null ) return false;
    //     final String noprefix = removeEchoPrefix( nextLn );
    //     if ( this.verbose ) System.out.println( HDR +"noprefix="+ noprefix );
    //     final boolean retb = noprefix.matches( REGEXP_YAMLLIBRARY ) || noprefix.matches( REGEXP_MKNEWROOT ) || noprefix.matches( REGEXP_BATCH )
    //                         || noprefix.matches( REGEXP_SAVETO ) || noprefix.matches( REGEXP_USEASINPUT ) || noprefix.matches( REGEXP_VERBOSE )
    //                         || noprefix.matches( REGEXP_PRINTDASH );
    //     if ( this.verbose ) System.out.println( HDR +" whether (y/n)="+ retb );

    //     !!!!!!!!! ATTENTION !!!!!!!!!!!! The above are NOT Built-In commands.  In identifyLine() method below, we only identify, we do NOT actually implement the command
    //     return false;
    // }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    public BatchCmdType getCmdType() {
        return this.whichCmd;
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /** Thie method overrides the parent/super class method {@link org.ASUX.common.ConfigFileScannerL2#nextLine()}
     *  @return for scripts that end in PRINT command, this returns null. Otherwise, Returns the next string in the list of lines.
     *  @throws Exception in case this class is messed up or hasNextLine() is false or has Not been invoked appropriately
     */
    @Override
    public String nextLine() throws Exception
    {   // !!!!!!!!!!!!!!!!!!!!!! OVERRIDES Parent Method !!!!!!!!!!!!!!!!!!!!!!!!
        final String nextLn = super.nextLine();
        this.identifyLine();
        return nextLn; // could also be: return this.currentLine();
    }

    /** Thie method overrides the parent/super class method {@link org.ASUX.common.ConfigFileScanner#nextLineOrNull()}
     *  @return either null (graceful failure) or the next string in the list of lines
     */
    @Override
    public String nextLineOrNull()
    {   final String HDR = CLASSNAME +": nextLineOrNull(): ";
        final String nextLn = super.nextLineOrNull();
        try {
            this.identifyLine();
        } catch (Exception e) {
            // since we shouldn't be getting this error, but .. as it's not the end of the world.. let's dump error on the user and return NULL.
            e.printStackTrace(System.out);
            System.out.println( "\n\n"+ HDR + " Unexpected Internal ERROR @ " + this.getState() +"." );
            return null;
        }
        return nextLn; // could also be: return this.currentLine();
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
    /** <p>New Method added to this subclass.  Implement your command parsing and do as appropriate.</p>
     *  <p>ATTENTION: Safely assume that any 'echo' prefix parsing and any 'print' parsing has happened already in a TRANSAPARENT way.</p>
     *  <p>This method is automatically invoked _WITHIN_ nextLine().  nextLine() is inherited from the parent {@link org.ASUX.common.ScriptFileScanner}.</p>
     *  @throws Exception in case of any errors.
     */
    protected void identifyLine() throws Exception
    {
        final String HDR = CLASSNAME +": parseLine(): ";

        String line = this.currentLineOrNull(); // remember the line is most likely already trimmed.  We need to chop off any 'echo' prefix
        if ( this.verbose ) System.out.println( HDR +": line=("+ line +")\t"+ this.getState() );
        assertTrue ( line != null );

        try {

            Pattern yamlLibraryPattern = Pattern.compile( REGEXP_YAMLLIBRARY );
            Matcher yamlLibraryMatcher    = yamlLibraryPattern.matcher( line );
            if (yamlLibraryMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ yamlLibraryMatcher.group() +" starting at index "+  yamlLibraryMatcher.start() +" and ending at index "+ yamlLibraryMatcher.end() );    
                this.YAMLLibrary = YAML_Libraries.fromString( yamlLibraryMatcher.group(1) ); // line.substring( yamlLibraryMatcher.start(), yamlLibraryMatcher.end() );
                if ( this.verbose ) System.out.println( "\t YAMLLibrary=[" + this.YAMLLibrary +"]" );
                this.whichCmd = BatchCmdType.Cmd_YAMLLibrary;
                return; // unlike all super-classes, we only 'flag' batch-file commands that this class recognizes.  we do NOT process that line.  Hence return 'false'
            }

            Pattern makeNewRootPattern = Pattern.compile( REGEXP_MKNEWROOT );
            Matcher makeNewRootMatcher    = makeNewRootPattern.matcher( line );
            if (makeNewRootMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ makeNewRootMatcher.group() +" starting at index "+  makeNewRootMatcher.start() +" and ending at index "+ makeNewRootMatcher.end() );    
                this.makeNewRoot = makeNewRootMatcher.group(1); // line.substring( makeNewRootMatcher.start(), makeNewRootMatcher.end() );
                if ( this.verbose ) System.out.println( "\t makeNewRoot=[" + this.makeNewRoot +"]" );
                this.whichCmd = BatchCmdType.Cmd_MakeNewRoot;
                return; // unlike all super-classes, we only 'flag' batch-file commands that this class recognizes.  we do NOT process that line.  Hence return 'false'
            }

            Pattern batchPattern = Pattern.compile( REGEXP_BATCH );
            Matcher batchMatcher    = batchPattern.matcher( line );
            if (batchMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ batchMatcher.group() +" starting at index "+  batchMatcher.start() +" and ending at index "+ batchMatcher.end() );    
                this.subBatchFile = batchMatcher.group(1); // line.substring( batchMatcher.start(), batchMatcher.end() );
                if ( this.verbose ) System.out.println( "\t batch=[" + this.subBatchFile +"]" );
                this.whichCmd = BatchCmdType.Cmd_SubBatch;
                return; // unlike all super-classes, we only 'flag' batch-file commands that this class recognizes.  we do NOT process that line.  Hence return 'false'
            }

			if ( line.equalsIgnoreCase( "foreach" ) ) {
				this.whichCmd = BatchCmdType.Cmd_Foreach;
                this.batchVerbose = false;  // I do Not want 'verbose on' to last OUTSIDE the loop/block in which it is specified.
				return; // unlike all super-classes, we only 'flag' batch-file commands that this class recognizes.  we do NOT process that line.  Hence return 'false'
			}

			if ( line.equalsIgnoreCase("end") ) {
                this.whichCmd = BatchCmdType.Cmd_End;
                this.batchVerbose = false;  // I do Not want 'verbose on' to last OUTSIDE the loop/block in which it is specified.
				return; // unlike all super-classes, we only 'flag' batch-file commands that this class recognizes.  we do NOT process that line.  Hence return 'false'
			}

            Pattern saveToPattern = Pattern.compile( REGEXP_SAVETO );
            Matcher saveToMatcher    = saveToPattern.matcher( line );
            if (saveToMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ saveToMatcher.group() +" starting at index "+  saveToMatcher.start() +" and ending at index "+ saveToMatcher.end() );    
                this.saveTo = saveToMatcher.group(1); // line.substring( saveToMatcher.start(), saveToMatcher.end() );
                if ( this.verbose ) System.out.println( "\t SaveTo=[" + this.saveTo +"]" );
                this.whichCmd = BatchCmdType.Cmd_SaveTo;
                return; // unlike all super-classes, we only 'flag' batch-file commands that this class recognizes.  we do NOT process that line.  Hence return 'false'
            }

            Pattern useAsInputPattern = Pattern.compile( REGEXP_USEASINPUT );
            Matcher useAsInputMatcher    = useAsInputPattern.matcher( line );
            if (useAsInputMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ useAsInputMatcher.group() +" starting at index "+  useAsInputMatcher.start() +" and ending at index "+ useAsInputMatcher.end() );    
                this.useAsInput = useAsInputMatcher.group(1); // line.substring( useAsInputMatcher.start(), useAsInputMatcher.end() );
                if ( this.verbose ) System.out.println( "\t useAsInput=[" + this.useAsInput +"]" );
                this.whichCmd = BatchCmdType.Cmd_UseAsInput;
                return; // unlike all super-classes, we only 'flag' batch-file commands that this class recognizes.  we do NOT process that line.  Hence return 'false'
            }

            Pattern verbosePattern = Pattern.compile( REGEXP_VERBOSE );
            Matcher verboseMatcher    = verbosePattern.matcher( line );
            if (verboseMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ verboseMatcher.group() +" starting at index "+  verboseMatcher.start() +" and ending at index "+ verboseMatcher.end() );    
                this.batchVerbose = "on".equals( verboseMatcher.group(1) ); // line.substring( verboseMatcher.start(), verboseMatcher.end() );
                if ( this.verbose ) System.out.println( "\t verbose=[" + this.batchVerbose +"]" );
                this.whichCmd = BatchCmdType.Cmd_Verbose;
                return; // unlike all super-classes, we only 'flag' batch-file commands that this class recognizes.  we do NOT process that line.  Hence return 'false'
            }

            Pattern printDashPattern = Pattern.compile( REGEXP_PRINTDASH );
            Matcher printDashMatcher    = printDashPattern.matcher( line );
            if (printDashMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ printDashMatcher.group() +" starting at index "+  printDashMatcher.start() +" and ending at index "+ printDashMatcher.end() );    
                if ( this.verbose ) System.out.println( "\t 'print -'" );
                this.whichCmd = BatchCmdType.Cmd_PrintDash;
                return; // unlike all super-classes, we only 'flag' batch-file commands that this class recognizes.  we do NOT process that line.  Hence return 'false'
            }

            if ( this.verbose ) System.out.println( HDR +" Oh! oh! oh! oh! oh! oh! oh! oh! oh! Unknown command=("+ line +")\t"+ this.getState() );
            // If we're here.. it means, This class did NOT process the current line

        } catch (PatternSyntaxException e) {
			e.printStackTrace(System.err); // too serious an internal-error.  Immediate bug-fix required.  The application/Program will exit .. in 2nd line below.
			System.err.println(CLASSNAME + ": isPropertyLine(): Unexpected Internal ERROR, while checking for patterns for line= [" + line +"]" );
			System.exit(91); // This is a serious failure. Shouldn't be happening.
        }
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /** This function helps detect if the current line pointed to by this.currentLine() contains a 'saveTo ___' entry
     *  @return String the argument provided to the saveTo command
     */
    public String getSaveTo() {
        if ( this.whichCmd == BatchCmdType.Cmd_SaveTo)
            return this.saveTo;
        else
            return null;
    }

    /** This function helps detect if the current line pointed to by this.currentLine() contains a 'useAsInput ___' entry
     *  @return String the argument provided to the useAsInput command
     */
    public String getUseAsInput() {
        if ( this.whichCmd == BatchCmdType.Cmd_UseAsInput )
            return this.useAsInput;
        else
            return null;
    }

    /** This function helps detect if the current line pointed to by this.currentLine() contains a 'makeNewRoot ___' entry
     *  @return String the argument provided to the makeNewRoot command
     */
    public String getMakeNewRoot() {
        if ( this.whichCmd == BatchCmdType.Cmd_MakeNewRoot )
            return this.makeNewRoot;
        else
            return null;
    }

    /** This function helps detect if the current line pointed to by this.currentLine() contains a 'batch ___' entry - which will cause a SUB-BATCH cmd to be triggered
     *  @return String the argument provided to the Batch command 
     */
    public String getSubBatchFile() {
        if ( this.whichCmd == BatchCmdType.Cmd_SubBatch )
            return this.subBatchFile;
        else
            return null;
    }

    /**
     * Tells you what internal implementation of the YAML read/parsing is, and by implication what the internal implementation for YAML-output generation is.
     * @return a reference to the YAML Library in use. See {@link YAML_Libraries} for legal values.
     */
    public YAML_Libraries getYAMLLibrary() {
        return this.YAMLLibrary;
    }

    /** This function helps detect if the current line pointed to by this.currentLine() contains a 'sleep ___' entry - which will cause the Batch-file-processing to take a quick nap as directed.
     *  @return String the argument provided to the 'sleep' command
     */
    public boolean getVerbose() {
        return this.batchVerbose || this.verbose;
    }

    //==================================
    /** This function helps detect if the current line pointed to by this.currentLine() contains just the word 'foreach' (nothing else other than comments and whitespace)
     * This keyword 'foreach' indicates the beginning of a looping-construct within the batch file.
     * @return true of false, if 'foreach' was detected in the current line of batch file
     */
    public boolean isForEachLine() {
        if ( this.whichCmd == BatchCmdType.Cmd_Foreach )
            return true;
        else
            return false;
    }

    /** This function helps detect if the current line pointed to by this.currentLine() contains just the word 'end' (nothing else other than comments and whitespace)
     * This keyword 'end' indicates the END of the looping-construct within the batch file
     * @return true of false, if 'end' was detected in the current line of batch file
     */
    public boolean isEndLine() {
        if ( this.whichCmd == BatchCmdType.Cmd_End )
            return true;
        else
            return false;
    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /**
     * This function should be called *AFTER* all the various is___() functions/methods have been called.
     * This function should NOT be called BEFORE isSaveToLine() and isUseAsInputLine(), as this function will get you confused.
     * @return String just for the command (whether 'yaml' 'aws' ..)
     */
    public String getCommand() {
        if ( this.whichCmd != BatchCmdType.Cmd_Any )
            return null; // Since.. It is one of the above commands like: properties, saveAs, foreach, end, useAsInput, makeNewRoot, .. ..

        try {
            final java.util.Scanner scanner = new java.util.Scanner( this.currentLine() );
            scanner.useDelimiter("\\s+");

            if (scanner.hasNext()) { // default whitespace delimiter used by a scanner
                final String cmd = scanner.next();
                if ( this.verbose ) System.out.println( CLASSNAME + ": getCommand(): \t Command=[" + cmd +"]" );
                scanner.close();
                return cmd;
            } // if

            scanner.close();
            return null;
		} catch (Exception e) {
            // scanner.hasNext() only throws a RUNTIMEEXCEPTION: IllegalStateException - if this scanner is closed
            // scanner.next() only throws a RUNTIMEEXCEPTION: NoSuchElementException - if no more tokens are available
			if ( this.verbose ) e.printStackTrace(System.err);
			System.err.println(CLASSNAME + ": getCommand(): Unexpected Internal ERROR, while checking for patterns for "+ this.getState() +".  Re-run this, this time using --verbose command-line option.\n Exception message: "+ e );
            return null;
        }
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /** This deepClone function is VERY MUCH necessary, as No cloning-code can handle 'transient' variables in this class/superclass.
     *  @param _orig what you want to deep-clone
     *  @return a deep-cloned copy, created by serializing into a ByteArrayOutputStream and reading it back (leveraging ObjectOutputStream)
     */
    public static BatchFileGrammer deepClone( final BatchFileGrammer _orig ) {
        try {
            final BatchFileGrammer newobj = Utils.deepClone( _orig );
            newobj.deepCloneFix();
            return newobj;
        } catch (Exception e) {
			e.printStackTrace(System.err); // Static Method. So.. can't avoid dumping this on the user.
            return null;
        }
    }

    // /**
    //  * In order to allow deepClone() to work seamlessly up and down the class-hierarchy.. I should allow subclasses to EXTEND (Not semantically override) this method.
    //  */
    // protected void deepCloneFix() {
    //         // UNLIKE SUPER-Class .. this CLASS DOES NOT __ANY__ TRANSIENT class-variable.. ..
    // }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================
    // For unit-testing purposes only
    public static void main(String[] args) {
        try {
            final BatchFileGrammer o = new BatchFileGrammer(true, new LinkedHashMap<String,Properties>() );
            o.openFile( args[0], true, false );
            while (o.hasNextLine()) {
                System.out.println(o.nextLine());
                o.getState();

                o.isLine2bEchoed();

                o.isForEachLine();
                o.isEndLine();
                o.getSaveTo();
                o.getUseAsInput();
                o.getMakeNewRoot();
                o.getSubBatchFile();
                final boolean bForEach = o.isForEachLine();
                if ( bForEach ) System.out.println("\t Loop begins=[" + bForEach + "]");
                final boolean bEndLine = o.isEndLine();
                if ( bEndLine ) System.out.println("\t Loop ENDS=[" + bEndLine + "]");

                o.getCommand();
            }
		} catch (Exception e) {
			e.printStackTrace(System.err); // main() for unit testing
			System.err.println( CLASSNAME + ": main(): Unexpected Internal ERROR, while processing " + ((args==null || args.length<=0)?"[No CmdLine Args":args[0]) +"]" );
			System.exit(91); // This is a serious failure. Shouldn't be happening.
        }
    }

}
