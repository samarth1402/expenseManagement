package com.adobe.prj.dao;

import com.adobe.prj.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDao extends JpaRepository<File, Integer> {
}
