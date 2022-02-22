package com.adobe.prj.api;

import com.adobe.prj.entity.Client;
import com.adobe.prj.exception.DuplicateEntityException;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.service.ClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/client")
@Api(value = "Client controller")
public class ClientController {
    @Autowired
    private ClientService service;

    @ApiOperation(value = "fetch all clients")
    @GetMapping("/list")
    public @ResponseBody
    List<Client> getClients() {
            return service.getClientList();
    }

    @ApiOperation(value = "fetch client by id")
    @GetMapping("/{emailId}")
    public @ResponseBody
    Client getClient(@PathVariable("emailId") String emailId) throws EntityNotFoundException {
        return service.getClientById(emailId);
    }

    @ApiOperation(value = "add a client")
    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public @ResponseBody Client addClient(@RequestBody @Valid Client c) throws DuplicateEntityException {
        return service.addClient(c);
    }

    @ApiOperation(value = "modify a client")
    @PutMapping("/{emailId}")
    public @ResponseBody Client modifyClient(@PathVariable("emailId") String emailId, @RequestBody Client c) throws EntityNotFoundException {
        return service.updateClient(emailId, c);
    }

    @ApiOperation(value = "delete a client")
    @DeleteMapping("/{emailId}")
    public void deleteClient(@PathVariable("emailId") String emailId) throws EntityNotFoundException {
        service.deleteClient(emailId);
    }

}
