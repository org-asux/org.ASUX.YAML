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
public class CmdLineArgs extends org.ASUX.yaml.CmdLineArgsCommon {

    public static final String CLASSNAME = CmdLineArgs.class.getName();
    protected static final String YAMLPATH = "yamlpath";
    protected static final String DELIMITER = "delimiter";
    protected static final String YAMLLIB = "yamllibrary";

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // These are for internal use - to help process user's commands
    protected org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    protected org.apache.commons.cli.CommandLine    apacheCmdProcessor;

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //These reflect the user's commandline options
    public String yamlRegExpStr = "undefined";
    public String yamlPatternDelimiter = ".";

    public YAML_Libraries YAMLLibrary = YAML_Libraries.NodeImpl_Library; // some default value for now

    public CmdEnum cmdType = CmdEnum.UNKNOWN;
    protected String cmdAsStr; // the string version of cmdType
    protected int numArgs = -1;

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /**
     *  @param args command line argument array - as received as-is from main().
     *  @param _cmdType enum denoting what the user's command-type was, as entered on the command line
     *  @param _shortCmd example "r" "zd"
     *  @param _longCmd example "read" "table"
     *  @param _cmdDesc long description. See org.apache.commons.cli for complex examples.
     *  @param _numArgs the # of additional arguments following this command
     *  @param _addlArgsDesc what the HELP command shows about these additional args
     *  @throws Exception like ClassNotFoundException while trying to serialize and deserialize the input-parameter
     */
    public CmdLineArgs( final String[] args, final CmdEnum _cmdType,
                            final String _shortCmd, final String _longCmd, final String _cmdDesc,
                            final int _numArgs, final String _addlArgsDesc )
                            throws Exception
    {
        this.args.addAll( java.util.Arrays.asList(args) );
        this.cmdType = _cmdType;
        this.cmdAsStr = _longCmd;
        this.numArgs = _numArgs;

        //==================================
        Option opt;

        super.defineCommonOptions( this.options );

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
            // what if the parse() statement below has issues.. ?  We can't expect to use this.apacheCmdProcessor.hasOption("verbose") 
// System.err.print( CLASSNAME +" parse(): _args = "+ _args +"  >>>>>>>>>>>>> "); for( String s: _args) System.out.print(s+"\t");  System.out.println();
// System.err.println( CLASSNAME +" parse(): this = "+ this.toString() );
            this.apacheCmdProcessor = parser.parse( this.options, _args );

            super.parseCommonOptions( this.apacheCmdProcessor );

            this.yamlPatternDelimiter = this.apacheCmdProcessor.getOptionValue(DELIMITER);
            if ( this.yamlPatternDelimiter == null || this.yamlPatternDelimiter.equals(".") )
                this.yamlPatternDelimiter = YAMLPath.DEFAULTDELIMITER;

            if ( this.apacheCmdProcessor.getOptionValue(YAMLLIB) != null )
                this.YAMLLibrary = YAML_Libraries.fromString( this.apacheCmdProcessor.getOptionValue(YAMLLIB) );
            else
                this.YAMLLibrary = YAML_Libraries.SNAKEYAML_Library; // default.

            assert( this.apacheCmdProcessor.hasOption( this.cmdAsStr ) ); // sanity check

            // following are defined to be optional arguments, but mandatory for a specific command (as you can see from the condition of the IF statements).
            this.yamlRegExpStr = this.apacheCmdProcessor.getOptionValue( this.cmdAsStr );

            final String[] addlArgs = this.apacheCmdProcessor.getOptionValues( this.cmdAsStr ); // CmdLineArgsBasic.REPLACECMD[1]
            if ( this.numArgs != addlArgs.length )
                throw new Exception ( CLASSNAME + ": parse("+ _args +"): does Not have the required "+ this.numArgs +" additional-arguments to the "+ this.cmdAsStr +" command" );
            this.moreParsing( _args );

            // System.err.println( CLASSNAME +": "+this.toString());

        } catch (ParseException e) {
            e.printStackTrace(System.err); // Too Serious an Error.  We do NOT have the benefit of '--verbose',as this implies a FAILURE to parse command line.
            formatter.printHelp( "\njava <jarL> "+CLASSNAME, this.options );
            System.err.println( "\n\n"+ CLASSNAME +" parse(): failed to parse the command-line: "+ this.options );
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
    @Override
    public String toString() {
        return
        super.toString()
        +" YAML-Library="+YAMLLibrary
        +" yamlRegExpStr="+yamlRegExpStr+" yamlPatternDelimiter="+yamlPatternDelimiter
        +" Cmd-Type="+cmdType +"("+cmdAsStr+") "
        ;
    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // For unit-testing purposes only
    public static void main(String[] args) {
        try{
            final CmdLineArgs cmdLineArgsBase = new CmdLineArgs( args, CmdEnum.READ, CmdLineArgsBasic.READCMD[0], CmdLineArgsBasic.READCMD[1], CmdLineArgsBasic.READCMD[2], 1, "YAMLPattern" );
            cmdLineArgsBase.parse(args);
        } catch( Exception e) {
            e.printStackTrace(System.err); // main() for unit testing
            System.exit(1);
        }
    }

}
