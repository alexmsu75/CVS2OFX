package com.ajic.fi.xls_ofx_converter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.UrlResource;

import com.ajic.fi.xls_ofx_converter.model.CapitalOneSparkTransaction;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import net.sf.ofx4j.domain.data.ApplicationSecurity;
import net.sf.ofx4j.domain.data.ResponseEnvelope;
import net.sf.ofx4j.domain.data.ResponseMessageSet;
import net.sf.ofx4j.domain.data.banking.AccountType;
import net.sf.ofx4j.domain.data.banking.BankAccountDetails;
import net.sf.ofx4j.domain.data.banking.BankStatementResponse;
import net.sf.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import net.sf.ofx4j.domain.data.banking.BankingResponseMessageSet;
import net.sf.ofx4j.domain.data.common.BalanceInfo;
import net.sf.ofx4j.domain.data.common.Status;
import net.sf.ofx4j.domain.data.common.Status.KnownCode;
import net.sf.ofx4j.domain.data.common.Status.Severity;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.domain.data.common.TransactionList;
import net.sf.ofx4j.domain.data.common.TransactionType;
import net.sf.ofx4j.domain.data.signon.FinancialInstitution;
import net.sf.ofx4j.domain.data.signon.SignonResponse;
import net.sf.ofx4j.domain.data.signon.SignonResponseMessageSet;
import net.sf.ofx4j.io.AggregateMarshaller;
import net.sf.ofx4j.io.v1.OFXV1Writer;

@SpringBootApplication
public class Application implements CommandLineRunner {
  private final static Logger log = Logger.getLogger(Application.class.getName());

  @Value("${xlsFileLocation}")
  private String xlsFileLocation;

  @Override
  public void run(final String... args) {
    final String __functionName = "run";
    final String __className = this.getClass().getName();
    if (log.isLoggable(Level.FINER))
      log.entering(__className, __functionName);
    try {
      log.logp(Level.FINEST, __className, __functionName, "xlsFileLocation: {0}", xlsFileLocation);
      final List<CapitalOneSparkTransaction> capitalOneSparkTransactions = loadObjectList(CapitalOneSparkTransaction.class, xlsFileLocation.contains("://") ? xlsFileLocation : ("file://" + xlsFileLocation));
      log.logp(Level.FINEST, __className, __functionName, "capitalOneSparkTransactions: {0}", capitalOneSparkTransactions);
      if (capitalOneSparkTransactions.size() > 0) {
        final String ofxFileLocation = xlsFileLocation.replace(".csv", ".ofx");
        final File ofxFile = new File(ofxFileLocation);
        if (!ofxFile.exists() || ofxFile.canWrite()) {
          if (ofxFile.exists())
            ofxFile.delete();
          try (FileWriter ofxFileWriter = new FileWriter(ofxFile)) {
            OFXV1Writer ofxV1Writer = null;
            try {
              final AggregateMarshaller marshaller = new AggregateMarshaller();
              ofxV1Writer = new OFXV1Writer(ofxFileWriter);
              final ResponseEnvelope responseEnvelope = new ResponseEnvelope();
              responseEnvelope.setUID("NONE");
              responseEnvelope.setSecurity(ApplicationSecurity.NONE);
              final SortedSet<ResponseMessageSet> messageSets = new TreeSet<>();
              final SignonResponseMessageSet signonResponseMessageSet = new SignonResponseMessageSet();
              final SignonResponse signonResponse = new SignonResponse();
              final FinancialInstitution financialInstitution = new FinancialInstitution();
              financialInstitution.setOrganization(CapitalOneSparkTransaction.ORG);
              financialInstitution.setId(CapitalOneSparkTransaction.FID);
              signonResponse.setFinancialInstitution(financialInstitution);
              final Status signonResponseStatus = new Status();
              signonResponseStatus.setCode(KnownCode.SUCCESS);
              signonResponseStatus.setSeverity(Severity.INFO);
              signonResponse.setStatus(signonResponseStatus);
              signonResponse.setTimestamp(new Date());
              signonResponseMessageSet.setSignonResponse(signonResponse);
              //              signonResponseMessageSet.setVersion("1");
              messageSets.add(signonResponseMessageSet);
              final BankingResponseMessageSet bankingResponseMessageSet = new BankingResponseMessageSet();
              final BankStatementResponseTransaction statementResponse = new BankStatementResponseTransaction();
              statementResponse.setUID("0");
              final Status statementResponseStatus = new Status();
              statementResponseStatus.setCode(KnownCode.SUCCESS);
              statementResponseStatus.setSeverity(Severity.INFO);
              statementResponse.setStatus(statementResponseStatus);
              final BankStatementResponse bankStatementResponse = new BankStatementResponse();
              final BankAccountDetails bankAccountDetails = new BankAccountDetails();
              bankAccountDetails.setRoutingNumber(CapitalOneSparkTransaction.ROUTING_NUMBER);
              bankAccountDetails.setAccountNumber(capitalOneSparkTransactions.get(0).getAccountNumber());
              bankAccountDetails.setAccountType(AccountType.CHECKING);
              bankStatementResponse.setAccount(bankAccountDetails);
              final BalanceInfo ledgerBalance = new BalanceInfo();
              ledgerBalance.setAmount(capitalOneSparkTransactions.get(0).getBalance());
              ledgerBalance.setAsOfDate(capitalOneSparkTransactions.get(0).getTransactionDate());
              bankStatementResponse.setLedgerBalance(ledgerBalance);
              final TransactionList transactionList = new TransactionList();
              transactionList.setStart(capitalOneSparkTransactions.get(0).getStartDate());
              transactionList.setEnd(capitalOneSparkTransactions.get(0).getEndDate());
              final List<Transaction> transactions = new ArrayList<>();
              for (final CapitalOneSparkTransaction capitalOneSparkTransaction : capitalOneSparkTransactions) {
                final Transaction transaction = new Transaction();
                switch (capitalOneSparkTransaction.getTransactionType()) {
                case Credit:
                  transaction.setTransactionType(TransactionType.CREDIT);
                  break;
                case Debit:
                  transaction.setTransactionType(TransactionType.DEBIT);
                  break;
                default:
                  break;
                }
                transaction.setDatePosted(capitalOneSparkTransaction.getTransactionDate());
                final StringBuilder txId = new StringBuilder();
                try (Formatter formatter = new Formatter(txId, Locale.US)) {
                  formatter.format("%1$d%2$04d", capitalOneSparkTransaction.getTransactionDate().getTime() / 1000, transactions.size());
                  if (log.isLoggable(Level.FINEST))
                    log.logp(Level.FINEST, __className, __functionName, "txId: " + txId);
                  transaction.setId(txId.toString());
                }
                transaction.setAmount((capitalOneSparkTransaction.getTransactionType() == com.ajic.fi.xls_ofx_converter.model.TransactionType.Debit ? -1 : 1) * capitalOneSparkTransaction.getTransactionAmount());
                final String description = capitalOneSparkTransaction.getTransactionDescription();
                transaction.setName(description.replace("Debit Card Purchase - ", ""));
                transaction.setMemo(description);
                transactions.add(transaction);
              }
              transactionList.setTransactions(transactions);
              bankStatementResponse.setTransactionList(transactionList);
              statementResponse.setMessage(bankStatementResponse);
              bankingResponseMessageSet.setStatementResponse(statementResponse);
              messageSets.add(bankingResponseMessageSet);
              responseEnvelope.setMessageSets(messageSets);
              marshaller.marshal(responseEnvelope, ofxV1Writer);
            } finally {
              if (ofxV1Writer != null)
                ofxV1Writer.close();
            }
          }
        }
      }
    } catch (final Throwable e) {
      log.logp(Level.SEVERE, __className, __functionName, "ERROR: file " + xlsFileLocation, e);
    } finally {
      if (log.isLoggable(Level.FINER))
        log.exiting(__className, __functionName);
    }
  }

  public <T> List<T> loadObjectList(final Class<T> type, final String fileName) {
    List<T> retValue = null;
    final String __functionName = "loadObjectList";
    final String __className = this.getClass().getName();
    if (log.isLoggable(Level.FINER))
      log.entering(__className, __functionName, new Object[] { "type, fileName", type, fileName });
    try {
      final CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
      //bootstrapSchema = CsvSchema.builder().addColumn("firstName").addColumn("lastName").addColumn("age", CsvSchema.ColumnType.NUMBER).build();
      final CsvMapper mapper = new CsvMapper();
      final File file = new UrlResource(fileName).getFile();
      final MappingIterator<T> readValues = mapper.readerFor(type).with(bootstrapSchema).readValues(file);
      retValue = readValues.readAll();
    } catch (final Throwable e) {
      log.logp(Level.SEVERE, __className, __functionName, "ERROR: Error occurred while loading object list from file " + fileName, e);
      retValue = Collections.emptyList();
    } finally {
      if (log.isLoggable(Level.FINER))
        log.exiting(__className, __functionName, "[].size=" + retValue.size());
    }
    return retValue;
  }

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
    //    final ApplicationContext ctx = SpringApplication.run(Application.class, args);
    //
    //    System.out.println("Let's inspect the beans provided by Spring Boot:");
    //
    //    final String[] beanNames = ctx.getBeanDefinitionNames();
    //    Arrays.sort(beanNames);
    //    for (final String beanName : beanNames) {
    //      System.out.println(beanName);
    //    }
  }

}
