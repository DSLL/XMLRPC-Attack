package org.eclipse.jdt.internal.jarinjarloader;

import java.net.*;

public class RsrcURLStreamHandlerFactory implements URLStreamHandlerFactory
{
    private ClassLoader classLoader;
    private URLStreamHandlerFactory chainFac;
    
    public RsrcURLStreamHandlerFactory(final ClassLoader cl) {
        super();
        this.classLoader = cl;
    }
    
    public URLStreamHandler createURLStreamHandler(final String protocol) {
        if ("rsrc".equals(protocol)) {
            return new RsrcURLStreamHandler(this.classLoader);
        }
        if (this.chainFac != null) {
            return this.chainFac.createURLStreamHandler(protocol);
        }
        return null;
    }
    
    public void setURLStreamHandlerFactory(final URLStreamHandlerFactory fac) {
        this.chainFac = fac;
    }
}
