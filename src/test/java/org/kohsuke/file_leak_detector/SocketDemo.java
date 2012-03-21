package org.kohsuke.file_leak_detector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kohsuke Kawaguchi
 */
public class SocketDemo {
    public static void main(String[] args) throws IOException {
        final ExecutorService es = Executors.newCachedThreadPool();
        
        final ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress("localhost",0));

        es.submit(new Callable<Object>() {
            public Object call() throws Exception {
                while (true) {
                    final Socket s = ss.accept();
                    es.submit(new Callable<Object>() {
                        public Object call() throws Exception {
                            s.shutdownInput();
                            s.shutdownOutput();
                            return null;
                        }
                    });
                }
            }
        });
        
        while (true) {
            int dst = ss.getLocalPort();
            Socket s = new Socket("localhost",dst);
            s.close();
        }
    }
}
