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

import java.util.ArrayList;

import org.apache.commons.cli.*;

import static org.junit.Assert.*;

/** <p>This class is a typical use of the org.apache.commons.cli package.</p>
 *  <p>This class has No other function - other than to parse the commandline arguments and handle user's input errors.</p>
 *  <p>For making it easy to have simple code generate debugging-output, added a toString() method to this class.</p>
 *  <p>Typical use of this class is to code a sub-class and use the sub-class in 3-steps: </p>
 *<pre>
public static void main(String[] args) {
    final CmdLineArgs cla = new CmdLineArgs();
    cla.define();
    cla.parse(args);
    .. ..
 *</pre>
 *
 *  <p>See full details of how to use this, in {@link org.ASUX.yaml.Cmd} as well as the <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com project.</p>
 * @see org.ASUX.yaml.Cmd
 */
public abstract class CmdLineArgsCommon implements java.io.Serializable {

    private static final long serialVersionUID = 141L;

    public static final String CLASSNAME = CmdLineArgsCommon.class.getName();

    public static final String NOQUOTE      = "no-quote";
    public static final String SINGLEQUOTE  = "single-quote";
    public static final String DOUBLEQUOTE  = "double-quote";

    protected static final String OFFLINE   = "offline";

    protected static final String YAMLLIB = "yamllibrary";

    protected static final String INPUTFILE = "inputfile";
    protected static final String OUTPUTFILE = "outputfile";

    //------------------------------------
    public boolean verbose      = false;
    public boolean showStats    = false;
    protected boolean offline   = false;

    protected Enums.ScalarStyle quoteType = Enums.ScalarStyle.UNDEFINED;
    protected YAML_Libraries YAMLLibrary = YAML_Libraries.NodeImpl_Library; // some default value for now

    public String inputFilePath = "/tmp/undefined";
    public String outputFilePath = "/tmp/undefined";

    //------------------------------------
    protected final ArrayList<String> argsAsIs = new ArrayList<>();

    protected final org.apache.commons.cli.Options options = new Options();

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    // /** <p>ONLY DEFAULT constructor.</p>
    //  */
    // public CmdLineArgsCommon() {
    //     // Do Nothing.
    // }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /** <p>This method is the 2nd of the 3 steps to using this class.</p>
     *  <p>Subclass should Not override this, but can extend this method (for example: 'adding' {@link #defineInputOutputOptions()}).</p>
     */
    public void define() {
        this.defineCommonOptions();
        this.defineAdditionalOptions();
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    protected static Option addSimpleOption( final Options _options, final String _short, final String _long, final String description ) {
        final Option opt = new Option( _short, _long, false /* boolean hasArg */, description );
        opt.setRequired( false );
        _options.addOption( opt );
        return opt;
    }

    protected static Option genOption( final String _short, final String _long, final String _description, final int _numArgs, final String _argNames ) {
        final Option opt = new Option( _short, _long, ( _numArgs > 0 ? true : false ), _description );
        opt.setArgs( _numArgs );
        opt.setOptionalArg(false);
        if ( _numArgs > 2 )
            opt.setValueSeparator(' ');
        if ( _numArgs > 0 && _argNames != null )
            opt.setArgName( _argNames );
        return opt;
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    /**
     * Add cmd-line argument definitions (using apache.commons.cli.Options) for the instance-variables defined in this class: {@link #verbose}, {@link #showStats}, {@link #offline}, {@link #quoteType}
     */
    protected void defineCommonOptions()
    {   final String HDR = CLASSNAME + ": defineCommonOptions(): ";
        Option opt;

        //----------------------------------
        CmdLineArgsCommon.addSimpleOption( this.options, "v",  "verbose", "Show debug output" );
        CmdLineArgsCommon.addSimpleOption( this.options, "vs", "showStats", "Show - at end output - a summary of how many matches happened, or entries were affected" );
        CmdLineArgsCommon.addSimpleOption( this.options, "zzz", OFFLINE, "whether internet is turned off (or, you want to pretend there's no internet) " );

        //----------------------------------
        OptionGroup grp2        = new OptionGroup();
        Option noQuoteOpt       = new Option( "nq", NOQUOTE,     false, "do Not use Quotes in YAML output" );
        Option singleQuoteOpt   = new Option( "sq", SINGLEQUOTE, false, "use ONLY Single-quote when generating YAML output" );
        Option doubleQuoteOpt   = new Option( "dq", DOUBLEQUOTE, false, "se ONLY Double-quote when generating YAML output" );
        grp2.addOption(noQuoteOpt);
        grp2.addOption(singleQuoteOpt);
        grp2.addOption(doubleQuoteOpt);
        grp2.setRequired(false);

        this.options.addOptionGroup(grp2);

        //----------------------------------
        opt = CmdLineArgsCommon.genOption( "zy", YAMLLIB, "only valid values are: "+ YAML_Libraries.list("\t"), 1, "yamllibparam" );
        opt.setRequired(false);
        this.options.addOption(opt);

// System.out.println( HDR +"completed function." );
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    /**
     *  <p>Add cmd-line argument definitions (using apache.commons.cli.Options) for the instance-variables defined in this class.</p>
     */
    protected abstract void defineAdditionalOptions();

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     * Add cmd-line argument definitions (using apache.commons.cli.Options) for 2 specific instance-variables defined in this class: 
     */
    protected void defineInputOutputOptions()
    {   final String HDR = CLASSNAME + ": defineInputOutputOptions(): ";
        Option opt;

        //----------------------------------
        opt = CmdLineArgsCommon.genOption( "i", INPUTFILE, "input file path", 1, "filename" );
        opt.setRequired(true);
        this.options.addOption(opt);

        opt = CmdLineArgsCommon.genOption( "o", OUTPUTFILE,  "output file path", 1, "new-file" );
        opt.setRequired(true);
        this.options.addOption(opt);

// System.out.println( HDR +"completed function." );
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /** <p>This method is part of the 3rd/final of the 3 steps to using this class.</p>
     *  <p>The 3rd/final step is composed of the following sequence: {@link #parse(String[])} in turn invokes this method, followed by {@link #parseAdditionalOptions(String[], org.apache.commons.cli.CommandLine)}</p>
     *  @param _apacheCmdProcessor a Non-null instance
     *  @throws Exception TBD by subclasses
     */
    protected void parseCommonOptions( final org.apache.commons.cli.CommandLine _apacheCmdProcessor ) throws Exception
    {
        final String HDR = CLASSNAME + ": parseCommonOptions(apache.commons.cli): ";

        this.verbose = _apacheCmdProcessor.hasOption("verbose");
        this.showStats = _apacheCmdProcessor.hasOption("showStats");

        this.offline = _apacheCmdProcessor.hasOption(OFFLINE);

        //-------------------------------------------
        if ( _apacheCmdProcessor.hasOption( NOQUOTE ) ) this.quoteType = Enums.ScalarStyle.PLAIN;
        if ( _apacheCmdProcessor.hasOption( SINGLEQUOTE ) ) this.quoteType = Enums.ScalarStyle.SINGLE_QUOTED;
        if ( _apacheCmdProcessor.hasOption( DOUBLEQUOTE ) ) this.quoteType = Enums.ScalarStyle.DOUBLE_QUOTED;
        if ( this.verbose ) System.out.println("this.quoteType = "+this.quoteType.toString());
        // DO NOT do this --> assertTrue( this.quoteType != Enums.ScalarStyle.UNDEFINED );
        // We now __actually use__ UNDEFINED to represent the fact that the end-user did NOT provide anything on the commandline (whether no-quote, single or double)

        //-------------------------------------------
        if ( _apacheCmdProcessor.hasOption( NOQUOTE     ) ) this.quoteType = org.ASUX.yaml.Enums.ScalarStyle.PLAIN; // this translates to 'null'
        if ( _apacheCmdProcessor.hasOption( SINGLEQUOTE ) ) this.quoteType = org.ASUX.yaml.Enums.ScalarStyle.SINGLE_QUOTED;
        if ( _apacheCmdProcessor.hasOption( DOUBLEQUOTE ) ) this.quoteType = org.ASUX.yaml.Enums.ScalarStyle.DOUBLE_QUOTED;
        if ( this.verbose ) System.out.println( HDR +"this.quoteType = "+this.quoteType.toString());
        // DO NOT do this --> assertTrue( this.quoteType != org.ASUX.yaml.Enums.ScalarStyle.UNDEFINED );
        // We now __actually use__ UNDEFINED to represent the fact that the end-user did NOT provide anything on the commandline (whether no-quote, single or double)

        //-------------------------------------------
        if ( _apacheCmdProcessor.getOptionValue(YAMLLIB) != null )
            this.YAMLLibrary = YAML_Libraries.fromString( _apacheCmdProcessor.getOptionValue(YAMLLIB) );
        else
            this.YAMLLibrary = YAML_Libraries.SNAKEYAML_Library; // default.
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     *  <p>Subclasses to override this method - - to parse for additional options.</p>
     *  <p>FYI: This method does nothing in this parent class, as it's a placeholder for any subclasses.</p>
     *  <p>This method is part of the 3rd/final of the 3 steps to using this class.</p>
     *  <p>The 3rd/final step is composed of the following sequence: {@link #parse(String[])} in turn invokes {@link #parseCommonOptions(org.apache.commons.cli.CommandLine)}, followed by this.</p>
     *  @param _args command line argument array - as received as-is from main().
     *  @param _apacheCmdProcessor a Non-Null instance of org.apache.commons.cli.CommandLine
     *  @throws MissingOptionException if user has Not provided the required cmd-line options/arguments as defined within {@link #defineCommonOptions()} and {@link #defineInputOutputOptions()}
     *  @throws ParseException if the additional-arguments in the user-provided commands line, do Not match the option-definitions within {@link #defineCommonOptions()} and {@link #defineInputOutputOptions()}
     *  @throws Exception tbd by subclasses
     */
    protected abstract void parseAdditionalOptions( String[] _args, final org.apache.commons.cli.CommandLine _apacheCmdProcessor )
                    throws MissingOptionException, ParseException, Exception;

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     *  <p>This method is to be ONLY invoked within subclasses' overridden implementation of {@link #parseAdditionalOptions(String[], org.apache.commons.cli.CommandLine)}</p>
     *  <p>This method is to be use by any Sub-classes (interested in using this 'pre-implemeted' code) - SPECIFICALLY for the 2 common instance-variables ({@link #inputFilePath} {@link #outputFilePath}) defined in this claass</p>
     *  <p>This method is part of the 3rd/final of the 3 steps to using this class.</p>
     *  <p>The 3rd/final step is composed of the following sequence: {@link #parse(String[])} in turn invokes {@link #parseCommonOptions(org.apache.commons.cli.CommandLine)}, followed by {@link #parseAdditionalOptions(String[], org.apache.commons.cli.CommandLine)}</p>
     *  <p>Recommended that thu subclasses override {@link #parseAdditionalOptions(String[], org.apache.commons.cli.CommandLine)} method, to explicity invoke this method. </p>
     *  @param _args command line argument array - as received as-is from main().
     *  @param _apacheCmdProcessor a Non-null instance
     *  @see #parseCommonOptions(org.apache.commons.cli.CommandLine)
     */
    protected final void parseInputOutputOptions( String[] _args, final org.apache.commons.cli.CommandLine _apacheCmdProcessor ) {
        final String HDR = CLASSNAME + ": parseInputOutputOptions(args[]):";
        this.inputFilePath = _apacheCmdProcessor.getOptionValue(INPUTFILE);
        if ( this.verbose ) System.out.println( HDR + "this.inputFilePath="+ this.inputFilePath );
        this.outputFilePath = _apacheCmdProcessor.getOptionValue(OUTPUTFILE);
        if ( this.verbose ) System.out.println( HDR + "this.outputFilePath="+ this.outputFilePath );
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /** Constructor.
     *  @param _args command line argument array - as received as-is from main().
     *  @throws Exception either MissingOptionException, ParseException or other (runtime) exception, when parsing the commandline.
     */
    public final void parse( String[] _args ) throws Exception
    {
        final String HDR = CLASSNAME + ": parse(args[]): ";

        this.argsAsIs.addAll( java.util.Arrays.asList(_args) );
// System.out.println( HDR + this.argsAsIs.toString() );

        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();
        org.apache.commons.cli.HelpFormatter formatter = new HelpFormatter();
        // formatter.printOptions( new java.io.PrintWriter(System.out), 120, this.options, 0, 1);
        formatter.setWidth(120);
        org.apache.commons.cli.CommandLine apacheCmdProcessor = null;

        try {
            // if ( ???.verbose ) ..
            // what if the parse() statement below has issues.. ?  We can't expect to use apacheCmdProcessor.hasOption("verbose") 
            apacheCmdProcessor = parser.parse( this.options, _args, true ); //3rd param: boolean stopAtNonOption

            //-------------------------------------------
            // this.verbose is set .. based on whether --verbose present on commandline
            this.parseCommonOptions( apacheCmdProcessor );
            if ( this.verbose ) System.out.println( HDR + this.toString() );

            //-------------------------------------------
            this.parseAdditionalOptions( _args, apacheCmdProcessor );
            if ( this.verbose ) System.out.println( HDR + this.toString() );

        } catch( MissingOptionException moe) {
            if ( this.verbose ) moe.printStackTrace(System.err); // Too Serious an Error.  We do NOT have the benefit of '--verbose',as this implies a FAILURE to parse command line.
            System.err.println( "\n\nERROR: @ "+ HDR +" Cmd-line options detected were:-\n"+ this.argsAsIs );
            formatter.printHelp( "\n\njava <jar> "+CLASSNAME, this.options );
            System.err.println( "\n\nERROR: "+ moe.getMessage() );
            throw new ParseException( moe.getMessage() );  // Specifically for use by Cmd.main()
        } catch (ParseException pe) {
            if ( this.verbose ) pe.printStackTrace(System.err); // Too Serious an Error.  We do NOT have the benefit of '--verbose',as this implies a FAILURE to parse command line.
            System.err.println( "\n\nERROR: @ "+ HDR +" Cmd-line options detected were:-\n"+ this.argsAsIs );
            formatter.printHelp( "\n\njava <jar> "+CLASSNAME, this.options );
            System.err.println( "\n\nERROR: failed to parse the command-line: "+ pe.getMessage() );
            throw pe;
        } catch( Exception e) {
            if ( this.verbose ) e.printStackTrace(System.err); // Too Serious an Error.  We do NOT have the benefit of '--verbose',as this implies a FAILURE to parse command line.
            System.err.println( "\n\nERROR: @ "+ HDR +" Cmd-line options detected were:-\n"+ this.argsAsIs );
            formatter.printHelp( "\n\njava <jar> "+CLASSNAME, this.options );
            System.err.println( "\n\nERROR: failed to parse the command-line (see error-details above) " );
            throw new ParseException( e.getMessage() );  // Specifically for use by Cmd.main()
        }
    }
    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     *  The command line arguments as-is
     *  @return instead of String[], you get an ArrayList (which is guaranteed to be NOT-Null)
     */
    public final ArrayList<String> getArgs() {
        @SuppressWarnings("unchecked")
        final ArrayList<String> ret = (ArrayList<String>) this.argsAsIs.clone();
        return ret;
    }

    //------------------------------------
    /** For making it easy to have simple code generate debugging-output, added this toString() method to this class.
     */
    public String toString() {
        return
        " --verbose="+verbose+" --showStats="+showStats
        +" inpfile="+inputFilePath+" outputfile="+outputFilePath
        +" this.quoteType=["+this.quoteType+"]  offline="+this.offline
        ;
    }

    //------------------------------------
    public boolean isOffline()              { return this.offline; }

    public Enums.ScalarStyle getQuoteType() { return this.quoteType; }

    public YAML_Libraries getYAMLLibrary()   { return this.YAMLLibrary; }


    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     * Given the original object of this class, copy these attributes to the 2nd object of this class: {@link #verbose}, {@link #showStats}, {@link #offline}, {@link #quoteType}
     * @param _orig a NotNull reference
     * @param _copy a NotNull reference
     */
    public static void copyBasicFlags( final CmdLineArgsCommon _orig, final CmdLineArgsCommon _copy ) {
        _copy.verbose   = _copy.verbose || _orig.verbose;  // pass on whatever this user specified on cmdline re: --verbose or not.
        _copy.showStats = _copy.showStats || _orig.showStats;
        _copy.offline = _copy.offline || _orig.offline;

        if ( _copy.quoteType == Enums.ScalarStyle.UNDEFINED )
            _copy.quoteType = _orig.quoteType; // if user did NOT specify a quote-option _INSIDE__ batchfile @ current line, then use whatever was specified on CmdLine when starting BATCH command.
    }

    /**
     * Copy these attributes to the provided object of this class: {@link #verbose}, {@link #showStats}, {@link #offline}, {@link #quoteType}
     * @param _copy a NotNull reference
     * @param _verbose  {@link #verbose}
     * @param _showStats {@link #showStats}
     * @param _offline {@link #offline}
     * @param _quoteType {@link #quoteType}
     */
    public static void copyBasicFlags( final CmdLineArgsCommon _copy,
                            final boolean _verbose, final boolean _showStats, final boolean _offline, final Enums.ScalarStyle _quoteType ) {
        _copy.verbose   = _copy.verbose || _verbose;  // pass on whatever this user specified on cmdline re: --verbose or not.
        _copy.showStats = _copy.showStats || _showStats;
        _copy.offline = _copy.offline || _offline;

        if ( _copy.quoteType == Enums.ScalarStyle.UNDEFINED )
            _copy.quoteType = _quoteType; // if user did NOT specify a quote-option _INSIDE__ batchfile @ current line, then use whatever was specified on CmdLine when starting BATCH command.
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

}
