package main.broker;

public class AverageMove {
    private double lineMinValue = 0;
    private double lineMaxValue = 0;
    private boolean crossOnUp = false;
    private boolean crossOnDown = false;
    private String direction = "";
    private int durationMove = 0;
    private double diffLineValue = 0;

    @Override
    public String toString() {
        return "AverageMove{" +
                "lineMinValue=" + lineMinValue +
                ", lineMaxValue=" + lineMaxValue +
                ", crossOnUp=" + crossOnUp +
                ", crossOnDown=" + crossOnDown +
                ", direction='" + direction + '\'' +
                ", durationMove=" + durationMove +
                '}';
    }

    public boolean isCrossOnUp() {
        return crossOnUp;
    }

    public void setCrossOnUp(boolean crossOnUp) {
        this.crossOnUp = crossOnUp;
    }

    public boolean isCrossOnDown() {
        return crossOnDown;
    }

    public void setCrossOnDown(boolean crossOnDown) {
        this.crossOnDown = crossOnDown;
    }

    public double getLineMinValue() {
        return lineMinValue;
    }

    public void setLineMinValue(double lineMinValue) {
        this.lineMinValue = lineMinValue;
    }

    public double getLineMaxValue() {
        return lineMaxValue;
    }

    public void setLineMaxValue(double lineMaxValue) {
        this.lineMaxValue = lineMaxValue;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getDurationMove() {
        return durationMove;
    }

    public void setDurationMove(int durationMove) {
        this.durationMove = durationMove;
    }

    public double getDiffLineValue() {
        if (lineMinValue > lineMaxValue)
            return lineMinValue - lineMaxValue;
        if (lineMaxValue > lineMinValue)
            return lineMaxValue - lineMinValue;

        return -1;
    }

    public void setDiffLineValue(double diffLineValue) {
        this.diffLineValue = diffLineValue;
    }
}
