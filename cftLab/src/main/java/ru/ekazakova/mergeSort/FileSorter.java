package ru.ekazakova.mergeSort;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import ru.ekazakova.mergeSort.exceptions.InvalidLine;
import ru.ekazakova.mergeSort.exceptions.TypeMismatch;
import ru.ekazakova.mergeSort.inputWorkers.DataType;
import ru.ekazakova.mergeSort.inputWorkers.InputInfo;
import ru.ekazakova.mergeSort.inputWorkers.SortMode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileSorter {
    private static final String STANDARD_ADDITION_FILE_NAME = "tmp";
    private static final String STANDARD_ADDITION_FILE_TYPE = ".txt";
    private static final String ELEMENT_DELIMITER = "\n";
    private static final String INVALID_SYMBOL = " ";
    private static final int NUM_OF_FILES_NOT_NEEDED_TO_ADD = 2;
    private static final int VALUE_FOR_EQUALS_ELS = 0;
    private static final int MINIMUM_FILES = 1;
    private static final int INDEX_FOR_OUTPUT_FILE = 0;

    private final InputInfo info;
    private final List<String> usingFileNames;

    private int numberOfAdditionFile = 0;

    private String computeNextUsingFileName() {
        if (usingFileNames.size() == NUM_OF_FILES_NOT_NEEDED_TO_ADD) {
            return info.getOutputFileName();
        }
        String res = STANDARD_ADDITION_FILE_NAME + numberOfAdditionFile + STANDARD_ADDITION_FILE_TYPE;
        ++numberOfAdditionFile;
        return res;
    }

    private boolean isStrContainsInvalidSymbols(String str) {
        return str.contains(INVALID_SYMBOL);
    }

    private boolean isLess(String el1, String el2) {
        if (info.getDataType() == DataType.DIGITS) {
            return Integer.parseInt(el1) < Integer.parseInt(el2);
        }
        return el1.compareTo(el2) < VALUE_FOR_EQUALS_ELS;
    }

    private void writeRightLine(BufferedWriter writer, String line1, String line2) throws IOException {
        String formattedLine1 = line1 + ELEMENT_DELIMITER;
        String formattedLine2 = line2 + ELEMENT_DELIMITER;
        if (isLess(line1, line2)) {
            writer.write(info.getMode() == SortMode.ASCENDING ? formattedLine1 : formattedLine2);
        } else {
            writer.write(info.getMode() == SortMode.ASCENDING ? formattedLine2 : formattedLine1);
        }
    }

    private void addRemaining(BufferedReader reader, String line, BufferedWriter writer) throws IOException {
        String startLine = line;
        while (line != null) {
            try {
                checkLineForValidation(line);
                if (isSortOrderBroken(startLine, line)) return;
                writer.write(line + ELEMENT_DELIMITER);
            } catch (InvalidLine | TypeMismatch ex) {
                System.out.println(ex.getMessage());
            }
            line = reader.readLine();
        }
    }

    private boolean addRemainingIfOneIsEmptyFile(BufferedReader firstReader, BufferedReader secondReader,
                                                 String lineFromFirst, String lineFromSecond,
                                                 BufferedWriter writer) throws IOException {
        if (lineFromFirst == null && lineFromSecond == null) {
            return true;
        }
        if (lineFromFirst == null) {
            addRemaining(secondReader, lineFromSecond, writer);
            return true;
        }
        if (lineFromSecond == null) {
            addRemaining(firstReader, lineFromFirst, writer);
            return true;
        }
        return false;
    }

    private void addRemainingFromFileWithoutBrokenOrder(BufferedReader firstReader, BufferedReader secondReader,
                                                        String previousLineFromFirst, String previousLineFromSecond,
                                                        String lineFromFirst, String lineFromSecond,
                                                        BufferedWriter writer) throws IOException {
        if (isSortOrderBroken(previousLineFromFirst, lineFromFirst)
                && isSortOrderBroken(previousLineFromSecond, lineFromSecond)) return;

        if (lineFromFirst != null && !isSortOrderBroken(previousLineFromFirst, lineFromFirst)) {
            addRemaining(firstReader, lineFromFirst, writer);
            return;
        }
        addRemaining(secondReader, lineFromSecond, writer);
    }

    private void addRemainingFromRightFile(BufferedReader firstReader, BufferedReader secondReader,
                                           String previousLineFromFirst, String previousLineFromSecond,
                                           String lineFromFirst, String lineFromSecond,
                                           BufferedWriter writer) throws IOException {
        boolean isAllAdded = addRemainingIfOneIsEmptyFile(firstReader, secondReader,
                lineFromFirst, lineFromSecond, writer);
        if (isAllAdded) return;
        addRemainingFromFileWithoutBrokenOrder(firstReader, secondReader, previousLineFromFirst, previousLineFromSecond,
                lineFromFirst, lineFromSecond, writer);
    }

    private void checkLinesForValidation(String line1, String line2) throws InvalidLine, TypeMismatch {
        checkLineForValidation(line1);
        checkLineForValidation(line2);
    }

    private void checkLineForValidation(String line) throws InvalidLine, TypeMismatch {
        if (isStrContainsInvalidSymbols(line)) {
            throw new InvalidLine("Line " + line + " has invalid symbol: \"" + INVALID_SYMBOL + "\"." +
                    "The line is skipped");
        }
        if ((!NumberUtils.isCreatable(line))
                && info.getDataType() == DataType.DIGITS) {
            throw new TypeMismatch("Specified type " + info.getDataType() + " but line " + line
                    + " consists elements of another type. The line is skipped");
        }
    }

    private boolean isStrValid(String line) {
        try {
            checkLineForValidation(line);
        } catch (InvalidLine | TypeMismatch e) {
            return false;
        }
        return true;
    }

    private String computeNextLine(String mutableLine, String comparedLine, BufferedReader reader) throws IOException {
        if (!isStrValid(mutableLine)) {
            return reader.readLine();
        }
        if (!isStrValid(comparedLine)) {
            return mutableLine;
        }

        if (info.getMode() == SortMode.ASCENDING ^ isLess(mutableLine, comparedLine)) {
            return mutableLine;
        }
        return reader.readLine();
    }

    private void merge(int firstFileIndex, int secondFileIndex, String additionFileName) throws IOException {
        try (BufferedReader firstReader = new BufferedReader(new FileReader(usingFileNames.get(firstFileIndex)));
             BufferedReader secondReader = new BufferedReader(new FileReader(usingFileNames.get(secondFileIndex)));
             BufferedWriter writer = new BufferedWriter(new FileWriter(additionFileName))) {
            String lineFromFirst = firstReader.readLine();
            String lineFromSecond = secondReader.readLine();
            String previousLineFromFirst = lineFromFirst;
            String previousLineFromSecond = lineFromSecond;
            while (lineFromFirst != null && lineFromSecond != null) {
                try {
                    checkLinesForValidation(lineFromFirst, lineFromSecond);
                    if (isSortOrderBroken(previousLineFromFirst, lineFromFirst)
                            || isSortOrderBroken(previousLineFromSecond, lineFromSecond)) {
                        break;
                    }
                    writeRightLine(writer, lineFromFirst, lineFromSecond);
                    previousLineFromFirst = lineFromFirst;
                    previousLineFromSecond = lineFromSecond;
                    if (lineFromFirst.equals(lineFromSecond)) { // if lines are equal write the first
                        lineFromFirst = firstReader.readLine();
                        continue;
                    }
                } catch (InvalidLine | TypeMismatch ex) {
                    System.out.println(ex.getMessage());
                }
                lineFromFirst = computeNextLine(previousLineFromFirst, previousLineFromSecond, firstReader);
                lineFromSecond = computeNextLine(previousLineFromSecond, previousLineFromFirst, secondReader);
            }
            log.info("write remaining lines to " + additionFileName);
            addRemainingFromRightFile(firstReader, secondReader, previousLineFromFirst, previousLineFromSecond, lineFromFirst,
                    lineFromSecond, writer);
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

    private void handleWrongFile(int numberOfFileName) {
        usingFileNames.remove(numberOfFileName);
    }

    private void handle2WrongFiles(int numberOfFileName1, int numberOfFileName2) {
        handleWrongFile(numberOfFileName2);
        handleWrongFile(numberOfFileName1);
    }

    public FileSorter(InputInfo info) {
        this.info = info;
        usingFileNames = new ArrayList<>(info.getInputFileNames());
    }

    public void mergeSort() throws IOException {
        if (usingFileNames.size() == MINIMUM_FILES && !usingFileNames.get(INDEX_FOR_OUTPUT_FILE).equals(info.getOutputFileName())) {
            FileUtils.copyFile(new File(usingFileNames.get(INDEX_FOR_OUTPUT_FILE)), new File(info.getOutputFileName()));
            return;
        }
        if (usingFileNames.size() <= MINIMUM_FILES) {
            return;
        }

        for (int numOfFileName = 0; numOfFileName + 1 < usingFileNames.size(); ++numOfFileName) {
            try {
                String nextUsingFileName = computeNextUsingFileName();
                log.info("start to merge " + usingFileNames.get(numOfFileName) + " and "
                        + usingFileNames.get(numOfFileName + 1) + " to " + nextUsingFileName);
                merge(numOfFileName, numOfFileName + 1, nextUsingFileName);
                updateFileList(nextUsingFileName, numOfFileName, numOfFileName + 1);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                handle2WrongFiles(numOfFileName, numOfFileName + 1);
            }
        }
        mergeSort();
    }
}
