package org.example;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class IPCounter {
    private static final long POWER_OF_THREE_OCTET = 16777216; //256*256*256
    private static final long POWER_OF_TWO_OCTET = 65536; //256*256
    private static final int MEMORY_PAGE_SIZE = 16384;
    private BitSet firstHalfSet = new BitSet(Integer.MAX_VALUE);
    private BitSet secondHalfSet = new BitSet(Integer.MAX_VALUE);
    private File file;

    public IPCounter(String fileName) {
        file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File " + fileName + " doesn't exist");
            System.exit(0);
        }
    }

    public long count() {
        readFile();
        long result = firstHalfSet.cardinality() + secondHalfSet.cardinality();
        return result;
    }

    private void readFile() {
        try (ZipFile compressedFile = new ZipFile(file)) {
            List<? extends ZipEntry> filesList = compressedFile.stream().toList();
            try (InputStream is = compressedFile.getInputStream(filesList.getFirst());
                 ReadableByteChannel fileChan = Channels.newChannel(is)) {

                ByteBuffer buffer = ByteBuffer.allocate(MEMORY_PAGE_SIZE);
                StringBuilder ipPartBuilder = new StringBuilder();
                int[] ipAddressArray = new int[4];
                int ipFullnessCounter = 0;

                while (fileChan.read(buffer) != -1) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        char c = (char) buffer.get();
                        if (Character.isDigit(c)) {
                            ipPartBuilder.append(c);
                        } else if (c == '.') {
                            ipAddressArray[ipFullnessCounter] = Integer.parseInt(ipPartBuilder.toString());

                            ipFullnessCounter++;
                            ipPartBuilder.delete(0, ipPartBuilder.length());
                        } else if (c == '\n') {
                            ipAddressArray[ipFullnessCounter] = Integer.parseInt(ipPartBuilder.toString());
                            markIPinBitSet(getOrdinalIPNumber(Arrays.copyOf(ipAddressArray, ipAddressArray.length)));

                            ipPartBuilder.delete(0, ipPartBuilder.length());
                            ipFullnessCounter = 0;
                        }
                    }
                    buffer.clear();
                }
            }
        } catch (ZipException e) {
            System.out.println("Something went wrong" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Something went wrong" + e.getMessage());
            e.printStackTrace();
        }
    }

    long getOrdinalIPNumber(int[] ipArray) {
        long result = ((long)ipArray[0] * POWER_OF_THREE_OCTET) + ((long)ipArray[1] * POWER_OF_TWO_OCTET) + ((long)ipArray[2] * 256L) + (long)ipArray[3];
        return result;
    }

    void markIPinBitSet(long ordinalNumber) {
        if (ordinalNumber > Integer.MAX_VALUE) {
            long ordinal = ordinalNumber - Integer.MAX_VALUE - 1;
            secondHalfSet.set((int)ordinal);
        } else {
            firstHalfSet.set((int)ordinalNumber);
        }
    }
}
