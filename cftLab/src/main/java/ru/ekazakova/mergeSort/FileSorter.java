package ru.ekazakova.mergeSort;

import ru.ekazakova.mergeSort.exceptions.InvalidLine;
import ru.ekazakova.mergeSort.exceptions.TypeMismatch;
import ru.ekazakova.mergeSort.inputWorkers.DataType;
import ru.ekazakova.mergeSort.inputWorkers.InputInfo;
import ru.ekazakova.mergeSort.inputWorkers.SortMode;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileSorter {
    private static final String STANDARD_ADDITION_FILE_NAME = "tmp";
    private static final String STANDARD_ADDITION_FILE_TYPE = ".txt";
    private static final String ELEMENT_DELIMITER = "\n";
    private static final String INVALID_SYMBOL = " ";
    private static final int NUM_OF_FILES_NOT_NEEDED_TO_ADD = 2;
    private static final int VALUE_FOR_EQUALS_ELS = 0;
    private static final int MINIMUM_FILES_FOR_SORT = 2;

    private final InputInfo info;
    private final List<String> usingFileNames;

    private int numberOfAdditionFile = 0;

    private String getNextUsingFileName() {
        if (usingFileNames.size() == NUM_OF_FILES_NOT_NEEDED_TO_ADD) {
            return info.getOutputFileName();
        }
        String res = STANDARD_ADDITION_FILE_NAME + numberOfAdditionFile + STANDARD_ADDITION_FILE_TYPE;
        ++numberOfAdditionFile;
        return res;
    }

    private boolean isStrValid(String str) {
        return !str.contains(INVALID_SYMBOL);
    }

    private boolean isLess(String el1, String el2) {
        if (info.getDataType() == DataType.DIGITS) {
            return Integer.parseInt(el1) < Integer.parseInt(el2);
        }
        return el1.compareTo(el2) < VALUE_FOR_EQUALS_ELS;
    }

    private void sortLine(BufferedWriter writer, String line1, String line2) throws IOException {
        String formattedLine1 = line1 + ELEMENT_DELIMITER;
        String formattedLine2 = line2 + ELEMENT_DELIMITER;
        if (isLess(line1, line2)) {
            writer.write(info.getMode() == SortMode.ASCENDING ? formattedLine1 : formattedLine2);
        } else {
            writer.write(info.getMode() == SortMode.ASCENDING ? formattedLine2 : formattedLine1);
        }
    }

    private void addRemaining(BufferedReader reader, String line, BufferedWriter writer, int numOfLine,
                              int numOfSortedLines) throws IOException {
        while(numOfLine < numOfSortedLines) {
            try {
                checkLine(line);
                writer.write(line + ELEMENT_DELIMITER);
                line = reader.readLine();
            } catch (InvalidLine | TypeMismatch ex) {
                System.out.println(ex.getMessage());
                line = reader.readLine();
            }
            ++numOfLine;
        }
    }

    private void addRemainingFrom2Lines(BufferedReader firstReader, BufferedReader secondReader, String line1,
                                        String line2, BufferedWriter writer, int numOfLine1, int numOfSortedLines1,
                                        int numOfLine2, int numOfSortedLines2) throws IOException {
        addRemaining(firstReader, line1, writer, numOfLine1, numOfSortedLines1);
        addRemaining(secondReader, line2, writer, numOfLine2, numOfSortedLines2);
    }

    private void checkLines(String line1, String line2) throws InvalidLine, TypeMismatch {
        checkLine(line1);
        checkLine(line2);
    }

    private void checkLine(String line) throws InvalidLine, TypeMismatch {
        if (!isStrValid(line)) {
            throw new InvalidLine("Line " + line + " has invalid symbol: \"" + INVALID_SYMBOL + "\"." +
                    "The line is skipped");
        }
        if ((!NumberUtils.isCreatable(line))
                && info.getDataType() == DataType.DIGITS) {
            throw new TypeMismatch("Specified type " + info.getDataType() + " but line " + line
                    + " consists elements of another type. The line is skipped");
        }
    }

    private boolean isValidated(String line) {
        try {
            checkLine(line);
        } catch (InvalidLine | TypeMismatch e) {
            return false;
        }
        return true;
    }
    
    private String getNextLine(String mutableLine, String comparedLine, BufferedReader reader) throws IOException {
        if (!isValidated(mutableLine)) {
            return reader.readLine();
        }
        if (info.getMode() == SortMode.ASCENDING ^ isLess(mutableLine, comparedLine)) {
            return mutableLine;
        }
        return reader.readLine();
    }

    private int getNextNumOfLine(String mutableLine, String comparedLine, int numOfLines) {
        if (!isValidated(mutableLine)) {
            return ++numOfLines;
        }
        if (info.getMode() == SortMode.ASCENDING ^ isLess(mutableLine, comparedLine)) {
            return numOfLines;
        }
        return ++numOfLines;
    }

    private void merge(int firstFileIndex, int secondFileIndex, String additionFileName, int numOfSortedLines1,
                       int numOfSortedLines2) throws IOException {
        try(BufferedReader firstReader = new BufferedReader(new FileReader(usingFileNames.get(firstFileIndex)));
                BufferedReader secondReader = new BufferedReader(new FileReader(usingFileNames.get(secondFileIndex)));
                BufferedWriter writer = new BufferedWriter(new FileWriter(additionFileName))) {
            String lineFromFirst = firstReader.readLine();
            String lineFromSecond = secondReader.readLine();
            int numOfLine1 = 0;
            int numOfLine2 = 0;
            while(numOfLine1 < numOfSortedLines1 && numOfLine2 < numOfSortedLines2) {
                try {
                    checkLines(lineFromFirst, lineFromSecond);
                    sortLine(writer, lineFromFirst, lineFromSecond);
                    if (lineFromFirst.equals(lineFromSecond)) { // if lines are equal write the first
                        lineFromFirst = firstReader.readLine();
                        ++numOfLine1;
                    } else {
                        numOfLine1 = getNextNumOfLine(lineFromFirst, lineFromSecond, numOfLine1);
                        numOfLine2 = getNextNumOfLine(lineFromSecond, lineFromFirst, numOfLine2);
                        String tmpLineFromFirst = getNextLine(lineFromFirst, lineFromSecond, firstReader);
                        lineFromSecond = getNextLine(lineFromSecond, lineFromFirst, secondReader);
                        lineFromFirst = tmpLineFromFirst;
                    }
                } catch (InvalidLine | TypeMismatch ex) {
                    System.out.println(ex.getMessage());
                    numOfLine1 = getNextNumOfLine(lineFromFirst, lineFromSecond, numOfLine1);
                    numOfLine2 = getNextNumOfLine(lineFromSecond, lineFromFirst, numOfLine2);
                    lineFromFirst = getNextLine(lineFromFirst, lineFromSecond, firstReader);
                    lineFromSecond = getNextLine(lineFromSecond, lineFromFirst, secondReader);
                }
            }
            addRemainingFrom2Lines(firstReader, secondReader, lineFromFirst, lineFromSecond, writer, numOfLine1,
                    numOfSortedLines1, numOfLine2, numOfSortedLines2);
        } catch (IOException e) {
            throw e;
        }
    }

    private void updateFileList(String newFileName, int firstUsedFile, int secondUsedFile) {
        usingFileNames.remove(secondUsedFile);
        usingFileNames.remove(firstUsedFile);
        usingFileNames.add(firstUsedFile, newFileName);
    }

    private boolean isSortOrderBroken(String startLine, String line) {
        return info.getMode() == SortMode.ASCENDING && isLess(line, startLine)
                || info.getMode() == SortMode.DESCENDING && isLess(startLine, line);
    }

    private String getValidatedStr(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null && !isValidated(line)) {
            line = reader.readLine();
        }
        return line;
    }

    private int getNumOfSortedLines(int numOfFileName) throws IOException {
        int numOfSortedLines = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(usingFileNames.get(numOfFileName)))) {
            String line = getValidatedStr(reader);
            String startLine = line;
            while(line != null && !isSortOrderBroken(startLine, line)) {
                ++numOfSortedLines;
                line = getValidatedStr(reader);
            }
        }
        return numOfSortedLines;
    }

    public FileSorter(InputInfo info) {
        this.info = info;
        usingFileNames = new ArrayList<>(info.getFileNames());
    }

    public void mergeSort() {
        if (usingFileNames.size() < MINIMUM_FILES_FOR_SORT) {
            return;
        }
        for (int numOfFileName = 0; numOfFileName + 1 < usingFileNames.size(); ++numOfFileName) {
            try {
                String nextUsingFileName = getNextUsingFileName();
                int numOfSortedLines1 = getNumOfSortedLines(numOfFileName);
                int numOfSortedLines2 = getNumOfSortedLines(numOfFileName + 1);
                merge(numOfFileName, numOfFileName + 1, nextUsingFileName, numOfSortedLines1,
                        numOfSortedLines2);
                updateFileList(nextUsingFileName, numOfFileName, numOfFileName + 1);
            } catch (IOException e) {
                e.printStackTrace(); // TODO : !!!
            }
        }
        mergeSort();
    }
}
