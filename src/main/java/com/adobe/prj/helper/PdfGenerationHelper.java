package com.adobe.prj.helper;

import com.adobe.prj.entity.Expense;
import com.adobe.prj.entity.ExpenseSheet;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Date;

@Component
public class PdfGenerationHelper {

    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public byte[] generatePdf(ExpenseSheet expenseSheet) {
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);

        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();
        Document document = new Document(pdfDoc);

        //Page heading
        Paragraph heading = new Paragraph("Expense Sheet Report").setTextAlignment(TextAlignment.CENTER).setFontSize(28).setBold();
        document.add(heading);

        document.add(new Paragraph("\n\n"));

        //Basic Info
        Table basicInfo = new Table(new float[]{120F, 120F, 120F, 120F, 120F}).setFontSize(16);

        basicInfo.startNewRow();
        basicInfo.addCell(new Cell().add("Filed By").setBold().setFontSize(20));
        basicInfo.addCell(new Cell().add("Approver").setBold().setFontSize(20));
        basicInfo.addCell(new Cell().add("Project Name").setBold().setFontSize(20));
        basicInfo.addCell(new Cell().add("Description").setBold().setFontSize(20));
        basicInfo.addCell(new Cell().add("Status").setBold().setFontSize(20));
        basicInfo.startNewRow();
        basicInfo.addCell(new Cell().add(expenseSheet.getFiledBy().getName()));
        basicInfo.addCell(new Cell().add(expenseSheet.getApprover().getName()));
        basicInfo.addCell(new Cell().add(expenseSheet.getProject().getProjectName()));
        basicInfo.addCell(new Cell().add(expenseSheet.getDescription() != null ? expenseSheet.getDescription() : "Description Not Provided"));
        basicInfo.addCell(new Cell().add(expenseSheet.getStatus().getStatus()));

        document.add(basicInfo);

        document.add(new Paragraph("\n\n"));

        //Amounts related info
        Table amountInfo = new Table(new float[]{200F, 200F, 200F}).setFontSize(16);

        amountInfo.startNewRow();
        amountInfo.addCell(new Cell().add("Total Amount").setBold().setFontSize(20));
        amountInfo.addCell(new Cell().add("Reimbursable Amount").setBold().setFontSize(20));
        amountInfo.addCell(new Cell().add("Billable Amount").setBold().setFontSize(20));
        amountInfo.startNewRow();
        amountInfo.addCell(new Cell().add(expenseSheet.getTotalAmount().toString()));
        amountInfo.addCell(new Cell().add(expenseSheet.getReimbursementAmount().toString()));
        amountInfo.addCell(new Cell().add(expenseSheet.getBillableAmount().toString()));

        document.add(amountInfo);

        document.add(new Paragraph("\n\n"));

        //Table of all expenses
        Table expenseTable = new Table(new float[]{120F, 120F, 120F, 120F, 120F}).setFontSize(16);

        expenseTable.startNewRow();
        expenseTable.addCell(new Cell().add("Expense Id").setBold().setFontSize(20));
        expenseTable.addCell(new Cell().add("Expense Name").setBold().setFontSize(20));
        expenseTable.addCell(new Cell().add("Date").setBold().setFontSize(20));
        expenseTable.addCell(new Cell().add("Amount").setBold().setFontSize(20));
        expenseTable.addCell(new Cell().add("Description").setBold().setFontSize(20));

        for (Expense expense : expenseSheet.getExpenseList()) {
            expenseTable.startNewRow();
            expenseTable.addCell(new Cell().add(expense.getExpenseId().toString()));
            expenseTable.addCell(new Cell().add(expense.getExpenseName()));
            expenseTable.addCell(new Cell().add((new Date(expense.getExpenseEntryDate()*1000)).toString()));
            expenseTable.addCell(new Cell().add(expense.getAmount().toString()));
            expenseTable.addCell(new Cell().add(expense.getDescription() != null ? expense.getDescription() : "Description Not Provided"));
        }
        document.add(expenseTable);

        document.close();

        return byteArrayOutputStream.toByteArray();
    }
}
