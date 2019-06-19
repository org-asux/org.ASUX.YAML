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

import java.util.LinkedHashMap;
import java.util.Properties;

import java.util.regex.*;

import static org.junit.Assert.*;


/** <p>This abstract class was written to re-use code to query/traverse a YAML file.</p>
 *  <p>This org.ASUX.yaml GitHub.com project and the <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com projects.</p>
 *  <p>This abstract class has 4 concrete sub-classes (representing YAML-COMMANDS to read/query, list, delete and replace).</p>
 *  <p>See full details of how to use this, in {@link org.ASUX.yaml.Cmd} as well as the <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com project.</p>
 */
public class MacroStringProcessor {

    public static final String CLASSNAME = MacroStringProcessor.class.getName();

    /** <p>Whether you want deluge of debug-output onto System.out.</p><p>Set this via the constructor.</p>
     *  <p>It's read-only (final data-attribute).</p>
     */
    private final boolean verbose;

    /** <p>Whether you want a final SHORT SUMMARY onto System.out.</p><p>a summary of how many matches happened, or how many entries were affected or even a short listing of those affected entries.</p>
     */
	public final boolean showStats;

	private int changesMade = 0;

    /** The only Constructor.
     *  @param _verbose Whether you want deluge of debug-output onto System.out
     *  @param _showStats Whether you want a final summary onto console / System.out
     */
    public MacroStringProcessor(boolean _verbose, final boolean _showStats) {
		this.verbose = _verbose;
		this.showStats = _showStats;
    }

    //------------------------------------------------------------------------------
    public static class MacroException extends Exception {
        private static final long serialVersionUID = 2L;
        public MacroException(String _s) { super(_s); }
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    private final String macroEval( final String _preMacroStr, final Properties _props, final LinkedHashMap<String,Properties> _allProps )
                                    throws Exception
    {
        final String HDR = CLASSNAME + ": macroEval(): ";

        final String postMacroStr = Macros.evalThoroughly( this.verbose, _preMacroStr, _allProps );
        final boolean bNoChange = ( postMacroStr == null ) ? (_preMacroStr == null): postMacroStr.equals( _preMacroStr );

        if ( this.verbose ) System.out.println( HDR + "(As-Is): " + _preMacroStr);
        if ( this.verbose ) System.out.println( HDR + "(Macros-substituted): " +  postMacroStr );

        if ( this.verbose ) {
            if ( bNoChange ) {
                System.out.println("\tNo change: " + _preMacroStr);
            } else {
                changesMade ++;
                System.out.println("\tAs-Is: " + _preMacroStr);
                System.out.println("\tMacros-substituted: " + postMacroStr );
            }
        }

        return postMacroStr;

    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    /** THis class offers 2 methods: (1) read-a-string and substitute macros in it. (2) read from a java.util.Scanner/File and substitute macros from the content read from that file.
     *  @param _fileName a string either 'inline' content or a filename (starting with '@' character) - cannot be Null, or NullPointerException
     *  @param _props can be null, otherwise an instance of {@link java.util.Properties}
     *  @param _allProps can be null, otherwise an instance of LinkedHashMap&lt;String,Properties&gt;
     *  @return true = whether at least one match of ${ASUX::} happened.
	 *  @throws MacroStringProcessor.MacroException - thrown if any attempt to evaluate MACROs fails within org.ASUX.common.Macros.eval() functions
     *  @throws java.io.IOException if any trouble reading the file referred to by '_fileName' (1st) argument
	 *  @throws Exception - forany other run time error (especially involving YAML issues)
     */
    public String searchNReplace(   final String _fileName,
                                    final Properties _props,
                                    final LinkedHashMap<String,Properties> _allProps
                            ) throws MacroStringProcessor.MacroException, java.io.IOException, Exception
    {
        final String HDR = CLASSNAME + ": searchNReplace(_fileName/String): ";

        try {
            java.util.Scanner scanner = null;
            if ( _fileName.startsWith("@") ) {
                final java.io.InputStream istrm = new java.io.FileInputStream( _fileName.substring(1) );
                scanner = new java.util.Scanner( istrm );
                scanner.useDelimiter( System.lineSeparator() );
                if ( this.verbose ) System.out.println( HDR +"successfully opened file [" + _fileName +"]" );
            } else {
                scanner = new java.util.Scanner( _fileName ); // what I thought was filename is actually INLINE-CONTENT to parse
                scanner.useDelimiter(";");
                if ( this.verbose ) System.out.println( HDR +" using special delimiter for INLINE Batch-commands provided via cmdline" );
            }

            final String retStr = searchNReplace( scanner, _props, _allProps );
            scanner.close();
            return retStr;

        } catch (PatternSyntaxException pse) {
			pse.printStackTrace(System.err); // PatternSyntaxException! too fatal an error, to allow program/application to continue to run.
			System.err.println( "\n\n"+ HDR +"Unexpected Serious Internal ERROR. Exception Message: "+ pse );
			System.exit(91); // This is a serious failure. Shouldn't be happening.
            return null;
        } catch (java.io.IOException ie) {
            if ( this.verbose ) ie.printStackTrace(System.err); // IOException! too fatal an error, to allow program/application to continue to run.
            if ( this.verbose ) System.err.println( "\n\n"+ HDR +"Failure to read/write IO for file ["+ _fileName +"]. Exception Message: "+ ie );
			throw ie;
        } catch (Exception e) {
            if ( this.verbose ) e.printStackTrace(System.err);// General Exception! too fatal an error, to allow program/application to continue to run.
            if ( this.verbose ) System.err.println( HDR +"Unknown Internal error: "+ e );
            throw e;
        }

    }


    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /** THis class offers 2 methods: (1) read-a-string and substitute macros in it. (2) read from a java.util.Scanner/File and substitute macros from the content read from that file.
     *  @param _inputScanner A java.util.Scanner object (Not Null)
     *  @param _props can be null, otherwise an instance of {@link java.util.Properties}
     *  @param _allProps can be null, otherwise an instance of LinkedHashMap&lt;String,Properties&gt;
     *  @return true = whether at least one match of ${ASUX::} happened.
	 *  @throws MacroStringProcessor.MacroException - thrown if any attempt to evaluate MACROs fails within org.ASUX.common.Macros.eval() functions
	 *  @throws Exception - forany other run time error (especially involving YAML issues)
     */
    public String searchNReplace(   final java.util.Scanner _inputScanner,
                                    final Properties _props,
                                    final LinkedHashMap<String,Properties> _allProps
                                ) throws MacroStringProcessor.MacroException, Exception
    {
        final String HDR = CLASSNAME + ": searchNReplace(java.util.Scanner): ";
        if ( _inputScanner == null ) return null;

        final StringBuffer strbuf = new StringBuffer();

        //--------------------------
        for ( int origLineNum=1;   _inputScanner.hasNext();   origLineNum++ ) {
            final String line = _inputScanner.next();
            if ( this.verbose ) System.out.println( HDR +"AS-IS line=[" + line +"]" );

            final String lineNoMacros = this.macroEval( line, _props, _allProps );
            assertTrue( lineNoMacros != null );

            strbuf.append( lineNoMacros );
        } // for loop

        //---------------------------
        // _inputScanner.close(); // This method does NOT own the lifecycle of this _inputScanner.
        return strbuf.toString();

        // scanner.hasNext() only throws a RUNTIMEEXCEPTION: IllegalStateException - if this scanner is closed
        // scanner.next() only throws a RUNTIMEEXCEPTION: NoSuchElementException - if no more tokens are available
    }

}
