package org.example;

import java.io.File;
import java.io.FileNotFoundException;
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
    private BitSet firstHalfSet = new BitSet(Integer.MAX_VALUE);
    private BitSet secondHalfSet = new BitSet(Integer.MAX_VALUE);

    public long count(String fileName) {
        readFile(fileName);
        long result = firstHalfSet.cardinality() + secondHalfSet.cardinality();
        return result;
    }

    private void readFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File " + fileName + " doesn't exist");
            return;
        }

        try (ZipFile compressedFile = new ZipFile(file)) {
            List<? extends ZipEntry> filesList = compressedFile.stream().toList();
            try (InputStream is = compressedFile.getInputStream(filesList.getFirst());
                 ReadableByteChannel fileChan = Channels.newChannel(is)) {

                ByteBuffer buffer = ByteBuffer.allocate(16384);
                StringBuilder ipPartBuilder = new StringBuilder();
                int[] ipAddressArray = new int[4];
                int ipFullnessCounter = 0;

                while (fileChan.read(buffer) != -1) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        char c = (char) buffer.get(); //читаю байт
                        if (Character.isDigit(c)) {
                            ipPartBuilder.append(c); //если это цифра, то собираю часть айпишника
                        } else if (c == '.') {
                            ipAddressArray[ipFullnessCounter] = Integer.parseInt(ipPartBuilder.toString());
                            ipFullnessCounter++;
                            //если наш байт – это точка, то кладем получившийся октет в массив
                            ipPartBuilder.delete(0, ipPartBuilder.length()); //очищаю билдер октета
                        } else if (c == '\n') {
                            ipAddressArray[ipFullnessCounter] = Integer.parseInt(ipPartBuilder.toString());
                            markIPinBitSet(getOrdinalIPNumber(Arrays.copyOf(ipAddressArray, ipAddressArray.length)));

                            ipPartBuilder.delete(0, ipPartBuilder.length()); //очищаю билдер октета
                            ipFullnessCounter = 0;
                        }
                    }
                    buffer.clear();
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        } catch (ZipException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long getOrdinalIPNumber(int[] ipArray) {
        //тут просто считаю порядковый номер
        long result = ((long)ipArray[0] * 256 * 256 * 256) + ((long)ipArray[1] * 256 * 256) + ((long)ipArray[2] * 256) + (long)ipArray[3];
        return result;
    }

    private void markIPinBitSet(long ordinalNumber) {
        if (ordinalNumber > 2147483647) {
            long ordinal = ordinalNumber - 2147483647 - 1;
            secondHalfSet.set((int)ordinal);
        } else {
            firstHalfSet.set((int)ordinalNumber);
        }
    }
}
