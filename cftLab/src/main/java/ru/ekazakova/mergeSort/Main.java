package ru.ekazakova.mergeSort;

import ru.ekazakova.mergeSort.exceptions.inputEx.LackOfFiles;
import ru.ekazakova.mergeSort.exceptions.inputEx.MissingDataType;
import ru.ekazakova.mergeSort.exceptions.inputEx.UndefinedParameter;
import ru.ekazakova.mergeSort.inputWorkers.InputHandler;
import ru.ekazakova.mergeSort.inputWorkers.InputInfo;

public class Main {
    public static void main(String[] args) {
        try {
            InputInfo info = InputHandler.getInfo(args);
            FileSorter fileHandler = new FileSorter(info);
            fileHandler.mergeSort();
        } catch (UndefinedParameter | MissingDataType | LackOfFiles e) {
            System.out.println(e.getMessage());
        }
    }
}
