package com.example.projectschedulehaircutserver.service.time;

import com.example.projectschedulehaircutserver.dto.TimeDTO;
import com.example.projectschedulehaircutserver.repository.TimeRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TimeServiceImpl implements TimeService{
    private final TimeRepo timeRepo;

    @Override
    public Set<TimeDTO> findTimeByEmployeeIdAndOrderDate(Integer employeeId, LocalDate orderDate) {
        Set<Object[]> results = timeRepo.findTimeByEmployeeIdAndOrderDate(employeeId, orderDate);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return results.stream()
                .map(obj -> {
                    Integer id = (Integer) obj[0];
                    String timeName = (String) obj[1];
                    Integer isBusy = ((Long) obj[2]).intValue();

                    // Nếu là hôm nay và khung giờ đã qua thì đánh dấu isBusy = 1
                    if (orderDate.equals(today)) {
                        LocalTime timeSlot = LocalTime.parse(timeName); // timeName là dạng "HH:mm"
                        if (timeSlot.isBefore(now)) {
                            isBusy = 1;
                        }
                    }

                    return new TimeDTO(id, timeName, isBusy);
                })
                .sorted(Comparator.comparingInt(TimeDTO::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}

