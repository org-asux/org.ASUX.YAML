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

/** 
 * <p>This class is the "memory" as we execute line by line - within a Batch-YAML-script.</p>
 * A Batch-YAML-script allows you to 'save a value' and retrieve it later.  That is the dictionary definition of the word "Memory"
 * @see org.ASUX.yaml.CmdInvoker
 */
public class MemoryAndContext implements java.io.Serializable, Cloneable {

    private static final long serialVersionUID = 119L;

    public static final String CLASSNAME = MemoryAndContext.class.getName();

    /** <p>Whether you want deluge of debug-output onto System.out.</p><p>Set this via the constructor.</p>
     *  <p>It's read-only (final data-attribute).</p>
     */
    private final boolean verbose;

    /** <p>Whether you want a final SHORT SUMMARY onto System.out.</p><p>a summary of how many matches happened, or how many entries were affected or even a short listing of those affected entries.</p>
     */
	public final boolean showStats;

    /**
     *  <p>This is a private LinkedHashMap&lt;String, LinkedHashMap&lt;String, Object&gt; &gt; savedOutputMaps = new LinkedHashMap&lt;&gt;(); .. cannot be null.  Most useful for @see org.ASUX.yaml.BatchYamlProcessor - which allows this this class to lookup !propertyvariable.</p>
     *  <p>In case you need access to it - be nice and use it in a read-only manner - use the getter()</p>
     *  <p>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ATTENTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!</p>
     *  <p>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ATTENTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!</p>
     *  <p>The YAML loaded as org.yaml.snakeyaml.nodes.Node via SnakeYAML library is NOT serializable.  *Ugh*</p>
     *  <p>So, we are FORCED to make this.savedOutputMaps transient, and therefore are FORCED to offer this setter()<p>
     *  <p>.. and after a deepClone() of this/CmdInvoker.java .. you'll need to call: </p>
     *  <p> <code> LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>(); </code> <br>
     *  <p> <code> tmp.putAll( oldObj.getSavedOutputMaps() ); </code> <br>
     *  <p> <code> newObj.setSavedOutputMaps( tmp ); </code> <br>
     *  <p>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ATTENTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!</p>
     *  <p>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ATTENTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!</p>
     */
    private transient LinkedHashMap<String, Object> savedOutputMaps = new LinkedHashMap<>();

    private LinkedHashMap<String,Properties> allPropsReference = null;

    private final org.ASUX.yaml.CmdInvoker cmdinvoker;

    //======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //======================================================================

    /** The only Constructor.
     *  @param _verbose Whether you want deluge of debug-output onto System.out
     *  @param _showStats Whether you want a final summary onto console / System.out
     *  @param _cmdinvoker the instance of {@link org.ASUX.yaml.CmdInvoker} that is the entry point to this whole progam, and know all about user's inputs, options and parameters
     */
    public MemoryAndContext( final boolean _verbose, final boolean _showStats, final org.ASUX.yaml.CmdInvoker _cmdinvoker ) {
		this.verbose = _verbose;
        this.showStats = _showStats;
        this.cmdinvoker = _cmdinvoker;
    }

    private MemoryAndContext() {
        this(false, true, null);
    }

    //======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //======================================================================

    /**
     * This allows this class to interact better with BatchYamlProcessor.java, which is the authoritative source of all "saveAs" outputs.
     * This class will use this object (this.savedOutputMaps) primarily for passing the replacement-Content and insert-Content (which is NOT the same as --input/-i cmdline option)
     * @return this.savedOutputMaps
     */
    public LinkedHashMap<String, Object> getSavedOutputMaps() {
        return this.savedOutputMaps;
    }

    /**
     *  <p>This allows this class to interact better with BatchYamlProcessor.java, which is the authoritative source of all "saveAs" outputs.</p>
     *  <p>This class will use this object (this.savedOutputMaps) primarily for passing the replacement-Content and insert-Content (which is NOT the same as --input/-i cmdline option).</p>
     *  <p>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ATTENTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!</p>
     *  <p>The YAML loaded as org.yaml.snakeyaml.nodes.Node via SnakeYAML library is NOT serializable.  *Ugh*</p>
     *  <p>So, we are FORCED to make this.savedOutputMaps transient, and therefore are FORCED to offer this setter()</p>
     *  <p>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ATTENTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!</p>
     * @param _savedOutputMaps from another instance of MemoryAndContext.class (by calling that other instance's {@link #getSavedOutputMaps})
     */
    public void setSavedOutputMaps( LinkedHashMap<String, Object>  _savedOutputMaps ) {
        this.savedOutputMaps = _savedOutputMaps;
    }

    //======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //======================================================================

    /**
     * This allows this class to interact better with BatchYamlProcessor.java, which is the authoritative source of all "saveAs" outputs.
     *  <p>this.allPropsReference is primarily used for Macro evaluations only</p>
     * @return this.savedOutputMaps
     */
    public LinkedHashMap<String,Properties> getAllPropsRef() {
        return this.allPropsReference;
    }

    /**
     *  <p>This allows this class to interact better with BatchYamlProcessor.java, which is the authoritative source of all "saveAs" outputs.</p>
     *  <p>this.allPropsReference is primarily used for Macro evaluations only</p>
     * @param _props from another instance of MemoryAndContext.class (by calling that other instance's {@link #getAllPropsRef})
     */
    public void setAllPropsRef( final LinkedHashMap<String,Properties> _props ) {
        this.allPropsReference = _props;
    }

    //======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //======================================================================

    /**
     * This functon takes a single parameter that is a javalang.String value - and, either detects it to be inline YAML/JSON, or a filename (must be prefixed with '@'), or a reference to something saved in {@link MemoryAndContext} within a Batch-file execution (must be prefixed with a '!')
     * @param _src a javalang.String value - either inline YAML/JSON, or a filename (must be prefixed with '@'), or a reference to a property within a Batch-file execution (must be prefixed with a '!')
     * @return an object (either LinkedHashMap, ArrayList or LinkedList)
     *  @throws Exception when input-parameter is invalid.  Rather than fail silently, this throws.
     */
    public Object getDataFromMemory( String _src  ) throws Exception
    {
        if (this.verbose) System.out.println( CLASSNAME +": getDataFromReference("+ _src +"): about to lookup memory " );
        if ( _src == null ) return null;
        _src = _src.trim();
        if ( _src.length() <= 0 )
            throw new Exception( CLASSNAME +": getDataFromMemory: Invalid label/name/reference ["+ _src +"] provided to retrieve this data from batch-memory " );

        final String savedMapName = _src.startsWith("!") ?  _src.substring(1) : _src;
        final Object recalledContent = (this.savedOutputMaps != null) ?  this.savedOutputMaps.get( savedMapName ) : null;
        if (this.verbose) System.out.println( CLASSNAME +": getDataFromReference("+ _src +"): memory says=" + ((recalledContent==null)?"null":recalledContent.toString()) );
        return recalledContent;
    }

    /**
     * This function saves '_val2bRemembered' to a reference to a file (_dest parameter must be prefixed with an '@').. or, to a string prefixed with '!' (in which it's saved into Working RAM, Not to disk/file)
     * @param _dest a javalang.String value - either a filename (must be prefixed with '@'), or a reference to a (new) property-variable within a Batch-file execution (must be prefixed with a '!')
     * @param _val2bRemembered the object to be saved using the reference provided in _dest paramater
     *  @throws Exception when input-parameter _dest is invalid.  Rather than fail silently, this throws.
     */
    public void saveDataIntoMemory( String _dest, final Object _val2bRemembered ) throws Exception
    {
        if (this.verbose) System.out.println( CLASSNAME +": saveDataIntoMemory("+ _dest +"): 1: saving into 'memoryAndContext': " + _val2bRemembered );
        if ( _dest == null || _val2bRemembered == null ) return;
        _dest = _dest.trim();
        if ( _dest.length() <= 0 )
            throw new Exception( CLASSNAME +": saveDataIntoMemory: Invalid label/name/reference ["+ _dest +"] provided to save this data --> " + _val2bRemembered );

        final String saveToMapName = _dest.startsWith("!") ?  _dest.substring(1) : _dest;
        if (this.verbose) System.out.println( CLASSNAME +": saveDataIntoMemory("+ saveToMapName +"): 2: saving into 'memoryAndContext': " + _val2bRemembered );
        if ( (this.savedOutputMaps != null) && (saveToMapName != null) && (saveToMapName.length() > 0) ) {
            // This can happen only within a BatchYaml-file context.  It only makes any sense (and will only work) within a BatchYaml-file context.
            this.savedOutputMaps.put( saveToMapName, _val2bRemembered );  // remove '!' as the 1st character in the destination-reference provided
            if (this.verbose) System.out.println( CLASSNAME +": saveDataIntoMemory("+ _dest +"): saved into 'memoryAndContext' --> " + _val2bRemembered );
        }
    }

    //======================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //======================================================================

    /**
     * @return reference to the {@link org.ASUX.yaml.CmdInvoker} instance that get the entire program running
     */
    public org.ASUX.yaml.CmdInvoker getContext() {
        return this.cmdinvoker;
    }

    /**
     * Debugging tool. To see how many objects are stored in memory.
     * @return the count of how many items in memory.
     */
    public int getCount() {
        return this.savedOutputMaps.size();
    }

    /**
     * Debugging tool.  See what's in memory, in case Batch-YAML-Commands are not having the right input.  This might be extreme option for you.  You are perhaps going to be better off using the 'print -' statement in your YAML-Batch script
     * @return the String format dump of memory.  Can be ugly to read.
     */
    public String dump() {
        return this.savedOutputMaps.toString();
    }

}
