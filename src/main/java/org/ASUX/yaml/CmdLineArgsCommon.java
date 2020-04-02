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
public abstract class CmdLineArgsCommon implements org.ASUX.language.antlr4.CmdLineArgs,    java.io.Serializable  {

    private static final long serialVersionUID = 141L;

    public static final String CLASSNAME = CmdLineArgsCommon.class.getName();

    //------------------------------------
    public boolean verbose      = false;
    public boolean showStats    = false;
    public boolean offline   = false;

    public Enums.ScalarStyle quoteType = Enums.ScalarStyle.UNDEFINED;
    public YAML_Libraries YAMLLibrary = YAML_Libraries.NodeImpl_Library; // some default value for now

    public String inputFilePath = "/tmp/undefined";
    public String outputFilePath = "/tmp/undefined";

    //------------------------------------
    public Enums.CmdEnum cmdType;
    public String cmdAsStr = ""; // the string version of cmdType

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

    /** For making it easy to have simple code generate debugging-output, added this toString() method to this class.
     */
    public String toString() {
        return
        " --verbose="+verbose+" --showStats="+showStats
        +" inpfile="+inputFilePath+" outputfile="+outputFilePath
        +" this.quoteType=["+this.quoteType+"]  offline="+this.offline
        +" Cmd-Type="+cmdType +"  Full-Command=("+cmdAsStr+") "
        ;
    }

    //------------------------------------
    // public boolean isOffline()              { return this.offline; }
    // public Enums.ScalarStyle getQuoteType() { return this.quoteType; }
    // public YAML_Libraries getYAMLLibrary()   { return this.YAMLLibrary; }


    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     * Given the original object of this class, copy these attributes to the 2nd object of this class: {@link #verbose}, {@link #showStats}, {@link #offline}, {@link #quoteType}
     * @param _orig a NotNull reference
     * @param _copy a NotNull reference
     */
    public void copyBasicFlags( final CmdLineArgsCommon _orig, final CmdLineArgsCommon _copy ) {
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
    public void copyBasicFlags( final CmdLineArgsCommon _copy,
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
