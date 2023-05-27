package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db
        List<Passenger> passengerList=new ArrayList<>();
        for(Integer x: bookTicketEntryDto.getPassengerIds()){
            if(!passengerRepository.findById(x).isPresent()){
                throw new Exception("Invalid passenger");
            }
            passengerList.add(passengerRepository.findById(x).get());
        }
        Optional<Train> op1=trainRepository.findById(bookTicketEntryDto.getTrainId());
        if(!op1.isPresent()){
            throw new Exception("Invalid train");
        }
        Train train=op1.get();
        String route= train.getRoute();
        String str[]=route.split(".");
        int startExist=route.indexOf(String.valueOf(bookTicketEntryDto.getFromStation()));
        int endExist=route.indexOf(String.valueOf(bookTicketEntryDto.getToStation()));
        if(startExist==-1 || endExist==-1){
            throw new Exception("Invalid stations");
        }
        boolean flag=false;
        int count=0;
        int startIndex=0;
        int endIndex=0;
        List<Ticket> ticketList=train.getBookedTickets();
        for(int i=0;i<str.length;i++){
            if(!flag && str[i].equals(String.valueOf(bookTicketEntryDto.getFromStation()))){flag=true;startIndex=i;}
            if(str[i].equals(String.valueOf(bookTicketEntryDto.getToStation()))){endIndex=i;break;}
            if(flag){
                for(Ticket x: ticketList){
                    if((String.valueOf(x.getFromStation())).equals(str[i])){
                        count+=x.getPassengersList().size();
                    }
                }
            }
        }
        int p=train.getNoOfSeats()-count;
        if(p<bookTicketEntryDto.getNoOfSeats()){
            throw new Exception("Less tickets are available");
        }
        Ticket ticket=new Ticket();
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTotalFare((300*(endIndex-startIndex)*bookTicketEntryDto.getNoOfSeats()));
        ticket.setTrain(train);
        for(Passenger x: passengerList){
            ticket.getPassengersList().add(x);
        }

        for(Passenger x: passengerList){
            x.getBookedTickets().add(ticket);
        }
        train.getBookedTickets().add(ticket);
        Train savedTrain= trainRepository.save(train);
       return ticket.getTicketId();
    }
}
