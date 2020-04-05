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

import org.ASUX.common.Macros;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 *  <p>This concrete class is part of a set of 4 concrete sub-classes (representing YAML-COMMANDS to read/query, list, delete and replace ).</p>
 *  <p>This class contains implementation batch-processing of multiple YAML commands (combinations of read, list, delete, replace, macro commands)</p>
 *  <p>This org.ASUX.yaml GitHub.com project and the <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com projects.</p>
 *  <p>See full details of how to use this, in {@link org.ASUX.yaml.CmdInvoker} as well as the <a href="https://github.com/org-asux/org-ASUX.github.io/wiki">org.ASUX.cmdline</a> GitHub.com projects.</p>
 */
 // *  @param T either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node .. or.. EsotericSoftware Library's preference for LinkedHashMap&lt;String,Object&gt;
public abstract class BatchCmdProcessor<T> {

    public static final String CLASSNAME = BatchCmdProcessor.class.getName();

    public static final String FOREACH_PROPERTIES = "foreach_loop.properties";

    public static final String FOREACH_INDEX        = "foreach.index"; // which iteration # (Int) are we in within the loop.
    public static final String FOREACH_INDEX_PLUS1  = "foreach.index+1";
    public static final String FOREACH_ITER_KEY     = "foreach.iteration.key"; // if 'foreach' ends up iterating over an array of strings, then you can get each string's value this way.
    public static final String FOREACH_ITER_VALUE   = "foreach.iteration.value"; // if 'foreach' ends up iterating over an array of strings, then you can get each string's value this way.

    // I prefer a LinkedHashMap over a plain HashMap.. as it can help with future enhancements like Properties#1, #2, ..
    // That is being aware of Sequence in which Property-files are loaded.   Can't do that with HashMap
    protected LinkedHashMap<String,Properties> allProps = BatchCmdProcessor.initProperties();

    /**
     * <p>Keep a copy of the command-line arguments provided by the user to run this Batch command.</p>
     * <p>In the scenario, where a batch command invokes another batch-command, use this to find out what attributes were passed on.</p>
     */
    protected CmdLineArgsCommon cmdLineArgs;

    // /** <p>Whether you want deluge of debug-output onto System.out.</p><p>Set this via the constructor.</p>
    //  *  <p>It's read-only (final data-attribute).</p>
    //  */
    // protected boolean verbose;
    //
    // /** <p>Whether you want a final SHORT SUMMARY onto System.out.</p><p>a summary of how many matches happened, or how many entries were affected or even a short listing of those affected entries.</p>
    //  */
    // public final boolean showStats;
    //
    // /**
    //  * @see org.ASUX.yaml.Enums
    //  */
    // public final Enums.ScalarStyle quoteType;
    // /**
    //  * True if we pretent no internet-access is available, and we use 'cached' AWS-SDK responses - if available.
    //  */
    // public final boolean offline;

    protected int runcount = 0;
    protected java.util.Date startTime = null;
    protected java.util.Date endTime = null;

    protected MemoryAndContext memoryAndContext = null;

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================
    //  *  @param _verbose Whether you want deluge of debug-output onto System.out.
    //  *  @param _showStats Whether you want a final summary onto console / System.out
    //  *  @param _offline true if we pretent no internet-access is available, and we use 'cached' AWS-SDK responses - if available.
    //  *  @param _quoteType one the values as defined in {@link org.ASUX.yaml.Enums} Enummeration
    /** <p>The only constructor - public/private/protected</p>
     * @param _cmdLineArgs NotNull instance of the command-line arguments passed in by the user.
     */
    // public BatchCmdProcessor( final boolean _verbose, final boolean _showStats, final boolean _offline, final Enums.ScalarStyle _quoteType ) {
    public BatchCmdProcessor( final CmdLineArgsCommon _cmdLineArgs ) {
        this.cmdLineArgs = _cmdLineArgs;
        // this.verbose = _verbose;
        // this.showStats = _showStats;
        // this.quoteType = _quoteType;
        // this.offline = _offline;

        // this.allProps.put( FOREACH_PROPERTIES, new Properties() );
        // this.allProps = org.ASUX.common.OSScriptFileScanner.initProperties( this.allProps );
        // this.allProps.put( org.ASUX.common.ScriptFileScanner.GLOBALVARIABLES, new Properties() );
        // this.allProps.put( org.ASUX.common.ScriptFileScanner.SYSTEM_ENV, System.getProperties() );

        // if ( this.cmdLineArgs.verbose ) new Debug(this.cmdLineArgs.verbose).printAllProps(" >>> ", this.allProps);
    }

    // private BatchCmdProcessor() { this.cmdLineArgs.verbose = false;    this.cmdLineArgs.showStats = true; } // Do Not use this.

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    public void setMemoryAndContext( final MemoryAndContext _mnc ) {
        this.memoryAndContext = _mnc;
        // this.memoryAndContext.setAllPropsRef( this.allProps );   <-- it should be the other way around (as in, next line)
        this.allProps = _mnc.getAllPropsRef();
        if ( this.allProps == null ) {
            this.allProps = BatchCmdProcessor.initProperties();
            this.memoryAndContext.setAllPropsRef( this.allProps );
        } else {
            this.allProps = BatchCmdProcessor.initProperties( this.allProps );
        }

    }

    //------------------------------------------------------------------------------
    public static class BatchFileException extends Exception {
        public static final long serialVersionUID = 391L;
        public BatchFileException(String _s) { super(_s); }
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     *  <p>Creates a well-initialized list of java.util.Properties objects, for use by Operating-System-linked OSScriptFileScanner or it's subclasses.</p>
     *  <p>Currently, the list is augmented by adding just one new Properties object labelled {@link #FOREACH_PROPERTIES}</p>
     *  <p>If the instance passed in as argument to this method _ALREADY_ has a Property object labelled {@link #FOREACH_PROPERTIES}, then no action is taken.</p>
     *  @param _allProps a NotNull instance (else NullPointerException is thrown)
     *  @return a NotNull object
     */
    public static LinkedHashMap<String,Properties> initProperties( final LinkedHashMap<String,Properties> _allProps ) {
        final Properties existing = _allProps.get( FOREACH_PROPERTIES );
        if ( existing == null )
            _allProps.put( FOREACH_PROPERTIES, new Properties() );
        return _allProps;
    }

    /**
     *  <p>Creates a well-initialized list of java.util.Properties objects, for use by Operating-System-linked OSScriptFileScanner or it's subclasses.</p>
     *  <p>Currently, the list is augmented by adding just one new Properties object labelled {@link #FOREACH_PROPERTIES}</p>
     *  @return a NotNull object
     */
    public static LinkedHashMap<String,Properties> initProperties() {
        LinkedHashMap<String,Properties> allProps = org.ASUX.common.OSScriptFileScanner.initProperties();
        // _allProps.put( org.ASUX.common.ScriptFileScanner.SYSTEM_ENV, System.getProperties() );
        allProps = BatchCmdProcessor.initProperties( allProps );
        return allProps;
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     * Because this class is a Generic&lt;T&gt;, compiler (for good reason) will Not allow me to type 'o instanceof T'.  Hence I am delegating this simple condition-check to the sub-classes.
     * @param o the object to check if it is an instance of T
     * @return true if 'o instanceof T' else false.
     */
    protected abstract boolean instanceof_YAMLImplClass( Object o );

    /**
     *  For SnakeYAML Library based subclass of this, simply return 'NodeTools.Node2YAMLString(tempOutput)'.. or .. for EsotericSoftware.com-based LinkedHashMap-based library, simply return 'tools.Map2YAMLString(tempOutputMap)'
     *  @param _o (nullable) either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node ( as generated by SnakeYAML library).. or.. EsotericSoftware Library's preference for LinkedHashMap&lt;String,Object&gt;, -- in either case, this object contains the entire Tree representing the YAML file.
     *  @return a Non-Null String or throws an exception
     *  @throws Exception Any issue whatsoever when dealing with convering YAML/JSON content into Strings
     */
    protected abstract String toStringDebug( Object _o ) throws Exception;

    /**
     *  For SnakeYAML Library based subclass of this, simply return 'NodeTools.getEmptyYAML( this.dumperoptions )' .. or .. for EsotericSoftware.com-based LinkedHashMap-based library, simply return 'new LinkedHashMap&lt;&gt;()'
     *  @return either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node ( as generated by SnakeYAML library).. or.. EsotericSoftware Library's preference for LinkedHashMap&lt;String,Object&gt;, -- in either case, this object contains the entire Tree representing the YAML file.
     */
    protected abstract T getEmptyYAML();

    /**
     *  If any of the Read/List/Replace/Table/Batch commands returned "Empty YAML" (assuming the code retured {@link #getEmptyYAML()}), this is your SIMPLEST way of checking if the YAML is empty.
     *  @param _n Nullable value
     *  @return true if the YAML is is NULL - or - is "empty"  as in, it is ===  what's returned by {@link #getEmptyYAML()})
     */
    protected abstract boolean isEmptyYAML( final T _n );

    /**
     *  For SnakeYAML Library based subclass of this, simply return 'NodeTools.getNewSingleMap( newRootElem, "", this.dumperoptions )' .. or .. for EsotericSoftware.com-based LinkedHashMap-based library, simply return 'new LinkedHashMap&lt;&gt;.put( newRootElem, "" )'
     *  @param _newRootElemStr the string representing 'lhs' in "lhs: rhs" single YAML entry
     *  @param _valElemStr the string representing 'rhs' in "lhs: rhs" single YAML entry
     *  @param _quoteType an enum value - see {@link Enums.ScalarStyle}
     *  @return either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node ( as generated by SnakeYAML library).. or.. EsotericSoftware Library's preference for LinkedHashMap&lt;String,Object&gt;, -- in either case, this object contains the entire Tree representing the YAML file.
     *  @throws Exception implementation can potentially throw an Exception
     */
    protected abstract T getNewSingleYAMLEntry( final String _newRootElemStr, final String _valElemStr, final Enums.ScalarStyle _quoteType ) throws Exception;

    /**
     * For SnakeYAML-based subclass of this, simply return 'NodeTools.deepClone( _node )' .. or .. for EsotericSoftware.com-based LinkedHashMap-based library, return ''
     * @param _node A Not-Null instance of either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node ( as generated by SnakeYAML library).. or.. EsotericSoftware Library's preference for LinkedHashMap&lt;String,Object&gt;, -- in either case, this object contains the entire Tree representing the YAML file.
     * @return full deep-clone (Not-Null)
     *  @throws Exception Any issue whatsoever when dealing with cloning, incl. Streamable amd other errors
     */
    protected abstract T deepClone( T _node ) throws Exception;

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================
    /** This is the entry point for this class, with the appropriate TRY-CATCH taken care of, hiding the innumerable exception types.
     *  @param _batchFileName batchfile full path (ry to avoid relative paths)
     *  @param _node either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node ( as generated by SnakeYAML library).. or.. EsotericSoftware Library's preference for LinkedHashMap&lt;String,Object&gt;, -- in either case, this object contains the entire Tree representing the YAML file.
     *  @return a BLANK/EMPTY/NON-NULL org.yaml.snakeyaml.nodes.Node object, as generated by SnakeYAML library and you'll get the final Map output representing all processing done by the batch file
     *  @throws Exception any exception while processing the command(s) within the batchfile
     */
    public T go( String _batchFileName, final T _node ) throws Exception
    {
        assertNotNull(_batchFileName);
        assertNotNull(_node);

        final String HDR = CLASSNAME +": go(_batchFileName="+ _batchFileName +","+ _node.getClass().getName() +"): ";
        // if ( _batchFileName == null || _node == null )
        //     return getEmptyYAML();  // null (BatchFile) is treated as  batchfile with ZERO commands.

        this.startTime = new java.util.Date();
        // String line = null;

        final BatchFileGrammer batchCmds = new BatchFileGrammer( this.cmdLineArgs.verbose, this.allProps );
        if ( _batchFileName.startsWith("@") ) {
            batchCmds.useDelimiter( System.lineSeparator() );
            // !!!!!!!!!!!!!!!! ATTENTION !!!!!!!!!!!!!!!!!  For actual files.. do _NOT_ use ';' as a separator --- for actual files.  see 2 lines below.
        } else {
            // for __INLINE__ batch-scripts provided via command-line
            batchCmds.useDelimiter( ";|"+System.lineSeparator() );  // for __INLINE__ strings provided on commandline, _YES_ use ';' as EOLN-substitute
            if ( this.cmdLineArgs.showStats ) System.out.println( HDR +" _batchFileName had VESTIGIAL QUOTES, which need to be removed.\n\t"+ _batchFileName +"\n");
            // If _batchFileName's content has any beginning and ending quote-characters.. remove them
            if ( _batchFileName.matches( "^'.+'$" )  || _batchFileName.matches( "^\".+\"$" ) )       // trim() invoked above ensures no white-space AFTER the quote character
                _batchFileName = _batchFileName.substring( 1, _batchFileName.length() - 1 );
            if ( this.cmdLineArgs.showStats ) System.out.println( HDR +" _batchFileName __AFTER__ it's QUOTES removed =\n\t"+ _batchFileName +"\n");
        }

        try {
            if ( batchCmds.openFile( _batchFileName, true, true ) ) {
                if ( this.cmdLineArgs.verbose ) System.out.println( HDR + ": go(): successfully opened _batchFileName [" + _batchFileName +"]" );
                if ( this.cmdLineArgs.showStats ) System.out.println( _batchFileName +" has "+ batchCmds.getCommandCount() );

                final T  retNode = this.processBatch( false, batchCmds, _node );
                if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" go():  retNode =" + retNode +"\n\n");

                this.endTime = new java.util.Date();
                if ( this.cmdLineArgs.showStats ) System.out.println( HDR + "Ran "+ this.runcount +" commands from "+ this.startTime +" until "+ this.endTime +" = " + (this.endTime.getTime() - this.startTime.getTime()) +" seconds" );
                return retNode;

            } else { // if-else openFile()
                return getEmptyYAML();
            }

        } catch (BatchFileException bfe) {
            if ( this.cmdLineArgs.verbose ) bfe.printStackTrace(System.err);
            System.err.println( bfe +"\n\nERROR while processing: Batch-"+ batchCmds.getState() + "\nERROR: " + bfe.getMessage() );
        } catch(java.io.FileNotFoundException fe) {
            if ( this.cmdLineArgs.verbose ) fe.printStackTrace(System.err);
            System.err.println( fe +"\n\nERROR: File Not found: within Batch-"+ batchCmds.getState() +".\nSee full-details by re-running command using --verbose cmdline option. " );
        } catch (Exception e) {
            if ( this.cmdLineArgs.verbose ) e.printStackTrace(System.err);
            System.err.println( e +"\n\nERROR: Unexpected Serious Internal ERROR while processing Batch-"+ batchCmds.getState() +".\nERROR: See full-details by re-running command using --verbose cmdline option.");
        }

        return null;
    }

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

    /**
     *  This function is meant for recursion.  Recursion happens when 'foreach' or 'batch' commands are detected in a batch file.
     *  After this function completes processing SUCCESSFULLY.. it returns a java.utils.LinkedHashMap&lt;String, Object&gt; object.
     *  If there is any failure whatsoever then the batch-file processing stops immediately.
     *  If there is any failure whatsoever either return value is NULL or an Exception is thrown.
     *  @param _bInRecursion true or false, whether this invocation is a recursive call or not.  If true, when the 'end' or [EOF] is detected.. this function returns
     *  @param _batchCmds an object of type BatchFileGrammer created by reading a batch-file, or .. .. the contents between 'foreach' and 'end' commands
     *  @param _input either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node ( as generated by SnakeYAML library).. or.. EsotericSoftware Library's preference for LinkedHashMap&lt;String,Object&gt;, -- in either case, this object contains the entire Tree representing the YAML file.
     *  @return a BLANK/EMPTY/NON-NULL org.yaml.snakeyaml.nodes.Node object, as generated by SnakeYAML/CollectionsImpl library and you'll get the final YAML output representing all processing done by the batch file.  If there is any failure, either an Exception is thrown.
     *  @throws BatchFileException if any failure trying to execute any entry in the batch file.  Batch file processing will Not proceed once a problem occurs.
     *  @throws java.io.FileNotFoundException if the batch file to be loaded does Not exist
     *  @throws Exception when any of the commands are being processed
     */
    protected T processBatch( final boolean _bInRecursion, final BatchFileGrammer _batchCmds, T _input )
                        throws BatchFileException, java.io.FileNotFoundException, Exception
    {
        assertNotNull( _batchCmds );
        assertNotNull( _input );
        final String HDR = CLASSNAME +": processBatch(recursion="+ _bInRecursion +","+ _batchCmds.getCmdType() +"): ";
        T tempOutput = null; // it's immediately re-initialized within WHILE-Loop below.

        if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" BEFORE STARTING while-loop.. "+ _batchCmds.hasNextLine() +" re: "+ _batchCmds.getState() +" @ BEGINNING _input="+ _input +"]" );

        while ( _batchCmds.hasNextLine() )
        {
            _batchCmds.nextLine(); // we can always get the return value of this statement .. via _batchCmds.getCurrentLine()

            if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" START of while-loop for "+ _batchCmds.getState() +" .. for input=["+ toStringDebug(_input) +"]" );

            // start each loop, with an 'empty' placeholder Map, to collect output of current batch command
            tempOutput = getEmptyYAML();

            switch( _batchCmds.getCmdType() ) {
                case Cmd_MakeNewRoot:
                    final String newRootElem = Macros.evalThoroughly( this.cmdLineArgs.verbose, _batchCmds.getMakeNewRoot(), this.allProps );
                    tempOutput = getNewSingleYAMLEntry( newRootElem, "", _batchCmds.getQuoteType() ); // Very simple YAML:-    NewRoot: <blank>
                    this.runcount ++;
                    break;
                case Cmd_SubBatch:
                    final String bSubBatch = Macros.evalThoroughly( this.cmdLineArgs.verbose, _batchCmds.getSubBatchFile(), this.allProps );
                    tempOutput = this.go( bSubBatch, _input );
// ????????????????????????????????????????????????????????????????
// As the above statement stands.. it will NEVER BE used, as 'include' is a far better way of doing SUB-BATCHES.
// Unless you want to use 'include' (for the equivalent of '.' in BASH / 'source' in TCSH)..
// .. and Cmd_SubBatch as the equivalent of forking a new 'SHell' to execute this sub-batch - completely isolating this current instance of BatchCmdProcessor.
// if that is what you want.. Ok.  Then! Let me invoke this.go() to create a NEW instance of BatchFileGrammer and BatchCmdProcessor
                    // technically, this.go() method is NOT meant to used recursively.  Semantically, this is NOT recursion :-(
                    this.runcount ++;
                    break;
                case Cmd_Foreach:
                    if ( this.cmdLineArgs.verbose ) System.out.println( HDR +"\t'foreach'_cmd detected'");
                    if ( this.cmdLineArgs.verbose ) System.out.println( HDR +"InputMap = "+ toStringDebug(_input) );
                    tempOutput = processFOREACHCmd_Step1( _batchCmds, _input  );
                    // since we processed the lines !!INSIDE!! the 'foreach' --> 'end' block .. via recursion.. we need to skip all those lines here.
                    _batchCmds.skip2MatchingEnd();
// skipInnerForeachLoops( _batchCmds, "processBatch(foreach)" );
                    this.runcount ++;
                    break;
                case Cmd_End:
                    if ( this.cmdLineArgs.verbose ) System.out.println( HDR +"found matching 'end' keyword for 'foreach' !!!!!!! \n\n");
                    this.runcount ++;
                    return _input;
                    // !!!!!!!!!!!! ATTENTION : Function exits here SUCCESSFULLY / NORMALLY. !!!!!!!!!!!!!!!!
                    // break;
                case Cmd_SaveTo:
                    // Might sound crazy - at first.  inpMap for this 'saveAs' command is the output of prior command.
                    // final String saveTo_AsIs = _batchCmds.getSaveTo();
                    tempOutput = processSaveToLine( _batchCmds, _input );
                    // Input map is cloned before saving.. so the and Output Map is different (when returning from this function)
                    this.runcount ++;
                    break;
                case Cmd_UseAsInput:
                    tempOutput = processUseAsInputLine( _batchCmds );
                    this.runcount ++;
                    break;
                case Cmd_PrintDash:
                    System.out.print( toStringDebug(  _input ) ); // There is already a 'final/ending' \n - generated by SnakeYaml Library's attempt to generate YAML as nicely-formatted MULTI-LINE String output
                    tempOutput = _input; // as nothing changes re: Input and Output Maps.
                    this.runcount ++;
                    break;
                case Cmd_DebugDump:
                    org.ASUX.common.Debug.printAllProps( ""+_batchCmds.getCmdType()+":\n", this.allProps ); // There is already a 'final/ending' \n - generated by SnakeYaml Library's attempt to generate YAML as nicely-formatted MULTI-LINE String output
                    tempOutput = _input; // as nothing changes re: Input and Output Maps.
                    this.runcount ++;
                    break;
                case Cmd_YAMLLibrary:
                    if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" Setting YAMLLibrary ="+ _batchCmds.getYAMLLibrary() );
                    // this.memoryAndContext.getContext().getYAMLImplementation().setYAMLLibrary( _batchCmds.getYAMLLibrary() );
System.err.println( HDR +"Not Implemented: The ability to switch YAML libraries within a BATCH Script @ "+ _batchCmds.getState() );
fail();
                    tempOutput = _input; // as nothing changes re: Input and Output Maps.
                    break;
                case Cmd_Verbose:
                    if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" this.cmdLineArgs.verbose = =["+ this.cmdLineArgs.verbose +"] & _batchCmds.getVerbose()=["+ _batchCmds.getVerbose() +"].");
                    this.cmdLineArgs.verbose = _batchCmds.getBatchVerbose();
                    _batchCmds.setVerbose( this.cmdLineArgs.verbose );
                    this.memoryAndContext.setVerbose( this.cmdLineArgs.verbose );
                    tempOutput = _input; // as nothing changes re: Input and Output Maps.
                    break;
                // case Cmd_Sleep:
                //     System.err.println("\n\tsleeping for (seconds) "+ _batchCmds.getSleepDuration() );
                //     Thread.sleep( _batchCmds.getSleepDuration()*1000 );
                //     tempOutput = _input; // as nothing changes re: Input and Output Maps.
                //     break;
                case Cmd_Any:
                    //This MUST ALWAYS be the 2nd last 'case' in this SWITCH statement
                    tempOutput = this.onAnyCmd( _batchCmds, _input );
                    this.runcount ++;
                    break;
                default:
                    System.out.println( HDR +"  unknown (new?) Batch-file command." );
                    System.exit(99);
            } // switch

            // this line below must be the very last line in the loop
            _input = tempOutput; // because we might be doing ANOTHER iteraton of the While() loop.
            this.cmdLineArgs.verbose = _batchCmds.getVerbose(); // always keep checking the verbose level, which can change 'implicitly' within _batchCmds / BatchFileGrammerr.java

            if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" _________________________ BOTTOM of WHILE-loop: tempOutput =" + toStringDebug(tempOutput) +"");
        } // while loop

        // !!!!!!!!!!!!!!!!!!! ATTENTION: MOVE FOLLOWING LINE into SUBCLASS
        if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" ---@END---  tempOutput =" + toStringDebug(tempOutput) +"\n\n");
        // reached end of file.
        return tempOutput;
    }

    //=============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=============================================================================

    /**
     *  Based on command type, process the inputNode and produce an output - for that specific command
     *  @param _batchCmds Non-Null instance of {@link BatchFileGrammer}
     *  @param _node Non-null instance of either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node ( as generated by SnakeYAML library).. or.. EsotericSoftware Library's preference for LinkedHashMap&lt;String,Object&gt;, -- in either case, this object contains the entire Tree representing the YAML file.
     *  @return a BLANK/EMPTY/NON-NULL org.yaml.snakeyaml.nodes.Node object, as generated by SnakeYAML/CollectionsImpl library and you'll get the final YAML output representing all processing done by the batch file.  If there is any failure, either an Exception is thrown.
     *  @throws BatchCmdProcessor.BatchFileException if there is any issue with the command in the batchfile
     *  @throws Macros.MacroException if there is any issues with evaluating Macros.  This is extremely rare, and indicates a software bug.
     *  @throws java.io.FileNotFoundException specifically thrown by the SnakeYAML-library subclass of this
     *  @throws java.io.IOException Any issues reading or writing to PropertyFiles or to JSON/YAML files
     *  @throws Exception Any other unexpected error
     */
    protected abstract T  processFOREACHCmd_Step1( final BatchFileGrammer _batchCmds, T _node )
                throws BatchCmdProcessor.BatchFileException, Macros.MacroException, java.io.FileNotFoundException, java.io.IOException, Exception;

    //=============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=============================================================================

    // /**
    //  * When this function returns, the "pointer" within _batchCmds (.currentLine and .getLineNum()) ..
    //  *   should be pointing to the command AFTER the 'end' command.
    //  * This function basically keeps track of any inner foreachs .. and that's how it knows when the matching 'end' was detected.
    //  * @param _batchCmds pass-by-reference, so we can alter it's state and move it to the line AFTER matching 'end' commamd
    //  * @param _sInvoker for use in debugging output only (as there is tons of recursion-loops within these classes)
    //  * @throws BatchFileException any error with interpreting a specific line in the batch-cmds file
    //  * @throws Exception when any of the commands in batch-file are being processed
    //  */
    // protected void skipInnerForeachLoops( final BatchFileGrammer _batchCmds, final String _sInvoker )
    //                                 throws BatchFileException, Exception
    // {
    //     assertTrue( _batchCmds != null );
    //     assertTrue( _sInvoker != null );
    //     final String HDR = CLASSNAME +": skipInnerForeachLoops("+_sInvoker+"): ";
    //     final int bookmark = _batchCmds.getLineNum();
    //     boolean bFoundMatchingENDCmd = false;
    //     int recursionLevel = 0;
    //     while ( _batchCmds.hasNextLine() ) {
    //         /* final String line22 = */ _batchCmds.nextLine(); // we do Not care what the line is about.
    //         if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" skipping cmd "+ _batchCmds.getState() );

    //         final boolean bForEach22 = _batchCmds.isForEachLine();
    //         if ( bForEach22 ) recursionLevel ++;

    //         final boolean bEnd22 = _batchCmds.isEndLine();
    //         if ( bEnd22 ) {
    //             recursionLevel --;
    //             if ( recursionLevel < 0 ) {
    //                 bFoundMatchingENDCmd = true;
    //                 break; // we're done completely SKIPPING all the lines between 'foreach' --> 'end'
    //             } else
    //                 continue; // while _batchCmds.hasNextLine()
    //         } // if bEnd22
    //     }
    //     if (  !  bFoundMatchingENDCmd ) // sanity check.  These exceptions will get thrown if logic in 100 lines above isn't water-tight
    //         throw new BatchFileException( HDR +" ERROR In "+ _batchCmds.getState() +"] !!STARTING!! from line# "+ bookmark +".. do NOT see a MATCHING 'end' keyword following the  'foreach'.");
    // }

    //======================================================================
    protected T processSaveToLine( final BatchFileGrammer _batchCmds, final T _node )
                                    throws Macros.MacroException,  java.io.IOException, Exception
    {
        final String HDR = CLASSNAME +": processSaveToLine(): ";
        final String saveTo_AsIs = new org.ASUX.common.StringUtils(this.cmdLineArgs.verbose).removeBeginEndQuotes(   _batchCmds.getSaveTo()   );
        if ( saveTo_AsIs != null ) {
            String saveTo = Macros.evalThoroughly( this.cmdLineArgs.verbose, saveTo_AsIs, this.allProps );
            if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" #1 saveTo='"+ saveTo +"' and saveTo.startsWith(?)="+ saveTo.startsWith("?") +" saveTo.substring(1)='"+ saveTo.substring(1) + "'" );
            final boolean bOkIfMissing = saveTo.startsWith("?"); // that is, the script-file line was:- 'properties kwom=?fnwom'
            saveTo = saveTo.startsWith("?") ? saveTo.substring(1) : saveTo; // remove the '?' prefix from key/lhs string
            // repeat a 2nd time - in case user entered '?' BEFORE the quotes surrounding the SaveTo-path
            saveTo = new org.ASUX.common.StringUtils(this.cmdLineArgs.verbose).removeBeginEndQuotes( saveTo );
            if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" #2 saveTo='"+ saveTo +"' and saveTo.startsWith(?)="+ saveTo.startsWith("?") +" saveTo.substring(1)='"+ saveTo.substring(1) + "'" );

            if ( this.memoryAndContext == null || this.memoryAndContext.getContext() == null )
                throw new BatchFileException( HDR +" ERROR In "+ _batchCmds.getState() +".. This program currently has NO/Zero memory from one line of the batch file to the next.  And a SaveTo line was encountered for ["+ saveTo +"]" );
            else {
                // if the user prefixed the saveTo-path with a '?', we should NOT overwrite existing content.
                if ( bOkIfMissing ) {
                    final Object o = this.memoryAndContext.getContext().getDataFromReference( saveTo ); // see if something exists __ALREADY__ in memory under this label.
                    if ( o != null ) {
                        @SuppressWarnings("unchecked")
                        T ret = (T) o;
                        return ret;
                    } // else fall thru below.
                } // else fall thru below.
                final T newnode = deepClone( _node );
                this.memoryAndContext.getContext().saveDataIntoReference( saveTo, newnode );
                return newnode;
            } // if-else
        } else 
            throw new BatchFileException( HDR +" ERROR In "+ _batchCmds.getState() +".. Missing or empty label for SaveTo line was encountered = ["+ saveTo_AsIs +"]" );
    }

    //=============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=============================================================================

    // protected abstract T processUseAsInputLine( final BatchFileGrammer _batchCmds )
    //                             throws java.io.FileNotFoundException, java.io.IOException, Exception,
    //                             Macros.MacroException, BatchFileException;

    private T processUseAsInputLine( final BatchFileGrammer _batchCmds )
                                throws java.io.FileNotFoundException, java.io.IOException, Exception,
                                Macros.MacroException, BatchFileException
    {
        final String HDR = CLASSNAME +": processUseAsInputLine(): ";
        final String inputFrom_AsIs = _batchCmds.getUseAsInput();
        String inputFrom = Macros.evalThoroughly( this.cmdLineArgs.verbose, inputFrom_AsIs, this.allProps );
        if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" #1 inputFrom='"+ inputFrom +"' and inputFrom.startsWith(?)="+ inputFrom.startsWith("?") +" inputFrom.substring(1)='"+ inputFrom.substring(1) + "'" );
        inputFrom = new org.ASUX.common.StringUtils(this.cmdLineArgs.verbose).removeBeginEndQuotes( inputFrom );
        final boolean bOkIfMissing = inputFrom.startsWith("?"); // that is, the script-file line was:- 'properties kwom=?fnwom'
        inputFrom = inputFrom.startsWith("?") ? inputFrom.substring(1) : inputFrom; // remove the '?' prefix from key/lhs string
        // repeat a 2nd time - in case user entered '?' BEFORE the quotes
        inputFrom = new org.ASUX.common.StringUtils(this.cmdLineArgs.verbose).removeBeginEndQuotes( inputFrom );
        if ( this.cmdLineArgs.verbose ) System.out.println( HDR +" #2 inputFrom='"+ inputFrom +"' and inputFrom.startsWith(?)="+ inputFrom.startsWith("?") +" inputFrom.substring(1)='"+ (inputFrom.length()>0?inputFrom.substring(1):"EMPTYString") + "'" );

        if ( this.memoryAndContext == null || this.memoryAndContext.getContext() == null )
            throw new BatchFileException( HDR +"ERROR In "+ _batchCmds.getState() +".. This program currently has NO/Zero memory to carry it from one line of the batch file to the next.  And a useAsInput line was encountered for ["+ inputFrom +"]" );

        try {
            final Object o = this.memoryAndContext.getContext().getDataFromReference( inputFrom );
            if ( instanceof_YAMLImplClass( o ) ) // o instanceof T <-- compiler cannot allow me to do this
            {   @SuppressWarnings("unchecked")
                final T retMap3 = (T) o;
                return retMap3;
                // if ( isEmptyYAML( retMap3 ) )
                //     return null;
                // else
                //     return retMap3;
            } else {
                if ( bOkIfMissing && o == null ) 
                    return this.getEmptyYAML();
                else {
                    final String es = (o==null) ? ("Nothing found from "+ inputFrom_AsIs) : ("We content of Type: "+ o.getClass().getName()  +" containing ["+ o.toString() +"]");
                    throw new BatchFileException( "ERROR In "+ _batchCmds.getState() +".. Failed to read YAML/JSON from ["+ inputFrom_AsIs +"].  "+ es );
                }
            } // if instanceof_YAMLImplClass( o )
        } catch( java.io.FileNotFoundException fnfe ) {
            if ( bOkIfMissing ) 
                return this.getEmptyYAML();
            else
                throw new BatchFileException( "ERROR In "+ _batchCmds.getState() +".. Missing file: '"+ inputFrom_AsIs +"'." );
        } // try-catch
    }

    //=============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=============================================================================

    private T onAnyCmd( final BatchFileGrammer _batchCmds, final T _input )
                    throws BatchFileException, Macros.MacroException, java.io.FileNotFoundException, java.io.IOException, Exception
    {
        assertNotNull(_batchCmds);
        assertNotNull(_input);
        final String HDR = CLASSNAME + ": onAnyCmd(): ";
        final String cmdStr_AsIs = _batchCmds.getCommand();
        assertNotNull(cmdStr_AsIs);
        final String cmdStrNM = Macros.evalThoroughly( this.cmdLineArgs.verbose, cmdStr_AsIs, this.allProps ).trim();
        assertNotNull(cmdStrNM);

        final boolean isYAMLCmd = cmdStrNM.equals("yaml");
        final boolean isAWSCmd = cmdStrNM.equals("aws.sdk");
        final boolean isAWSCFNCmd = cmdStrNM.equals("aws.cfn");

        //--------------------------------
        // completeCmdLine was enhanced in the above IF-ELSE
        // String[] cmdLineArgsStrArr = convStr2Array( completeCmdLine );

        // cmdLineArgsStrArr = java.util.Arrays.copyOfRange( cmdLineArgsStrArr, 1, cmdLineArgsStrArr.length ); // get rid of the 'yaml' or 'aws.sdk' word at the beginning of the command
        // final Class[] mainArgsClassList = new Class[] { cmdLineArgsStrArr.getClass() };
        // final Object[] mainArgs         =  new Object[] { cmdLineArgsStrArr };

        //--------------------------------
        // We need to invoke constructor of the SUB-CLASS of org.ASUX.yaml.CmdInvoker - from the appropriate YAML-Library or AWS-SDK Library.
        // For that let's gather the Constructor parameter-types and arguments
        String cmdArgsClassNameStr = null;
        String antlr4ParserClassNameStr = null;
        String implClassNameStr = null;

        // ASUX has multiple COMMAND-FAMILIES: YAML, AWS, CFN, TEXT, .. ..

        if ( isYAMLCmd ) {
            cmdArgsClassNameStr = "org.ASUX.yaml.CmdLineArgsCommon";
            antlr4ParserClassNameStr = "org.ASUX.yaml.YAMLCmdANTLR4Parser";
            implClassNameStr = "org.ASUX.YAML.NodeImpl.CmdInvoker";
        } else if ( isAWSCmd ) {
            cmdArgsClassNameStr = "org.ASUX.AWSSDK.CmdLineArgsAWS";
            antlr4ParserClassNameStr = "org.ASUX.yaml.AWSSDKCmdANTLR4Parser";
            implClassNameStr = "org.ASUX.AWSSDK.CmdInvoker";
        } else if ( isAWSCFNCmd ) {
            cmdArgsClassNameStr = "org.ASUX.AWS.CFN.CmdLineArgs";
            antlr4ParserClassNameStr = "org.ASUX.yaml.AWSCFNCmdANTLR4Parser";
            implClassNameStr = "org.ASUX.AWS.CFN.CmdInvoker";
        } else {
            throw new BatchFileException( "Unknown Batchfile command ["+ cmdStr_AsIs +"] / ["+ cmdStrNM +"] in "+ _batchCmds.getState() );
        }

        if ( this.cmdLineArgs.verbose ) System.out.println( HDR +"cmdArgsClassNameStr ="+ cmdArgsClassNameStr );
        assertNotNull(cmdArgsClassNameStr);
        if ( this.cmdLineArgs.verbose )  System.out.println( HDR +"antlr4ParserClassNameStr ="+ antlr4ParserClassNameStr );
        assertNotNull(antlr4ParserClassNameStr);
        if ( this.cmdLineArgs.verbose )  System.out.println( HDR +"implClassNameStr ="+ implClassNameStr );
        assertNotNull(implClassNameStr);

        //--------------------------------
        // Do the equivalent of:- new org.ASUX.YAML.NodeImpl.CmdInvoker( this.cmdLineArgs.verbose, this.cmdLineArgs.showStats, .. .. );
        // Do the equivalent of:- new org.ASUX.AWSSDK.CmdInvoker( this.cmdLineArgs.verbose, this.cmdLineArgs.showStats, .. .. );
        try {
            if ( this.cmdLineArgs.verbose ) System.out.println( HDR +"about to invoke "+ antlr4ParserClassNameStr +".constructor()." );

            //  Just like for YAML, AWS.SDK and AWS.CFN need their own Parser & Grammer.
            // So, all those ASUX_Projects will have their own EQUIVALENT of YAMLCmdANTLR4Parser, implementing the org.ASUX.language.antlr4.GenericCmdANTLR4Parser interface.
            // So, the generic code here, is to find out what the equivalent of YAMLCmdANTLR4Parser is.
            final Class<?> antlr4ParserClass = Cmd.class.getClassLoader().loadClass( antlr4ParserClassNameStr ); // returns: protected Class<?> -- throws ClassNotFoundException
            if ( this.cmdLineArgs.verbose )  System.out.println( HDR +"antlr4ParserClassNameStr=["+ antlr4ParserClassNameStr +"] successfully loaded using ClassLoader.");
            // // final Object oo2 = org.ASUX.common.GenericProgramming.invokeStaticMethod( cmdArgsClass, "create", mainArgsClassList, mainArgs );
            final Constructor<?> constructor = antlr4ParserClass.getConstructor( boolean.class );
            // Constructor<?> constructor = antlr4ParserClass.getConstructors()[0]; // <--- for DEFAULT CONSTRUCTOR only
            final Object oo2 = constructor.newInstance( this.cmdLineArgs.verbose );
            final org.ASUX.language.antlr4.GenericCmdANTLR4Parser genericCmdANTLR4Parser = (org.ASUX.language.antlr4.GenericCmdANTLR4Parser) oo2;

            //--------------------------------------------------------------
            final String completeCmdLine = _batchCmds.currentLine() + " -i - -o -"; // Adding the '-i' and '-o' is harmless, but required because CmdLineArgs.java will barf otherwise (as CmdLineArgs.java thinks it's being run on commandline by a user)
            if ( this.cmdLineArgs.verbose ) System.out.println( HDR +"about to parse completeCmdLine="+ completeCmdLine );

            final ArrayList<org.ASUX.language.antlr4.CmdLineArgs> cmds =genericCmdANTLR4Parser.parseYamlCommandLine( completeCmdLine );
            if ( this.cmdLineArgs.verbose ) System.out.println( HDR +"Got "+ cmds.size() +" complete CmdLined" );

            for ( org.ASUX.language.antlr4.CmdLineArgs obj: cmds ) {
                final CmdLineArgsCommon newCmdLineArgsObj = (CmdLineArgsCommon) obj;
                if ( this.cmdLineArgs.verbose ) System.out.println( HDR +"about to execute command: "+ newCmdLineArgsObj +" " );

                // if user did NOT specify a quote-option _INSIDE__ batchfile @ current line, then use whatever was specified on CmdLine when starting BATCH command.
                newCmdLineArgsObj.copyBasicFlags( this.cmdLineArgs );
                if ( this.cmdLineArgs.verbose ) System.out.println( HDR +"newCmdLineArgsObj="+ newCmdLineArgsObj );

                // newCmdLineArgsObj.copyBasicFlags( newCmdLineArgsObj, this.cmdLineArgs.verbose, this.cmdLineArgs.showStats, this.cmdLineArgs.offline, this.cmdLineArgs.quoteType );
                // newCmdLineArgsObj.verbose   = newCmdLineArgsObj.verbose || this.cmdLineArgs.verbose;  // pass on whatever this user specified on cmdline re: --verbose or not.
                // newCmdLineArgsObj.showStats = newCmdLineArgsObj.showStats || this.cmdLineArgs.showStats;
                // newCmdLineArgsObj.offline = newCmdLineArgsObj.offline || this.cmdLineArgs.offline;
                // if ( newCmdLineArgsObj.quoteType == Enums.ScalarStyle.UNDEFINED )
                //     newCmdLineArgsObj.quoteType = this.cmdLineArgs.quoteType; // if user did NOT specify a quote-option _INSIDE__ batchfile @ current line, then use whatever was specified on CmdLine when starting BATCH command.

                //--------------------------------
                // We need to invoke constructor of the SUB-CLASS of org.ASUX.yaml.CmdInvoker - from the appropriate YAML-Library or AWS-SDK Library.
                // For that let's gather the Constructor parameter-types and arguments
                Class[] paramClassList;
                Object[] methodArgs;

                // if ( isYAMLCmd ) {
// now that I am invoking 'newCmdinvoker.setYAMLImplementation( clone );' about 60 lines below..
// .. i do NOT think methodArgs need to longer for 'isYAMLCmd' - versus 'isAWSCmd'
// Come back later and simplify the code.
                    // paramClassList  = new Class[] { boolean.class, boolean.class, MemoryAndContext.class, this.memoryAndContext.getContext().getYAMLImplementation().getLibraryOptionsClass() };
                    // methodArgs      = new Object[] { newCmdLineArgsObj.verbose, newCmdLineArgsObj.showStats, this.memoryAndContext, this.memoryAndContext.getContext().getYAMLImplementation().getLibraryOptionsObject() };
                // } else if ( isAWSCmd || isAWSCFNCmd ) {

                if ( isYAMLCmd || isAWSCmd || isAWSCFNCmd ) {
                    // paramClassList  = new Class[] { boolean.class, boolean.class,           this.memoryAndContext.getContext().getYAMLImplementation().getLibraryOptionsClass() };
                    // methodArgs      = new Object[] { newCmdLineArgsObj.verbose, newCmdLineArgsObj.showStats,          this.memoryAndContext.getContext().getYAMLImplementation().getLibraryOptionsObject() };
                    paramClassList  = new Class[]  { CmdLineArgsCommon.class,       MemoryAndContext.class };
                    methodArgs      = new Object[] { this.cmdLineArgs,              this.memoryAndContext  };
                } else {
                    throw new BatchFileException( "Unknown Batchfile command ["+ cmdStr_AsIs +"] / ["+ cmdStrNM +"] in "+ _batchCmds.getState() );
                }

                //--------------------------------
                // we should NOT be invoking Cmd.go() within the appropriate Command-Family.
                // REASON: We need to pass in "Context" (this.memoryAndContext), "YAML-implementation" object, etc.. ..
                // Now invoke constructor of the SUB-CLASS of org.ASUX.yaml.CmdInvoker - from the appropriate YAML-Library or AWS-SDK Library.
                org.ASUX.yaml.CmdInvoker<T> newCmdinvoker;
                try {
                    if ( this.cmdLineArgs.verbose ) System.out.println( HDR +"about to invoke "+ implClassNameStr +".constructor()." );

                    final Class<?> implClass = Cmd.class.getClassLoader().loadClass( implClassNameStr ); // returns: protected Class<?> -- throws ClassNotFoundException
                    if ( this.cmdLineArgs.verbose )  System.out.println( HDR +"implClassNameStr=["+ implClassNameStr +"] successfully loaded using ClassLoader.");
                    final Object oo = org.ASUX.common.GenericProgramming.invokeConstructor( implClass, paramClassList, methodArgs );
                    if ( this.cmdLineArgs.verbose ) System.out.println( HDR +"returned from successfully invoking "+ implClassNameStr +".constructor()." );

                    @SuppressWarnings("unchecked")
                    final org.ASUX.yaml.CmdInvoker<T> tmpObj = (org.ASUX.yaml.CmdInvoker<T>) oo;
                    newCmdinvoker = tmpObj;

                // } catch (ClassNotFoundException e) {
                //     final String estr = "ERROR In "+ _batchCmds.getState() +".. Failed to run the command in current line.";
                //     if ( this.cmdLineArgs.verbose ) e.printStackTrace(System.err);
                //     if ( this.cmdLineArgs.verbose ) System.err.println( HDR + estr +"\n"+ e );
                //     throw new BatchFileException( e.getMessage() );

                } catch (Exception e) {
                    final String estr = "ERROR In "+ _batchCmds.getState() +".. Failed to run the command in current line.";
                    if ( this.cmdLineArgs.verbose ) e.printStackTrace(System.err);
                    if ( this.cmdLineArgs.verbose ) System.err.println( HDR + estr +"\n"+ e );
                    throw new BatchFileException( e.getMessage() );
                }

                //--------------------------------
                if ( isYAMLCmd || isAWSCmd || isAWSCFNCmd ) {
                    @SuppressWarnings("unchecked")
                    final CmdInvoker<T> cmdI = (CmdInvoker<T>) this.memoryAndContext.getContext();
                    final YAMLImplementation<T> orig = cmdI.getYAMLImplementation();
                    final YAMLImplementation<T> clone = orig.deepClone();

                    newCmdinvoker.setYAMLImplementation( clone );
                    if (this.cmdLineArgs.verbose) System.out.println( HDR +" set YAML-Library to [" + orig.getYAMLLibrary() + " and [" + newCmdinvoker.getYAMLImplementation().getYAMLLibrary() + "]" );

                } else {
                    // We must have a previous replica of this same IF-ELSE-ELSE above.  So, how come we're still here in this block?
                    System.err.println( HDR +"FATAL ERROR: Unknown Batchfile command ["+ cmdStr_AsIs +"] / ["+ cmdStrNM +"] in "+ _batchCmds.getState() );
                    System.exit(61);
                }

                //--------------------------------
                // We expect the underlying library to generate the object of type T for the return value of newCmdinvoker.processCommand().
                @SuppressWarnings("unchecked")
                final T output = (T) newCmdinvoker.processCommand( newCmdLineArgsObj, _input );
                if (this.cmdLineArgs.verbose) System.out.println( HDR +" processing of command returned [" + (output==null?"null":output.getClass().getName()) + "]" );
                return output;

            } // For (Cmds)

            // shouldnt be here
            return null;

            //--------------------------------------------------------------
            // } catch (ClassNotFoundException e) {
            //     final String estr = "ERROR In "+ _batchCmds.getState() +".. Failed to run the command in current line.";
            //     if ( this.cmdLineArgs.verbose ) e.printStackTrace(System.err);
            //     if ( this.cmdLineArgs.verbose ) System.err.println( HDR + estr +"\n"+ e );
            //     throw new BatchFileException( e.getMessage() );
        } catch (Exception e) {
            final String estr = "ERROR In "+ _batchCmds.getState() +".. Failed to run the command in current line.";
            if ( this.cmdLineArgs.verbose ) e.printStackTrace(System.err);
            if ( this.cmdLineArgs.verbose ) System.err.println( HDR + estr +"\n"+ e );
            throw new BatchFileException( e.getMessage() );
        }
    }

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

    private String[] convStr2Array( final String _cmdStr )
                            throws Macros.MacroException, java.io.IOException
    {
        final String HDR = CLASSNAME + ": convStr2Array(): ";
        String [] cmdLineArgsStrArr = null;
        if (this.cmdLineArgs.verbose) System.out.println( HDR +"_cmdStr="+ _cmdStr );
        String cmdStrCompacted = _cmdStr.replaceAll("\\s\\s*", " "); // replace multiple spaces with a single space.
        // cmdStrCompacted = cmdStrCompacted.trim(); // no need.  The _batchCmds already took care of it.
        final String cmdStrNoMacros = Macros.eval( this.cmdLineArgs.verbose, cmdStrCompacted, this.allProps ).trim();
        if (this.cmdLineArgs.verbose) System.out.println( HDR +"cmdStrCompacted = "+ cmdStrCompacted );

        // https://mvnrepository.com/artifact/com.opencsv/opencsv
        final java.io.StringReader reader = new java.io.StringReader( cmdStrNoMacros );
        final com.opencsv.CSVParser parser = new com.opencsv.CSVParserBuilder().withSeparator(' ').withQuoteChar('\'').withIgnoreQuotations(false).build();
        final com.opencsv.CSVReader cmdLineParser = new com.opencsv.CSVReaderBuilder( reader ).withSkipLines(0).withCSVParser( parser ).build();
        cmdLineArgsStrArr = cmdLineParser.readNext(); // pretend we're reading the 1st line ONLY of a CSV file.
        if (this.cmdLineArgs.verbose) { System.out.print( HDR +"cmdLineArgsStrArr = ");  for( String s: cmdLineArgsStrArr) System.out.println(s+"\t"); System.out.println(); }
        // some of the strings in this.cmdLineArgsStrArr may still have a starting and ending single/double-quote
        cmdLineArgsStrArr = new org.ASUX.common.StringUtils(this.cmdLineArgs.verbose).removeBeginEndQuotes( cmdLineArgsStrArr );
        if (this.cmdLineArgs.verbose) { System.out.print( HDR +"cmdLineArgsStrArr(REMOVEDALLQUOTES) = ");  for( String s: cmdLineArgsStrArr) System.out.print(s+"\t"); System.out.println(); }
        return cmdLineArgsStrArr;
    }

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

    // For unit-testing purposes only
    public static void main(String[] args) {
        // try {
        //     final BatchCmdProcessor o = new BatchCmdProcessor(true, true);
        //     T inpMap = null;
        //     T outpMap = o.go( args[0], inpMap );
        // } catch (Exception e) {
        //     e.printStackTrace(System.err); // main() method for unit-testing
        // }
    }

}
