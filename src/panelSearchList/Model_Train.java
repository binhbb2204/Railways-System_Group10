
package panelSearchList;

public class Model_Train {
    String trainName;
    String departureDate;
    String arrivalDate;
    int available;
    

    public Model_Train(String trainName, String departueDate, String arrivalDate, int available){
        this.trainName = trainName;
        this.departureDate = departueDate;
        this.arrivalDate = arrivalDate;
        this.available  = available;
    }
    public String getTrainName(){
        return trainName;
    }
    public void setTrainName(String trainName){
        this.trainName = trainName;
    }
    public String getDepartureDate(){
        return departureDate;
    }
    public void setDepartureDate(String departureDate){
        this.departureDate = departureDate;
    }

    public String getArrivalDate(){
        return arrivalDate;
    }
    public void setArrivalDate(String arrivalDate){
        this.arrivalDate = arrivalDate;
    }

    public int getAvailable(){
        return available;
    }
    public void setAvailable(int available){
        this.available = available;
    }
}
