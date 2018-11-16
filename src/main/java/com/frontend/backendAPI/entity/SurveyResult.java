package com.frontend.backendAPI.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class SurveyResult
{
    private int labID;
    private int difficult;
    private int interesting;
    private int prelabtime;
    private int labtime;
    private int rate;
    private String comment;
}
