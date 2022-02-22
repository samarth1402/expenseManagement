package com.adobe.prj.service;

import com.adobe.prj.dao.ExpenseDao;
import com.adobe.prj.dao.FileDao;
import com.adobe.prj.dto.ExpenseDTO;
import com.adobe.prj.entity.Expense;
import com.adobe.prj.entity.ExpenseSheet;
import com.adobe.prj.entity.File;
import com.adobe.prj.entity.Project;
import com.adobe.prj.enums.ExpenseStatus;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.helper.ExpenseSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;


import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ExpenseService {

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private FileDao fileDao;

    @Autowired
    private ExpenseSpecification expenseSpecification;

    @Autowired
    private ExpenseSheetService expenseSheetService;

    @Transactional
    public ExpenseSheet addExpense(int expenseSheetId, ExpenseDTO expenseDTO) throws EntityNotFoundException {

        ExpenseSheet expenseSheet = expenseSheetService.getExpenseSheet(expenseSheetId);
        Expense exp = expenseDtoToExpense(expenseDTO);
        exp.setFiledBy(expenseSheet.getFiledBy());
        exp.setStatus(expenseSheet.getStatus());
        exp.setExpenseSheet(expenseSheet);
        expenseDao.save(exp);


        expenseSheet.getExpenseList().add(exp);
        expenseSheet.setTotalAmount(expenseSheet.getTotalAmount() + exp.getAmount());
        if(exp.getBillable())
            expenseSheet.setBillableAmount(expenseSheet.getBillableAmount() + exp.getAmount());
        if(exp.getReimburse())
            expenseSheet.setReimbursementAmount(expenseSheet.getReimbursementAmount() + exp.getAmount());

        Project prj = expenseSheet.getProject();
        prj.setTotalExpense(prj.getTotalExpense() + exp.getAmount());
        if(exp.getBillable())
            prj.setBillableAmount(prj.getBillableAmount() + exp.getAmount());
        return expenseSheet;
    }

    @Transactional
    public ExpenseSheet updateExpense(int expenseSheetId, int expenseId, ExpenseDTO expenseDTO) throws EntityNotFoundException {

        ExpenseSheet expenseSheet = expenseSheetService.getExpenseSheet(expenseSheetId);
        Expense expense = getExpenseByExpenseId(expenseId);
        Project project = expenseSheet.getProject();

        project.setTotalExpense(project.getTotalExpense() - expenseSheet.getTotalAmount());
        project.setBillableAmount(project.getBillableAmount() - expenseSheet.getBillableAmount());

        expenseSheet.setTotalAmount(expenseSheet.getTotalAmount() - expense.getAmount());
        if (expense.getReimburse())     expenseSheet.setReimbursementAmount(expenseSheet.getReimbursementAmount() - expense.getAmount());
        if (expense.getBillable())      expenseSheet.setBillableAmount(expenseSheet.getBillableAmount() - expense.getAmount());

        expense.setExpenseName(expenseDTO.getExpenseName());
        expense.setPaymentMethod(expenseDTO.getPaymentMethod());
        expense.setCurrency(expenseDTO.getCurrency());
        expense.setExpenseEntryDate(expenseDTO.getExpenseEntryDate());
        expense.setBillable(expenseDTO.isBillable());
        expense.setReimburse(expenseDTO.isReimburse());
        expense.setNetAmount(expenseDTO.getNetAmount());
        expense.setTaxZone(expenseDTO.getTaxZone());
        expense.setTax(expenseDTO.getTax());
        expense.setAmount(expenseDTO.getAmount());
        expense.setDescription(expenseDTO.getDescription());
        expenseDao.save(expense);

        expenseSheet.setTotalAmount(expenseSheet.getTotalAmount() + expense.getAmount());
        if (expense.getReimburse())     expenseSheet.setReimbursementAmount(expenseSheet.getReimbursementAmount() + expense.getAmount());
        if (expense.getBillable())      expenseSheet.setBillableAmount(expenseSheet.getBillableAmount() + expense.getAmount());

        project.setTotalExpense(project.getTotalExpense() + expenseSheet.getTotalAmount());
        project.setBillableAmount(project.getBillableAmount() + expenseSheet.getBillableAmount());

        return expenseSheet;
    }

    public Expense getExpenseByExpenseId(int expenseId) throws EntityNotFoundException {
        Optional<Expense> exp = expenseDao.findById(expenseId);
        if(exp.isPresent()){
            return exp.get();
        } else {
            throw new EntityNotFoundException("No expense found with given expenseId");
        }
    }

    private Expense expenseDtoToExpense(ExpenseDTO expense) {

        return Expense.builder().expenseName(expense.getExpenseName())
                .paymentMethod(expense.getPaymentMethod())
                .currency(expense.getCurrency())
                .expenseEntryDate(expense.getExpenseEntryDate())
                .billable(expense.isBillable())
                .reimburse(expense.isReimburse())
                .netAmount(expense.getNetAmount())
                .taxZone(expense.getTaxZone())
                .tax(expense.getTax())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .build();
    }

    @Transactional
    public ExpenseSheet deleteExpense(int expenseSheetId, int expenseId) throws EntityNotFoundException {
        ExpenseSheet expenseSheet = expenseSheetService.getExpenseSheet(expenseSheetId);
        Expense expense = getExpenseByExpenseId(expenseId);
        Project project = expenseSheet.getProject();

        project.setTotalExpense(project.getTotalExpense() - expense.getAmount());
        expenseSheet.setTotalAmount(expenseSheet.getTotalAmount() - expense.getAmount());

        if (expense.getReimburse()) {
            expenseSheet.setReimbursementAmount(expenseSheet.getReimbursementAmount() - expense.getAmount());
        }
        if (expense.getBillable()) {
            expenseSheet.setBillableAmount(expenseSheet.getBillableAmount() - expense.getAmount());
            project.setBillableAmount(project.getBillableAmount() - expenseSheet.getBillableAmount());
        }

        expenseSheet.setExpenseList(expenseSheet.getExpenseList().stream()
                                                                    .filter(exp -> exp.getExpenseId() != expenseId)
                                                                    .collect(Collectors.toList())
        );

        expenseDao.delete(expense);
        return expenseSheet;
    }

    public List<Expense> getExpenseList(Integer expenseSheetId, String filedBy, Long startDate, Long endDate, ExpenseStatus status, Boolean reimburse) {
        List<Specification<Expense>> specificationList = new ArrayList<>();
        if (!Objects.isNull(expenseSheetId))    specificationList.add(expenseSpecification.expenseSheetIdQuery(expenseSheetId));
        if (!Objects.isNull(filedBy))           specificationList.add(expenseSpecification.filedByQuery(filedBy));
        if (!Objects.isNull(startDate))         specificationList.add(expenseSpecification.startDateQuery(startDate));
        if (!Objects.isNull(endDate))           specificationList.add(expenseSpecification.endDateQuery(endDate));
        if (!Objects.isNull(status))            specificationList.add(expenseSpecification.statusQuery(status));
        if (!Objects.isNull(reimburse))         specificationList.add(expenseSpecification.reimburseQuery(reimburse));

        if (specificationList.size() == 0) {
            return expenseDao.findAll();
        } else {
            Specification<Expense> finalSpecification = where(specificationList.remove(0));
            for (Specification<Expense> specification : specificationList) {
                finalSpecification = finalSpecification.and(specification);
            }
            return expenseDao.findAll(finalSpecification);
        }
    }

    public List<Double> getExpenseSummary(String filedBy, int year) throws ParseException {

        List<Double> summary = new ArrayList<>();
        List<pair> months = func(year);

        for(int i=0; i<12; i++) {

            List<Specification<Expense>> specificationList = new ArrayList<>();
            specificationList.add(expenseSpecification.filedByQuery(filedBy));
            specificationList.add(expenseSpecification.startDateQuery(months.get(i).start_date));
            specificationList.add(expenseSpecification.endDateQuery(months.get(i).end_date));

            Specification<Expense> finalSpecification = where(specificationList.remove(0));
            for (Specification<Expense> specification : specificationList) {
                finalSpecification = finalSpecification.and(specification);
            }
            List<Expense> expenseList = expenseDao.findAll(finalSpecification);

            double amount = expenseList.stream().mapToDouble(x -> x.getAmount()).sum();

            summary.add(amount);
        }
        return summary;
    }

    private List<pair> func(int year) throws ParseException {
        List<pair> eachMonthEpoch = new ArrayList<>();

        String months[] ={"Jan ", "Feb ","Mar ", "Apr ", "May ", "Jun ", "Jul ", "Aug ", "Sep ", "Oct ", "Nov ", "Dec "};
        String end_dates[]={"31 ", "28 ", "31 ", "30 ", "31 ", "30 ", "31 ", "31 ", "30 ", "31 ", "30 ", "31 "};
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
        Date date;
        long start_epoch, end_epoch;
        String start_Date, end_Date;

        for(int i=0; i < 12; i++){
            start_Date = months[i]+"01 "+year+" 00:00:00.000 IST";
            date = df.parse(start_Date);
            start_epoch = date.getTime();

            end_Date = months[i]+end_dates[i]+year+" 23:59:59.999 IST";
            date = df.parse(end_Date);
            end_epoch = date.getTime();

            eachMonthEpoch.add(new pair(start_epoch, end_epoch));
        }

        return eachMonthEpoch;
    }

    class pair{
        long start_date;
        long end_date;
        pair(long start_date,long end_date){
            this.start_date = start_date;
            this.end_date = end_date;
        }
    }

    //FILES

    public List<File> getFiles(int expenseId) throws EntityNotFoundException {
        Expense exp = getExpenseByExpenseId(expenseId);
        return exp.getFiles();
    }

    @Transactional
    public File storeFile(int expenseId, MultipartFile file) throws IOException, EntityNotFoundException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Expense exp = getExpenseByExpenseId(expenseId);
        File newFile = File.builder().fileName(fileName)
                .fileType(file.getContentType())
                .data(file.getBytes())
                .build();
        fileDao.save(newFile);
        exp.getFiles().add(newFile);
        return newFile;
    }

    public File getFile(int expenseId, int fileId) throws EntityNotFoundException {
        Expense exp = getExpenseByExpenseId(expenseId);
        List<File> files = exp.getFiles();
        for(File f: files){
            if(f.getFileId() == fileId)
                return f;
        }
        throw new EntityNotFoundException("File with given fileId not found");
    }

    public void deleteFile(int expenseId, int fileId) throws EntityNotFoundException {
        Expense exp = getExpenseByExpenseId(expenseId);
        List<File> files = exp.getFiles();
        for(File f: files){
            if(f.getFileId() == fileId) {
                files.remove(f);
                exp.setFiles(files);
                fileDao.delete(f);
                break;
            }
        }
    }

}
