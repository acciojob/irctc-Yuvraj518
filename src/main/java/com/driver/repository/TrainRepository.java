package com.driver.repository;

import com.driver.model.Station;
import com.driver.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;


@Repository
public interface TrainRepository extends JpaRepository<Train,Integer> {
    @Query(value = "select * from trains as s where s.station=:station and s.startTime>:startTime and s.endTime<:endTime",nativeQuery = true)

    List<Train> getListOfTrainBeteenTime(Station station, LocalTime startTime, LocalTime endTime);
}
