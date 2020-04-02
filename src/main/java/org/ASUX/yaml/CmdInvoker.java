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

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * <p> This org.ASUX.yaml GitHub.com project and the
 * <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com projects. </p>
 * <p> This abstract class is the "wrapper-processor" for the various "YAML-commands" (which traverse a YAML file to do what you want).
 * </p>
 * <p> There are 2 concrete implementation sub-classes: org.ASUX.yaml.CollectionsImpl.CmdInvoker and org.ASUX.YAML.NodeImpl.CmdInvoker</p>
 *
 * <p> The 4 YAML-COMMANDS are: <b>read/query, list, delete</b> and <b>replace</b>. </p>
 * <p> See full details of how to use these commands - in this GitHub project's wiki - or - in
 * <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com project and its wiki.
 * </p>
 *
 * <p> Example:
 * <code>java org.ASUX.yaml.Cmd --delete --yamlpath "paths.*.*.responses.200" -i $cwd/src/test/my-petstore-micro.yaml -o /tmp/output2.yaml  --double-quote</code><br>
 * Example: <b><code>java org.ASUX.yaml.Cmd</code></b> will show all command
 * line options supported.
 * </p>
 */
public abstract class CmdInvoker<T> implements java.io.Serializable, Cloneable {

    private static final long serialVersionUID = 202L;

    public static final String CLASSNAME = CmdInvoker.class.getName();

    protected transient YAMLImplementation<T> yamlImpl = null; // will not be covered _AUTOMATICALLY_ by deepClone()

    /**
     * <p>Keep a copy of the command-line arguments provided by the user to run this Batch command.</p>
     * <p>In the scenario, where a batch command invokes another batch-command, use this to find out what attributes were passed on.</p>
     */
    protected CmdLineArgsCommon cmdLineArgs;

    // /**
    //  * <p>Whether you want deluge of debug-output onto System.out.</p>
    //  * <p>Set this via the constructor.</p>
    //  * <p>It's read-only (final data-attribute).</p>
    //  */
    // public final boolean verbose;

    /**
     * This is a private LinkedHashMap&lt;String, LinkedHashMap&lt;String, Object&gt; &gt; memoryAndContext = new LinkedHashMap&lt;&gt;(); .. cannot be null.  Most useful for @see org.ASUX.yaml.BatchYamlProcessor - which allows this this class to lookup !propertyvariable.
     * In case you need access to it - be nice and use it in a read-only manner - use the getter()
     */
    protected final MemoryAndContext memoryAndContext;

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================
    /**
     *  The constructor exclusively for use by  main() classes anywhere.
     *  @param _cmdLineArgs NotNull instance of the command-line arguments passed in by the user.
     */
    public CmdInvoker( final CmdLineArgsCommon _cmdLineArgs ) {
        this( _cmdLineArgs, null );
    }
    // public CmdInvoker( final boolean _verbose, final boolean _showStats ) {
    //     this( _verbose, _showStats, null );
    // }

    // *  @param _verbose Whether you want deluge of debug-output onto System.out.
    // *  @param _showStats Whether you want a final summary onto console / System.out
    // public CmdInvoker( final boolean _verbose, final boolean _showStats, final MemoryAndContext _memoryAndContext ) {
    /**
     *  Variation of constructor that allows you to pass-in memory from another previously existing instance of this class.  Useful within org.ASUX.YAML.NodeImp.BatchYamlProcessor which creates new instances of this class, whenever it encounters a YAML or AWS command within the Batch-file.
     *  @param _cmdLineArgs NotNull instance of the command-line arguments passed in by the user.
     *  @param _memoryAndContext pass in memory from another previously existing instance of this class.  Useful within org.ASUX.YAML.CollectionImpl.BatchYamlProcessor which creates new instances of this class, whenever it encounters a YAML or AWS command within the Batch-file.
     */
    public CmdInvoker( final CmdLineArgsCommon _cmdLineArgs, final MemoryAndContext _memoryAndContext ) {
        this.cmdLineArgs = _cmdLineArgs;

        if ( _memoryAndContext == null )
            this.memoryAndContext = new MemoryAndContext( this.cmdLineArgs.verbose, this.cmdLineArgs.showStats, this );
        else
            this.memoryAndContext = _memoryAndContext;
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     * Reference to the implementation of the YAML read/parsing ONLY
     * @return a reference to the YAML Library in use.
     */
    public YAMLImplementation<T> getYAMLImplementation() {
        return this.yamlImpl;
    }

    /**
     * @param _yi a reference to the YAML Library to use.
     */
    public void setYAMLImplementation( final YAMLImplementation<T> _yi ) {
        this.yamlImpl = _yi;
    }

    /**
     * This allows this class (CmdInvoker) to interact better with BatchYamlProcessor.java, which is the authoritative source of all "saveAs" outputs.
     * This class (CmdInvoker) will use this object (this.memoryAndContext) primarily for passing the replacement-Content and insert-Content (which is NOT the same as --input/-i cmdline option)
     * @return this.memoryAndContext
     */
    public MemoryAndContext getMemoryAndContext() {
        return this.memoryAndContext;
    }

    /**
     *  To know which YAML-parsing/emitting library was chosen by user.  Ideally used within a Batch-Yaml script / BatchYamlProcessor.java
     *  @return the YAML-library in use. See {@link YAML_Libraries} for legal values to this parameter
     */
    public YAML_Libraries getYAMLLibrary() { return this.getYAMLImplementation().getYAMLLibrary(); }

    // /**
    //  * Allows you to set the YAML-parsing/emitting library of choice.  Ideally used within a Batch-Yaml script.
    //  * @param _l the YAML-library to use going forward. See {@link YAML_Libraries} for legal values to this parameter
    //  */
    // public abstract void setYAMLLibrary( final YAML_Libraries _l );

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================
    // /**
    //  *  This function is meant to be used by Cmd.main() and by BatchProcessor.java.  Read the code *FIRST*, to see if you can use this function too.
    //  *  @param _cmdLineArgs Everything passed as commandline arguments to the Java program {@link org.ASUX.yaml.CmdLineArgsCommon}
    //  *  @return a NotNull reference to a subclass of {@link YAMLImplementation}
    //  *  @throws Exception if any invalid configuration was detected within the only argument passed in.
    //  */
    // public abstract YAMLImplementation<T> config ( org.ASUX.yaml.CmdLineArgsCommon _cmdLineArgs )
    //             throws Exception;

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================
    /**
     *  This function is meant to be used by Cmd.main() and by BatchProcessor.java.  Read the code *FIRST*, to see if you can use this function too.
     *  @param _cmdLineArgs Everything passed as commandline arguments to the Java program {@link org.ASUX.yaml.CmdLineArgsCommon}
     *  @param _inputData _the YAML inputData that is the input to pretty much all commands (based on the library it's either a org.yaml.snakeyaml.nodes.Node or a java.utils.LinkedHashMap&lt;String, Object&gt; object).
     *  @return either a String, java.utils.LinkedHashMap&lt;String, Object&gt;
     *  @throws YAMLPath.YAMLPathException if Pattern for YAML-Path provided is either semantically empty or is NOT java.util.Pattern compatible.
     *  @throws FileNotFoundException if the filenames within _cmdLineArgs do NOT exist
     *  @throws IOException if the filenames within _cmdLineArgs give any sort of read/write troubles
     *  @throws Exception by ReplaceYamlCmd method and this nethod (in case of unknown command)
     */
    public abstract Object processCommand ( org.ASUX.yaml.CmdLineArgsCommon _cmdLineArgs, final Object _inputData )
                throws FileNotFoundException, IOException, Exception,
                YAMLPath.YAMLPathException;

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     * This functon takes a single parameter that is a javalang.String value - and, either detects it to be inline YAML/JSON, or a filename (must be prefixed with '@'), or a reference to something saved in {@link MemoryAndContext} within a Batch-file execution (must be prefixed with a '!')
     * @param _src a javalang.String value - either inline YAML/JSON, or a filename (must be prefixed with '@'), or a reference to a property within a Batch-file execution (must be prefixed with a '!')
     * @return an object (either LinkedHashMap, ArrayList or LinkedList)
     * @throws FileNotFoundException if the filenames within _cmdLineArgs do NOT exist
     * @throws IOException if the filenames within _cmdLineArgs give any sort of read/write troubles
     * @throws Exception by ReplaceYamlCmd method and this nethod (in case of unknown command)
     */
    public abstract Object getDataFromReference( final String _src  )
                throws FileNotFoundException, IOException, Exception;

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     * This function saved _inputMap to a reference to a file (_dest parameter must be prefixed with an '@').. or, to a string prefixed with '!' (in which it's saved into Working RAM, Not to disk/file)
     * @param _dest a javalang.String value - either a filename (must be prefixed with '@'), or a reference to a (new) property-variable within a Batch-file execution (must be prefixed with a '!')
     * @param _inputMap the object to be saved using the reference provided in _dest paramater
     * @throws FileNotFoundException if the filenames within _cmdLineArgs do NOT exist
     * @throws IOException if the filenames within _cmdLineArgs give any sort of read/write troubles
     * @throws Exception by ReplaceYamlCmd method and this nethod (in case of unknown command)
     */
    public abstract void saveDataIntoReference( final String _dest, final Object _inputMap )
                throws FileNotFoundException, IOException, Exception;

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     *  <p>This method needs to supplement org.ASUX.common.Utils.deepClone() only because the MemoryAndContext instance-variable has org.yaml.snakeyaml.nodes.Node (created via SnakeYAML library and is therefore NOT serializable).</p>
     *  <p>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ATTENTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!</p>
     *  <p>So, after a deepClone() of CmdInvoker.java .. you'll need to call: </p>
     *  <p> <code> clone.setYamlLibrary( origObj.getYamlLibrary() ); </code> <br>
     *  <p> <code> java.utils.LinkedHashMap&lt;String, Object&gt; tmp = new java.utils.LinkedHashMap&lt;String, Object&gt;(); </code> <br>
     *  <p> <code> tmp.putAll( origObj.getSavedOutputMaps() ); </code> <br>
     *  <p> <code> clone.setSavedOutputMaps( tmp ); </code> <br>
     *  @param <T> either SnakeYAML's Node.class or LinkedHashMap&lt;String,Object&gt; for EsotericSoftware's YAML implementation
     *  @param origObj the object to be cloned - cannot be null (will throw NullPointerException)
     *  @return a properly cloned and re-initiated clone of the original
     *  @throws Exception when org.ASUX.common.Utils.deepClone clones the core of this class-instance 
     */
    public static <T> CmdInvoker<T> deepClone( final CmdInvoker<T> origObj ) throws Exception {
        final org.ASUX.yaml.CmdInvoker<T> newCmdinvoker = org.ASUX.common.Utils.deepClone( origObj );

        final LinkedHashMap<String, Object> tmp = new LinkedHashMap<>(  origObj.getMemoryAndContext().getSavedOutputMaps()  );
        newCmdinvoker.getMemoryAndContext().setSavedOutputMaps( tmp );

        newCmdinvoker.setYAMLImplementation( origObj.getYAMLImplementation().deepClone() );

        return newCmdinvoker;
    }
}
