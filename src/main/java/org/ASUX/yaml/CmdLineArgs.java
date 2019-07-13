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

import static org.junit.Assert.*;

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

    private static final long serialVersionUID = 333L;
    public static final String CLASSNAME = CmdLineArgs.class.getName();

    protected static final String YAMLPATH = "yamlpath";
    protected static final String DELIMITER = "delimiter";
    protected static final String YAMLLIB = "yamllibrary";

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //These reflect the user's commandline options
    public String yamlRegExpStr = "undefined";
    public String yamlPatternDelimiter = ".";

    public final Enums.CmdEnum cmdType;
    protected final String cmdAsStr; // the string version of cmdType

    protected int numArgs = -1;
    protected String shortCmd = null;
    protected String longCmd = null;
    protected String cmdDesc = null;
    protected String addlArgsDesc = null;

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
    public CmdLineArgs( final String[] args, final Enums.CmdEnum _cmdType,
                            final String _shortCmd, final String _longCmd, final String _cmdDesc,
                            final int _numArgs, final String _addlArgsDesc )
                            throws Exception
    {
        this.cmdType = _cmdType;
        this.cmdAsStr = _longCmd;

        this.shortCmd = _shortCmd;
        this.longCmd = _longCmd;
        this.cmdDesc = _cmdDesc;
        this.numArgs = _numArgs;
        this.addlArgsDesc = _addlArgsDesc;

        //------------------------------  !!!!!!!!!! ATTENTION below !!!!!!!!!!!
        this.define(); // define is overridden in this class.
        super.parse( args );
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    // /** @see org.ASUX.yaml.CmdLineArgsCommon#
    //  */
    // @Override
    // public void define() {
    //     super.define(); // this will automatically invoke defineAdditionalOptions();
    //     super.defineInputOutputOptions();
    // }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     *  <p>Add cmd-line argument definitions (using apache.commons.cli.Options) for the instance-variables defined in this class.</p>
     *  @param options a Non-Null instance of org.apache.commons.cli.Options
     */
    @Override
    protected void defineAdditionalOptions()
    {   final String HDR = CLASSNAME + ": defineAdditionalOptions(): ";
        Option opt;

        //----------------------------------
        super.defineInputOutputOptions();

        //----------------------------------
        opt = CmdLineArgsCommon.genOption( "zd", DELIMITER, "whether period/dot comma pipe or other character is the delimiter to use within the YAMLPATHPATTERN",
                                            1, "delimcharacter" );
        opt.setRequired(false);
        this.options.addOption(opt);

        //----------------------------------
        opt = genOption( this.shortCmd, this.longCmd, this.cmdDesc, this.numArgs, this.addlArgsDesc);
        opt.setRequired(true);
        this.options.addOption( opt );

        if ( this.verbose ) System.out.println( HDR +"completed function." );
    } // method

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    /**
     *  @see org.ASUX.yaml.CmdLineArgsCommon#parseAdditionalOptions
     */
    @Override
    protected void parseAdditionalOptions( String[] _args, final org.apache.commons.cli.CommandLine _apacheCmdProcessor )
                    throws MissingOptionException, ParseException, Exception
    {
        final String HDR = CLASSNAME + ": parseAdditionalOptions([]],..): ";

        //-----------------------
        super.parseInputOutputOptions( _args, _apacheCmdProcessor );

        //-----------------------
        this.yamlPatternDelimiter = _apacheCmdProcessor.getOptionValue(DELIMITER);
        if ( this.yamlPatternDelimiter == null || this.yamlPatternDelimiter.equals(".") )
            this.yamlPatternDelimiter = YAMLPath.DEFAULTDELIMITER;

        assertTrue( _apacheCmdProcessor.hasOption( this.cmdAsStr ) ); // sanity check

        //-----------------------
        // following are defined to be optional arguments, but mandatory for a specific command (as you can see from the condition of the IF statements).
        this.yamlRegExpStr = _apacheCmdProcessor.getOptionValue( this.cmdAsStr );

        final String[] addlArgs = _apacheCmdProcessor.getOptionValues( this.cmdAsStr ); // CmdLineArgsBasic.REPLACECMD[1]
        if ( this.numArgs != addlArgs.length )
            throw new Exception ( CLASSNAME + ": parse("+ _args +"): does Not have the required "+ this.numArgs +" additional-arguments to the "+ this.cmdAsStr +" command" );

        //-----------------------
        if ( this.verbose ) System.err.println( HDR +": "+this.toString());
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
            final CmdLineArgs cmdLineArgsBase = new CmdLineArgs( args, Enums.CmdEnum.READ, CmdLineArgsBasic.READCMD[0], CmdLineArgsBasic.READCMD[1], CmdLineArgsBasic.READCMD[2], 1, "YAMLPattern" );
            cmdLineArgsBase.define();
            cmdLineArgsBase.parse(args);
        } catch( Exception e) {
            e.printStackTrace(System.err); // main() for unit testing
            System.exit(1);
        }
    }

}
