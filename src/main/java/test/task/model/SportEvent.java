package test.task.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor
@Getter
public class SportEvent {
    private LocalTime startTime;
    private String firstTeam;
    private String secondTeam;
    private String tournament;
    private String sportType;
    private String link;
}
