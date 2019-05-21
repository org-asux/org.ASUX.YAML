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

import java.util.regex.*;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 *  <p>This is part of org.ASUX.yaml GitHub.com project and the <a href="https://github.com/org-asux/org-ASUX.github.io/wiki">org.ASUX.cmdline</a> GitHub.com projects.</p>
 *  <p>This class is a bunch of tools to help make it easy to work with the Configuration and Propertyfiles - while making it very human-friendly w.r.t .comments etc...</p>
 *
 * @see org.ASUX.yaml.Cmd
 * @see org.ASUX.yaml.BatchYamlProcessor
 */
public class BatchFileGrammer extends org.ASUX.common.ConfigFileScanner {

    private static final long serialVersionUID = 5L;
    public static final String CLASSNAME = "org.ASUX.yaml.BatchFileGrammer";
    public static final String FOREACH_PROPERTIES = "foreachCMD.properties";
    public static final String GLOBALVARIABLES = "GLOBAL.VARIABLES";
    public static final String SYSTEM_ENV = "System.env";

	public static final String REGEXP_INLINEVALUE = "['\" ${}@%a-zA-Z0-9\\[\\]\\.,:_/-]+";
	public static final String REGEXP_NAMESUFFIX  =     "[${}@%a-zA-Z0-9\\.,:_/-]+";
	public static final String REGEXP_NAME = "[a-zA-Z$]" + REGEXP_NAMESUFFIX;
	public static final String REGEXP_FILENAME = "[a-zA-Z$/\\.]" + REGEXP_NAMESUFFIX;
	public static final String REGEXP_OBJECT_REFERENCE = "[@!]" + REGEXP_FILENAME;

    //--------------------------------------------------------

    enum BatchCmdType { Cmd_MakeNewRoot, Cmd_Batch, Cmd_Foreach, Cmd_End, Cmd_Properties, Cmd_SetProperty, Cmd_SaveTo, Cmd_UseAsInput, Cmd_Print, Cmd_YAMLLibrary, Cmd_Verbose, Cmd_Sleep, Cmd_Any };
    private BatchCmdType whichCmd = BatchCmdType.Cmd_Any;

    private boolean bLine2bEchoed = false;
    private YAML_Libraries YAMLLibrary = YAML_Libraries.ASUXYAML_Library;

    private String saveTo = null;
    private String useAsInput = null;
    private String makeNewRoot = null;
    private String subBatchFile = null;
    private Tuple<String,String> propertiesKV = null;
    private boolean batchVerbose = false;
    private String printExpr = null;
    private int sleepDuration = 1; // === 1millisecond.  Precaution: So that by mistake we do Not end up calling sleep(0), which is forever.


    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    /** <p>The only constructor - public/private/protected</p>
     *  @param _verbose Whether you want deluge of debug-output onto System.out.
     */
    public BatchFileGrammer(boolean _verbose) {
        super ( _verbose );
    }

    private BatchFileGrammer() { super(); }


    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /** This function is exclusively for use within the go() - the primary function within this class - to make this very efficient when responding to the many isXXX() methods in this class.
     */
    protected void resetFlagsForEachLine() {
        this.whichCmd = BatchCmdType.Cmd_Any;
        this.bLine2bEchoed = false;
        this.YAMLLibrary = YAML_Libraries.ASUXYAML_Library;

        this.propertiesKV = null;
        this.printExpr = null;
        this.saveTo = null;
        this.useAsInput = null;
        this.makeNewRoot = null;
        this.subBatchFile = null;
        this.batchVerbose = false;
        this.sleepDuration = 1; // 1millisecond.  Precaution: So that by mistake we do Not end up calling sleep(0), which is forever.
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /** For use by any Processor of this batch-file.. whether the user added the 'echo' prefix to a command, requesting that that specific line/command be echoed while executing
     * @return true or false, whether the user added the 'echo' prefix to a command, requesting that that specific line/command be echoed while executing
    */
    public boolean isLine2bEchoed() {
        return this.bLine2bEchoed;
    }

    public BatchCmdType getCmdType() {
        return this.whichCmd;
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     * This method should be called after nextLine().  nextLine() is inherited from the parent {@link org.ASUX.common.ConfigFileScanner}.
     * @throws Exception in case of any errors.
     */
    public void determineCmdType() throws Exception {

        this.resetFlagsForEachLine();
        String line = this.currentLineOrNull(); // remember the line is most likely already trimmed.  We need to chop off any 'echo' prefix
        if ( this.verbose ) System.out.println( CLASSNAME +": determineCmdType("+ line +"): "+ this.getState() );

        if ( line == null )
            return;

        try {
            // ATTENTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // This block of code below (echoPattern, echoMatcher, this.bLine2bEchoed) MUST be the very BEGINNNG of this function
            Pattern echoPattern = Pattern.compile( "^\\s*echo\\s+(\\S.*\\S)\\s*$" );
            Matcher echoMatcher    = echoPattern.matcher( line );
            if (echoMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the command to be ECHO-ed '"+ echoMatcher.group(1) +"' starting at index "+  echoMatcher.start() +" and ending at index "+ echoMatcher.end() );    
                line = echoMatcher.group(1); // line.substring( echoMatcher.start(), echoMatcher.end() );
                this.bLine2bEchoed = true;
                if ( this.verbose ) System.out.println( "\t 2nd echoing Line # "+ this.getState() );
                // fall thru below.. to identify the commands
            }

            Pattern yamlLibraryPattern = Pattern.compile( "^\\s*useYAMLLibrary\\s+("+ YAML_Libraries.list("|") +")\\s*$" );
            Matcher yamlLibraryMatcher    = yamlLibraryPattern.matcher( line );
            if (yamlLibraryMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ yamlLibraryMatcher.group() +" starting at index "+  yamlLibraryMatcher.start() +" and ending at index "+ yamlLibraryMatcher.end() );    
                this.YAMLLibrary = YAML_Libraries.fromString( yamlLibraryMatcher.group(1) ); // line.substring( yamlLibraryMatcher.start(), yamlLibraryMatcher.end() );
                if ( this.verbose ) System.out.println( "\t YAMLLibrary=[" + this.YAMLLibrary +"]" );
                this.whichCmd = BatchCmdType.Cmd_YAMLLibrary;
                return;
            }

            Pattern makeNewRootPattern = Pattern.compile( "^\\s*makeNewRoot\\s+("+ REGEXP_NAME +")\\s*$" );
            Matcher makeNewRootMatcher    = makeNewRootPattern.matcher( line );
            if (makeNewRootMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ makeNewRootMatcher.group() +" starting at index "+  makeNewRootMatcher.start() +" and ending at index "+ makeNewRootMatcher.end() );    
                this.makeNewRoot = makeNewRootMatcher.group(1); // line.substring( makeNewRootMatcher.start(), makeNewRootMatcher.end() );
                if ( this.verbose ) System.out.println( "\t makeNewRoot=[" + this.makeNewRoot +"]" );
                this.whichCmd = BatchCmdType.Cmd_MakeNewRoot;
                return;
            }

            Pattern batchPattern = Pattern.compile( "^\\s*batch\\s+("+ REGEXP_FILENAME +")\\s*$" );
            Matcher batchMatcher    = batchPattern.matcher( line );
            if (batchMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ batchMatcher.group() +" starting at index "+  batchMatcher.start() +" and ending at index "+ batchMatcher.end() );    
                this.subBatchFile = batchMatcher.group(1); // line.substring( batchMatcher.start(), batchMatcher.end() );
                if ( this.verbose ) System.out.println( "\t batch=[" + this.subBatchFile +"]" );
                this.whichCmd = BatchCmdType.Cmd_Batch;
                return;
            }

			if ( line.equalsIgnoreCase( "foreach" ) ) {
				this.whichCmd = BatchCmdType.Cmd_Foreach;
                this.batchVerbose = false;  // I do Not want 'verbose on' to last OUTSIDE the loop/block in which it is specified.
				return;
			}

			if ( line.equalsIgnoreCase("end") ) {
                this.whichCmd = BatchCmdType.Cmd_End;
                this.batchVerbose = false;  // I do Not want 'verbose on' to last OUTSIDE the loop/block in which it is specified.
				return;
			}

            Pattern propsPattern = Pattern.compile( "^\\s*properties\\s+("+ REGEXP_NAME +")=("+ REGEXP_FILENAME +")\\s*$" );
            Matcher propsMatcher    = propsPattern.matcher( line );
            if (propsMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ propsMatcher.group() +" starting at index "+  propsMatcher.start() +" and ending at index "+ propsMatcher.end() );    
                this.propertiesKV = new Tuple<String,String>( propsMatcher.group(1), propsMatcher.group(2) );
                            // line.substring( propsMatcher.start(), propsMatcher.end() );
                if ( this.verbose ) System.out.println( "\t KVPair=[" + this.propertiesKV.key +","+ this.propertiesKV.val +"]" );
                this.whichCmd = BatchCmdType.Cmd_Properties;
				return;
            }

            Pattern setPropPattern = Pattern.compile( "^\\s*setProperty\\s+("+ REGEXP_NAME +")=("+ REGEXP_FILENAME +")\\s*$" );
            Matcher setPropMatcher    = setPropPattern.matcher( line );
            if (setPropMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ setPropMatcher.group() +" starting at index "+  setPropMatcher.start() +" and ending at index "+ setPropMatcher.end() );    
                this.propertiesKV = new Tuple<String,String>( setPropMatcher.group(1), setPropMatcher.group(2) );
                            // line.substring( setPropMatcher.start(), setPropMatcher.end() );
                if ( this.verbose ) System.out.println( "\t KVPair=[" + this.propertiesKV.key +","+ this.propertiesKV.val +"]" );
                this.whichCmd = BatchCmdType.Cmd_SetProperty;
				return;
            }

            Pattern printPattern = Pattern.compile( "^\\s*print\\s+(\\S.*\\S|-)\\s*$" );
            Matcher printMatcher    = printPattern.matcher( line );
            if (printMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ printMatcher.group() +" starting at index "+  printMatcher.start() +" and ending at index "+ printMatcher.end() );    
                this.printExpr  = printMatcher.group(1); // line.substring( printMatcher.start(), printMatcher.end() );
                if ( this.verbose ) System.out.println( "\t print=[" + this.printExpr +"]" );
                this.whichCmd = BatchCmdType.Cmd_Print;
                return ;
            }

            Pattern saveToPattern = Pattern.compile( "^\\s*saveTo\\s+("+ REGEXP_OBJECT_REFERENCE +")\\s*$" );
            Matcher saveToMatcher    = saveToPattern.matcher( line );
            if (saveToMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ saveToMatcher.group() +" starting at index "+  saveToMatcher.start() +" and ending at index "+ saveToMatcher.end() );    
                this.saveTo = saveToMatcher.group(1); // line.substring( saveToMatcher.start(), saveToMatcher.end() );
                if ( this.verbose ) System.out.println( "\t SaveTo=[" + this.saveTo +"]" );
                this.whichCmd = BatchCmdType.Cmd_SaveTo;
                return;
            }

            Pattern useAsInputPattern = Pattern.compile( "^\\s*useAsInput\\s+("+ REGEXP_OBJECT_REFERENCE +"|"+ REGEXP_INLINEVALUE +")\\s*$" );
            Matcher useAsInputMatcher    = useAsInputPattern.matcher( line );
            if (useAsInputMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ useAsInputMatcher.group() +" starting at index "+  useAsInputMatcher.start() +" and ending at index "+ useAsInputMatcher.end() );    
                this.useAsInput = useAsInputMatcher.group(1); // line.substring( useAsInputMatcher.start(), useAsInputMatcher.end() );
                if ( this.verbose ) System.out.println( "\t useAsInput=[" + this.useAsInput +"]" );
                this.whichCmd = BatchCmdType.Cmd_UseAsInput;
                return;
            }

            Pattern verbosePattern = Pattern.compile( "^\\s*verbose\\s+(on|off)\\s*$" );
            Matcher verboseMatcher    = verbosePattern.matcher( line );
            if (verboseMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ verboseMatcher.group() +" starting at index "+  verboseMatcher.start() +" and ending at index "+ verboseMatcher.end() );    
                this.batchVerbose = "on".equals( verboseMatcher.group(1) ); // line.substring( verboseMatcher.start(), verboseMatcher.end() );
                if ( this.verbose ) System.out.println( "\t verbose=[" + this.batchVerbose +"]" );
                this.whichCmd = BatchCmdType.Cmd_Verbose;
                return;
            }

            Pattern sleepPattern = Pattern.compile( "^\\s*sleep\\s+(\\d\\d*)\\s*$" );
            Matcher sleepMatcher    = sleepPattern.matcher( line );
            if (sleepMatcher.find()) {
                if ( this.verbose ) System.out.println( CLASSNAME +": I found the text "+ sleepMatcher.group() +" starting at index "+  sleepMatcher.start() +" and ending at index "+ sleepMatcher.end() );    
                this.sleepDuration = Integer.parseInt( sleepMatcher.group(1) ); // line.substring( sleepMatcher.start(), sleepMatcher.end() );
                if ( this.verbose ) System.out.println( "\t sleep=[" + this.sleepDuration +"]" );
                this.whichCmd = BatchCmdType.Cmd_Sleep;
                return;
            }

            return;

        } catch (PatternSyntaxException e) {
			e.printStackTrace(System.err);
			System.err.println(CLASSNAME + ": isPropertyLine(): Unexpected Internal ERROR, while checking for patterns for line= [" + line +"]" );
			System.exit(91); // This is a serious failure. Shouldn't be happening.
        }
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================
    /** This function helps detect if the current line pointed to by this.currentLine() contains a property entry (a.k.a. a KVPair entry of the form key=value)
     * @return either null.. or, the Key + Value (an instance of the Tuple class) detected in the current line of batch file
     */
    public Tuple<String,String> getPropertyKV() {
        if ( this.whichCmd == BatchCmdType.Cmd_Properties || this.whichCmd == BatchCmdType.Cmd_SetProperty )
            return this.propertiesKV; // we've already executed the code below - SPECIFICALLY for the current Line!
        else
            return null;
    }

    /** This function helps detect if the current line pointed to by this.currentLine() contains a property entry (a.k.a. a Tools.KVPair entry of the form key=value)
     * @return either null.. or, the Key + Value (an instance of the Tuple class) detected in the current line of batch file
     */
    public String getPrintExpr() {
        if ( this.whichCmd == BatchCmdType.Cmd_Print )
            return this.printExpr; 
        else
            return null;
    }

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
        if ( this.whichCmd == BatchCmdType.Cmd_Batch )
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
        // if ( this.whichCmd == BatchCmdType.Cmd_Verbose )
        //     return this.batchVerbose;
        // else
        //     return false;
    }

    /** This function helps detect if the current line pointed to by this.currentLine() contains a 'sleep ___' entry - which will cause the Batch-file-processing to take a quick nap as directed.
     *  @return String the argument provided to the 'sleep' command
     */
    public int getSleepDuration() {
        if ( this.whichCmd == BatchCmdType.Cmd_Sleep )
            return this.sleepDuration;
        else
            return 1; // === 1millisecond.  Precaution: So that by mistake we do Not end up calling sleep(0), which is forever.
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
            // scanner.hasNext() only throws a RUNTIMEEXCEPTION: IllegalStateException - if this scanner is closed
            // scanner.next() only throws a RUNTIMEEXCEPTION: NoSuchElementException - if no more tokens are available
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.println(CLASSNAME + ": getCommand(): Unexpected Internal ERROR, while checking for patterns for "+ this.getState() );
            return null;
        }
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /** This deepClone function is unnecessary, if you can invoke org.apache.commons.lang3.SerializationUtils.clone(this)
     *  @param _orig what you want to deep-clone
     *  @return a deep-cloned copy, created by serializing into a ByteArrayOutputStream and reading it back (leveraging ObjectOutputStream)
     */
    public static BatchFileGrammer deepClone(BatchFileGrammer _orig) {
        final BatchFileGrammer newobj = (BatchFileGrammer) org.ASUX.common.ConfigFileScanner.deepClone(_orig);
        return newobj;
        // try {
        //     ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //     ObjectOutputStream oos = new ObjectOutputStream(baos);
        //     oos.writeObject(_orig);
            
        //     ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        //     ObjectInputStream ois = new ObjectInputStream(bais);
        //     final BatchFileGrammer newobj = (BatchFileGrammer) ois.readObject();

        //     // because this class has at least one TRANSIENT class-variable.. ..
        //     newobj.reset();
 
        //     return newobj;

        // } catch (java.io.IOException e) {
		// 	e.printStackTrace(System.err);
        //     return null;
        // } catch (ClassNotFoundException e) {
		// 	e.printStackTrace(System.err);
        //     return null;
        // }
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================
    // For unit-testing purposes only
    public static void main(String[] args) {
        try {
            final BatchFileGrammer o = new BatchFileGrammer(true);
            o.openFile( args[0], true, false );
            while (o.hasNextLine()) {
                System.out.println(o.nextLine());
                o.getState();

                o.isLine2bEchoed();
                o.getPropertyKV();
                o.getPrintExpr();
                // final Tuple kv = o.isPropertyLine(); // could be null, implying NOT a kvpair

                o.isForEachLine();
                o.isEndLine();
                o.getSaveTo();
                o.getUseAsInput();
                o.getMakeNewRoot();
                o.getSubBatchFile();
                o.getSleepDuration();
                final boolean bForEach = o.isForEachLine();
                if ( bForEach ) System.out.println("\t Loop begins=[" + bForEach + "]");
                final boolean bEndLine = o.isEndLine();
                if ( bEndLine ) System.out.println("\t Loop ENDS=[" + bEndLine + "]");

                o.getCommand();
            }
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.println( CLASSNAME + ": main(): Unexpected Internal ERROR, while processing " + ((args==null || args.length<=0)?"[No CmdLine Args":args[0]) +"]" );
			System.exit(91); // This is a serious failure. Shouldn't be happening.
        }
    }

}
