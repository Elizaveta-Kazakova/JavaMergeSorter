package ru.ekazakova.mergeSort.inputWorkers;

import ru.ekazakova.mergeSort.exceptions.inputEx.LackOfFiles;
import ru.ekazakova.mergeSort.exceptions.inputEx.MissingDataType;
import ru.ekazakova.mergeSort.exceptions.inputEx.UndefinedParameter;

public class InputHandler {
    private static final int START_POS = 0;
    private static final int SIZE_OF_EL = 1;
    private static final int MINIMUM_FILES = 2;
    private static final char PARAMETER_IDENTIFIER = '-';
    private static final char ASCENDING_PARAM = 'a';
    private static final char DESCENDING_PARAM = 'd';
    private static final char SYMBOLS_PARAM = 's';
    private static final char DIGITS_PARAM = 'i';
    private static final String MESSAGE_DELIMITER = ", ";
    private static final String ADDITION_MESSAGE_DELIMITER = " or ";

    private static void handleParam(String arg, InputInfo info) throws UndefinedParameter {
        switch (arg.charAt(START_POS + SIZE_OF_EL)) {
            case ASCENDING_PARAM  -> info.setMode(SortMode.ASCENDING);
            case DESCENDING_PARAM -> info.setMode(SortMode.DESCENDING);
            case SYMBOLS_PARAM -> info.setDataType(DataType.SYMBOLS);
            case DIGITS_PARAM  -> info.setDataType(DataType.DIGITS);
            default -> throw new UndefinedParameter("Passed option " + arg.charAt(START_POS + SIZE_OF_EL)
                    + " that is not defined. Please write " + PARAMETER_IDENTIFIER + ASCENDING_PARAM + MESSAGE_DELIMITER
                    + PARAMETER_IDENTIFIER + DESCENDING_PARAM + MESSAGE_DELIMITER + PARAMETER_IDENTIFIER
                    + SYMBOLS_PARAM + ADDITION_MESSAGE_DELIMITER + PARAMETER_IDENTIFIER + DIGITS_PARAM);
        }
    }

    private static void setFileName(InputInfo info, String fileName) {
        if (info.getOutputFileName() == null) {
            info.setOutputFileName(fileName);
        } else {
            info.addInputFileName(fileName);
        }
    }

    public static InputInfo getInfo(String[] inputArgs) throws UndefinedParameter, MissingDataType, LackOfFiles {
        InputInfo info = new InputInfo();
        for(String arg : inputArgs) {
            if (arg.charAt(START_POS) == PARAMETER_IDENTIFIER) {
                handleParam(arg, info);
            } else {
                setFileName(info, arg);
            }
        }
        if (info.getDataType() == DataType.UNKNOWN) {
            throw new MissingDataType("Datatype parameter not set. Please write " + PARAMETER_IDENTIFIER
                    + SYMBOLS_PARAM + ADDITION_MESSAGE_DELIMITER + PARAMETER_IDENTIFIER + DIGITS_PARAM);
        }
        if (info.getFileNames().size() < MINIMUM_FILES) {
            throw new LackOfFiles("Passed less than minimum number of files - " + MINIMUM_FILES);
        }
        return info;
    }
}
