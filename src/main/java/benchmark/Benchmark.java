package benchmark;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;

import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.ExecutionException;

import io.etcd.jetcd.kv.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

class Benchmark {
    static final int[] scaleFactorArr = new int[] { 405_184};//10_000,100_000,200_000,400_000,405_184 };
    static final int repeatNr = 10;
    static final int nrKeys = 405_184;

    public static void main(String[] args) {
        try {
            // create client
            Client client = Client.builder().endpoints("http://localhost:2379").build();
            KV kvClient = client.getKVClient();

            BufferedReader reader = new BufferedReader(
                    new FileReader("/home/mircea/CTI/AN1/SEM1/SABD/PROIECT/iot_telemetry_data.csv"));

            String line = null;
            int nrLines = 0;
            

            String[] charKeys = new String[nrKeys];
            String[] charValues = new String[nrKeys];
            int end = 0, start = 0;
            line = reader.readLine();// first line contains description not actual values
            while ((line = reader.readLine()) != null && nrLines < nrKeys) {
                charValues[nrLines] = line;
                charKeys[nrLines] = String.valueOf(++nrLines);
            }
            System.out.println("nr lines: " + nrLines);
            reader.close();
            String[] charKeysCopy = Arrays.copyOfRange(charKeys, 0, scaleFactorArr[i]);
            String[] charValuesCopy = Arrays.copyOfRange(charValues, 0, scaleFactorArr[i]);
            ByteSequence[] keySeqArr = getByteSequence(charKeysCopy); // key sequentaly array
            ByteSequence[] valSeqArr = getByteSequence(charValuesCopy); // values sequentaly array
            put(kvClient, keySeqArr, valSeqArr);

            //operations
            runMultipleTimes(kvClient,keySeqArr,valSeqArr);

            delete(kvClient, keySeqArr);

            // // get the CompletableFuture
            // CompletableFuture<GetResponse> getFuture = kvClient.get(key);

            // // get the value from CompletableFuture
            // GetResponse response = getFuture.get();

            // // delete the key
            // kvClient.delete(key).get();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("opreste");
    }

    public static int getIndex(String line) {
        int i = 0;
        for (; i < line.length(); ++i) {
            if (line.charAt(i) == ',')
                break;
        }
        return i >= line.length() ? -1 : i;
    }

    public static ByteSequence[] getByteSequence(String[] charArr) {
        ByteSequence[] arr = new ByteSequence[charArr.length];
        int i = 0;
        for (String key : charArr) {
            arr[i++] = ByteSequence.from(key.getBytes());
        }
        return arr;
    }

    public static void put(KV kvClient, ByteSequence[] keys, ByteSequence[] values) {
        if (keys.length != values.length) {
            System.out.println("eroare in put. Lungimi diferite chei si valori");
            return;
        }
        int i = 0;
        try {
            for (; i < keys.length; ++i) {
                kvClient.put(keys[i], values[i]).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void delete(KV kvClient, ByteSequence[] keys) {
        try{
            int i = 0;
            for (ByteSequence key : keys) {
                kvClient.delete(key).get();
            }
        } catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void runMultipleTimes(KV kvClient,ByteSequence[] keySeqArr, ByteSequence[] valSeqArr) {
        double average;
        //init
        double[] durationArr = new double[repeatNr];
        
        //operations
        System.out.println("update");
        ByteSequence keySeq = ByteSequence.from(String.valueOf(7).getBytes()) ;
        for (int j = 0; j < repeatNr; ++j) {
            durationArr[j] = updateKey(kvClient,keySeq,25);
        }
        printMeanAndStddev(durationArr);
        
    }

    public static void printMeanAndStddev(double[] durationArr) {
        // computing average
        double sum = 0;
        for (int j = 0; j < repeatNr; ++j) {
            sum += durationArr[j];
        }
        average = sum / (double) repeatNr;

        // computing standard deviation
        sum = 0;
        for (int j = 0; j < repeatNr; ++j) {
            double diff = average - durationArr[j];
            sum += diff * diff;
        }
        double stddev = Math.sqrt(1.0 / (double) repeatNr * sum);
        System.out.println("Mean : " + average + " ms. Std dev: " + stddev + "\n");
    }

    /**
     * updates the temperature and returns the time needed for the modification
     * @param kvClient the client class to interact with the ETCD database
     * @param key the key for the temperature
     * @param newTemperature the new temperature
     * @return the time needed for the operation
     */
    public static double updateKey(KV kvClient, ByteSequence key, double newTemperature) {
        double duration = 0.;
        try {
            long startTime = System.currentTimeMillis();    
                
            // get the CompletableFuture
            CompletableFuture<GetResponse> getFuture = kvClient.get(key);
            // get the value from CompletableFuture
            GetResponse response =null; 
            response = getFuture.get();
            List<KeyValue> list = response.getKvs();
            KeyValue keyValue = list.get(0);
            ByteSequence seq = keyValue.getValue();
            String value = seq.toString();
            String[] arr = value.split(",");
            if (arr.length == 9) arr[8] = "\"" + String.valueOf(newTemperature) + "\"";
            StringBuilder builder = new StringBuilder();
            for (String el:arr){
                builder.append(el).append(",");
            }
            builder.deleteCharAt(builder.length()-1);
            String newValue = builder.toString();
            kvClient.put(key, ByteSequence.from(newValue.getBytes())).get();

            long endTime = System.currentTimeMillis();

            duration = (double) (endTime - startTime);
            System.out.println("Update time:" + duration + " ms");
        } catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1.0;
        }
        return duration;
    }
}
