package com.ubi.masterservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentServiceImplTest {

    @Test
    public void addStudent() throws ParseException, JsonProcessingException {

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        String toParse = "20-12-2014 02:30:00";
        LocalDate date = LocalDate.now();
        Dto event = new Dto("party", date);

        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(event);
        //assertThat(result, containsString(toParse));
        System.out.println(result);
    }


}
class Dto
{
    String test="";
    LocalDate date;

    public String getTest() {
        return test;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Dto(String test, LocalDate date) {
        this.test = test;
        this.date = date;
    }


}
