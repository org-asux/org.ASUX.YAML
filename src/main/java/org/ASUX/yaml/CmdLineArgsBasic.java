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

/** <p>This class is a typical use of the org.apache.commons.cli package.</p>
 *  <p>This class has No other function - other than to parse the commandline arguments and handle user's input errors.</p>
 *  <p>For making it easy to have simple code generate debugging-output, added a toString() method to this class.</p>
 *  <p>Typical use of this class is: </p>
 *<pre>
 public static void main(String[] args) {
 cmdLineArgs = new CmdLineArgsBasic(args);
 .. ..
 *</pre>
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

    //------------------------------------
    private CmdLineArgs cmdLineArgs;

    public Enums.CmdEnum cmdType = Enums.CmdEnum.UNKNOWN;
    public String yamlPatternDelimiter = ".";

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    //------------------------------------
    /**
     * This object reference is either to a CmdLineArgs class (for READ, LIST and DELETE commands), or subclasses of CmdLineArgs (for INSERT, REPLACE, TABLE, MACRO, BATCH commands)
     * @return either an instance of CmdLineArgs or one of it's subclasses (depends on this.cmdType {@link #cmdType})
     */
    public CmdLineArgs getSpecificCmd() {
        return this.cmdLineArgs;
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     *  <p>Add cmd-line argument definitions (using apache.commons.cli.Options) for the instance-variables defined in this class.</p>
     */
    @Override
    protected void defineAdditionalOptions()
    {   final String HDR = CLASSNAME + ": defineAdditionalOptions(): ";

        Option opt;

        //----------------------------------
        OptionGroup grp = new OptionGroup();
        Option readCmdOpt = new Option( READCMD[0], READCMD[1], true, READCMD[2] );
        Option listCmdOpt = new Option( LISTCMD[0], LISTCMD[1], true, LISTCMD[2] );
        Option insCmdOpt = new Option( INSERTCMD[0], INSERTCMD[1], true, INSERTCMD[2] );
        Option replCmdOpt = new Option( REPLACECMD[0], REPLACECMD[1], true, REPLACECMD[2] );
        Option delCmdOpt = new Option( DELETECMD[0], DELETECMD[1], true, DELETECMD[2] );
        Option tableCmdOpt = new Option( TABLECMD[0], TABLECMD[1], false, TABLECMD[2] );
        Option macroCmdOpt = new Option( MACROYAMLCMD[0], MACROYAMLCMD[1], true, MACROYAMLCMD[2] );
        Option batchCmdOpt = new Option( BATCHCMD[0], BATCHCMD[1], true, BATCHCMD[2] );
        grp.addOption(readCmdOpt);
        grp.addOption(listCmdOpt);
        grp.addOption(tableCmdOpt);
        grp.addOption(delCmdOpt);
        grp.addOption(insCmdOpt);
        grp.addOption(replCmdOpt);
        grp.addOption(macroCmdOpt);
        grp.addOption(batchCmdOpt);
        grp.setRequired(true);

        this.options.addOptionGroup(grp);

        //----------------------------------
        opt = CmdLineArgsCommon.genOption( "zd", DELIMITER, "whether period/dot comma pipe or other character is the delimiter to use within the YAMLPATHPATTERN",
                                            1, "delimcharacter" );
        opt.setRequired(false);
        this.options.addOption(opt);

    } // method

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

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

        //----------------------------------------------

        if ( _apacheCmdProcessor.hasOption(READCMD[1]) ) {
            this.cmdType = Enums.CmdEnum.READ;
            this.cmdLineArgs = new CmdLineArgs( this.cmdType, READCMD[0], READCMD[1], READCMD[2], 1, "YAMLPattern" );
        }
        if ( _apacheCmdProcessor.hasOption(LISTCMD[1]) ) {
            this.cmdType = Enums.CmdEnum.LIST;
            this.cmdLineArgs = new CmdLineArgs( this.cmdType, LISTCMD[0], LISTCMD[1], LISTCMD[2], 1, "YAMLPattern" );
        }
        if ( _apacheCmdProcessor.hasOption(INSERTCMD[1]) ) {
            this.cmdType = Enums.CmdEnum.INSERT;
            final CmdLineArgsInsertCmd insertCmdLineArgs = new CmdLineArgsInsertCmd( this.cmdType, INSERTCMD[0], INSERTCMD[1], INSERTCMD[2], 2, "YAMLPattern> <newValue" );
            this.cmdLineArgs = insertCmdLineArgs;
        }
        if ( _apacheCmdProcessor.hasOption(DELETECMD[1]) ) {
            this.cmdType = Enums.CmdEnum.DELETE;
            this.cmdLineArgs = new CmdLineArgs( this.cmdType, DELETECMD[0], DELETECMD[1], DELETECMD[2], 1, "YAMLPattern" );
        }
        if ( _apacheCmdProcessor.hasOption(REPLACECMD[1]) ) {
            this.cmdType = Enums.CmdEnum.REPLACE;
            final CmdLineArgsReplaceCmd replaceCmdLineArgs = new CmdLineArgsReplaceCmd( this.cmdType, REPLACECMD[0], REPLACECMD[1], REPLACECMD[2], 2, "YAMLPattern> <newValue" );
            this.cmdLineArgs = replaceCmdLineArgs;
        }
        if ( _apacheCmdProcessor.hasOption(TABLECMD[1]) ) {
            this.cmdType = Enums.CmdEnum.TABLE;
            final CmdLineArgsTableCmd tableCmdLineArgs = new CmdLineArgsTableCmd( this.cmdType, TABLECMD[0], TABLECMD[1], TABLECMD[2], 2, "YAMLPattern> <column,column" );
            this.cmdLineArgs = tableCmdLineArgs;
        }
        if ( _apacheCmdProcessor.hasOption(MACROYAMLCMD[1]) ) {
            this.cmdType = Enums.CmdEnum.MACROYAML;
            final CmdLineArgsMacroCmd macroCmdLineArgs = new CmdLineArgsMacroCmd( this.cmdType, MACROYAMLCMD[0], MACROYAMLCMD[1], MACROYAMLCMD[2], 1, "propertiesFile" );
            this.cmdLineArgs = macroCmdLineArgs;
        }
        if ( _apacheCmdProcessor.hasOption(BATCHCMD[1]) ) {
            this.cmdType = Enums.CmdEnum.BATCH;
            final CmdLineArgsBatchCmd batchCmdLineArgs = new CmdLineArgsBatchCmd( this.cmdType, BATCHCMD[0], BATCHCMD[1], BATCHCMD[2], 1, "batchFile" );
            this.cmdLineArgs = batchCmdLineArgs;
        }
        this.cmdLineArgs.define(); // define is overridden in this class.
        this.cmdLineArgs.parse( _args );

        //-----------------------
        if ( this.verbose ) System.err.println( HDR +": "+this.toString());
    }


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
