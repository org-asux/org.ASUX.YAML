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
 CmdLineArgsReplaceCmd = new CmdLineArgsReplaceCmd(args);
 .. ..
 *</pre>
 *
 *  <p>See full details of how to use this, in {@link org.ASUX.yaml.Cmd} as well as the <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com project.</p>
 * @see org.ASUX.yaml.Cmd
 */
public class CmdLineArgsReplaceCmd extends CmdLineArgs {

    public static final String CLASSNAME = CmdLineArgsReplaceCmd.class.getName();

    public String replaceFilePath = null;       // optional argument, but required for 'replace' command

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /** Constructor.
     *  @param _cmdType enum denoting what the user's command-type was, as entered on the command line
     *  @param _shortCmd example "r" "zd"
     *  @param _longCmd example "read" "table"
     *  @param _cmdDesc long description. See org.apache.commons.cli for complex examples.
     *  @param _numArgs the # of additional arguments following this command
     *  @param _addlArgsDesc what the HELP command shows about these additional args
     *  @throws Exception like ClassNotFoundException while trying to serialize and deserialize the input-parameter
     */
    public CmdLineArgsReplaceCmd( final CmdEnum _cmdType,
                                final String _shortCmd, final String _longCmd, final String _cmdDesc,
                                final int _numArgs, final String _addlArgsDesc  )
                                throws Exception
    {
        super( _cmdType, _shortCmd, _longCmd, _cmdDesc, _numArgs, _addlArgsDesc );
    } // method


    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /**
     *  <p>Subclasses to override this method to parse for additional options.</p>
     *  <p>This method does nothing in this parent class</p>
     *  @param _args command line argument array - as received as-is from main().
     *  @throws Exception like ClassNotFoundException while trying to serialize and deserialize the input-parameter
     */
    protected void moreParsing( String[] _args ) throws Exception {
        final String[] replaceArgs = this.apacheCmd.getOptionValues( this.cmdAsStr ); // CmdLineArgsBasic.REPLACECMD[1]
        // because we set .setArgs(2) above.. you can get the values for:- replaceArgs[0] and replaceArgs[1].
        this.yamlRegExpStr = replaceArgs[0]; // 1st of the 2 arguments for replace cmd.
        this.replaceFilePath = replaceArgs[1];
    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /** For making it easy to have simple code generate debugging-output, added this toString() method to this class.
     */
    public String toString() {
        return super.toString() +" replaceFile="+replaceFilePath;
    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // For unit-testing purposes only
    public static void main(String[] args) {
        try{
            final CmdLineArgsReplaceCmd cla = new CmdLineArgsReplaceCmd( CmdEnum.REPLACE, CmdLineArgsBasic.REPLACECMD[0], CmdLineArgsBasic.REPLACECMD[1], CmdLineArgsBasic.REPLACECMD[2], 2, "YAMLPattern> <newValue" );  // Note: there's a trick in the parameter-string.. as setArgName() assumes a single 'word' and puts a '<' & '>' around that single-word.
            cla.parse(args);
        } catch( Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
