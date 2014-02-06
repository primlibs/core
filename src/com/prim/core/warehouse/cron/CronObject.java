/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * объект крона
 * @author Кот
 */
public class CronObject implements Serializable {

  private String serviceName;
  private Integer min;
  private Integer hour;
  private Integer day;
  private Integer months;
  private Integer weekday;
  static final long serialVersionUID = 12345L;

  /**
   * установить название сервиса
   * @param serviceName 
   */
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public void setMin(Integer min) {
    this.min = min;
  }

  public void setHour(Integer hour) {
    this.hour = hour;
  }

  public void setDay(Integer day) {
    this.day = day;
  }

  public void setMonths(Integer months) {
    this.months = months;
  }

  public void setWeekday(Integer weekday) {
    this.weekday = weekday;
  }

  public String getServiceName() {
    return serviceName;
  }

  public Boolean checkDate(Date date) {
    Boolean res = true;
    if (serviceName != null && !"".equals(serviceName) && date != null) {
      Calendar ca = Calendar.getInstance();
      ca.setTime(date);
      if (min == null || ca.get(Calendar.MINUTE) == min) {
        if (hour == null || ca.get(Calendar.HOUR) == hour) {
          if (day == null || ca.get(Calendar.DAY_OF_MONTH) == day) {
            if (months == null || ca.get(Calendar.MONTH) == months) {
              if (weekday == null || ca.get(Calendar.DAY_OF_WEEK) == weekday) {
                res = true;
              }
            }
          }
        }
      }
    }
    return res;
  }

  public Integer getMin() {
    return min;
  }

  public Integer getHour() {
    return hour;
  }

  public Integer getDay() {
    return day;
  }

  public Integer getMonths() {
    return months;
  }

  public Integer getWeekday() {
    return weekday;
  }
}
