package com.frontend.backendAPI;

import com.frontend.backendAPI.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TokenManager
{
    @Autowired
    private JdbcTemplate jdbc;

    @PostConstruct
    public void init()
    {
        jdbc.update("CREATE TABLE IF NOT EXISTS token (token VARCHAR(37) PRIMARY KEY, lab INT NOT NULL)");
    }

    public Optional<Token> getToken(String token)
    {
        if (token != null && !token.isEmpty())
            return jdbc.query("SELECT * FROM token t WHERE t.token = ?", (rs, i) ->
                    new Token(rs.getString(1), rs.getInt(2)), token).stream().findAny();
        return Optional.empty();
    }

    public boolean deleteToken(Token token)
    {
        return jdbc.update("DELETE FROM token WHERE token = ?", token.getToken()) > 0;
    }

    public List<Token> generateTokens(int number, int lab)
    {
        var list = new ArrayList<Token>();
        for (int i = 0; i < number; ++i)
            list.add(new Token(UUID.randomUUID().toString(), lab));
        list.forEach(t -> jdbc.update("INSERT INTO token VALUES (?, ?)", t.getToken(), lab));
        return list;
    }
}
