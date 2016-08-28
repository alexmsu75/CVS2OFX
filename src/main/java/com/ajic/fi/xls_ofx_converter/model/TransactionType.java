package com.ajic.fi.xls_ofx_converter.model;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum TransactionType {
  Debit, Credit;
}