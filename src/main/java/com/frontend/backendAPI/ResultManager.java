package com.frontend.backendAPI;

import com.frontend.backendAPI.entity.SurveyResult;
import com.frontend.backendAPI.entity.TimedCreditsPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class ResultManager
{
    @Autowired
    private JdbcTemplate jdbc;

    @PostConstruct
    public void init()
    {
        jdbc.update("CREATE TABLE IF NOT EXISTS surveyResults (ID INT PRIMARY KEY AUTO_INCREMENT," +
                "labID INT NOT NULL," +
                "difficult INT NOT NULL," +
                "interesting INT NOT NULL," +
                "prelabtime INT NOT NULL," +
                "labtime INT NOT NULL," +
                "rate INT NOT NULL," +
                "comment VARCHAR(10000) NOT NULL)");

        jdbc.update("CREATE TABLE IF NOT EXISTS timecredits (labid INT NOT NULL," +
                "time DOUBLE NOT NULL, credits DOUBLE NOT NULL)");
    }

    public boolean storeResult(SurveyResult r)
    {
        return jdbc.update("INSERT INTO surveyResults VALUES (0," +
                "?, ?, ?, ?, ?, ?, ?)", r.getLabID(), r.getDifficult(), r.getInteresting(), r.getPrelabtime(),
                r.getLabtime(), r.getRate(), r.getComment()) > 0;
    }

    public boolean storeCreditTimePair(TimedCreditsPojo p, int lab)
    {
        return jdbc.update("INSERT INTO timecredits VALUES (?, ?, ?)",lab, p.getCredits(), p.getTime()) > 0;
    }

    public TimedCreditsPojo getCreditTimeAverages(int lab)
    {
        return jdbc.query("SELECT labid, AVG(time) AS timeavg, AVG(credits) AS creditsavg FROM timecredits WHERE labid = ? GROUP BY labid",
                (rs, i) -> new TimedCreditsPojo(rs.getDouble("timeavg"), rs.getDouble("creditsavg")), lab).stream().findAny().orElse(new TimedCreditsPojo(0,0));
    }

    public List<Integer> getRatingStats(int lab)
    {
        return jdbc.query("SELECT t.x as rate, IFNULL(a.count, 0) as count FROM (SELECT 5 AS x UNION ALL SELECT 4 " +
                        "UNION ALL SELECT 3 UNION ALL SELECT 2 UNION ALL SELECT 1) AS t LEFT JOIN ( SELECT rate, COUNT(*) " +
                        "AS count FROM surveyResults WHERE labID = ? GROUP BY rate) AS a ON a.rate = t.x",
                (rs, i) -> rs.getInt("count")
                ,lab);
    }

    public List<Integer> getDifficultyStats(int lab)
    {
        return jdbc.query("SELECT t.x as difficult, IFNULL(a.count, 0) as count FROM (SELECT 5 AS x UNION ALL SELECT 4 " +
                        "UNION ALL SELECT 3 UNION ALL SELECT 2 UNION ALL SELECT 1) AS t LEFT JOIN ( SELECT difficult, COUNT(*) " +
                        "AS count FROM surveyResults WHERE labID = ? GROUP BY difficult) AS a ON a.difficult = t.x",
                (rs, i) -> rs.getInt("count")
                ,lab);
    }

    public List<Integer> getInterestingStats(int lab)
    {
        return jdbc.query("SELECT t.x as interesting, IFNULL(a.count, 0) as count FROM (SELECT 5 AS x UNION ALL SELECT 4 " +
                        "UNION ALL SELECT 3 UNION ALL SELECT 2 UNION ALL SELECT 1) AS t LEFT JOIN ( SELECT interesting, COUNT(*) " +
                        "AS count FROM surveyResults WHERE labID = ? GROUP BY interesting) AS a ON a.interesting = t.x",
                (rs, i) -> rs.getInt("count")
                ,lab);
    }

    public double getAveragePreLabTime(int lab)
    {
        return jdbc.query("SELECT AVG(prelabtime) AS avg FROM surveyResults WHERE labID = ?",
                (rs, i) -> rs.getDouble("avg"), lab).stream().findAny().orElse(0D);
    }

    public double getAverageLabTime(int lab)
    {
        return jdbc.query("SELECT AVG(labtime) AS avg FROM surveyResults WHERE labID = ?",
                (rs, i) -> rs.getDouble("avg"), lab).stream().findAny().orElse(0D);
    }

    public List<String> getAllComments(int lab)
    {
        return jdbc.query("SELECT comment FROM surveyResults WHERE labID = ?", (rs,i) -> rs.getString("comment") ,lab);
    }


}
