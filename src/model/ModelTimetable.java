
package model;


public class ModelTimetable {
    String departureStation;
    String arrivalTime;
    String departureTime;
    String departureDate;

    public ModelTimetable(String departureStation, String arrivalTime, String departureTime, String departureDate){
        this.departureStation = departureStation;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.departureDate = departureDate;
    }
    public ModelTimetable(String departureStation, String arrivalTime, String departureTime){
        this.departureStation = departureStation;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public void setDepartureStation(String departureStation){
        this.departureStation = departureStation;
    }
    public String getDepartureStation(){
        return departureStation;
    }

    public void setArrivalTime(String arrivalTime){
        this.arrivalTime = arrivalTime;
    }
    public String getArrivalTime(){
        return arrivalTime;
    }

    public void setDepartureTime(String departureTime){
        this.departureTime = departureTime;
    }
    public String getDepartureTime(){
        return departureTime;
    }

    public void setDepartureDate(String departureDate){
        this.departureDate = departureDate;
    }
    public String getDepartureDate(){
        return departureDate;
    }
}
