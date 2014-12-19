package org.eclipse.jdt.internal.jarinjarloader;

import java.io.*;
import java.net.*;

public class RsrcURLConnection extends URLConnection
{
    private ClassLoader classLoader;
    
    public RsrcURLConnection(final URL url, final ClassLoader classLoader) {
        super(url);
        this.classLoader = classLoader;
    }
    
    public void connect() throws IOException {
    }
    
    public InputStream getInputStream() throws IOException {
        final String file = URLDecoder.decode(this.url.getFile(), "UTF-8");
        final InputStream result = this.classLoader.getResourceAsStream(file);
        if (result == null) {
            throw new MalformedURLException("Could not open InputStream for URL '" + this.url + "'");
        }
        return result;
    }
}
