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
    private boolean[][] matrixH = {
            {true, true, true, true, false, false, false, false, true, false, false, false, false, false, false, false},
            {true, true, false, false, true, true, false, false, false, true, false, false, false, false, false, false},
            {true, false, true, false, true, false, true, false, false, false, true, false, false, false, false, false},
            {false, true, false, true, false, true, true, false, false, false, false, true, false, false, false, false},
            {true, true, true, false, true, false, false, true, false, false, false, false, true, false, false, false},
            {true, false, false, true, false, true, false, true, false, false, false, false, false, true, false, false},
            {false, true, true, true, true, false, true, true, false, false, false, false, false, false, true, false},
            {true, true, true, false, false, true, true, true, false, false, false, false, false, false, false, true}};
  /*{1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
    {1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
    {1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0},
    {0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
    {1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0},
    {1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
    {0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0},
    {1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1}*/

    public MistakeDetector() { }

    public MistakeDetector(File inputFile) throws IOException {
        byte[] tab = Files.readAllBytes(Paths.get(inputFile.getPath()));
        receivedMessage = Arrays.asList(ArrayUtils.toObject(tab));
    }

    public StringBuilder encodeFile(File outputFile) throws IOException {
        StringBuilder resultString = new StringBuilder();
        ByteArrayOutputStream encodedBytes = new ByteArrayOutputStream();
        for(Byte obj: receivedMessage) {
            encodedBytes.write(encodeByte(obj));
            encodedBytes.write(System.lineSeparator().getBytes()); //Znak nowej lini w buforze
        }
        resultString.append(encodedBytes.toString());
        OutputStream fileOutput = new FileOutputStream(outputFile);
        fileOutput.write(encodedBytes.toByteArray());
        fileOutput.flush();
        fileOutput.close();
        return resultString;
    }

    private byte[] encodeByte(Byte object) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        boolean[] check= new boolean[8];
        BitSet bits = BitSet.valueOf(new byte[]{object}); //Konwersja bajtu na 8 bitów
        for (int i = 0; i < 8; i++) {
            check[i] = false;
            for (int j = 0; j < 8; j++) {
                if(bits.get(7 - j))
                    check[i]  ^= matrixH[i][j]; // Ustalanie kolejnych bitów parzystości poprzez operacje logiczną XOR na bitach
            }
            if(bits.get(7 - i)) //Zapis bitu znaku do bufora
                os.write(49);
            else
                os.write(48);
        }

        for (boolean value : check) { //Zapis bitów parzystości do bufora
            if(value) os.write(49);
            else os.write(48);
        }

        return os.toByteArray();
    }
}
