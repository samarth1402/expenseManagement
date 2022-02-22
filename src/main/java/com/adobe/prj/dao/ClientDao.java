package com.adobe.prj.dao;

import com.adobe.prj.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientDao extends JpaRepository<Client,String> , JpaSpecificationExecutor<Client> {
    List<Client> findByName(String name);
}
