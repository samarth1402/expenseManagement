package com.adobe.prj.service;

import com.adobe.prj.dao.ClientDao;
import com.adobe.prj.entity.Client;
import com.adobe.prj.exception.DuplicateEntityException;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.helper.ClientSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class ClientService {

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ClientSpecification clientSpecification;

    public List<Client> getClientList(){
        Specification<Client> specification = where(clientSpecification.isDeletedQuery(false));

        return clientDao.findAll(specification);
    }

    public Client getClientById(String emailId) throws EntityNotFoundException {
        Optional<Client> opt = clientDao.findById(emailId);
        if(opt.isPresent() && !opt.get().isDeleted()) {
            return opt.get();
        } else {
            throw new EntityNotFoundException("Client with id " + emailId + " doesn't exist!!!") ;
        }
    }

    public Client addClient(Client c) throws DuplicateEntityException {
        Optional<Client> clientOptional = clientDao.findById(c.getEmail());
        if(clientOptional.isPresent() && !clientOptional.get().isDeleted()){
            throw new DuplicateEntityException("Given client EmailId already exists");
        }
        return clientDao.save(c);
    }

    @Transactional
    public Client updateClient(String emailId, Client c) throws EntityNotFoundException {
        Client client = getClientById(emailId);
        if(client.isDeleted())
            throw new EntityNotFoundException("Client EmailId not found");
        return clientDao.save(c);
    }

    public void deleteClient(String emailId) throws EntityNotFoundException {
        Client client = getClientById(emailId);
        client.setDeleted(true);
        clientDao.save(client);
    }

    public Client getClientByName(String name){
        List<Client> clientList = clientDao.findByName(name);
        if (clientList.size()==1 && !clientList.get(0).isDeleted()){
            return  clientList.get(0);
        }
        return null;
    }



}
