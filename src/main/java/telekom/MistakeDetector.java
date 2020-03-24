package telekom;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class MistakeDetector {
    private List<Byte> receivedMessage;
    private int[][] matrixH = {
            {1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0},
            {1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0},
            {1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1}};

    public MistakeDetector() { }

    public MistakeDetector(File inputFile) throws IOException {
        byte[] tab = Files.readAllBytes(Paths.get(inputFile.getPath()));
        receivedMessage = Arrays.asList(ArrayUtils.toObject(tab));
    }

    public StringBuilder encodeFile(File outputFile) throws IOException {
        StringBuilder result = new StringBuilder();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int[] check= new int[8];
        for(Byte obj: receivedMessage) {
            BitSet bits = BitSet.valueOf(new byte[]{obj});
            for (int i = 0; i < 8; i++) {
                check[i] = 0;
                for (int j = 0; j < 8; j++) {
                    if(bits.get(7 - j))
                        check[i] += matrixH[i][j];
                }
                check[i] %= 2;
                if(bits.get(7 - i))
                    os.write(49);
                else
                    os.write(48);
            }
            for (int value : check) os.write((value + 48));
            os.write(System.lineSeparator().getBytes());
        }
        result.append(os.toString());
        OutputStream fileOutput = new FileOutputStream(outputFile);
        fileOutput.write(os.toByteArray());
        os.flush();
        fileOutput.flush();
        os.close();
        fileOutput.close();
        return result;
    }
}
