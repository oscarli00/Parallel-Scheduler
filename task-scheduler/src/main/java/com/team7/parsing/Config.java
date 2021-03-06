package com.team7.parsing;

import java.util.Objects;

public class Config {
    /*
        This is the number of processors on which the scheduling is done on.
     */
    private int numOfProcessors;

    /*
        This is the number of thread which are used for running the parallel scheduling algorithm.
     */
    private int numOfThreads;

    private boolean isVisualised;
    private String outputName;
    private String inputName;

    /**
     * initialise with default values, as specified in the requirement
     */
    public Config(){
        this(1,1, false,"","");
    }

    public Config(int numOfProcessors, int numOfThreads, boolean isVisualised, String outputName, String inputName) {
        this.numOfProcessors = numOfProcessors;
        this.numOfThreads = numOfThreads;
        this.isVisualised = isVisualised;
        this.outputName = outputName;
        this.inputName = inputName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return numOfProcessors == config.numOfProcessors &&
                numOfThreads == config.numOfThreads &&
                isVisualised == config.isVisualised &&
                Objects.equals(outputName, config.outputName) &&
                Objects.equals(inputName, config.inputName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numOfProcessors, numOfThreads, isVisualised, outputName, inputName);
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }

    public void setNumOfThreads(int numOfThreads) {
        this.numOfThreads = numOfThreads;
    }

    public int getNumOfProcessors() {
        return numOfProcessors;
    }

    public void setNumOfProcessors(int numOfProcessors) {
        this.numOfProcessors = numOfProcessors;
    }

    public boolean isVisualised() {
        return isVisualised;
    }

    public void setVisualised(boolean visualised) {
        isVisualised = visualised;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public String toString() {
        return "Config {" +
                "numOfProcessors=" + numOfProcessors +
                ", numOfCores=" + numOfThreads +
                ", isVisualized=" + isVisualised +
                ", outputName=" + outputName +
                ", inputName=" + inputName;
    }
}
