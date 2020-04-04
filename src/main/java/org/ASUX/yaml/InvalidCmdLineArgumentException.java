package org.ASUX.yaml;

/**
 * This class exists so that I can throw this from anywhere, and main() method will - just for this - show just the error message instead of declaring an "INTERNAL ERROR" and showing Exception-Context.
 */
public class InvalidCmdLineArgumentException extends Exception {
    public InvalidCmdLineArgumentException( final String _errorMessage ){
        super( _errorMessage );
    }
}
