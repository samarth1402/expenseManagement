package com.adobe.prj.service;


import com.adobe.prj.dao.ProjectDao;
import com.adobe.prj.dto.ProjectDTO;
import com.adobe.prj.entity.Client;
import com.adobe.prj.entity.Project;
import com.adobe.prj.entity.User;
import com.adobe.prj.exception.DuplicateEntityException;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.helper.ProjectSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class ProjectService {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectSpecification projectSpecification;

    public Project addProject(ProjectDTO p) throws EntityNotFoundException, DuplicateEntityException {

        Optional<Project> prjOptional = projectDao.findById(p.getProjectCode());
        if(prjOptional.isPresent() && !prjOptional.get().isDeleted()){
            throw new DuplicateEntityException("Given projectCode already exists");
        }   else {
            Project prj = projectDtoToProject(p);
            return projectDao.save(prj);
        }
    }

    public Project getProjectByProjectCode(String projectCode) throws EntityNotFoundException {
        Optional<Project> prjOptional = projectDao.findById(projectCode);
        if(prjOptional.isPresent() && !prjOptional.get().isDeleted()){
            return prjOptional.get();
        }   else {
            throw new EntityNotFoundException("No Project found with given projectCode");
        }
    }

    public List<Project> getProjectList(String emailId) {
        Specification<Project> specification = where(projectSpecification.isDeletedQuery(false));

        List<Project> projectList =  projectDao.findAll(specification);

        if (Objects.isNull(emailId))    return projectList;

        return projectList.stream()
                .filter(project ->
                            project.getProjectManager().getEmailId().equals(emailId) ||
                            project.getTeamLead().getEmailId().equals(emailId) ||
                            project.getTeamMembers().stream()
                                                .map(teamMember -> teamMember.getEmailId())
                                                .collect(Collectors.toList())
                                    .contains(emailId))
                .collect(Collectors.toList());
    }

    public Project updateProject(String projectCode, ProjectDTO p) throws EntityNotFoundException {
        Project prj = getProjectByProjectCode(projectCode);

        if(prj.isDeleted())
            throw new EntityNotFoundException("Given projectCode not found");

        return  projectDao.save(projectDtoToProject(p));
    }

    public void deleteProjectById(String projectCode) throws EntityNotFoundException {
        Project prj = getProjectByProjectCode(projectCode);
        prj.setDeleted(true);
        projectDao.save(prj);
    }

    public Project projectDtoToProject(ProjectDTO p) throws EntityNotFoundException {

        Client client = clientService.getClientByName(p.getClientName());

        User teamLead = userService.getUserByName(p.getTeamLead());

        User projectManager = userService.getUserByName(p.getProjectManager());

        List<User> teamMembers = p.getTeamMembers().stream()
                .map(userName -> userService.getUserByName(userName))
                .collect(Collectors.toList());

        StringBuilder errorMessage = new StringBuilder();
        boolean flag = false;

        if(client == null){
            errorMessage.append("given client name not found");
            flag = true;
        }

        if(teamLead == null){
            errorMessage.append("given teamLead name not found in users");
            flag = true;
        }

        if(projectManager == null){
            if(flag) errorMessage.append(", ");
            errorMessage.append("given projectManager name not found in users");
            flag = true;
        }

        if(teamMembers.contains(null) ) {
            if(flag) errorMessage.append(", ");
            errorMessage.append("One of the teamMember not found in users");
            flag = true;
        }

        if(flag)
            throw new EntityNotFoundException(errorMessage.toString());

        return Project.builder().projectCode(p.getProjectCode())
                        .projectName(p.getProjectName())
                        .client(client)
                        .teamLead(teamLead)
                        .projectManager(projectManager)
                        .teamMembers(teamMembers)
                        .timesheetApprovalType(p.getTimesheetApprovalType())
                        .expenseApprovalType(p.getExpenseApprovalType())
                        .projectDescription(p.getProjectDescription())
                        .duration(p.getDuration())
                        .cost(p.getCost())
                        .status(p.getStatus())
                        .projectType(p.getProjectType())
                        .startDate(p.getStartDate())
                        .dueDate(p.getDueDate())
                        .build();
    }

    public Project getProjectByProjectName(String projectName) {
        List<Project> prj = projectDao.findByProjectName(projectName);
        if(prj.size()==1 && !prj.get(0).isDeleted()){
            return prj.get(0);
        }
        return null;
    }
}
