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

import org.apache.commons.cli.*;

import java.util.Arrays;

/** <p>This class is a typical use of the org.apache.commons.cli package.</p>
 *  <p>This class has No other function - other than to parse the commandline arguments and handle user's input errors.</p>
 *  <p>For making it easy to have simple code generate debugging-output, added a toString() method to this class.</p>
 *  <p>Typical use of this class is: </p>
 *
 *  <p>See full details of how to use this, in {@link org.ASUX.yaml.Cmd} as well as the <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com project.</p>
 * @see org.ASUX.yaml.Cmd
 */
public class CmdLineArgsBasic extends org.ASUX.yaml.CmdLineArgsCommon {

    private static final long serialVersionUID = 330L;
    public static final String CLASSNAME = CmdLineArgsBasic.class.getName();

    public static final String[] READCMD = { "r", "read", "output all YAML-elements that match" };
    public static final String[] LISTCMD = { "l", "list", "List YAML-Keys (lhs) that match" };
    public static final String[] INSERTCMD = { "n", "insert", "insert new element (json-string parameter) @ the locations identified by the YAML path" };
    public static final String[] REPLACECMD = { "c", "replace", "change/replace all elements that match with json-string provided on cmdline" };
    public static final String[] DELETECMD = { "d", "delete", "Delete all elements that match" };
    public static final String[] TABLECMD = { "t", "table", "produce a tabular output like a traditional SQL-query would" };
    // public static final char REPLACECMDCHAR = 'c'; // -c === --replace
    public static final String[] MACROYAMLCMD = { "m", "macroyaml", "run valid-proper YAML-input (file) thru a MACRO processor searching for ${ASUX::__} and replacing __ with values from Properties file" };
    public static final String[] BATCHCMD = { "b", "batch", "run a batch of commands, which are listed in the <batchfile>" };

    protected static final String DELIMITER = "delimiter";
    public static final String NOQUOTE      = "no-quote";
    public static final String SINGLEQUOTE  = "single-quote";
    public static final String DOUBLEQUOTE  = "double-quote";

    protected static final String OFFLINE   = "offline";

    protected static final String YAMLLIB = "yamllibrary";

    //------------------------------------
    // private CmdLineArgs cmdLineArgs;

    public Enums.CmdEnum cmdType = Enums.CmdEnum.UNKNOWN;
    public String yamlPatternDelimiter = ".";

    protected final org.apache.commons.cli.Options options = new Options();

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
        final Option opt = new Option( _short, _long, (_numArgs > 0), _description );
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
    //=================================================================================

    /** <p>This method is the 2nd of the 3 steps to using this class.</p>
     *  <p>Subclass should Not override this, but can extend this method.</p>
     */
    public void define() {
        this.defineCommonOptions();
        this.defineAdditionalOptions();
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     * Add cmd-line argument definitions (using apache.commons.cli.Options) for the instance-variables defined in this class: {@link #verbose}, {@link #showStats}, {@link #offline}, {@link #quoteType}
     */
    protected void defineCommonOptions()
    {   // final String HDR = CLASSNAME + ": defineCommonOptions(): ";
        Option opt;

        //----------------------------------
        addSimpleOption( this.options, "v",  "verbose", "Show debug output" );
        addSimpleOption( this.options, "vs", "showStats", "Show - at end output - a summary of how many matches happened, or entries were affected" );
        addSimpleOption( this.options, "zzz", OFFLINE, "set internet-connection to off (or, you want to pretend there's no internet) " );

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
        opt = genOption( "zy", YAMLLIB, "only valid values are: "+ YAML_Libraries.list("\t"), 1, "yamllibparam" );
        opt.setRequired(false);
        this.options.addOption(opt);

// System.out.println( HDR +"completed function." );
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     *  <p>Add cmd-line argument definitions (using apache.commons.cli.Options) for the instance-variables defined in this class.</p>
     */
    protected void defineAdditionalOptions()
    {   // final String HDR = CLASSNAME + ": defineAdditionalOptions(): ";

        //----------------------------------
        // OptionGroup grp = new OptionGroup();
        // Option readCmdOpt = new Option( READCMD[0], READCMD[1], true, READCMD[2] );
        // Option listCmdOpt = new Option( LISTCMD[0], LISTCMD[1], true, LISTCMD[2] );
        // Option insCmdOpt = new Option( INSERTCMD[0], INSERTCMD[1], true, INSERTCMD[2] );
        // Option replCmdOpt = new Option( REPLACECMD[0], REPLACECMD[1], true, REPLACECMD[2] );
        // Option delCmdOpt = new Option( DELETECMD[0], DELETECMD[1], true, DELETECMD[2] );
        // Option tableCmdOpt = new Option( TABLECMD[0], TABLECMD[1], false, TABLECMD[2] );
        // Option macroCmdOpt = new Option( MACROYAMLCMD[0], MACROYAMLCMD[1], true, MACROYAMLCMD[2] );
        // Option batchCmdOpt = new Option( BATCHCMD[0], BATCHCMD[1], true, BATCHCMD[2] );
        // grp.addOption(readCmdOpt);
        // grp.addOption(listCmdOpt);
        // grp.addOption(tableCmdOpt);
        // grp.addOption(delCmdOpt);
        // grp.addOption(insCmdOpt);
        // grp.addOption(replCmdOpt);
        // grp.addOption(macroCmdOpt);
        // grp.addOption(batchCmdOpt);
        // grp.setRequired(true);
        //
        // this.options.addOptionGroup(grp);

        //----------------------------------
        final Option opt = genOption( "zd", DELIMITER, "whether period/dot comma pipe or other character is the delimiter to use within the YAMLPATHPATTERN",
                                            1, "delimcharacter" );
        opt.setRequired(false);
        this.options.addOption(opt);

    } // method

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
    {   final String HDR = CLASSNAME + ": parseCommonOptions(apache.commons.cli): ";

        this.verbose = _apacheCmdProcessor.hasOption("verbose");
        this.showStats = _apacheCmdProcessor.hasOption("showStats");

        this.offline = _apacheCmdProcessor.hasOption(OFFLINE);

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

        YAMLImplementation.setDefaultYAMLImplementation( this.YAMLLibrary );
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
     *  @throws MissingOptionException if user has Not provided the required cmd-line options/arguments as defined within {@link #defineCommonOptions()}.
     *  @throws ParseException if the additional-arguments in the user-provided commands line, do Not match the option-definitions within {@link #defineCommonOptions()}.
     *  @throws Exception tbd by subclasses
     */
    protected void parseAdditionalOptions( String[] _args, final org.apache.commons.cli.CommandLine _apacheCmdProcessor )
                    throws MissingOptionException, ParseException, Exception
    {
        final String HDR = CLASSNAME + ": parseAdditionalOptions([]],..): ";

        //-----------------------
        // parseInputOutputOptions( _args, _apacheCmdProcessor );

        //-----------------------
        this.yamlPatternDelimiter = _apacheCmdProcessor.getOptionValue(DELIMITER);
        if ( this.yamlPatternDelimiter == null || this.yamlPatternDelimiter.equals(".") )
            this.yamlPatternDelimiter = YAMLPath.DEFAULTDELIMITER;

        //----------------------------------------------

        if ( _apacheCmdProcessor.hasOption(READCMD[1]) ) {
            this.cmdType = Enums.CmdEnum.READ;
            // this.cmdLineArgs = new CmdLineArgs( this.cmdType, READCMD[0], READCMD[1], READCMD[2], 1, "YAMLPattern" );
        }
        if ( _apacheCmdProcessor.hasOption(LISTCMD[1]) ) {
            this.cmdType = Enums.CmdEnum.LIST;
            // this.cmdLineArgs = new CmdLineArgs( this.cmdType, LISTCMD[0], LISTCMD[1], LISTCMD[2], 1, "YAMLPattern" );
        }
        if ( _apacheCmdProcessor.hasOption(INSERTCMD[1]) ) {
            this.cmdType = Enums.CmdEnum.INSERT;
            // final CmdLineArgsInsertCmd insertCmdLineArgs = new CmdLineArgsInsertCmd( this.cmdType, INSERTCMD[0], INSERTCMD[1], INSERTCMD[2], 2, "YAMLPattern> <newValue" );
            // this.cmdLineArgs = insertCmdLineArgs;
        }
        if ( _apacheCmdProcessor.hasOption(DELETECMD[1]) ) {
            this.cmdType = Enums.CmdEnum.DELETE;
            // this.cmdLineArgs = new CmdLineArgs( this.cmdType, DELETECMD[0], DELETECMD[1], DELETECMD[2], 1, "YAMLPattern" );
        }
        if ( _apacheCmdProcessor.hasOption(REPLACECMD[1]) ) {
            this.cmdType = Enums.CmdEnum.REPLACE;
            // final CmdLineArgsReplaceCmd replaceCmdLineArgs = new CmdLineArgsReplaceCmd( this.cmdType, REPLACECMD[0], REPLACECMD[1], REPLACECMD[2], 2, "YAMLPattern> <newValue" );
            // this.cmdLineArgs = replaceCmdLineArgs;
        }
        if ( _apacheCmdProcessor.hasOption(TABLECMD[1]) ) {
            this.cmdType = Enums.CmdEnum.TABLE;
            // final CmdLineArgsTableCmd tableCmdLineArgs = new CmdLineArgsTableCmd( this.cmdType, TABLECMD[0], TABLECMD[1], TABLECMD[2], 2, "YAMLPattern> <column,column" );
            // this.cmdLineArgs = tableCmdLineArgs;
        }
        if ( _apacheCmdProcessor.hasOption(MACROYAMLCMD[1]) ) {
            this.cmdType = Enums.CmdEnum.MACROYAML;
            // final CmdLineArgsMacroCmd macroCmdLineArgs = new CmdLineArgsMacroCmd( this.cmdType, MACROYAMLCMD[0], MACROYAMLCMD[1], MACROYAMLCMD[2], 1, "propertiesFile" );
            // this.cmdLineArgs = macroCmdLineArgs;
        }
        if ( _apacheCmdProcessor.hasOption(BATCHCMD[1]) ) {
            this.cmdType = Enums.CmdEnum.BATCH;
            // final CmdLineArgsBatchCmd batchCmdLineArgs = new CmdLineArgsBatchCmd( this.cmdType, BATCHCMD[0], BATCHCMD[1], BATCHCMD[2], 1, "batchFile" );
            // this.cmdLineArgs = batchCmdLineArgs;
        }


// ?????
// ?????
// ?????
// ?????
// ?????
// ?????
// Following 2 lines are commented out.  Before uncommenting these 2 lines, get rid of constructors for various subclasses of CmdLineArgs.java above.
//         this.cmdLineArgs.define(); // define is overridden in this class.
//         this.cmdLineArgs.parse( _args );
// ?????
// ?????
// ?????
// ?????
// ?????
// ?????

        //-----------------------
        if ( this.verbose ) System.err.println( HDR +": "+this.toString());
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

//     /**
//      * Add cmd-line argument definitions (using apache.commons.cli.Options) for 2 specific instance-variables defined in this class:
//      */
//     protected void defineInputOutputOptions()
//     {   final String HDR = CLASSNAME + ": defineInputOutputOptions(): ";
//         Option opt;
//
//         //----------------------------------
//         opt = genOption( "i", INPUTFILE, "input file path", 1, "filename" );
//         opt.setRequired(true);
//         this.options.addOption(opt);
//
//         opt = genOption( "o", OUTPUTFILE,  "output file path", 1, "new-file" );
//         opt.setRequired(true);
//         this.options.addOption(opt);
//
// // System.out.println( HDR +"completed function." );
//     }


    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    // /**
    //  *  <p>This method is to be ONLY invoked within subclasses' overridden implementation of {@link #parseAdditionalOptions(String[], org.apache.commons.cli.CommandLine)}</p>
    //  *  <p>This method is to be use by any Sub-classes (interested in using this 'pre-implemeted' code) - SPECIFICALLY for the 2 common instance-variables ({@link #inputFilePath} {@link #outputFilePath}) defined in this claass</p>
    //  *  <p>This method is part of the 3rd/final of the 3 steps to using this class.</p>
    //  *  <p>The 3rd/final step is composed of the following sequence: {@link #parse(String[])} in turn invokes {@link #parseCommonOptions(org.apache.commons.cli.CommandLine)}, followed by {@link #parseAdditionalOptions(String[], org.apache.commons.cli.CommandLine)}</p>
    //  *  <p>Recommended that thu subclasses override {@link #parseAdditionalOptions(String[], org.apache.commons.cli.CommandLine)} method, to explicity invoke this method. </p>
    //  *  @param _args command line argument array - as received as-is from main().
    //  *  @param _apacheCmdProcessor a Non-null instance
    //  *  @see #parseCommonOptions(org.apache.commons.cli.CommandLine)
    //  */
    // protected final void parseInputOutputOptions( String[] _args, final org.apache.commons.cli.CommandLine _apacheCmdProcessor ) {
    //     final String HDR = CLASSNAME + ": parseInputOutputOptions(args[]):";
    //     this.inputFilePath = _apacheCmdProcessor.getOptionValue(INPUTFILE);
    //     if ( this.verbose ) System.out.println( HDR + "this.inputFilePath="+ this.inputFilePath );
    //     this.outputFilePath = _apacheCmdProcessor.getOptionValue(OUTPUTFILE);
    //     if ( this.verbose ) System.out.println( HDR + "this.outputFilePath="+ this.outputFilePath );
    // }

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

        this.cmdAsStr = Arrays.toString( _args );
// System.out.println( HDR + this.argsAsIs.toString() );

        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();
        org.apache.commons.cli.HelpFormatter formatter = new HelpFormatter();
        // formatter.printOptions( new java.io.PrintWriter(System.out), 120, this.options, 0, 1);
        formatter.setWidth(120);
        org.apache.commons.cli.CommandLine apacheCmdProcessor;

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
            System.err.println( "\n\nERROR: @ "+ HDR +" Cmd-line options detected were:-\n"+ this.cmdAsStr );
            formatter.printHelp( "\n\njava <jar> "+CLASSNAME, this.options );
            System.err.println( "\n\nERROR: "+ moe.getMessage() );
            throw new ParseException( moe.getMessage() );  // Specifically for use by Cmd.main()
        } catch (ParseException pe) {
            if ( this.verbose ) pe.printStackTrace(System.err); // Too Serious an Error.  We do NOT have the benefit of '--verbose',as this implies a FAILURE to parse command line.
            System.err.println( "\n\nERROR: @ "+ HDR +" Cmd-line options detected were:-\n"+ this.cmdAsStr );
            formatter.printHelp( "\n\njava <jar> "+CLASSNAME, this.options );
            System.err.println( "\n\nERROR: failed to parse the command-line: "+ pe.getMessage() );
            throw pe;
        } catch( Exception e) {
            if ( this.verbose ) e.printStackTrace(System.err); // Too Serious an Error.  We do NOT have the benefit of '--verbose',as this implies a FAILURE to parse command line.
            System.err.println( "\n\nERROR: @ "+ HDR +" Cmd-line options detected were:-\n"+ this.cmdAsStr );
            formatter.printHelp( "\n\njava <jar> "+CLASSNAME, this.options );
            System.err.println( "\n\nERROR: failed to parse the command-line (see error-details above) " );
            throw new ParseException( e.getMessage() );  // Specifically for use by Cmd.main()
        }
    } // parse

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /** For making it easy to have simple code generate debugging-output, added this toString() method to this class.
     */
    @Override
    public String toString() {
        return
        super.toString()
        +" yamlPatternDelimiter="+ this.yamlPatternDelimiter
        +" Cmd-Type="+this.cmdType
        ;
    }


    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

}
