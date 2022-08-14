package ru.ekazakova.mergeSort.inputWorkers;

import java.util.ArrayList;
import java.util.List;

public class InputInfo {
    private SortMode mode;
    private DataType dataType;
    private List<String> inputFileNames = new ArrayList<>();
    private String outputFileName;

    public InputInfo(SortMode mode, DataType dataType) {
        this.mode = mode;
        this.dataType = dataType;
    }

    public InputInfo(DataType dataType) {
        this(SortMode.ASCENDING, dataType);
    }

    public InputInfo() {
        this(DataType.UNKNOWN);
    }

    public DataType getDataType() {
        return dataType;
    }

    public SortMode getMode() {
        return mode;
    }

    public List<String> getFileNames() {
        return inputFileNames;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setMode(SortMode mode) {
        this.mode = mode;
    }

    public void setInputFileNames(List<String> inputFileNames) {
        this.inputFileNames = inputFileNames;
    }

    public void addInputFileName(String fileName) {
        inputFileNames.add(fileName);
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

}
