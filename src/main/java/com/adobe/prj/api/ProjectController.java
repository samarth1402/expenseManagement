package com.adobe.prj.api;

import com.adobe.prj.dto.ProjectDTO;
import com.adobe.prj.entity.Project;
import com.adobe.prj.exception.DuplicateEntityException;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "Project controller")
@RequestMapping("api/project")
public class ProjectController {
    @Autowired
    private ProjectService service;

    @ApiOperation(value = "Add a project")
    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public @ResponseBody Project addProject(@RequestBody ProjectDTO project) throws EntityNotFoundException, DuplicateEntityException {
        return service.addProject(project);
    }

    @ApiOperation(value = "Get project by Project code")
    @GetMapping("/{projectCode}")
    public @ResponseBody Project getProjectByProjectCode(@PathVariable("projectCode") String projectCode) throws EntityNotFoundException {
        return service.getProjectByProjectCode(projectCode);
    }

    @ApiOperation(value = "Get project lists")
    @GetMapping("/list")
    public @ResponseBody
    List<Project> getProjects(@RequestParam(value = "emailId", required = false) String emailId) {
        return service.getProjectList(emailId);
    }

    @ApiOperation(value = "Modify a Project")
    @PutMapping("/{projectCode}")
    public @ResponseBody Project updateProject(@PathVariable("projectCode") String projectCode, @RequestBody ProjectDTO project) throws EntityNotFoundException {
        return service.updateProject(projectCode, project);
    }

    @ApiOperation(value = "Delete a Project")
    @DeleteMapping("/{projectCode}")
    public @ResponseBody void deleteProjectById(@PathVariable("projectCode") String projectCode) throws EntityNotFoundException {
        service.deleteProjectById(projectCode);
    }
}
