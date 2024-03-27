
package main;

import javax.swing.*;
import java.awt.*;
public class ModelSchedule {


    public String getTrain() {
        return train;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDayOperation() {
        return dayOperation;
    }

    public String getStatus() {
        return status;
    }



    public void setTrain(String train) {
        this.train = train;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setDayOperation(String dayOperation) {
        this.dayOperation = dayOperation;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ModelSchedule(String train, String origin, String destination, String departureTime, String arrivalTime, String dayOperation, String status) {
        this.train = train;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.dayOperation = dayOperation;
        this.status = status;
    }
    public ModelSchedule(){

    }

    
    String train;
    String origin;
    String destination;
    String departureTime;
    String arrivalTime;
    String dayOperation;
    String status;

    public Object[] toDataTable(){
        return new Object[]{train, origin, destination, departureTime, arrivalTime, dayOperation, status};
    }
}
