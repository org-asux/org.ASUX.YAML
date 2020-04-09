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

/**
 *  <p>This class is part of a bunch of tools to help make it easy to work with the java.util.Map objects that the YAML library creates.</p>
 *  <p>This class represents one of the 2 interfaces: (1) CommandLine {@link Cmd} and (2) JSON Object interface (this).</p>
 *  <p>The JSON to be passed in as argument to {@link #main(String[])} must be as follows:</p>
<ul> <ul>
<pre>
cmdAsJson = {
   cmdEnum: 'READ',
   inputfile: '/tmp/i.yaml',
   outputfile: '/user/me/project/newJson.json',
   quoteType: "\"",
};
</pre>
</ul></ul>
 * <p>NOTE: For single quote, replace last line above with quoteType: "'". No no-quotes at all, use quoteType: "none". For "Folded" quotes, use quoteType: ">", etc ..</p>
 * <p>Optional entries (for ALL YAML-Commands) in the above JSON are:-</p>
<ul> <ul>
<pre>
YAMLLibrary: 'SnakeYAML', verbose: false, showStats: false, offline: false
</pre>
</ul></ul>

 * <p>Optional entry (for READ YAML-Command) is:-</p>
</pre>
</ul></ul>
projectionPath: '..'
</pre>
</ul></ul>

 * <p>Optional entry (for INSERT & REPLACE YAML-Commands) is:-</p>
</pre>
</ul></ul>
insertFilePath: '/user/me/inputs/newJson.json'
</pre>
</ul></ul>

 * <p>MACRO YAML-Command has a mandatory entry:</p>
</pre>
</ul></ul>
propertiesFilePath: '/user/me/properties/KVPairs.txt'
</pre>
</ul></ul>

 * <p>BATCH YAML-Command has a mandatory entry:</p>
</pre>
</ul></ul>
batchFilePath: '/user/me/properties/ScriptWithMultipleYAMLCommands.txt'
</pre>
</ul></ul>

 */
public abstract class JSONInterface {

    private static final long serialVersionUID = 127L;

    public static final String CLASSNAME = JSONInterface.class.getName();

    // /** <p>Whether you want deluge of debug-output onto System.out.</p><p>Set this via the constructor.</p>
    //  *  <p>It's read-only (final data-attribute).</p>
    //  */
    // public final boolean verbose;

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    // /**
    //   * <p>Utility class for use within the org.ASUX.yaml library only</p><p>one of 2 constructors - public/private/protected</p>
    //   * @param _verbose Whether you want deluge of debug-output onto System.out.
    //   */
    // public JSONInterface( boolean _verbose ) {
    //     this.verbose = _verbose;
    // }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    public static void main( String[] args) {
        final String HDR = CLASSNAME + ": main(): ";
        try {
            boolean verbose = false;
            StringBuilder filenameBuf = new StringBuilder();
            for( String s: args ) {
                if ( "--verbose".equals( s ) ) {
                    verbose = true;
                } else {
                    filenameBuf.append( s );
                }
            }
            final String filename = filenameBuf.toString();
            if ( verbose ) System.err.println( HDR +"filename = ["+ filename +"]" );

            if ( filename.length() <= 0 ) {
                System.err.println( HDR +"\nProvide a JSON filename (or inline JSON) as input" );
                if ( verbose ) new Exception().printStackTrace(System.err); // main().  For Unit testing
                System.exit(92); // This is a serious failure. Shouldn't be happening.
            }

            //=========================================
            final org.ASUX.common.ConfigFileScannerL3 scanner = new org.ASUX.common.ScriptFileScanner( verbose );

            scanner.openFile( filename, true, true );
            final String jsonString = scanner.toString();
            if ( verbose ) System.err.println( HDR +"jsonString = ["+ jsonString +"]" );

            final CmdLineArgsCommon cmdLineArgsCommon = JSONTools.toCmdLineArgs( verbose, jsonString );
            if ( verbose ) System.err.println( HDR +"cmdLineArgsCommon = ["+ cmdLineArgsCommon +"]" );

            Cmd.go( cmdLineArgsCommon );

        } catch (Exception e) {
            e.printStackTrace(System.err); // main().  For Unit testing
            System.err.println( HDR + "Unexpected Internal ERROR, while processing " + ((args==null || args.length<=0)?"[No CmdLine Args":args[0]) +"]" );
            System.exit(91); // This is a serious failure. Shouldn't be happening.
        }
    }

}
