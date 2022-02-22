package com.adobe.prj.dao;

import com.adobe.prj.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectDao extends JpaRepository<Project, String> , JpaSpecificationExecutor<Project> {
    List<Project> findByProjectName(String projectName);
}
