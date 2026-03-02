package Vector;

import java.io.Serializable;
import java.util.Arrays;

public class Vector implements Serializable {
    private static final long serialVersionUID = 1L;

    private double[] coordinates;
    private Double length;
    private boolean stopSignal;

    public Vector(double[] coordinates) {
        this.coordinates = coordinates;
        this.length = null;
        this.stopSignal = false;
    }

    public Vector(boolean stopSignal) {
        this.stopSignal = stopSignal;
    }



    public double[] getCoordinates() {
        return coordinates;
    }
    public double getLength(){
        return length;
    }
    public void setLength(double length){
        this.length = length;
    }
    public boolean isStopSignal(){
        return stopSignal;
    }

    @Override
    public String toString() {
        if (stopSignal) {
            return "VectorData{STOP_SIGNAL}";
        }
        return "VectorData{" +
                "coordinates=" + Arrays.toString(coordinates) +
                ", length=" + length +
                '}';
    }
}
