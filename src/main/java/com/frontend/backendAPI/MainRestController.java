package com.frontend.backendAPI;

import com.frontend.backendAPI.entity.SurveyResult;
import com.frontend.backendAPI.entity.TimedCreditsPojo;
import com.frontend.backendAPI.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/")
public class MainRestController
{
    @Autowired
    private TokenManager tk;

    @Autowired
    private ResultManager rm;

    @RequestMapping(value = "result", method = RequestMethod.POST)
    public void postResult(@RequestParam String token, @RequestBody SurveyResult result)
    {
        if (token != null && !token.isEmpty())
        {
            var tkOpt = tk.getToken(token);
            if (tkOpt.isPresent())
            {
                var t = tkOpt.get();
                result.setLabID(t.getLabID());
                if (rm.storeResult(result))
                {
                    tk.deleteToken(t);
                    return;
                }
            }
        }
        throw new UnknownTokenException();
    }

    @RequestMapping(value = "lab/{lab}/timecredit", method = RequestMethod.POST)
    public void postTimeCredit(@PathVariable int lab, @RequestBody TimedCreditsPojo p)
    {
        rm.storeCreditTimePair(p, lab);
    }

    @RequestMapping(value = "lab/{lab}/stats/timecredit", method = RequestMethod.GET)
    public TimedCreditsPojo getTimedCreditsStats(@PathVariable int lab)
    {
        return rm.getCreditTimeAverages(lab);
    }

    @RequestMapping(value = "lab/{lab}/tokens", method = RequestMethod.GET)
    public List<String> generateTokens(@PathVariable int lab, @RequestParam int n)
    {
        return tk.generateTokens(n, lab).stream().map(Token::getToken).collect(Collectors.toList());
    }

    @RequestMapping(value="lab/{labID}/stats/rating", method = RequestMethod.GET)
    public List<Integer> getRatingStats(@PathVariable int labID)
    {
        return rm.getRatingStats(labID);
    }

    @RequestMapping(value="lab/{labID}/stats/difficulty", method = RequestMethod.GET)
    public List<Integer> getDifficultyStats(@PathVariable int labID)
    {
        return rm.getDifficultyStats(labID);
    }

    @RequestMapping(value="lab/{labID}/stats/interesting", method = RequestMethod.GET)
    public List<Integer> getInterestingStats(@PathVariable int labID)
    {
        return rm.getInterestingStats(labID);
    }

    @RequestMapping(value="lab/{labID}/stats/prelabtime", method = RequestMethod.GET)
    public double getPreLabTimeAvg(@PathVariable int labID)
    {
        return rm.getAveragePreLabTime(labID);
    }

    @RequestMapping(value="lab/{labID}/stats/labtime", method = RequestMethod.GET)
    public double getLabTimeAvg(@PathVariable int labID)
    {
        return rm.getAverageLabTime(labID);
    }

    @RequestMapping(value="lab/{labID}/comments")
    public List<String> getAllComments(@PathVariable int labID)
    {
        return rm.getAllComments(labID);
    }

    @ExceptionHandler(UnknownTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unknownToken()
    {
        return "{\"error\":" + HttpStatus.UNAUTHORIZED.value() + ", \"reason\":\"Token does not exist\"}";
    }


}
