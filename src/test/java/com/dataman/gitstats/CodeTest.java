package com.dataman.gitstats;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CodeTest {

	public static void main(String[] args) {

		
		LocalDate today = LocalDate.now();
		System.out.println(today.format(DateTimeFormatter.ISO_LOCAL_DATE));
		System.out.println(today.toEpochDay());
		
		LocalDate begin= LocalDate.of(2017, 3, 4);
		System.out.println(begin.format(DateTimeFormatter.ISO_LOCAL_DATE));
		System.out.println(begin.toEpochDay());
		
		
		while (begin.toEpochDay()<=today.toEpochDay()) {
			System.out.println(begin.format(DateTimeFormatter.ISO_LOCAL_DATE));
			Long nextday=begin.toEpochDay()+1;
			begin=LocalDate.ofEpochDay(nextday);
		}
		
		String dateStr="2013-07-06";
		LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
		
		
		String str= "01";
		System.out.println(Integer.parseInt(str));
		
		
		List<String> list=new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add(1,"3");
		System.out.println(list.toString());
		
	}
}
