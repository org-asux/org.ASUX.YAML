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

import java.util.LinkedHashMap;
import java.util.Properties;

import java.util.regex.*;

/** <p>This class has 2 static methods to make it easy for rest of org.ASUX.yaml libraries to evaluate Macro expressions within YALM content</p>
 */
public class Macros {

    public static final String CLASSNAME = Macros.class.getName();

	/** This is NOT a final variable.  So, you can set to whatever you want, to evaluate MACROs in your own context, without disturbing other ${XYZ} expressions. */
	public static String MACRO_VAR_PREFIX = "ASUX::";

    /** <p>Whether you want deluge of debug-output onto System.out.</p><p>Set this via the constructor.</p>
     *  <p>It's read-only (final data-attribute).</p>
     */
    private final boolean verbose;

    /** The only Constructor.
     *  @param _verbose Whether you want deluge of debug-output onto System.out
     */
    public Macros(boolean _verbose ) {
		this.verbose = _verbose;
    }
    private Macros(){
		this(false);
    }

    //------------------------------------------------------------------------------
    public static class MacroException extends Exception {
        private static final long serialVersionUID = 2L;
        public MacroException(String _s) { super(_s); }
    }

	//=============================================================================
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	//=============================================================================

	//------------------------------------------------------------------
	public static final String pattStr = "[$]\\{"+ MACRO_VAR_PREFIX +"([^${}]+)\\}";

	private static Pattern macroPattern = null;

	private static Pattern getPattern( final boolean _verbose ) throws Macros.MacroException
	{
		final String HDR = CLASSNAME + ": getMatcher() ";
		if ( Macros.macroPattern != null )
			return Macros.macroPattern;

		try {
			Macros.macroPattern = Pattern.compile( Macros.pattStr );
			return Macros.macroPattern;
		} catch (PatternSyntaxException e) {
			if ( _verbose ) e.printStackTrace(System.err);
			final String s = "PatternSyntaxException when parsing pattern " + Macros.pattStr;
			if ( _verbose ) System.err.println( HDR + " Unexpected Internal ERROR: " + s +"\nException message: "+ e );
			throw new MacroException( s );
		}
    }

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

	/**
	 *  <p>Pass in a string as 1st parameter and a properties file as 2nd parameter.</p>
	 *  <p>All instances of ${ASUX::___} are replaced with values for ___ (within Properties instance).</p>
	 *  Any other expressions like ${XYZABC} are LEFT UNTOUCHED, as it does NOT have the ASUX:: prefixc.
	 *  @param _verbose Whether you want deluge of debug-output onto System.out
	 *  @param _s the string which CAN (not required to) contain macro expressions like ${ASUX::___}
	 *  @param _props a java.util.Properties object (null will mean function returns immediately)
	 *  @return the original string as-is (if no macros were detected).. or the altered version
	 *  @throws Macros.MacroException if ANY failure in evaluating the macro on the input _s
	 */
	public static String eval( final boolean _verbose, final String _s, final Properties _props)
										throws Macros.MacroException
	{
		if (_s==null) return null;
		if (_props==null || _props.size() <= 0) return _s;

		final String HDR = CLASSNAME + ":eval("+ _verbose +","+ _s +",_props): ";
		try {

			final Pattern macroPatt = Macros.getPattern( _verbose );
			Matcher matcher = macroPatt.matcher( _s );

			boolean found = false;
			String retstr = "";
			int prevIndex = 0;

			while (matcher.find()) {
				found = true;
				// System.out.println("I found the text "+matcher.group()+" starting at index "+  matcher.start()+" and ending at index "+matcher.end());    
				retstr += _s.substring( prevIndex, matcher.start() );
				final String macroVarStr = matcher.group(1);
				final String v = _props.getProperty( macroVarStr ); // lookup value for ${ASUX::__} and add it to retStr

				if ( v == null )
					retstr += "${"+ MACRO_VAR_PREFIX + macroVarStr +"}"; // we are NOT going to replace      ${ASUX::unknownVARIABLE}      with        null
				else
					retstr += v;
				prevIndex = matcher.end();
				// System.out.println( CLASSNAME + ": Matched: "+matcher.group(1)+" to ["+ v +"]@ index "+ matcher.start()+" and ending @ "+matcher.end());
			}

			if(found){
				if ( prevIndex < _s.length() ) {
					// whatever is LEFT ***AFTER*** the last match.. we can't forget about that!
					retstr += _s.substring( prevIndex );
				}
				// System.out.println( "Properties LOOKUP found: for ["+ retstr +"]");
				return retstr;
			} else {
				// System.out.println("No match found.");
				return _s;
			}

		} catch (PatternSyntaxException e) {
			if ( _verbose ) e.printStackTrace(System.err);
			final String s = "PatternSyntaxException when checking if '" + _s + "' matches pattern " + Macros.pattStr;
			System.err.println( HDR + " Unexpected Internal ERROR: " + s +"\nException message: "+ e );
			throw new MacroException( s );
		}

		// return _s; // program control should never get here.
	} // function

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

	/**
	 *  This is a variant of eval(), to support Batch-Cmd mode, where BatchFile can load MULTIPLE property-files
	 *  @param _verbose Whether you want deluge of debug-output onto System.out
	 *  @param _s the string which CAN (not required to) contain macro expressions like ${ASUX::___}
	 *  @param _propsSet can be null,  and otherwise an instance of LinkedHashMap&lt;String,Properties&gt; (representing multiple java.util.Properties objects)
	 *  @return the original string as-is (if no macros were detected).. or the altered version
	 *  @throws Macros.MacroException - thrown if any attempt to evaluate MACROs fails within eval() functions
	 */
	public static String eval( final boolean _verbose, final String _s, final LinkedHashMap<String,Properties> _propsSet )
											throws Macros.MacroException
	{
		if (_s==null) return null;
		if (_propsSet==null || _propsSet.size() <= 0) return _s;

		String retStr = _s;
		for( String key: _propsSet.keySet() ) {
			final Properties p = _propsSet.get(key);
			final String newstr = eval( _verbose, retStr, p );
			if ( newstr != null ) // this can happen if the _s is just the MACRO only.  Example:    ${ASUX::variable}
				retStr = newstr;
			// We can have multiple variables, for which values can be in different properties files
		}
		return retStr;
	}

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

	/**
	 *  <p>Refer to {@link #eval(boolean, String, Properties)}.</p>
	 *  <p>This method simply iteratively calls above eval() until the evaluated-output is unchanged.  This is useful to resolve expressions like: <code>setProperty AWSLocation=${ASUX::AWS-${ASUX::AWSRegion}}</code>.</p>
	 *  @param _verbose Whether you want deluge of debug-output onto System.out
	 *  @param _s the string which CAN (not required to) contain macro expressions like ${ASUX::___}
	 *  @param _props can be null, otherwise an instance of {@link java.util.Properties}
	 *  @return the original string as-is (if no macros were detected).. or the altered version
	 *  @throws Macros.MacroException if ANY failure in evaluating the macro on the input _s
	 */
	public static String evalThoroughly( final boolean _verbose, final String _s, final Properties _props )
										throws Macros.MacroException
	{
		if (_s == null) return null;
		if ( _props == null ) return _s;

        final String HDR = CLASSNAME +": evalThoroughly(verbose,_s="+ _s +","+ _props.getClass().getName() +"): ";
		String retStr = _s;
		try {
			Pattern macroPatt = Macros.getPattern( _verbose );
			do {
				final String prev = retStr;
				assert( prev != null );
				if ( _verbose ) System.out.println( HDR + "starting iteration with "+ prev );

				retStr = Macros.eval( _verbose, retStr, _props );

				// Now, check whether we need to 'rinse+repeat' the above "Macros.eval" above, until no further change happens
				Matcher matcher = macroPatt.matcher( retStr );
				if ( matcher.matches() ) { // means, we still have ${ASUX::___} still left (perhaps nested expressions, so we need multiple passes)
					if ( prev.equals( retStr) ) {
						// clearly.. the ${ASUX::__} still left WILL NEVER get resolved.
						// if we do NOT quit this do-while loop, we'll be stuck in an infinite loop
						return retStr; // !!!!!!!!!!!!!!! ATTENTION !!!!!!!!!!!!!!!!! method returns here
					}
				} // if matcher.matches()
				if ( _verbose ) System.out.println( HDR + "ENDED iteration with "+ retStr );
			} while (true);
		} catch ( Exception e ) {
			if ( _verbose ) e.printStackTrace(System.err);
			final String s = "Exception when checking if '" + _s + "' matches pattern " + Macros.pattStr;
			System.err.println( HDR + " Unexpected Internal ERROR: " + s +"\nException message: "+ e );
			throw new MacroException( s );
		}
		// return retStr;
    }

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

	/**
	 *  <p>Refer to {@link #eval(boolean, String, LinkedHashMap)}.</p>
	 *  <p>This method simply iteratively calls above eval() until the evaluated-output is unchanged.  This is useful to resolve expressions like: <code>setProperty AWSLocation=${ASUX::AWS-${ASUX::AWSRegion}}</code>.</p>
	 *  @param _verbose Whether you want deluge of debug-output onto System.out
	 *  @param _s the string which CAN (not required to) contain macro expressions like ${ASUX::___}
	 *  @param _propsSet can be null,  and otherwise an instance of LinkedHashMap&lt;String,Properties&gt; (representing multiple java.util.Properties objects)
	 *  @return the original string as-is (if no macros were detected).. or the altered version
	 *  @throws Macros.MacroException - thrown if any attempt to evaluate MACROs fails within eval() functions
	 */
	public static String evalThoroughly( final boolean _verbose, final String _s, final LinkedHashMap<String,Properties> _propsSet )
											throws Macros.MacroException
	{
		if (_s==null) return null;
		if (_propsSet==null || _propsSet.size() <= 0) return _s;

        final String HDR = CLASSNAME +": evalThoroughly(verbose,_s="+ _s +",_propsSet): ";
		String retStr = _s;
		try {
			final Pattern macroPattern = Macros.getPattern( _verbose );
			do {
				final String prev = retStr;
				assert( prev != null );
				if ( _verbose ) System.out.println( HDR + "starting iteration with "+ prev );

				// first do a macro-eval using ALL the Properties objects withn _propsSet
				for( String key: _propsSet.keySet() ) {
					final Properties p = _propsSet.get(key);
					final String newstr = eval( _verbose, retStr, p );
					if ( newstr != null ) // this can happen if the _s is just the MACRO only.  Example:    ${ASUX::variable}
						retStr = newstr;
					// We can have multiple variables, for which values can be in different properties files
				}

				// Now, check whether we need to 'rinse+repeat' the "inner FOR-LOOP" above, until no further change happens
				Matcher matcher = macroPattern.matcher( retStr );
				if (  !   matcher.matches() ) { // WHETHER we still have ${ASUX::___} still left (perhaps nested expressions, so we need multiple passes)
					return retStr;
				} else {
					if ( prev.equals( retStr) ) {
						// clearly.. the ${ASUX::__} still left WILL NEVER get resolved.
						// if we do NOT quit this do-while loop, we'll be stuck in an infinite loop
						return retStr; // !!!!!!!!!!!!!!! ATTENTION !!!!!!!!!!!!!!!!! method returns here
					}
					// else continue this do-while() loop
					if ( _verbose ) System.out.println( HDR + "ENDED iteration with "+ retStr );
				} // if matcher.matches()
			} while (true);
		} catch ( Exception e ) {
			if ( _verbose ) e.printStackTrace(System.err);
			final String s = "Exception when checking if '" + _s + "' matches pattern " + Macros.pattStr;
			System.err.println( HDR + " Unexpected Internal ERROR: " + s +"\nException message: "+ e );
			throw new MacroException( s );
		}
		// return retStr;
	}

    //=======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=======================================================================

}
