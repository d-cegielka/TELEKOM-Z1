package telekom;

import org.apache.commons.lang3.ArrayUtils;

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

    public void encodeFile(File inputFile, File outputFile) throws IOException {
        ByteArrayOutputStream encodedBytes = new ByteArrayOutputStream();
        for (Byte obj : readInputFileToEncode(inputFile)) {
            encodedBytes.write(encodeByte(obj));
            encodedBytes.write(System.lineSeparator().getBytes()); //Znak nowej lini w buforze
        }
        OutputStream fileOutput = new FileOutputStream(outputFile);
        fileOutput.write(encodedBytes.toByteArray());
        fileOutput.flush();
        fileOutput.close();
    }

    public StringBuilder encodeText(String inputText) throws IOException {
        ByteArrayOutputStream encodedBytes = new ByteArrayOutputStream();
        StringBuilder resultString = new StringBuilder();
        for(Byte obj: ArrayUtils.toObject(inputText.getBytes())) {
            encodedBytes.write(encodeByte(obj));
            encodedBytes.write(System.lineSeparator().getBytes()); //Znak nowej lini w buforze
        }
        resultString.append(encodedBytes.toString());
        return resultString;
    }

    private byte[] encodeByte(Byte object) {
        ByteArrayOutputStream encodedByte = new ByteArrayOutputStream();
        boolean[] check= new boolean[8];
        BitSet bits = BitSet.valueOf(new byte[]{object}); //Konwersja bajtu na 8 bitów.
        for (int i = 0; i < 8; i++) { //Indeks kolejnych bitów parzystości i kolejnych wierszy macierzy H.
            for (int j = 0; j < 8; j++) { //Indeks kolejnych kolumn w wierszu macierzy H.
                if(bits.get(7 - j))
                    check[i]  ^= matrixH[i][j]; //Ustalanie kolejnych bitów parzystości poprzez operacje logiczną XOR na bitach.
            }
            if(bits.get(7 - i)) //Zapis bitu znaku do bufora.
                encodedByte.write(49);
            else
                encodedByte.write(48);
        }

        for (boolean value : check) { //Zapis bitów parzystości do bufora.
            if(value)
                encodedByte.write(49);
            else
                encodedByte.write(48);
        }

        return encodedByte.toByteArray();
    }

    public void decodeFile(File inputFile, File outputFile) throws IOException {
        ByteArrayOutputStream decodedBytes = new ByteArrayOutputStream();
        String currentLine;

        BufferedReader buffor = new BufferedReader(new FileReader(inputFile));
        while((currentLine = buffor.readLine()) != null) {
            boolean[] inputFileLineArray = readLineToBooleanArray(currentLine);
            decodedBytes.write(decodeLineBits(inputFileLineArray));
        }
        OutputStream fileOutput = new FileOutputStream(outputFile);
        fileOutput.write(decodedBytes.toByteArray());
        fileOutput.flush();
        fileOutput.close();
    }

    public StringBuilder decodeText(String inputText) throws IOException {
        ByteArrayOutputStream decodedBytes = new ByteArrayOutputStream();
        StringBuilder resultString = new StringBuilder();
        String currentLine;
        BufferedReader buffor = new BufferedReader(new StringReader(inputText));
        while((currentLine = buffor.readLine()) != null) {
            boolean[] inputFileLineArray = readLineToBooleanArray(currentLine);
            decodedBytes.write(decodeLineBits(inputFileLineArray));
        }
        resultString.append(decodedBytes.toString());
        return resultString;
    }

    private byte[] decodeLineBits(boolean[] lineOfArray) {
        boolean isFirstMistake = false, isSecondMistake = false;
        boolean[] invalidBits = new boolean[8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < lineOfArray.length; j++) {
                if(lineOfArray[j])
                    invalidBits[i] ^= matrixH[i][j]; //Operacja XOR na wartościach macierzy.
            }

            if(invalidBits[i])
                isFirstMistake = true; //Zapisuje czy wystąpił błąd transmisji.
        }

        if(isFirstMistake){
            for (int i = 0; i < lineOfArray.length - 1; i++) {
                for (int j = i + 1; j < lineOfArray.length; j++) {
                    isSecondMistake = true;// Jeśli XOR wartości z 2 kolumn daje takie same wartości jak tablica invalidBits to indeksy tych kolumn są miejscami błędu w wiadomości.
                    for (int k = 0; k < 8; k++) {
                        if (invalidBits[k] != matrixH[k][i] ^ matrixH[k][j]) {// Jeśli wynik operacji XOR wartości z odpowiednich kolumn macierzy jest identyczny z wartością
                            isSecondMistake = false;                          // w tablicy błędów (invalidBits), to indeksy tych kolumn są miejscami błędnych bitów w wiadomości,
                            break;                                            // w przeciwnym wypadku dane kolumny nie są indeksami błędnych bitów w wiadomości.
                        }
                    }

                    if (isSecondMistake) {
                        lineOfArray[i] = !lineOfArray[i]; //Korekta błędnych bitów.
                        lineOfArray[j] = !lineOfArray[j];
                        i = lineOfArray.length;
                        break;
                    }
                }
            }
            if(!isSecondMistake){
                for (int i = 0; i < lineOfArray.length; i++) { //Sprawdzamy wszystkie kolumny macierzy.
                    for (int j = 0; j < 8; j++) { //Sprawdzamy każdą wartość w danej kolumnie przechodząc do kolejnych wierszy.
                        if(matrixH[j][i] != invalidBits[j]) //Jeżeli wartość jest różna od wartości w tablicy błędu to bit wiadomości o indeksie aktualnej kolumny jest poprawny.
                            break;

                        if(j == 7){ //Po przejściu do ostatniej wartości w kolumnie, czyli cała kolumna jest identyczna jak tablica błędu wykonujemy korekcje bitu według indeksu i kolumny
                            lineOfArray[i] = !lineOfArray[i];
                            i = lineOfArray.length;
                        }
                    }
                }
            }
        }
        BitSet decodedBits = new BitSet(8);
        for(int i =0; i<8;i++)
            decodedBits.set(7-i,lineOfArray[i]);

        return decodedBits.toByteArray();
    }

    private boolean[] readLineToBooleanArray(String line) {
        boolean[] result = new boolean[line.length()];
        for (int i = 0; i < line.length(); i++)
            if(line.charAt(i) == '1') result[i] = true;

        return result;
    }
}
