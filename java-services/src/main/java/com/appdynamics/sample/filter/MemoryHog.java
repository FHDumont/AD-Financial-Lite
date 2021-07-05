package com.appdynamics.sample.filter;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;


public class MemoryHog {

    private static byte[] Stuff = null;
    private Random rnd;
    private InnerHeapUsageProcess proc;

    public void bloat() throws InterruptedException {
        // int num_mb = 50;

        // if (System.getenv("MEMORY_HOG_SIZE_MB") != null) {
        //     num_mb = Integer.parseInt(System.getenv("MEMORY_HOG_SIZE_MB"));
        // }

        // if (MemoryHog.Stuff == null) {
        //     MemoryHog.Stuff = new byte[num_mb * 1024 * 1024];
        // }

        // rnd = new Random();
        // rnd.nextBytes(MemoryHog.Stuff);

        int num_secs = 6;
        if (System.getenv("HEAP_SIZE_INC_SECS") != null) {
            num_secs = Integer.parseInt(System.getenv("HEAP_SIZE_INC_SECS"));
        }

        proc = new InnerHeapUsageProcess();
        proc.run();
        
    }

    /**
     * InnerHeapUsageProcess
     */
    public class InnerHeapUsageProcess extends Thread {
        @Override
        public void run() {
            // int heap_inc_size_mb = 200;
            // if (System.getenv("HEAP_SIZE_INC_MB") != null) {
            //     heap_inc_size_mb = Integer.parseInt(System.getenv("HEAP_SIZE_INC_MB"));
            // }
            List<byte[]> list = new ArrayList<>();
            Runtime rt = Runtime.getRuntime();

            long memory1 = rt.freeMemory();

            double byteSize = .001;
            int loops = (int) (100/(100 * byteSize));

            int num_secs = 5;
            for (int i = 0; i < loops+(10); i++) {
                byte[] result = byteCount(rt, byteSize);
                list.add(result);
                result = null;    
                // System.out.println("Memeory used by allocation: " + String.format("\t\t%,d", (memory1 - gfg.freeMemory())));
            }            
            
            try {
                Thread.sleep(num_secs * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.gc();
            }
        }

        private byte[] byteCount(Runtime gfg, double howMuch) {
            int littlebytes = (int) (gfg.freeMemory() * howMuch);            
            byte[] result = new byte[littlebytes];
            return result;
        }
    }

    public void deflate() throws InterruptedException {
        // if (MemoryHog.Stuff != null) {
        //     MemoryHog.Stuff = null;
        // }

        System.gc();
        Thread.sleep(1000);
    }

}