package com.appdynamics.sample.filter;

import java.math.BigInteger;
import java.util.Random;

public class CPUHog {

    public void start() {
        int numCore = Runtime.getRuntime().availableProcessors();
        int numThreadsPerCore = 2;
        
        double load = 0.6;
        if (System.getenv("CPU_LOAD") != null) {
            load = Double.parseDouble(System.getenv("CPU_LOAD"));
        }

        long duration = 100000;
        if (System.getenv("CPU_LOAD_DURATION") != null) {
            duration = Long.parseLong("CPU_LOAD_DURATION");
        }
        
        for (int thread = 0; thread < numCore * numThreadsPerCore; thread++) {
            new BusyThread("Thread" + thread, load, duration).start();
        }
        // FindBigPrime();
    }

    private void FindBigPrime() {
        int cpuExponentBase = 2500;

        if (System.getenv("CPU_SPIKE_EXPONENT_BASE") != null) {
            cpuExponentBase = Integer.parseInt(System.getenv("CPU_SPIKE_EXPONENT_BASE"));
        }
        // Process borrowed from https://stackoverflow.com/questions/1032201/long-running-methods-from-java-sdk-for-testing-purposes
        BigInteger veryBig = new BigInteger(cpuExponentBase, new Random());
        System.out.println(veryBig.nextProbablePrime());
    }

    /**
     * Thread that actually generates the given load     
     */
    private static class BusyThread extends Thread {
        private double load;
        private long duration;

        /**
         * Constructor which creates the thread
         * @param name Name of this thread
         * @param load Load % that this thread should generate
         * @param duration Duration that this thread should generate the load for
         */
        public BusyThread(String name, double load, long duration) {
            super(name);
            this.load = load;
            this.duration = duration;
        }

        /**
         * Generates the load when run
         */
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                // Loop for the given duration
                while (System.currentTimeMillis() - startTime < duration) {
                    // Every 100ms, sleep for the percentage of unladen time
                    if (System.currentTimeMillis() % 100 == 0) {
                        Thread.sleep((long) Math.floor((1 - load) * 100));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}