package com.ajic.fi.xls_ofx_converter.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Data;

@Data
@JsonTypeName("capitalOneSparkTransaction")
@XmlRootElement(name = "capitalOneSparkTransaction")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(getterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public class CapitalOneSparkTransaction {
  public static final String FID = "1001";
  public static final String ORG = "CapitalOne";
  public static final String ROUTING_NUMBER = "031176110";

  @JsonProperty("Account Number")
  private String accountNumber;
  @JsonProperty("Account Type")
  private String accountType;
  @JsonProperty("Start Date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
  private Date startDate;
  @JsonProperty("End Date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
  private Date endDate;
  @JsonProperty("Balance")
  private Double balance;
  @JsonProperty("Transaction Type")
  private TransactionType transactionType;
  @JsonProperty("Transaction Amount")
  private Double transactionAmount;
  @JsonProperty("Transaction Date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEE MMM dd HH:mm:ss z yyyy")
  private Date transactionDate;
  @JsonProperty("Transaction Id")
  private String transactionId;
  @JsonProperty("Transaction Description")
  private String transactionDescription;

  public CapitalOneSparkTransaction() {
  }

}
