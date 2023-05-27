package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto){

        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library
        String route="";
        for(Station x: trainEntryDto.getStationRoute()){
            route+=(x+".");
        }
        route=route.substring(0,route.length()-1);
        Train train=new Train();
        train.setRoute(route);
        train.setDepartureTime(trainEntryDto.getDepartureTime());
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());
        Train savedTrain=trainRepository.save(train);
        return savedTrain.getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto){

        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats avaialble in total in the train
        //and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to C and A to B. If a seat is empty between 2 station it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //Inshort : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.
        Train train=trainRepository.findById(seatAvailabilityEntryDto.getTrainId()).get();
        String route= train.getRoute();
        String str[]=route.split(".");
        boolean flag=false;
        int count=0;
        List<Ticket> ticketList=train.getBookedTickets();
        for(int i=0;i<str.length;i++){
            if(!flag && str[i].equals(String.valueOf(seatAvailabilityEntryDto.getFromStation()))){flag=true;}
            if(str[i].equals(String.valueOf(seatAvailabilityEntryDto.getToStation()))){break;}
            if(flag){
                for(Ticket x: ticketList){
                    if((String.valueOf(x.getFromStation())).equals(str[i])){
                        count+=x.getPassengersList().size();
                    }
                }
            }
        }
       return train.getNoOfSeats()-count;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId,Station station) throws Exception{

        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //  in a happy case we need to find out the number of such people.
        Train train=trainRepository.findById(trainId).get();
        List<Ticket> tickets=train.getBookedTickets();
        int getTotal=0;
        for(Ticket x: tickets){
            if(x.getFromStation().equals(station)){
                getTotal+=x.getPassengersList().size();
            }
        }
        return getTotal;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId){

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0
        int maxAge=0;
        Train train=trainRepository.findById(trainId).get();
        List<Ticket> ticketsList=train.getBookedTickets();
        if(ticketsList.size()==0){return 0;}
        for(Ticket x: ticketsList){
            List<Passenger> passengerList=x.getPassengersList();
            for(Passenger xx: passengerList){
                if(xx.getAge()>maxAge){
                    maxAge=xx.getAge();
                }
            }
        }
        return maxAge;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){

        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.
        List<Train> getList=trainRepository.getListOfTrainBeteenTime(station,startTime,endTime);
        List<Integer> list=new ArrayList<>();
        for(Train x: getList){ list.add(x.getTrainId());}
        return list;
    }

}
