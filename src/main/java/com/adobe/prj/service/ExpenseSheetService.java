package com.adobe.prj.service;

import com.adobe.prj.dao.ExpenseSheetDao;
import com.adobe.prj.dto.ExpenseSheetDTO;
import com.adobe.prj.entity.ExpenseSheet;
import com.adobe.prj.entity.File;
import com.adobe.prj.entity.Project;
import com.adobe.prj.entity.User;
import com.adobe.prj.enums.ExpenseStatus;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.exception.UnauthorizedAccessException;
import com.adobe.prj.helper.ExpenseSheetSpecification;
import com.adobe.prj.helper.PdfGenerationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class ExpenseSheetService {

    @Autowired
    private ExpenseSheetDao expenseSheetDao;

    @Autowired
    private ExpenseSheetSpecification expenseSheetSpecification;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PdfGenerationHelper pdfGenerationHelper;

    public List<ExpenseSheet> getExpenseSheetList(String filedBy, Long startDate, Long endDate, String approver, ExpenseStatus status) {
        List<Specification<ExpenseSheet>> specificationList = new ArrayList<>();
        if (!Objects.isNull(filedBy))       specificationList.add(expenseSheetSpecification.filedByQuery(filedBy));
        if (!Objects.isNull(startDate))     specificationList.add(expenseSheetSpecification.startDateQuery(startDate));
        if (!Objects.isNull(endDate))       specificationList.add(expenseSheetSpecification.endDateQuery(endDate));
        if(!Objects.isNull(approver))         specificationList.add(expenseSheetSpecification.approverQuery(approver));
        if (!Objects.isNull(status))        specificationList.add(expenseSheetSpecification.statusQuery(status));

        if (specificationList.size() == 0) {
            return expenseSheetDao.findAll();
        } else {
            Specification<ExpenseSheet> finalSpecification = where(specificationList.remove(0));
            for (Specification<ExpenseSheet> specification : specificationList) {
                finalSpecification = finalSpecification.and(specification);
            }
            return expenseSheetDao.findAll(finalSpecification);
        }
    }

    public ExpenseSheet addExpenseSheet(ExpenseSheetDTO expenseSheetDTO) throws EntityNotFoundException {
        ExpenseSheet expenseSheet = expenseSheetDtoToExpenseSheet(expenseSheetDTO);
        return expenseSheetDao.save(expenseSheet);
    }

    private ExpenseSheet expenseSheetDtoToExpenseSheet(ExpenseSheetDTO expenseSheetDTO) throws EntityNotFoundException {

        StringBuilder errorMessage = new StringBuilder();
        boolean flag = false;

        User filedBy = userService.getUserByName(expenseSheetDTO.getFiledBy());
        if(filedBy == null) {
            errorMessage.append("User not found for given filedBy name");
            flag = true;
        }

        Project prj = projectService.getProjectByProjectName(expenseSheetDTO.getProjectName());
        if(prj == null){
            if(flag)
                errorMessage.append(", ");
            errorMessage.append("Given projectName not found");
            flag = true;
        }

        if(flag)
            throw new EntityNotFoundException(errorMessage.toString());

        return ExpenseSheet.builder().filedBy(filedBy)
                .sheetDate(expenseSheetDTO.getSheetDate())
                .status(expenseSheetDTO.getStatus())
                .currency(expenseSheetDTO.getCurrency())
                .description(expenseSheetDTO.getDescription())
                .project(prj)
                .approver(prj.getProjectManager())
                .build();

    }

    public ExpenseSheet getExpenseSheet(int expenseSheetId) throws EntityNotFoundException {
        Optional<ExpenseSheet> expenseSheetOptional = expenseSheetDao.findById(expenseSheetId);
        if(expenseSheetOptional.isPresent()){
            return expenseSheetOptional.get();
        }
        throw new EntityNotFoundException("No expenseSheet found with given expenseSheetId");
    }

    @Transactional
    public void deleteExpenseSheet(int expenseSheetId) throws EntityNotFoundException {
        ExpenseSheet expenseSheet = getExpenseSheet(expenseSheetId);

        Project project = expenseSheet.getProject();
        project.setTotalExpense(project.getTotalExpense() - expenseSheet.getTotalAmount());
        project.setBillableAmount(project.getBillableAmount() - expenseSheet.getBillableAmount());

        expenseSheetDao.delete(expenseSheet);
    }

    @Transactional
    public ExpenseSheet updateExpenseSheet(int expenseSheetId, ExpenseSheetDTO expenseSheetDTO) throws EntityNotFoundException, UnauthorizedAccessException {
        if (expenseSheetDTO.getStatus().equals(ExpenseStatus.APPROVED)
                || expenseSheetDTO.getStatus().equals(ExpenseStatus.REJECTED)) {
            throw new UnauthorizedAccessException("Update not authorized");
        }

        ExpenseSheet expSheet = getExpenseSheet(expenseSheetId);

        Project prj = expSheet.getProject();
        prj.setTotalExpense(prj.getTotalExpense() - expSheet.getTotalAmount());
        prj.setBillableAmount(prj.getBillableAmount() - expSheet.getBillableAmount());

        ExpenseSheet tempSheet = expenseSheetDtoToExpenseSheet(expenseSheetDTO);
        expSheet.setFiledBy(tempSheet.getFiledBy());
        expSheet.setSheetDate(tempSheet.getSheetDate());
        expSheet.setStatus(tempSheet.getStatus());
        expSheet.setCurrency(tempSheet.getCurrency());
        expSheet.setDescription(tempSheet.getDescription());
        expSheet.setProject(tempSheet.getProject());

        Project newProject = expSheet.getProject();
        newProject.setTotalExpense(newProject.getTotalExpense() + expSheet.getTotalAmount());
        newProject.setBillableAmount(newProject.getBillableAmount() + expSheet.getBillableAmount());

        expSheet.getExpenseList().forEach(exp -> exp.setStatus(expSheet.getStatus()));

        return expenseSheetDao.save(expSheet);

    }

    public ExpenseSheet approveExpenseSheet(int expenseSheetId, String status) throws EntityNotFoundException {
        ExpenseStatus statusEnum = ExpenseStatus.decode(status);
        ExpenseSheet expSheet = getExpenseSheet(expenseSheetId);
        expSheet.setStatus(statusEnum);
        expSheet.getExpenseList().forEach(exp -> exp.setStatus(expSheet.getStatus()));
        return expenseSheetDao.save(expSheet);
    }

    public File printExpenseSheet(int expenseSheetId) throws EntityNotFoundException {
        ExpenseSheet expSheet = getExpenseSheet(expenseSheetId);
        File pdfFile = File.builder()
                            .fileName("ExpenseSheet_" + expenseSheetId)
                            .fileType(MediaType.APPLICATION_PDF_VALUE)
                            .data(pdfGenerationHelper.generatePdf(expSheet))
                        .build();
        return pdfFile;
    }
}
