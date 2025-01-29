package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.*;

class IPCounterTest {
    IPCounter testCounter = new IPCounter("/Users/sharon/task_ip_addresses_compressed.zip");
    BitSet firstTestHalfSet = new BitSet(Integer.MAX_VALUE);
    BitSet secondTestHalfSet = new BitSet(Integer.MAX_VALUE);

    @Test
    public void calculateIPPosition() {
        long expectedResult = 760878341;
        int[] testIP = new int[4];
        testIP[0] = 45;
        testIP[1] = 90;
        testIP[2] = 21;
        testIP[3] = 5;

        long result = testCounter.getOrdinalIPNumber(testIP);
        assertEquals(expectedResult, result);
    }

    @Test
    public void calculateIPPositionIfAllZeroes() {
        long expectedResult = 0;
        int[] testIP = new int[4];
        testIP[0] = 0;
        testIP[1] = 0;
        testIP[2] = 0;
        testIP[3] = 0;
        long result = testCounter.getOrdinalIPNumber(testIP);
        assertEquals(expectedResult, result);
    }

    @Test
    public void checkIPMarkInBitSet() {
        long expectedResult = 1;
        long ordinalNumber = 3;
        if (ordinalNumber > Integer.MAX_VALUE) {
            long ordinal = ordinalNumber - Integer.MAX_VALUE - 1;
            secondTestHalfSet.set((int)ordinal);
        } else {
            firstTestHalfSet.set((int)ordinalNumber);
        }

        long result = firstTestHalfSet.cardinality() + secondTestHalfSet.cardinality();
        assertEquals(expectedResult, result);
    }



}