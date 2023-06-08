package test.task.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class SportEvent implements Comparable<SportEvent>{
    private LocalTime startTime;
    private String firstTeam;
    private String secondTeam;
    private String tournament;
    private String sportType;
    private String link;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SportEvent that = (SportEvent) o;
        return Objects.equals(startTime, that.startTime)
                && Objects.equals(firstTeam, that.firstTeam)
                && Objects.equals(secondTeam, that.secondTeam)
                && Objects.equals(tournament, that.tournament)
                && Objects.equals(sportType, that.sportType)
                && Objects.equals(link, that.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, firstTeam, secondTeam, tournament, sportType, link);
    }

    @Override
    public int compareTo(SportEvent o) {
        return startTime.compareTo(o.startTime);
    }
}
