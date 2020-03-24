package telekom;

import org.apache.commons.lang3.ArrayUtils;
import sun.security.util.BitArray;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class MistakeDetector {
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

    private List<Byte> readInputFileToEncode(File inputFile) throws IOException {
        byte[] tab = Files.readAllBytes(Paths.get(inputFile.getPath()));
        return Arrays.asList(ArrayUtils.toObject(tab));
    }

    public StringBuilder encodeFile(File inputFile,File outputFile) throws IOException {
        StringBuilder resultString = new StringBuilder();
        ByteArrayOutputStream encodedBytes = new ByteArrayOutputStream();
        for(Byte obj: readInputFileToEncode(inputFile)) {
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

    private byte[] encodeByte(Byte object) {
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

    public StringBuilder decodeFile(File inputFile, File outputFile) throws IOException {
        StringBuilder resultString = new StringBuilder();
        ByteArrayOutputStream decodedBytes = new ByteArrayOutputStream();
        String currentLine;

        BufferedReader buffor = new BufferedReader(new FileReader(inputFile));
        while((currentLine = buffor.readLine()) != null) {
            boolean[] inputFileLineArray = readLineToBooleanArray(currentLine);
            decodedBytes.write(decodeLine(inputFileLineArray));
        }
        resultString.append(decodedBytes.toString());
        OutputStream fileOutput = new FileOutputStream(outputFile);
        fileOutput.write(decodedBytes.toByteArray());
        fileOutput.flush();
        fileOutput.close();
        return resultString;
    }

    private byte[] decodeLine(boolean[] lineOfArray)
    {
        int firstInvalidBit, secondInvalidBit; // indeksy błędnych bitów
        boolean isFirst = false, isSecond = false;
        boolean[] invalidBits = new boolean[8];
        for (int i = 0; i < 8; i++) {
            invalidBits[i] = false;

            for (int j = 0; j < lineOfArray.length; j++) {
                if(lineOfArray[j])
                    invalidBits[i] ^= matrixH[i][j]; // operacja XOR na wartościach macierzy
            }

            if(invalidBits[i])
                isFirst = true; // zapisuje czy czy znaleziono pierwszy błąd bitu
        }

        if(isFirst){
            for (int i = 0; i < lineOfArray.length; i++) {
                for (int j = i + 1; j < lineOfArray.length + 1; j++) {
                    isSecond = true;
                    for (int k = 0; k < 8; k++) {
                        if(invalidBits[k] != matrixH[k][i] ^ matrixH[k][j]) { //XOR w celu sprawdzenia czy są dwa błędy
                            isSecond = false;
                            break;
                        }
                    }

                    if(isSecond) {
                        lineOfArray[i] = !lineOfArray[i]; //zamiana błędnych bitów
                        lineOfArray[j] = !lineOfArray[j];
                        i= lineOfArray.length;
                        break;
                    }
                }
            }
            if(!isSecond){
                for (int i = 0; i < lineOfArray.length; i++) {
                    for (int j = 0; j < 8; j++) {
                        if(matrixH[j][i] != invalidBits[j])
                            break;

                        if(j == 7){
                            invalidBits[i] = !lineOfArray[i];
                            i = lineOfArray.length;
                        }

                    }
                }
            }
        }
        BitArray bb = new BitArray(Arrays.copyOfRange(lineOfArray,0,7));
        return bb.toByteArray();
    }

    private boolean[] readLineToBooleanArray(String line)
    {
        boolean[] result = new boolean[line.length()];
        for (int i = 0; i < line.length(); i++) {
            if(line.charAt(i) == '1') result[i] = true;
        }
        return result;
    }
}
