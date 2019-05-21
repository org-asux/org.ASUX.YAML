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

/** <p>This class is a typical use of the org.apache.commons.cli package.</p>
 *  <p>This class has No other function - other than to parse the commandline arguments and handle user's input errors.</p>
 *  <p>For making it easy to have simple code generate debugging-output, added a toString() method to this class.</p>
 *  <p>Typical use of this class is: </p>
 *<pre>
 public static void main(String[] args) {
 CmdLineArgs = new CmdLineArgs(args);
 .. ..
 *</pre>
 *
 *  <p>See full details of how to use this, in {@link org.ASUX.yaml.Cmd} as well as the <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com project.</p>
 * @see org.ASUX.yaml.Cmd
 */
public class CmdLineArgs {

    public static final String CLASSNAME = CmdLineArgs.class.getName();

    private static final String INPUTFILE = "inputfile";
    private static final String OUTPUTFILE = "outputfile";
    private static final String YAMLPATH = "yamlpath";
    private static final String DELIMITER = "delimiter";
    private static final String YAMLLIB = "yamllibrary";
    // private static final String PROPERTIES = "properties";

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // These are for internal use - to help process user's commands
    protected org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    protected org.apache.commons.cli.CommandLine    apacheCmd;
    public CmdEnum cmdType = CmdEnum.UNKNOWN;
    protected String cmdAsStr;
    protected int numArgs = -1;

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //These reflect the user's commandline options
    public boolean verbose = false;
    public boolean showStats = false;

    public String yamlRegExpStr = "undefined";
    public String yamlPatternDelimiter = ".";

    public String inputFilePath = "/tmp/undefined";
    public String outputFilePath = "/tmp/undefined";

    public YAML_Libraries YAMLLibrary = YAML_Libraries.NodeImpl_Library; // some default value for now
    // public com.esotericsoftware.yamlbeans.YamlConfig.Quote quoteType = com.esotericsoftware.yamlbeans.YamlConfig.Quote.SINGLE;

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /**
     *  @param _cmdType enum denoting what the user's command-type was, as entered on the command line
     *  @param _shortCmd example "r" "zd"
     *  @param _longCmd example "read" "table"
     *  @param _cmdDesc long description. See org.apache.commons.cli for complex examples.
     *  @param _numArgs the # of additional arguments following this command
     *  @param _addlArgsDesc what the HELP command shows about these additional args
     *  @throws Exception like ClassNotFoundException while trying to serialize and deserialize the input-parameter
     */
    public CmdLineArgs( final CmdEnum _cmdType,
                            final String _shortCmd, final String _longCmd, final String _cmdDesc,
                            final int _numArgs, final String _addlArgsDesc )
                            throws Exception
    {
        this.cmdType = _cmdType;
        this.cmdAsStr = _longCmd;
        this.numArgs = _numArgs;

        //==================================
        Option opt;

        opt= new Option("v", "verbose", false, "Show debug output");
        opt.setRequired(false);
        this.options.addOption(opt);

        opt= new Option("v", "showStats", false, "Show - at end output - a summary of how many matches happened, or entries were affected");
        opt.setRequired(false);
        this.options.addOption(opt);

        //----------------------------------
        OptionGroup grp2 = new OptionGroup();
        Option noQuoteOpt = new Option("nq", "no-quote", false, "do Not use Quotes in YAML output");
        Option singleQuoteOpt = new Option("sq", "single-quote", false, "use ONLY Single-quote when generating YAML output");
        Option doubleQuoteOpt = new Option("dq", "double-quote", false, "se ONLY Double-quote when generating YAML output");
        grp2.addOption(noQuoteOpt);
        grp2.addOption(singleQuoteOpt);
        grp2.addOption(doubleQuoteOpt);
        grp2.setRequired(false);

        this.options.addOptionGroup(grp2);

        //----------------------------------
        opt = new Option("zd", DELIMITER, false, "whether period/dot comma pipe or other character is the delimiter to use within the YAMLPATHPATTERN");
        opt.setRequired(false);
        opt.setArgs(1);
        opt.setOptionalArg(false);
        opt.setArgName("delimcharacter");
        this.options.addOption(opt);

        opt = new Option("zy", YAMLLIB, false, "only valid values are: "+ YAML_Libraries.list("\t") );
        opt.setRequired(false);
        opt.setArgs(1);
        opt.setOptionalArg(false);
        opt.setArgName("yamllibparam");
        opt.setType(YAML_Libraries.class);
        this.options.addOption(opt);

        //----------------------------------
        opt = new Option("i", INPUTFILE, true, "input file path");
        opt.setRequired(true);
        this.options.addOption(opt);

        opt = new Option("o", OUTPUTFILE, true, "output file");
        opt.setRequired(true);
        this.options.addOption(opt);

        //----------------------------------
        Option thisCmdOpt = new Option( _shortCmd, _longCmd, false, _cmdDesc );
            thisCmdOpt.setRequired(true);
            thisCmdOpt.setOptionalArg(false);
            thisCmdOpt.setArgs( _numArgs );
            if ( _numArgs > 1 )
                thisCmdOpt.setValueSeparator(' ');
            thisCmdOpt.setArgName( _addlArgsDesc );

        this.options.addOption( thisCmdOpt );

    } // method

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /** Constructor.
     *  @param _args command line argument array - as received as-is from main().
     *  @throws Exception like ClassNotFoundException while trying to serialize and deserialize the input-parameter
     */
    public final void parse( String[] _args ) throws Exception
    {
        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();
        org.apache.commons.cli.HelpFormatter formatter = new HelpFormatter();

        try {
            // if ( ???.verbose ) ..
            // what if the parse() statement below has issues.. ?  We can't expect to use this.apacheCmd.hasOption("verbose") 
// System.err.print( CLASSNAME +" parse(): _args = "+ _args +"  >>>>>>>>>>>>> "); for( String s: _args) System.out.print(s+"\t");  System.out.println();
// System.err.println( CLASSNAME +" parse(): this = "+ this.toString() );
            this.apacheCmd = parser.parse( this.options, _args );

            this.verbose = this.apacheCmd.hasOption("verbose");
            this.showStats = this.apacheCmd.hasOption("showStats");

            this.yamlPatternDelimiter = this.apacheCmd.getOptionValue(DELIMITER);
            if ( this.yamlPatternDelimiter == null || this.yamlPatternDelimiter.equals(".") )
                this.yamlPatternDelimiter = YAMLPath.DEFAULTDELIMITER;

            if ( this.apacheCmd.getOptionValue(YAMLLIB) != null )
                this.YAMLLibrary = YAML_Libraries.fromString( this.apacheCmd.getOptionValue(YAMLLIB) );
            else
                this.YAMLLibrary = YAML_Libraries.SNAKEYAML_Library; // default.

            assert( this.apacheCmd.hasOption( this.cmdAsStr ) ); // sanity check

            // this.yamlRegExpStr = this.apacheCmd.getOptionValue(YAMLPATH);
            this.inputFilePath = this.apacheCmd.getOptionValue(INPUTFILE);
            this.outputFilePath = this.apacheCmd.getOptionValue(OUTPUTFILE);

            // following are defined to be optional arguments, but mandatory for a specific command (as you can see from the condition of the IF statements).
            this.yamlRegExpStr = this.apacheCmd.getOptionValue( this.cmdAsStr );

            final String[] addlArgs = this.apacheCmd.getOptionValues( this.cmdAsStr ); // CmdLineArgsBasic.REPLACECMD[1]
            if ( this.numArgs != addlArgs.length )
                throw new Exception ( CLASSNAME + ": parse("+ _args +"): does Not have the required "+ this.numArgs +" additional-arguments to the "+ this.cmdAsStr +" command" );
            this.moreParsing( _args );

            // this.quoteType = com.esotericsoftware.yamlbeans.YamlConfig.Quote.SINGLE; // default behavior
            // if ( this.apacheCmd.hasOption( noQuoteOpt.getLongOpt()) ) this.quoteType = com.esotericsoftware.yamlbeans.YamlConfig.Quote.NONE;
            // if ( this.apacheCmd.hasOption( singleQuoteOpt.getLongOpt()) ) this.quoteType = com.esotericsoftware.yamlbeans.YamlConfig.Quote.SINGLE;
            // if ( this.apacheCmd.hasOption( doubleQuoteOpt.getLongOpt()) ) this.quoteType = com.esotericsoftware.yamlbeans.YamlConfig.Quote.DOUBLE;
            // if ( this.verbose ) System.out.println("this.quoteType = "+this.quoteType.toString());

            // System.err.println( CLASSNAME +": "+this.toString());

        } catch (ParseException e) {
            e.printStackTrace(System.err);
            System.err.println( CLASSNAME +" parse(): failed for "+ this.options );
            formatter.printHelp( "java <jarL> "+CLASSNAME, this.options );
            throw e;
        }
    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /**
     *  <p>Subclasses to override this method to parse for additional options.</p>
     *  <p>This method does nothing in this parent class</p>
     *  @param _args command line argument array - as received as-is from main().
     *  @throws Exception like ClassNotFoundException while trying to serialize and deserialize the input-parameter
     */
    protected void moreParsing( String[] _args ) throws Exception {
        // Do nothing in this parent-class.
    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /** For making it easy to have simple code generate debugging-output, added this toString() method to this class.
     */
    public String toString() {
        return
        "CmdEnumType="+cmdType +"("+cmdAsStr+") # of addl-args="+numArgs
        +"verbose="+verbose+" showStats="+showStats+" YAML-Library="+YAMLLibrary
        +" yamlRegExpStr="+yamlRegExpStr+" yamlPatternDelimiter="+yamlPatternDelimiter
        +" inpfile="+inputFilePath+" outputfile="+outputFilePath
        ;
        // yamlRegExpStr="+yamlRegExpStr+" 
    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // For unit-testing purposes only
    public static void main(String[] args) {
        try{
            final CmdLineArgs cmdLineArgsBase = new CmdLineArgs( CmdEnum.READ, CmdLineArgsBasic.READCMD[0], CmdLineArgsBasic.READCMD[1], CmdLineArgsBasic.READCMD[2], 1, "YAMLPattern" );
            cmdLineArgsBase.parse(args);
        } catch( Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
