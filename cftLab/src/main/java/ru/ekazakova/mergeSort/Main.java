package ru.ekazakova.mergeSort;

import ru.ekazakova.mergeSort.exceptions.inputEx.LackOfFiles;
import ru.ekazakova.mergeSort.exceptions.inputEx.MissingDataType;
import ru.ekazakova.mergeSort.exceptions.inputEx.UndefinedParameter;
import ru.ekazakova.mergeSort.inputWorkers.InputHandler;
import ru.ekazakova.mergeSort.inputWorkers.InputInfo;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            InputInfo info = InputHandler.generateInfo(args);
            FileSorter fileSorter = new FileSorter(info);
            fileSorter.mergeSort();
        } catch (UndefinedParameter | MissingDataType | LackOfFiles | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
