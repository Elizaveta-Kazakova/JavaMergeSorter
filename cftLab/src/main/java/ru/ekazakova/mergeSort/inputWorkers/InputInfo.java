package ru.ekazakova.mergeSort.inputWorkers;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    public void addInputFileName(String fileName) {
        inputFileNames.add(fileName);
    }

}
